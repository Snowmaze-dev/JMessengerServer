package jmessenger.jmessengerserver

import jmessenger.Server
import jmessenger.storages.Storage
import java.io.IOException
import java.net.ServerSocket
import java.net.Socket


class CoreServer(storage: Storage): Server {

    override var serverName = "Core server"

    private val clientsManager = CoreUsersManager(storage)
    override val online
        get() = clientsManager.online

    override fun start() {
        val serverSocket = ServerSocket(port)
        super.start()
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
            println("Connected: " + clientSocket.inetAddress.hostAddress)
            Thread.sleep(10)
        }
    }

    companion object {

        const val port = 1111

    }

}