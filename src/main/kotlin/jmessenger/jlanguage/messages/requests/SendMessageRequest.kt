package jmessenger.jlanguage.messages.requests

import jmessenger.jlanguage.messages.TextMessage
import jmessenger.jlanguage.utils.MessagesUtils.SEND_MESSAGE_REQUEST

class SendMessageRequest : RequestMessage(SEND_MESSAGE_REQUEST) {

    lateinit var textMessage: TextMessage

}