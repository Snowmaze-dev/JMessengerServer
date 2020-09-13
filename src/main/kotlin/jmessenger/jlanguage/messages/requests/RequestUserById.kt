package jmessenger.jlanguage.messages.requests

import jmessenger.jlanguage.utils.JMessagesUtils.REQUEST_USER_BY_ID

class RequestUserById(var id: Int = 0): RequestMessage(REQUEST_USER_BY_ID)