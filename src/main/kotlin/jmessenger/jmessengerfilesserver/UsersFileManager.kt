package jmessenger.jmessengerfilesserver

import jmessenger.UsersManager
import jmessenger.jlanguage.messages.Document
import jmessenger.jlanguage.messages.Document.Companion.IMAGE
import jmessenger.jlanguage.messages.DocumentUploadedMessage
import jmessenger.jlanguage.messages.requests.RequestDownloadDocument
import jmessenger.storages.Storage

class UsersFileManager(storage: Storage) : UsersManager(storage), UserFileThread.UserFileThreadCallback {

    override fun onImageReceived(userFileThread: FileSocketUser, name: String, requestId: Int) {
        if (checkUnauthorized(userFileThread, requestId)) return
        println("Received image $name")
        val id = storage.addFile(name, IMAGE, userFileThread.user!!.id)
        val document = Document().apply {
            this.id = id
            this.name = name
            documentType = IMAGE
        }
        userFileThread.sendMessage(DocumentUploadedMessage(document), requestId)
    }

    override fun onRequestDownloadImage(userFileThread: FileSocketUser, request: RequestDownloadDocument) {
        if (checkUnauthorized(userFileThread, request.requestId)) return
        val name = storage.getDocumentName(request.id)
        userFileThread.sendFileToUser(name)
    }

}