package jmessenger

import jmessenger.coreserver.LoggedUser
import jmessenger.jlanguage.messages.JMessage

interface User {

    var user: LoggedUser?

    fun user() = (user?.login ?: "unauthorized user")

    fun sendMessage(message: JMessage, log: Boolean = true)

    fun onMessageReceived(message: JMessage)

}