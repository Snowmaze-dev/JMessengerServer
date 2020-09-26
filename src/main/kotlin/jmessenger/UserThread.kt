package jmessenger

import jmessenger.coreserver.LoggedUser
import jmessenger.jlanguage.JLanguageInputStream
import jmessenger.jlanguage.Task
import jmessenger.jlanguage.WriteMessageTask
import jmessenger.jlanguage.messages.AuthMessage
import jmessenger.jlanguage.messages.DisconnectMessage
import jmessenger.jlanguage.messages.JMessage
import jmessenger.jlanguage.messages.SignalMessage
import jmessenger.jlanguage.utils.DataOutputStream
import jmessenger.jlanguage.utils.exceptions.TimeoutException
import jmessenger.jlanguage.utils.exceptions.UnknownMessage
import jmessenger.utils.LogsManager.log
import java.io.IOException
import java.net.Socket
import java.net.SocketException
import java.util.*

abstract class UserThread(private val socket: Socket, private val serverName: String = "", private val callback: UserCallback) : Thread(), SocketUser {

    internal val inputStream = JLanguageInputStream(socket.inputStream, 20*1000)
    internal val outputStream = DataOutputStream(socket.outputStream)
    private val tasks = LinkedList<Task>()
    var currentTask: Task? = null
    private set

    override var user: LoggedUser? = null

    init {
        name = toString()
    }

    override fun run() {
        var passed = 0
        var waitingForSignal = false
        val sleepTime = 10L
        val timeout = (50/sleepTime * 1000).toInt()
        loop@ while (socket.isConnected) {
            sleep(sleepTime)
            passed++
            if (passed == timeout) {
                if (waitingForSignal)  break
                else {
                    sendMessage(SignalMessage(), false)
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
                e.printStackTrace()
                break
            }
            catch(e: TimeoutException) {
                e.printStackTrace()
                break
            }
            catch (e: UnknownMessage) {
                e.printStackTrace()
                inputStream.skip(inputStream.available().toLong())
                continue
            }
            when (message) {
                is SignalMessage -> {
                    passed = 0
                    waitingForSignal = false
                }
                is DisconnectMessage -> break@loop
                is AuthMessage -> {
                    logMessage(message)
                    callback.onAuthorize(this, message)
                }
                else -> onMessageReceived(message)
            }
        }
        onDisconnect()
    }

    private fun logMessage(message: JMessage) {
        log("$serverName: Received message from " + toString() + " - $message")
    }

    override fun onMessageReceived(message: JMessage) {
        logMessage(message)
    }

    fun addTask(task: Task) {
        tasks.addLast(task)
        if(currentTask == null) executeFirstTask()
    }

    override fun sendMessage(message: JMessage, log: Boolean) {
        addTask(WriteMessageTask(message, outputStream) {
            if (log) log("Message $message sent to " + user())
        })
    }

    private fun executeFirstTask() {
        val task = tasks.poll() ?: run {
            currentTask = null
            return
        }
        try {
            currentTask = task
            task.execute()
        } catch (e: SocketException) {
            socket.close()
            tasks.clear()
            return
        }
        executeFirstTask()
    }

    private fun onDisconnect() {
        log("$serverName: " + toString() + " disconnected")
        if (!socket.isClosed) socket.close()
        callback.onDisconnect(this)
    }

    override fun disconnect() {
        sendMessage(DisconnectMessage(), false)
        while (tasks.isNotEmpty()) {
            sleep(5)
        }
        socket.close()
    }

    override fun toString() = user() + "(${socket.inetAddress.hostAddress})"

    interface UserCallback {

        fun onAuthorize(user: SocketUser, authMessage: AuthMessage)

        fun onDisconnect(user: SocketUser)

    }

}
