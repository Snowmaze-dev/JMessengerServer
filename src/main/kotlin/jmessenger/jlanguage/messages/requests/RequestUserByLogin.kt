package jmessenger.jlanguage.messages.requests

import jmessenger.jlanguage.utils.MessagesUtils.REQUEST_USER_BY_LOGIN

class RequestUserByLogin(var login: String = ""): RequestMessage(REQUEST_USER_BY_LOGIN)