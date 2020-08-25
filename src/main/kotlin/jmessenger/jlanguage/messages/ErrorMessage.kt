package jmessenger.jlanguage.messages

import jmessenger.jlanguage.messages.requests.RequestMessage
import jmessenger.jlanguage.utils.MessagesUtils.ERROR_MESSAGE

open class ErrorMessage(requestId: Int = 0): RequestMessage(ERROR_MESSAGE, requestId) {

    open var code = 0

    open var message = ""

    var messageType = 0 // TODO

    companion object {

        const val UNAUTHORIZED = 0

        const val ALREADY_LOGGED = 1

        const val WRONG_PASSWORD = 2

        const val NO_SUCH_USER = 3

        const val USER_ALREADY_EXIST = 4

        const val CONNECTION_FAILED = 5

        const val PERMISSION_DENIED = 6

        const val UNKNOWN = 7

    }

    override fun toString(): String {
        return "ErrorMessage(code=$code, message='$message')"
    }

}