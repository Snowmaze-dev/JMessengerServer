package jmessenger.jlanguage.messages

import jmessenger.jlanguage.utils.MessagesUtils.DOCUMENT

open class Document(id: Int = 0, var name: String = "", var documentType: Int = -1) : Attachment(DOCUMENT, id) {

    companion object {

        const val IMAGE = 0

        const val VIDEO = 1

    }

    override fun toString(): String {
        return "Document($id=id, name='$name', documentType=$documentType)"
    }


}