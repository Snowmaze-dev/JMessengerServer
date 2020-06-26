package jlanguage.messages

import jlanguage.MessagesUtils.NEW_MESSAGE_MESSAGE

class NewTextMessage : JMessage(NEW_MESSAGE_MESSAGE) {

    lateinit var textMessage: TextMessage

}