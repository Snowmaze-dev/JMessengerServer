package jmessenger.jlanguage.messages.requests

import jmessenger.jlanguage.messages.TextMessage
import jmessenger.jlanguage.utils.JMessagesUtils.SEND_MESSAGE_REQUEST

class SendMessageRequest : RequestMessage(SEND_MESSAGE_REQUEST) {

    lateinit var textMessage: TextMessage

}