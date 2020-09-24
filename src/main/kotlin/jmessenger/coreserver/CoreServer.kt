package jmessenger.coreserver

import jmessenger.Server
import jmessenger.storages.Storage
import jmessenger.utils.LogsManager
import java.io.IOException
import java.net.ServerSocket
import java.net.Socket

class CoreServer(storage: Storage, port: Int): Server {

    override val serverName = "Core server"

    private val serverSocket = ServerSocket(port, 50)

    private val clientsManager = CoreUsersManager(storage)

    override val online
        get() = clientsManager.online

    override fun start() {
        val lowerServerName = serverName.toLowerCase()
        super.start()
        while (!serverSocket.isClosed) {
            val clientSocket: Socket
            try {
                clientSocket = serverSocket.accept()
            } catch (e: IOException) {
                continue
            }
            val thread = CoreUserThread(clientSocket, serverName, clientsManager)
            clientsManager.addClient(thread)
            thread.start()
            LogsManager.log("Connected to $lowerServerName: " + clientSocket.inetAddress.hostAddress)
            Thread.sleep(10)
        }
    }

    override fun stop() {
        clientsManager.stop()
        serverSocket.close()
        super.stop()
    }
}