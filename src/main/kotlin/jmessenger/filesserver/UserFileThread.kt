package jmessenger.filesserver

import jmessenger.UserThread
import jmessenger.jlanguage.WriteStreamTask
import jmessenger.jlanguage.messages.Document.Companion.IMAGE
import jmessenger.jlanguage.messages.JMessage
import jmessenger.jlanguage.messages.requests.RequestCancelDownloadDocument
import jmessenger.jlanguage.messages.requests.RequestDownloadDocument
import jmessenger.jlanguage.messages.requests.RequestUploadDocument
import jmessenger.utils.LogsManager.log
import jmessenger.utils.RandomStringGenerator
import jmessenger.utils.round
import java.io.File
import java.net.Socket
import java.util.*

class UserFileThread(socket: Socket, serverName: String, private val threadCallback: UserFileThreadCallback, private val folder: File) : UserThread(socket, serverName, threadCallback), FileSocketUser {

    override fun onMessageReceived(message: JMessage) {
        super.onMessageReceived(message)
        when (message) {
            is RequestUploadDocument -> {
                val bufferSize = inputStream.readInt()
                if (bufferSize > 32) return // TODO
                val bytesCount = inputStream.readInt()
                if (message.documentType == IMAGE) {
                    val fileName = RandomStringGenerator.randomString(20) + ".jpg"
                    val file = File(folder, fileName)
                    file.createNewFile()
                    val fileOut = file.outputStream()
                    inputStream.readBytesToStream(fileOut, bufferSize, bytesCount)
                    threadCallback.onImageReceived(this, fileName, message.requestId)
                }
            }
            is RequestDownloadDocument -> threadCallback.onRequestDownloadImage(this, message)
            is RequestCancelDownloadDocument -> (currentTask as? WriteStreamTask)?.cancel()
        }
    }

    override fun sendFileToUser(filename: String) {
        val fileInputStream = File(folder, filename).inputStream()
        val start = Date().time
        addTask(WriteStreamTask(fileInputStream, outputStream) {
            log("File $filename sent in " + ((Date().time - start) / 1000.0).round(2) + " seconds to " + user())
        })
    }

    interface UserFileThreadCallback : UserCallback {

        fun onImageReceived(user: FileSocketUser, name: String, requestId: Int)

        fun onRequestDownloadImage(user: FileSocketUser, request: RequestDownloadDocument)

    }


}