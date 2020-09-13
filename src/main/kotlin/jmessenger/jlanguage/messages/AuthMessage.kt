package jmessenger.jlanguage.messages

import jmessenger.jlanguage.utils.JMessagesUtils.AUTH_MESSAGE

class AuthMessage(var login: String = "", var password: String = ""): JMessage(AUTH_MESSAGE) {

    var action = LOGIN

    companion object {

        const val LOGIN = 0

        const val REGISTRATION = 1

    }

    override fun toString(): String {
        return "AuthMessage(login='$login', action=$action)"
    }

}