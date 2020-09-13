package jmessenger.jlanguage.messages

import jmessenger.jlanguage.messages.requests.RequestMessage
import jmessenger.jlanguage.utils.JMessagesUtils.SUCCESS_LOGIN_MESSAGE

class SuccessLoginMessage(var id: Int = 0): RequestMessage(SUCCESS_LOGIN_MESSAGE)