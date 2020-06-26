import jlanguage.messages.*
import jlanguage.messages.AuthMessage.Companion.LOGIN
import jlanguage.messages.ErrorMessage.Companion.ALREADY_LOGGED
import jlanguage.messages.ErrorMessage.Companion.NO_SUCH_USER
import jlanguage.messages.ErrorMessage.Companion.USER_ALREADY_EXIST
import jlanguage.messages.ErrorMessage.Companion.WRONG_PASSWORD
import storages.MySQLStorage
import storages.UserAlreadyExistException
import storages.UserNotFoundException
import java.util.*

class UsersManager : UserThread.UserCallback {

    private val usersThreads = mutableMapOf<Int, UserThread>()
    private val unauthorizedUserThreads = mutableListOf<Thread>()
    private val storage = MySQLStorage()

    fun addClient(thread: Thread) {
        unauthorizedUserThreads.add(thread)
    }

    override fun onAuthorize(userThread: UserThread, authMessage: AuthMessage) {
        authMessage.password = authMessage.password.toLowerCase()
        userThread.sendMessage(if (unauthorizedUserThreads.contains(userThread)) {
            if (authMessage.action == LOGIN) {
                try {
                    val user = storage.getUser(authMessage.login)
                    if (authMessage.password == user.password.toLowerCase()) {
                        unauthorizedUserThreads.remove(userThread)
                        usersThreads[user.id] = userThread
                        userThread.user = user
                        SuccessLoginMessage(user.id)
                    } else {
                        ErrorMessage().apply {
                            code = WRONG_PASSWORD
                            message = "Wrong password"
                        }
                    }
                } catch (e: UserNotFoundException) {
                    ErrorMessage().apply {
                        message = "No such user"
                        code = NO_SUCH_USER
                    }
                }
            } else {
                try {
                    val id = storage.addUser(authMessage.login, authMessage.password)
                    usersThreads[id] = userThread
                    userThread.user = User(id, authMessage.login, authMessage.password)
                    SuccessLoginMessage(id)
                } catch (e: UserAlreadyExistException) {
                    ErrorMessage().apply {
                        code = USER_ALREADY_EXIST
                        message = "User already exist"
                    }
                }
            }
        } else {
            ErrorMessage().apply {
                code = ALREADY_LOGGED
                message = "This session already logged"
            }
        }, authMessage.requestId)
    }

    private fun checkAuthorized(userThread: UserThread, requestId: Int): Boolean {
        if (unauthorizedUserThreads.contains(userThread)) {
            userThread.sendMessage(UnAuthorizedErrorMessage(), requestId)
            return false
        }
        return true
    }

    override fun onTextMessage(userThread: UserThread, message: TextMessage) {
        if (checkAuthorized(userThread, message.requestId)) {
            message.fromUser = userThread.user!!.id
            val DBMessage = storage.addMessage(message) // TODO UserNotFoundException
            val time = Date().time
            message.apply {
                message.dialogId = DBMessage.dialogId
                id = DBMessage.id
                date = time
            }
            if (usersThreads.containsKey(message.toUser)) {
                usersThreads[message.toUser]?.onNewMessage(message)
            }
            userThread.sendMessage(MessageSentMessage(message.id, message.dialogId, time), message.requestId)
        }
    }

    override fun onRequestDialogs(userThread: UserThread, message: RequestDialogs) {
        if(checkAuthorized(userThread, message.requestId)) {
            val dialogs = storage.getDialogs(userThread.user!!.id, message.page)
            userThread.sendMessage(ListDialogs(message.page).apply {
                list = dialogs.toMutableList()
            }, message.requestId)
        }
    }

    override fun onDisconnect(userThread: UserThread) {
        if (unauthorizedUserThreads.contains(userThread)) unauthorizedUserThreads.remove(userThread)
        else usersThreads.remove(userThread.user!!.id)
    }


}