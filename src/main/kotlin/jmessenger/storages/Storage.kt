package jmessenger.storages

import jmessenger.jlanguage.messages.Dialog
import jmessenger.jlanguage.messages.Document
import jmessenger.jlanguage.messages.TextMessage
import jmessenger.jmessengerserver.LoggedUser

interface Storage {

    fun init()

    fun addUser(login: String, password: String): Int

    fun getUser(login: String): LoggedUser

    fun getUserLogin(id: Int): String

    fun getUserId(login: String): Int

    fun getDialogs(userId: Int, page: Int): List<Dialog>

    fun addMessage(textMessage: TextMessage): Message

    fun getMessages(dialogId: Int, fromId: Int): List<TextMessage>

    fun getDialog(userId: Int, otherUser: Int): Dialog?

    fun getMessage(id: Int): TextMessage

    fun editMessage(id: Int, message: String)

    fun deleteMessage(id: Int)

    fun addFile(name: String, type: Int, parentId: Int): Int

    fun getDocumentName(id: Int): String

    fun getDocument(id: Int): Document

    fun addDocumentToMessage(messageId: Int, documentId: Int, type: Int)

    fun removeAttachmentFromMessage(messageId: Int, documentId: Int)

}