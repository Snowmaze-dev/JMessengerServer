package jlanguage.messages

import jlanguage.MessagesUtils.DIALOG_MESSAGE

class Dialog: JMessage(DIALOG_MESSAGE) {

    var login = ""

    lateinit var lastMessage: TextMessage

}