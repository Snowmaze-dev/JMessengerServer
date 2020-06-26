package jlanguage

import jlanguage.messages.*

object MessagesUtils {

    const val AUTH_MESSAGE: Short = 0

    const val TEXT_MESSAGE: Short = 1

    const val ERROR_MESSAGE: Short = 2

    const val SUCCESS_MESSAGE: Short = 3

    const val SUCCESS_LOGIN_MESSAGE: Short = 4

    const val DIALOG_MESSAGE: Short = 5

    const val NEW_MESSAGE_MESSAGE: Short = 6

    const val NEW_MESSAGE_NOTIFICATION_MESSAGE: Short = 7

    const val MESSAGE_DELETED_MESSAGE: Short = 8

    const val MESSAGE_SENT_MESSAGE: Short = 9

    const val LIST_TEXT_MESSAGES: Short = 10

    const val LIST_DIALOGS: Short = 11

    const val REQUEST_DIALOGS: Short = 12

    const val REQUEST_DIALOG_MESSAGES: Short = 13

    fun getMessage(messageType: Short) = when (messageType) {
        AUTH_MESSAGE -> AuthMessage()
        TEXT_MESSAGE -> TextMessage()
        LIST_TEXT_MESSAGES -> ListTextMessages()
        LIST_DIALOGS -> ListDialogs()
        ERROR_MESSAGE -> ErrorMessage()
        SUCCESS_MESSAGE -> SuccessMessage()
        DIALOG_MESSAGE -> Dialog()
        REQUEST_DIALOGS -> RequestDialogs()
        REQUEST_DIALOG_MESSAGES -> RequestDialogMessages()
        NEW_MESSAGE_MESSAGE -> NewTextMessage()
        SUCCESS_LOGIN_MESSAGE -> SuccessLoginMessage()
        MESSAGE_SENT_MESSAGE -> MessageSentMessage()
        else -> throw Exception()
    }

}