package jmessenger

import jmessenger.jlanguage.messages.JMessage
import jmessenger.jmessengerserver.LoggedUser

interface User {

    var user: LoggedUser?

    fun user() = (user?.login ?: "unauthorized user")

    fun sendMessage(message: JMessage, log: Boolean = true)

    fun onMessageReceived(message: JMessage)

}