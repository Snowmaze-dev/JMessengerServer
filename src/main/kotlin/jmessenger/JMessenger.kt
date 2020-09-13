package jmessenger

import jmessenger.jmessengerfilesserver.FilesServer
import jmessenger.jmessengerserver.CoreServer
import jmessenger.storages.Storages
import jmessenger.storages.jdbc.MySQLStorage
import jmessenger.storages.jdbc.PostgreSQLStorage
import jmessenger.utils.LogsManager
import jmessenger.utils.LogsManager.log
import jmessenger.utils.LogsManager.logInput
import jmessenger.utils.addAll
import jmessenger.utils.startThread
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
        try {
            val config = Yaml().load<Map<String, *>>(configFile.inputStream())
            (config["logs-enabled"] as? Boolean)?.let { LogsManager.init(it) }
            val storageSettings = config["storage"] as? Map<String, *>
            if (storageSettings == null) {
                log("You must specify the storage in config.yml")
                return
            }
            val serversSettings = config["servers"] as? Map<String, *>
            if (serversSettings == null) {
                log("You must specify servers settings in config.yml")
                return
            }
            val storage = storageSettings.let {
                val type = Storages.valueOf(it["type"] as? String ?: "MySQL")
                val host = it["host"] as? String ?: "127.0.0.1"
                val port = it["port"] as? Int ?: 3306
                val database = it["database"] as? String ?: "main"
                val login = it["login"] as? String ?: "root"
                val password = it["password"] as? String ?: "password"
                if (type == Storages.MySQL) MySQLStorage(host, port, database, login, password)
                else PostgreSQLStorage(host, port, database, login, password)
            }
            val coreServerSettings = serversSettings["CoreServer"] as Map<String, *>
            val filesServerSettings = serversSettings["FilesServer"] as Map<String, *>
            val coreServerPort = coreServerSettings["port"] as Int
            val filesServerPort = filesServerSettings["port"] as Int
            storage.init()
            servers.addAll(CoreServer(storage, coreServerPort), FilesServer(storage, filesServerPort))
            startServers(servers)
        } catch (e: Exception) {
            log("Invalid config.yml file")
            e.printStackTrace()
            return
        }
        val scanner = Scanner(System.`in`)
        while (true) {
            val command = scanner.next().trim()
            logInput(command)
            if (command == "quit") continue // TODO
            if (command == "online") {
                log("Online: ")
                for (server in servers) {
                    log(" ${server.serverName}: ${server.online}")
                }
            }
        }
    }

    private fun startServers(servers: List<Server>) {
        servers.forEach { startThread { it.start() } }
    }

}