package jmessenger.jmessengerfilesserver

import jmessenger.Server
import jmessenger.storages.Storage
import java.io.File
import java.io.IOException
import java.net.ServerSocket
import java.net.Socket
import java.net.URLDecoder

class FilesServer(storage: Storage): Server {

    private val socket = ServerSocket(filesServerPort, 50)
    private val clientsManager = UsersFileManager(storage)
    override var serverName = "Files server"
    override val online: Int
        get() = clientsManager.online

    override fun start() {
        val folder = File(getProgramPath(), "files")
        if(folder.mkdirs()) println("Files folder created at ${folder.path} ")
        super.start()
        while (true) {
            val clientSocket: Socket
            try {
                clientSocket = socket.accept()
            } catch (e: IOException) {
                e.printStackTrace()
                continue
            }
            println("Connected to files server: " + clientSocket.inetAddress.hostAddress)
            val thread = UserFileThread(clientSocket, serverName, clientsManager, folder)
            clientsManager.addClient(thread)
            thread.start()
            Thread.sleep(10)
        }
    }

    private fun getProgramPath(): File {
        val url = javaClass.protectionDomain.codeSource.location
        val jarPath = URLDecoder.decode(url.file, "UTF-8")
        return File(jarPath).parentFile
    }

    companion object {

        const val filesServerPort = 2222

    }

}