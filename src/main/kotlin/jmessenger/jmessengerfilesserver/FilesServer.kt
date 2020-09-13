package jmessenger.jmessengerfilesserver

import jmessenger.Server
import jmessenger.storages.Storage
import jmessenger.utils.LogsManager
import java.io.File
import java.io.IOException
import java.net.ServerSocket
import java.net.Socket

class FilesServer(storage: Storage, private val port: Int): Server {

    private val clientsManager = UsersFileManager(storage)
    override var serverName = "Files server"
    override val online: Int
        get() = clientsManager.online

    override fun start() {
        val lowerServerName = serverName.toLowerCase()
        val socket = ServerSocket(port, 50)
        val folder = File("files")
        if(folder.mkdirs()) LogsManager.log("Files folder created at ${folder.absolutePath} ")
        super.start()
        while (true) {
            val clientSocket: Socket
            try {
                clientSocket = socket.accept()
            } catch (e: IOException) {
                e.printStackTrace()
                continue
            }
            LogsManager.log("Connected to $lowerServerName: " + clientSocket.inetAddress.hostAddress)
            val thread = UserFileThread(clientSocket, serverName, clientsManager, folder)
            clientsManager.addClient(thread)
            thread.start()
            Thread.sleep(10)
        }
    }

}