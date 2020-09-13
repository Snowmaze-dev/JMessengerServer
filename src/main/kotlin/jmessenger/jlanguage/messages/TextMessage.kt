package jmessenger.jlanguage.messages

import jmessenger.jlanguage.utils.JMessagesUtils.TEXT_MESSAGE

open class TextMessage: JMessage(TEXT_MESSAGE) {

    var id = 0

    var dialogId = 0

    var fromUser = 0

    var toUser = 0

    var message = ""

    var time = 0L

    var attachments = mutableListOf<Attachment>()

    override fun toString(): String {
        return "TextMessage(id=$id, fromUser=$fromUser, message='$message', time=$time, attachments=$attachments)"
    }


}