package jmessenger.jlanguage.messages

import jmessenger.jlanguage.messages.requests.RequestMessage
import jmessenger.jlanguage.utils.JMessagesUtils.MESSAGE_SENT_MESSAGE

class MessageSentMessage(var id: Int = 0, var dialogId: Int = 0, var date: Long = 0): RequestMessage(MESSAGE_SENT_MESSAGE)