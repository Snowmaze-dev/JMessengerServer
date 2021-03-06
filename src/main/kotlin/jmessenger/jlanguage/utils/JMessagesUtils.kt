package jmessenger.jlanguage.utils

import jmessenger.jlanguage.Ignore
import jmessenger.jlanguage.messages.*
import jmessenger.jlanguage.messages.requests.*
import jmessenger.jlanguage.utils.exceptions.UnknownMessage
import jmessenger.jlanguage.utils.exceptions.UnknownMessageType
import jmessenger.jlanguage.utils.fields.Field
import jmessenger.jlanguage.utils.fields.FieldWrapper
import jmessenger.jlanguage.utils.fields.MethodsField
import jmessenger.jlanguage.utils.fields.exceptions.FieldGetterAndSetterNotAccessibleException
import java.lang.reflect.Modifier
import kotlin.reflect.KClass

object JMessagesUtils {

    const val SIGNAL_MESSAGE = 0

    const val AUTH_MESSAGE = 1

    const val TEXT_MESSAGE = 2

    const val ERROR_MESSAGE = 3

    const val SUCCESS_MESSAGE = 4

    const val SUCCESS_LOGIN_MESSAGE = 5

    const val DIALOG_MESSAGE = 6

    const val SEND_MESSAGE_REQUEST = 7

    const val NEW_MESSAGE_NOTIFICATION_MESSAGE = 8

    const val MESSAGE_DELETED_MESSAGE = 9

    const val MESSAGE_SENT_MESSAGE = 10

    const val LIST_TEXT_MESSAGES = 11

    const val LIST_DIALOGS = 12

    const val REQUEST_DIALOGS = 13

    const val REQUEST_DIALOG_MESSAGES = 14

    const val REQUEST_USER_BY_ID = 15

    const val USER_MESSAGE = 16

    const val REQUEST_USER_BY_LOGIN = 17

    const val REQUEST_EDIT_MESSAGE = 18

    const val REQUEST_DELETE_MESSAGE = 19 // TODO

    const val MESSAGE_EDITED_NOTIFICATION = 20

    const val DOCUMENT = 21

    const val DOCUMENT_UPLOADED_MESSAGE = 22

    const val DISCONNECT_MESSAGE = 23

    const val REQUEST_UPLOAD_DOCUMENT = 24

    const val REQUEST_DOWNLOAD_DOCUMENT = 25

    const val REQUEST_DELETE_DOCUMENT = 26

    const val REQUEST_CANCEL_DOWNLOAD_DOCUMENT = 27

    private val messagesFields = mutableMapOf<Class<out JMessage>, Array<Field>>()

    fun getMessageFields(obj: JMessage): Array<Field> {
        var clazz: Class<JMessage> = obj.javaClass
        do {
            if(messagesFields.containsKey(clazz)) {
                return messagesFields[clazz]!!
            }
            clazz = clazz.superclass as Class<JMessage>
        } while(clazz != JMessage::class.java)
        throw UnknownMessage("Unknown message: ${obj.javaClass.name}")
    }

    fun init() {
        addJMessagesFields(SignalMessage::class,
            AuthMessage::class,
            TextMessage::class,
            ListTextMessages::class,
            ListDialogs::class,
            ErrorMessage::class,
            SuccessMessage::class,
            RequestUserById::class,
            RequestUserByLogin::class,
            RequestDialogs::class,
            RequestDialogMessages::class,
            RequestEditMessage::class,
            SendMessageRequest::class,
            SuccessLoginMessage::class,
            RequestUploadDocument::class,
            RequestDownloadDocument::class,
            DisconnectMessage::class,
            RequestDeleteDocument::class,
            Document::class,
            Dialog::class,
            MessageSentMessage::class,
            MessageEditedNotification::class,
            NewMessageNotification::class,
            DocumentUploadedMessage::class
        )
    }

    private fun addJMessagesFields(vararg jMessages: KClass<out JMessage>) {
        for (jMessage in jMessages) {
            addJMessageFields(jMessage.java)
        }
    }

    private fun addJMessageFields(clazz: Class<out JMessage>) {
        val fields = mutableListOf<Field>()
        for (field in ReflectUtils.getFields(clazz)) {
            if (field.isAnnotationPresent(Ignore::class.java) || Modifier.isStatic(field.modifiers)) continue
            try {
                fields.add(if (field.isAccessible) FieldWrapper(field) else MethodsField(clazz, field))
            }
            catch (e: FieldGetterAndSetterNotAccessibleException) { }
        }
        messagesFields[clazz] = fields.toTypedArray()
    }

    fun getMessage(messageType: Int) = when (messageType) {
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