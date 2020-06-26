import java.io.IOException
import java.net.ServerSocket
import java.net.Socket

class Server {

    private var socket = ServerSocket(port)
    private val clientsManager = UsersManager()

    fun start() {
        while (true) {
            val clientSocket: Socket
            try {
                clientSocket = socket.accept()
            } catch (e: IOException) {
                e.printStackTrace()
                continue
            }
            println("Connected: " + clientSocket.inetAddress.hostAddress)
            val thread = UserThread(clientSocket, clientsManager)
            clientsManager.addClient(thread)
            thread.start()
        }
    }

    companion object {

        const val port = 1111

    }

}