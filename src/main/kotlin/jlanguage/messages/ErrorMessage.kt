package jlanguage.messages

import jlanguage.MessagesUtils.ERROR_MESSAGE

open class ErrorMessage: JMessage(ERROR_MESSAGE) {

    open var code = 0

    open var message = ""

    var messageType = 0

    companion object {

        const val UNAUTHORIZED = 0

        const val ALREADY_LOGGED = 1

        const val WRONG_PASSWORD = 2

        const val NO_SUCH_USER = 3

        const val USER_ALREADY_EXIST = 4

    }

    override fun toString(): String {
        return "ErrorMessage(code=$code, message='$message')"
    }

}