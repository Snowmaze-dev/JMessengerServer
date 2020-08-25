package jmessenger.jlanguage.messages

import jmessenger.jlanguage.messages.requests.RequestMessage
import jmessenger.jlanguage.utils.MessagesUtils.USER_MESSAGE

class UserMessage(var id: Int = 0, var login: String = "", var dialog: Dialog? = null): RequestMessage(USER_MESSAGE)