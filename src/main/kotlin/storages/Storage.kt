package storages

import User
import jlanguage.messages.Dialog
import jlanguage.messages.TextMessage

internal interface Storage {

    fun addUser(login: String, password: String): Int

    fun getUser(login: String): User

    fun getUserLogin(id: Int): String

    fun getDialogs(userId: Int, page: Int): List<Dialog>

    fun addMessage(textMessage: TextMessage): Message

}