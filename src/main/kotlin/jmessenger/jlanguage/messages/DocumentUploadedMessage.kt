package jmessenger.jlanguage.messages

import jmessenger.jlanguage.messages.requests.RequestMessage
import jmessenger.jlanguage.utils.JMessagesUtils.DOCUMENT_UPLOADED_MESSAGE

class DocumentUploadedMessage(requestId: Int = 0) : RequestMessage(DOCUMENT_UPLOADED_MESSAGE, requestId) {

    lateinit var document: Document

    constructor(document: Document): this(0) {
        this.document = document
    }

}