package jmessenger

import jmessenger.coreserver.LoggedUser
import jmessenger.jlanguage.messages.*
import jmessenger.storages.Storage
import jmessenger.storages.exceptions.NoSuchUserException
import jmessenger.storages.exceptions.UserAlreadyExistException
import java.net.ConnectException
import java.util.*
import java.util.concurrent.ConcurrentHashMap

abstract class UsersManager(protected val storage: Storage) : UserThread.UserCallback {

    protected val usersThreads = ConcurrentHashMap<Int, MutableList<SocketUser>>()
    protected val unauthorizedUserThreads: MutableList<SocketUser> =
        Collections.synchronizedList(mutableListOf<SocketUser>())

    val online: Int
        get() = usersThreads.size + unauthorizedUserThreads.size

    fun addClient(user: SocketUser) {
        unauthorizedUserThreads.add(user)
    }

    override fun onAuthorize(user: SocketUser, authMessage: AuthMessage) {
        user.sendMessage(if (unauthorizedUserThreads.contains(user)) {
                if (authMessage.action == AuthMessage.LOGIN) {
                    try {
                        val dbUser = storage.getUser(authMessage.login)
                        if (authMessage.password == dbUser.password) {
                            unauthorizedUserThreads.remove(user)
                            user.user = dbUser
                            addUserThread(dbUser.id, user)
                            SuccessLoginMessage(dbUser.id)
                        } else ErrorMessage(ErrorMessage.WRONG_PASSWORD, "Wrong password")
                    } catch (e: NoSuchUserException) {
                        NoSuchUserError()
                    } catch (e: ConnectException) {
                        ErrorMessage()
                    }
                } else try {
                    val id = storage.addUser(authMessage.login, authMessage.password)
                    addUserThread(id, user)
                    unauthorizedUserThreads.remove(user)
                    user.user = LoggedUser(id, authMessage.login, authMessage.password)
                    SuccessLoginMessage(id)
                } catch (e: UserAlreadyExistException) {
                    ErrorMessage(ErrorMessage.USER_ALREADY_EXIST, "User already exist")
                }
            } else ErrorMessage(ErrorMessage.ALREADY_LOGGED, "This session already logged")
        )
    }

    override fun onDisconnect(user: SocketUser) {
        if(!unauthorizedUserThreads.remove(user)) {
            val id = user.user!!.id
            val sessions = usersThreads[id]
            sessions?.apply {
                if (sessions.size == 1) usersThreads.remove(id)
                else remove(user)
            }
        }
    }

    private fun addUserThread(id: Int, user: SocketUser) {
        if (usersThreads.containsKey(id)) usersThreads[id]?.add(user)
        else usersThreads[id] = Collections.synchronizedList(mutableListOf(user))
    }

    protected fun checkUnauthorized(userThread: SocketUser, requestId: Int = 0): Boolean {
        if (unauthorizedUserThreads.contains(userThread)) {
            userThread.sendMessage(UnauthorizedErrorMessage(requestId))
            return true
        }
        return false
    }

    fun stop() {
        usersThreads.forEachValue(10) { it -> it.forEach { it.disconnect() } }
        unauthorizedUserThreads.forEach { it.disconnect() }
    }

}