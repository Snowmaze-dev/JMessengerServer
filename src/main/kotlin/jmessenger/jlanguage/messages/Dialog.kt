package jmessenger.jlanguage.messages

import jmessenger.jlanguage.utils.JMessagesUtils.DIALOG_MESSAGE

class Dialog(var login: String = ""): JMessage(DIALOG_MESSAGE) {

    lateinit var lastMessage: TextMessage

    override fun toString() = "Dialog(login='$login', lastMessage=$lastMessage)"

    fun dialogId() = lastMessage.dialogId

    fun fromUser() = lastMessage.fromUser

    fun toUser() = lastMessage.toUser

    override fun equals(other: Any?) = if(other is Dialog) other.dialogId() == dialogId() else false

    override fun hashCode() = dialogId()

}