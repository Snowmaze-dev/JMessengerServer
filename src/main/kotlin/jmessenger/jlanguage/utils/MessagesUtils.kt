package jmessenger.jlanguage.utils

import jmessenger.jlanguage.UnknownMessageType
import jmessenger.jlanguage.messages.*
import jmessenger.jlanguage.messages.requests.*

object MessagesUtils {

    const val SIGNAL_MESSAGE: Short = 0

    const val AUTH_MESSAGE: Short = 1

    const val TEXT_MESSAGE: Short = 2

    const val ERROR_MESSAGE: Short = 3

    const val SUCCESS_MESSAGE: Short = 4

    const val SUCCESS_LOGIN_MESSAGE: Short = 5

    const val DIALOG_MESSAGE: Short = 6

    const val SEND_MESSAGE_REQUEST: Short = 7

    const val NEW_MESSAGE_NOTIFICATION_MESSAGE: Short = 8

    const val MESSAGE_DELETED_MESSAGE: Short = 9

    const val MESSAGE_SENT_MESSAGE: Short = 10

    const val LIST_TEXT_MESSAGES: Short = 11

    const val LIST_DIALOGS: Short = 12

    const val REQUEST_DIALOGS: Short = 13

    const val REQUEST_DIALOG_MESSAGES: Short = 14

    const val REQUEST_USER_BY_ID: Short = 15

    const val USER_MESSAGE: Short = 16

    const val REQUEST_USER_BY_LOGIN: Short = 17

    const val REQUEST_EDIT_MESSAGE: Short = 18

    const val REQUEST_DELETE_MESSAGE: Short = 19 // TODO

    const val MESSAGE_EDITED_NOTIFICATION: Short = 20

    const val DOCUMENT: Short = 21

    const val DOCUMENT_UPLOADED_MESSAGE: Short = 22

    const val DISCONNECT_MESSAGE: Short = 23

    const val REQUEST_UPLOAD_DOCUMENT: Short = 24

    const val REQUEST_DOWNLOAD_DOCUMENT: Short = 25

    const val REQUEST_DELETE_DOCUMENT: Short = 26

    fun getMessage(messageType: Short) = when (messageType) {
        SIGNAL_MESSAGE -> SignalMessage()
        AUTH_MESSAGE -> AuthMessage()
        TEXT_MESSAGE -> TextMessage()
        LIST_TEXT_MESSAGES -> ListTextMessages()
        LIST_DIALOGS -> ListDialogs()
        ERROR_MESSAGE -> ErrorMessage()
        SUCCESS_MESSAGE -> SuccessMessage()
        REQUEST_USER_BY_ID -> RequestUserById()
        REQUEST_USER_BY_LOGIN -> RequestUserByLogin()
        REQUEST_DIALOGS -> RequestDialogs()
        REQUEST_DIALOG_MESSAGES -> RequestDialogMessages()
        REQUEST_EDIT_MESSAGE -> RequestEditMessage()
        SEND_MESSAGE_REQUEST -> SendMessageRequest()
        SUCCESS_LOGIN_MESSAGE -> SuccessLoginMessage()
        REQUEST_UPLOAD_DOCUMENT -> RequestUploadDocument()
        REQUEST_DOWNLOAD_DOCUMENT -> RequestDownloadDocument()
        DISCONNECT_MESSAGE -> DisconnectMessage()
        REQUEST_DELETE_DOCUMENT -> RequestDeleteDocument()
        DOCUMENT -> Document()
        DIALOG_MESSAGE -> Dialog()
        else -> throw UnknownMessageType("Unknown message type $messageType")
    }

}