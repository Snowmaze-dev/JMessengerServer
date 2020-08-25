package jmessenger.jmessengerserver

import jmessenger.SocketUser
import jmessenger.User
import jmessenger.UsersManager
import jmessenger.jlanguage.messages.*
import jmessenger.jlanguage.messages.requests.*
import jmessenger.storages.Storage
import jmessenger.storages.exceptions.NoSuchUserException
import java.sql.SQLException
import java.util.*

class CoreUsersManager(storage: Storage) : UsersManager(storage), CoreUserThread.UserCallback {

    override fun onTextMessage(userThread: SocketUser, request: SendMessageRequest) {
        if (checkUnauthorized(userThread, request.requestId)) return
        val message = request.textMessage
        message.fromUser = userThread.user!!.id
        val time = Date().time
        try {
            val addedMessage = storage.addMessage(message)
            message.apply {
                this.time = time
                message.dialogId = addedMessage.dialogId
                id = addedMessage.id
            }
        } catch (e: NoSuchUserException) {
            userThread.sendMessage(NoSuchUserError(request.requestId))
            return
        } catch (e: SQLException) {
            e.printStackTrace()
            userThread.sendMessage(UnknownError(request.requestId))
            return
        }
        val notification = NewMessageNotification().apply { this.message = message }
        sendMessageExcept(message.fromUser, userThread, notification)
        if(message.fromUser != message.toUser) {
            usersThreads[message.toUser]?.toList()?.forEach {
                it.sendMessage(notification)
            }
        }
        userThread.sendMessage(MessageSentMessage(message.id, message.dialogId, time), request.requestId)
    }

    override fun onRequestDialogs(userThread: SocketUser, message: RequestDialogs) {
        if (checkUnauthorized(userThread, message.requestId)) return
        var dialogs = storage.getDialogs(userThread.user!!.id, message.page)
        dialogs = dialogs.reversed()
        userThread.sendMessage(ListDialogs(message.page).apply {
            list = dialogs.toMutableList()
            requestId = message.requestId
        })
    }

    override fun onRequestDialogMessages(userThread: SocketUser, message: RequestDialogMessages) {
        if (checkUnauthorized(userThread, message.requestId)) return
        val messages = storage.getMessages(message.dialogId, message.fromId)
        userThread.sendMessage(ListTextMessages(messages), message.requestId)
    }

    override fun onRequestLoginById(userThread: SocketUser, requestUserById: RequestUserById) {
        if (checkUnauthorized(userThread, requestUserById.requestId)) return
        try {
            val login = storage.getUserLogin(requestUserById.id)
            userThread.sendMessage(
                UserMessage(requestUserById.id, login,
                    storage.getDialog(userThread.user!!.id, requestUserById.id)?.apply {
                        this.login = login
                    }),
                requestUserById.requestId
            )
        } catch (e: NoSuchUserException) {
            userThread.sendMessage(NoSuchUserError(), requestUserById.requestId)
        }
    }

    override fun onRequestIdByLogin(userThread: SocketUser, requestUserByLogin: RequestUserByLogin) {
        try {
            val id = storage.getUserId(requestUserByLogin.login)
            userThread.sendMessage(UserMessage(id, requestUserByLogin.login, storage.getDialog(userThread.user!!.id, id)).apply {
                    this.login = requestUserByLogin.login
                }, requestUserByLogin.requestId
            )
        } catch (e: NoSuchUserException) {
            userThread.sendMessage(NoSuchUserError(), requestUserByLogin.requestId)
        }
    }

    override fun onRequestEditMessage(userThread: SocketUser, requestEditMessage: RequestEditMessage) {
        if (checkUnauthorized(userThread, requestEditMessage.requestId)) return
        val editedMessage = requestEditMessage.textMessage
        val message = storage.getMessage(editedMessage.id) // MessageNotFound TODO
        val userId = userThread.user!!.id
        if (message.fromUser == userId) {
            storage.editMessage(message.id, editedMessage.message)
            editedMessage.attachments.forEach {
                if(!message.attachments.contains(it)) {
                    if (it is Document) {
                        storage.addDocumentToMessage(editedMessage.id, it.id, it.documentType)
                    }
                }
            }
            message.attachments.forEach {
                if(!editedMessage.attachments.contains(it)) {
                    storage.removeAttachmentFromMessage(editedMessage.id, it.id)
                }
            }
            val otherUser = if (userId == message.toUser) message.fromUser else message.toUser
            val notification = MessageEditedNotification().apply {
                textMessage = editedMessage
            }
            sendMessageExcept(userThread.user!!.id, userThread, notification)
            if(message.fromUser != message.toUser) {
                usersThreads[otherUser]?.toList()?.forEach {
                    it.sendMessage(notification)
                }
            }
            userThread.sendMessage(SuccessMessage(), requestEditMessage.requestId)
        }
        else userThread.sendMessage(PermissionDeniedMessage(), requestEditMessage.requestId)
    }

    private fun sendMessageExcept(toUser: Int, user: SocketUser, message: RequestMessage, requestId: Int) {
            usersThreads[toUser]?.forEach {
                if (user != it) it.sendMessage(message, requestId)
            }
    }

    private fun sendMessageExcept(toUser: Int, userThread: User, message: JMessage) {
            usersThreads[toUser]?.forEach {
                if (userThread != it) it.sendMessage(message)
            }
    }


}