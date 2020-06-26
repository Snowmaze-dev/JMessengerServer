package jlanguage.messages

import jlanguage.MessagesUtils.MESSAGE_SENT_MESSAGE

class MessageSentMessage(var id: Int = 0, var dialogId: Int = 0, var date: Long = 0): JMessage(MESSAGE_SENT_MESSAGE)