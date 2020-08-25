package jmessenger

import jmessenger.jlanguage.messages.*
import jmessenger.jmessengerserver.LoggedUser
import jmessenger.storages.Storage
import jmessenger.storages.exceptions.NoSuchUserException
import jmessenger.storages.exceptions.UserAlreadyExistException
import java.net.ConnectException
import java.util.*
import java.util.concurrent.ConcurrentHashMap

abstract class UsersManager(protected val storage: Storage) : UserThread.UserCallback {

    internal val usersThreads = ConcurrentHashMap<Int, MutableList<SocketUser>>()
    internal val unauthorizedUserThreads = Collections.synchronizedList(mutableListOf<SocketUser>())

    val online: Int
        get() = usersThreads.size + unauthorizedUserThreads.size

    fun addClient(user: SocketUser) {
        unauthorizedUserThreads.add(user)
    }

    override fun onAuthorize(socketUser: SocketUser, authMessage: AuthMessage) {
        socketUser.sendMessage(if (unauthorizedUserThreads.contains(socketUser)) {
            if (authMessage.action == AuthMessage.LOGIN) {
                try {
                    val user = storage.getUser(authMessage.login)
                    if (authMessage.password == user.password.toLowerCase()) {
                        unauthorizedUserThreads.remove(socketUser)
                        addUserThread(user.id, socketUser)
                        socketUser.user = user
                        SuccessLoginMessage(user.id)
                    } else {
                        ErrorMessage().apply {
                            code = ErrorMessage.WRONG_PASSWORD
                            message = "Wrong password"
                        }
                    }
                } catch (e: NoSuchUserException) {
                    NoSuchUserError()
                } catch (e: ConnectException) {
                    ErrorMessage()
                }
            } else {
                try {
                    val id = storage.addUser(authMessage.login, authMessage.password)
                    addUserThread(id, socketUser)
                    unauthorizedUserThreads.remove(socketUser)
                    socketUser.user =
                        LoggedUser(id, authMessage.login, authMessage.password)
                    SuccessLoginMessage(id)
                } catch (e: UserAlreadyExistException) {
                    ErrorMessage().apply {
                        code = ErrorMessage.USER_ALREADY_EXIST
                        message = "User already exist"
                    }
                }
            }
        } else {
            ErrorMessage().apply {
                code = ErrorMessage.ALREADY_LOGGED
                message = "This session already logged"
            }
        })
    }

    override fun onDisconnect(user: SocketUser) {
        if (unauthorizedUserThreads.contains(user)) {
            unauthorizedUserThreads.remove(user)
            return
        } else {
            val id = user.user!!.id
            val sessions = usersThreads[id]
            sessions?.apply {
                if (sessions.size == 1) usersThreads.remove(id)
                else remove(user)
            }
            return
        }
    }

    private fun addUserThread(id: Int, user: SocketUser) {
        if (usersThreads.containsKey(id)) usersThreads[id]?.add(user)
        else usersThreads[id] = mutableListOf(user)
    }

    internal fun checkUnauthorized(userThread: SocketUser, requestId: Int = 0): Boolean {
        if (unauthorizedUserThreads.contains(userThread)) {
            userThread.sendMessage(UnauthorizedErrorMessage().apply {
                this.requestId = requestId
            })
            return true
        }
        return false
    }

}