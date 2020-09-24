package jmessenger.filesserver

import jmessenger.Server
import jmessenger.storages.Storage
import jmessenger.utils.LogsManager
import java.io.File
import java.io.IOException
import java.net.ServerSocket
import java.net.Socket

class FilesServer(storage: Storage, port: Int, private val started: () -> Unit): Server {

    override val serverName = "Files server"

    private val serverSocket = ServerSocket(port, 50)

    private val clientsManager = UsersFileManager(storage)
    override val online: Int
        get() = clientsManager.online

    override fun start() {
        val lowerServerName = serverName.toLowerCase()
        val folder = File("files")
        if(folder.mkdirs()) LogsManager.log("Files folder created at ${folder.absolutePath} ")
        super.start()
        started()
        while (!serverSocket.isClosed) {
            val clientSocket: Socket
            try {
                clientSocket = serverSocket.accept()
            } catch (e: IOException) {
                continue
            }
            LogsManager.log("Connected to $lowerServerName: " + clientSocket.inetAddress.hostAddress)
            val thread = UserFileThread(clientSocket, serverName, clientsManager, folder)
            clientsManager.addClient(thread)
            thread.start()
            Thread.sleep(10)
        }
    }

    override fun stop() {
        clientsManager.stop()
        serverSocket.close()
        super.stop()
    }

}