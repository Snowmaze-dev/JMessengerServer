package jmessenger

import jmessenger.jlanguage.messages.requests.RequestMessage

interface SocketUser: User {

    fun sendMessage(message: RequestMessage, requestId: Int): Boolean {
        message.requestId = requestId
        return sendMessage(message)
    }

}