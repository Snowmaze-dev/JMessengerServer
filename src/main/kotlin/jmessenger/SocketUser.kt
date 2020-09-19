package jmessenger

import jmessenger.jlanguage.messages.requests.RequestMessage

interface SocketUser: User {

    fun sendMessage(message: RequestMessage, requestId: Int) {
        message.requestId = requestId
        return sendMessage(message)
    }

    fun disconnect()

}