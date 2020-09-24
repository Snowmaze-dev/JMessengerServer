package jmessenger.filesserver

import jmessenger.UsersManager
import jmessenger.jlanguage.messages.Document
import jmessenger.jlanguage.messages.Document.Companion.IMAGE
import jmessenger.jlanguage.messages.DocumentUploadedMessage
import jmessenger.jlanguage.messages.requests.RequestDownloadDocument
import jmessenger.storages.Storage
import jmessenger.utils.LogsManager.log

class UsersFileManager(storage: Storage) : UsersManager(storage), UserFileThread.UserFileThreadCallback {

    override fun onImageReceived(user: FileSocketUser, name: String, requestId: Int) {
        if (checkUnauthorized(user, requestId)) return
        log("Received image $name")
        val id = storage.addFile(name, IMAGE, user.user!!.id)
        user.sendMessage(DocumentUploadedMessage(Document(id, name, IMAGE)), requestId)
    }

    override fun onRequestDownloadImage(user: FileSocketUser, request: RequestDownloadDocument) {
        if (checkUnauthorized(user, request.requestId)) return
        val name = storage.getDocumentName(request.id)
        user.sendFileToUser(name)
    }

}