package jmessenger.jmessengerserver

import jmessenger.Server
import jmessenger.storages.Storage
import jmessenger.utils.LogsManager
import java.io.IOException
import java.net.ServerSocket
import java.net.Socket


class CoreServer(storage: Storage, private val port: Int): Server {

    override var serverName = "Core server"

    private val clientsManager = CoreUsersManager(storage)
    override val online
        get() = clientsManager.online

    override fun start() {
        super.start()
        val serverSocket = ServerSocket(port, 50)
        val lowerServerName = serverName.toLowerCase()
        while (true) {
            val clientSocket: Socket
            try {
                clientSocket = serverSocket.accept()
            } catch (e: IOException) {
                e.printStackTrace()
                continue
            }
            val thread = CoreUserThread(clientSocket, serverName, clientsManager)
            clientsManager.addClient(thread)
            thread.start()
            LogsManager.log("Connected to $lowerServerName: " + clientSocket.inetAddress.hostAddress)
            Thread.sleep(10)
        }
    }

}