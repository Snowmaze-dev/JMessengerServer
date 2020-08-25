package jmessenger.jlanguage.messages.requests

import jmessenger.jlanguage.messages.JMessage

open class RequestMessage(type: Short, var requestId: Int = 0) : JMessage(type)