package jmessenger.jlanguage.messages.requests

import jmessenger.jlanguage.messages.JMessage

open class RequestMessage(type: Int, var requestId: Int = 0) : JMessage(type)