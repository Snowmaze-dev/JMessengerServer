package jmessenger.jlanguage.messages.requests

import jmessenger.jlanguage.utils.MessagesUtils.REQUEST_DIALOG_MESSAGES

class RequestDialogMessages(var dialogId: Int = 0, var fromId: Int = 0): RequestMessage(REQUEST_DIALOG_MESSAGES) {

    override fun toString() = "RequestDialogMessages(dialogId=$dialogId, fromId=$fromId)"

}