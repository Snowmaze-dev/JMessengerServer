package jmessenger.jmessengerserver

import jmessenger.SocketUser
import jmessenger.UserThread
import jmessenger.jlanguage.messages.JMessage
import jmessenger.jlanguage.messages.requests.*
import java.net.Socket

class CoreUserThread(socket: Socket, serverName: String, private val callback: CoreUserCallback) : UserThread(socket, serverName, callback) {

    override fun onMessageReceived(message: JMessage) {
        super.onMessageReceived(message)
        if (message is SendMessageRequest) {
            callback.onTextMessage(this, message)
        }
        if (message is RequestDialogs) {
            callback.onRequestDialogs(this, message)
        }
        if (message is RequestDialogMessages) {
            callback.onRequestDialogMessages(this, message)
        }
        if (message is RequestUserById) {
            callback.onRequestLoginById(this, message)
        }
        if (message is RequestUserByLogin) {
            callback.onRequestIdByLogin(this, message)
        }
        if (message is RequestEditMessage) {
            callback.onRequestEditMessage(this, message)
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