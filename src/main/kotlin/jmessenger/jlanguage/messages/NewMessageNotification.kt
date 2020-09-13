package jmessenger.jlanguage.messages

import jmessenger.jlanguage.utils.JMessagesUtils.NEW_MESSAGE_NOTIFICATION_MESSAGE

class NewMessageNotification: JMessage(NEW_MESSAGE_NOTIFICATION_MESSAGE) {

    lateinit var message: TextMessage

}