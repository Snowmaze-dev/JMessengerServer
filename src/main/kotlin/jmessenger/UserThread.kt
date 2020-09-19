package jmessenger

import jmessenger.jlanguage.JLanguageInputStream
import jmessenger.jlanguage.JLanguageOutputStream
import jmessenger.jlanguage.messages.AuthMessage
import jmessenger.jlanguage.messages.DisconnectMessage
import jmessenger.jlanguage.messages.JMessage
import jmessenger.jlanguage.messages.SignalMessage
import jmessenger.jmessengerserver.LoggedUser
import jmessenger.utils.LogsManager
import java.io.IOException
import java.net.Socket
import java.net.SocketException
import java.util.*

abstract class UserThread(private val socket: Socket, private val serverName: String = "", private val callback: UserCallback) : Thread(), SocketUser {

    internal val inputStream: JLanguageInputStream = JLanguageInputStream(socket.inputStream)
    internal val outputStream: JLanguageOutputStream = JLanguageOutputStream(socket.outputStream)
    private val messagesQueue = LinkedList<QueueJMessage>()
    private var sending: Boolean = false

    override var user: LoggedUser? = null

    override fun run() {
        var passed = 0
        var waitingForSignal = false
        val timeout = 100 * 1000
        while (true) {
            if (!socket.isConnected) break
            sleep(5)
            passed++
            if (passed == timeout) {
                if (waitingForSignal) break
                else {
                    try {
                        sendMessage(SignalMessage(), false)
                    } catch (e: Exception) {
                        break
                    }
                    waitingForSignal = true
                    passed = 0
                }
            }
            try {
                if (inputStream.available() == 0) {
                    continue
                }
            } catch (e: Exception) {
                break
            }
            val message = try {
                inputStream.parseLastMessage()
            } catch (e: IOException) {
                break
            } catch (e: SocketException) {
                break
            }
            if (message is SignalMessage) {
                passed = 0
                waitingForSignal = false
                continue
            }
            if (message is DisconnectMessage) {
                break
            }
            onMessageReceived(message)
        }
        onDisconnect()
    }

    override fun onMessageReceived(message: JMessage) {
        if (message is AuthMessage) {
            callback.onAuthorize(this, message)
        }
        LogsManager.log("$serverName: Received message from " + user() + " $message")
    }

    override fun sendMessage(message: JMessage, log: Boolean) {
        messagesQueue.addLast(QueueJMessage(message, log))
        if(!sending) {
            sendFirstMessage()
        }
    }

    private fun sendFirstMessage() {
        val message = messagesQueue.poll() ?: run {
            sending = false
            return
        }
        sending = true
        try {
            outputStream.sendMessage(message.jMessage)
        } catch (e: SocketException) {
            socket.close()
            messagesQueue.clear()
            callback.onDisconnect(this)
        }
        if (message.log) LogsManager.log("Send message to " + user() + ": ${message.jMessage}")
        sendFirstMessage()
    }

    private fun onDisconnect() {
        LogsManager.log("$serverName: " + user() + " disconnected")
        if (!socket.isClosed) socket.close()
        callback.onDisconnect(this)
    }

    override fun disconnect() {
        sendMessage(DisconnectMessage(), false)
        while (messagesQueue.isNotEmpty()) {
            sleep(5)
        }
        socket.close()
    }

    interface UserCallback {

        fun onAuthorize(user: SocketUser, authMessage: AuthMessage)

        fun onDisconnect(user: SocketUser)

    }

}
