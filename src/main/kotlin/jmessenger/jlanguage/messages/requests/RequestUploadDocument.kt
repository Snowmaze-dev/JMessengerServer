package jmessenger.jlanguage.messages.requests

import jmessenger.jlanguage.utils.MessagesUtils.REQUEST_UPLOAD_DOCUMENT

open class RequestUploadDocument(requestId: Int = 0) : RequestMessage(REQUEST_UPLOAD_DOCUMENT, requestId) {

    var documentType = 0

    override fun toString(): String {
        return "RequestUploadDocument(documentType=$documentType, requestId=$requestId)"
    }


}