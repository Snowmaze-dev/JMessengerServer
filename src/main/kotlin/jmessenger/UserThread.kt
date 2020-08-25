package jmessenger

import jmessenger.jlanguage.JLanguageInputStream
import jmessenger.jlanguage.JLanguageOutputStream
import jmessenger.jlanguage.messages.AuthMessage
import jmessenger.jlanguage.messages.DisconnectMessage
import jmessenger.jlanguage.messages.JMessage
import jmessenger.jlanguage.messages.SignalMessage
import jmessenger.jmessengerserver.LoggedUser
import java.io.IOException
import java.net.Socket
import java.net.SocketException

abstract class UserThread(private val socket: Socket, private val serverName: String = "", private val callback: UserCallback) : Thread(), SocketUser {

    internal val inputStream: JLanguageInputStream = JLanguageInputStream(socket.inputStream)
    internal val outputStream: JLanguageOutputStream = JLanguageOutputStream(socket.outputStream)

    override var user: LoggedUser? = null

    override fun run() {
        var passed = 0
        var waitingForSignal = false
        val timeout = 100*1000
        while (true) {
            if (!socket.isConnected) break
            sleep(5)
            passed++
            if (passed == timeout) {
                if(waitingForSignal) break
                else {
                    try {
                        outputStream.sendMessage(SignalMessage())
                    }
                    catch (e: Exception) { break }
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
            }
            catch (e: SocketException) {
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
        println("$serverName: Received message from " + user() + " $message")
    }

    override fun sendMessage(message: JMessage) = try {
        println("sendMessage to " + user() + ": $message")
        outputStream.sendMessage(message)
        true
    } catch (e: SocketException) {
        socket.close()
        callback.onDisconnect(this)
        false
    }

    private fun onDisconnect() {
        println(user() + " disconnected from ${serverName.toLowerCase()}")
        if (!socket.isClosed) socket.close()
        callback.onDisconnect(this)
    }

    interface UserCallback {

        fun onAuthorize(userThread: SocketUser, authMessage: AuthMessage)

        fun onDisconnect(userThread: SocketUser)

    }

}
