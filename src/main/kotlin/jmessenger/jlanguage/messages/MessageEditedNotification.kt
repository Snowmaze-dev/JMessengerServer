package jmessenger.jlanguage.messages

import jmessenger.jlanguage.messages.requests.RequestMessage
import jmessenger.jlanguage.utils.MessagesUtils.MESSAGE_EDITED_NOTIFICATION

class MessageEditedNotification: RequestMessage(MESSAGE_EDITED_NOTIFICATION) {

    lateinit var textMessage: TextMessage

}