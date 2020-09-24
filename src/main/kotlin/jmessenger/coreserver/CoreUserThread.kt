package jmessenger.coreserver

import jmessenger.SocketUser
import jmessenger.UserThread
import jmessenger.jlanguage.messages.JMessage
import jmessenger.jlanguage.messages.requests.*
import java.net.Socket

class CoreUserThread(socket: Socket, serverName: String, private val callback: CoreUserCallback) : UserThread(socket, serverName, callback) {

    override fun onMessageReceived(message: JMessage) {
        super.onMessageReceived(message)
        callback.let {
            when (message) {
                is SendMessageRequest -> it.onTextMessage(this, message)
                is RequestDialogs -> it.onRequestDialogs(this, message)
                is RequestDialogMessages -> it.onRequestDialogMessages(this, message)
                is RequestUserById -> it.onRequestLoginById(this, message)
                is RequestUserByLogin -> it.onRequestIdByLogin(this, message)
                is RequestEditMessage -> it.onRequestEditMessage(this, message)
            }
        }
    }

    interface CoreUserCallback : UserCallback {

        fun onTextMessage(user: SocketUser, request: SendMessageRequest)

        fun onRequestDialogs(user: SocketUser, message: RequestDialogs)

        fun onRequestDialogMessages(user: SocketUser, message: RequestDialogMessages)

        fun onRequestLoginById(user: SocketUser, requestUserById: RequestUserById)

        fun onRequestIdByLogin(user: SocketUser, requestUserByLogin: RequestUserByLogin)

        fun onRequestEditMessage(user: SocketUser, requestEditMessage: RequestEditMessage)

    }
}