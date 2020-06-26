package jlanguage.messages

import jlanguage.MessagesUtils.TEXT_MESSAGE

open class TextMessage: JMessage(TEXT_MESSAGE) {

    var id = 0

    var dialogId = 0

    var fromUser = 0

    var toUser = 0

    var message = ""

    var date = 0L

    override fun toString(): String {
        return "TextMessage(id=$id, fromUser=$fromUser, message='$message', date=$date)"
    }


}