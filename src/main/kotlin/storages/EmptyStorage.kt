package storages

import User
import jlanguage.messages.Dialog
import jlanguage.messages.TextMessage

class EmptyStorage: Storage {

    override fun addUser(login: String, password: String): Int {
        return 0
    }

    override fun getUser(login: String): User {
        throw UserNotFoundException()
    }

    override fun getUserLogin(id: Int): String {
        throw UserNotFoundException()
    }

    override fun getDialogs(userId: Int, page: Int): List<Dialog> {
        return listOf()
    }

    override fun addMessage(textMessage: TextMessage): Message {
        return Message(0, 0)
    }
}