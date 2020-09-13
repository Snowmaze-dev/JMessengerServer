package jmessenger.jlanguage.messages.requests

import jmessenger.jlanguage.messages.TextMessage
import jmessenger.jlanguage.utils.JMessagesUtils.REQUEST_EDIT_MESSAGE

class RequestEditMessage: RequestMessage(REQUEST_EDIT_MESSAGE) {

    lateinit var textMessage: TextMessage

}