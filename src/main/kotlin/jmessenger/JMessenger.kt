package jmessenger

import jmessenger.jmessengerfilesserver.FilesServer
import jmessenger.jmessengerserver.CoreServer
import jmessenger.storages.Storages
import jmessenger.storages.jdbc.MySQLStorage
import jmessenger.storages.jdbc.PostgreSQLStorage
import org.yaml.snakeyaml.Yaml
import java.io.File
import java.util.*

class JMessenger {

    private var servers = mutableListOf<Server>()

    fun start() {
        val configFile = File("config.yml")
        if (!configFile.exists()) {
            configFile.createNewFile()
            val inputStream = javaClass.getResourceAsStream("/config.yml")
            val outputStream = configFile.outputStream()
            outputStream.write(inputStream.readBytes())
            inputStream.close()
            outputStream.close()
        }
        val config = Yaml().load<Map<String, Map<String, Any>>>(configFile.inputStream())
        val storageSettings = config["storage"]
        if (storageSettings == null) {
            println("You must specify the storage in config.yml")
            return
        }
        val type = Storages.valueOf(storageSettings["type"] as? String ?: "MySQL")
        val host = storageSettings["host"] as? String ?: "127.0.0.1"
        val port = storageSettings["port"] as? Int ?: 3306
        val database = storageSettings["database"] as? String ?: "main"
        val login = storageSettings["login"] as? String ?: "root"
        val password = storageSettings["password"] as? String ?: "password"
        val storage = if(type == Storages.MySQL) {
            MySQLStorage(host, port, database, login, password)
        }
        else {
            PostgreSQLStorage(host, port, database, login, password)
        }
        storage.init()
        Thread {
            val server = CoreServer(storage)
            servers.add(server)
            server.start()
        }.start()
        Thread {
            val filesServer = FilesServer(storage)
            servers.add(filesServer)
            filesServer.start()
        }.start()
        val scanner = Scanner(System.`in`)
        while (true) {
            val command = scanner.next().trim()
            if (command == "quit") continue
            if (command == "online") {
                println("Online: ")
                for (server in servers) {
                    println(" ${server.serverName}: ${server.online}")
                }
            }
        }
    }

}