package jmessenger

import jmessenger.coreserver.CoreServer
import jmessenger.filesserver.FilesServer
import jmessenger.jlanguage.utils.JMessagesUtils
import jmessenger.storages.Storage
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
import kotlin.system.exitProcess

class JMessenger {

    private var servers = mutableListOf<Server>()
    private var stopped = false
    private lateinit var storage: Storage

    fun start() {
        val startTime = Date().time
        JMessagesUtils.init()
        val configFile = File("config.yml")
        if (!configFile.exists()) {
            configFile.createNewFile()
            val inputStream = javaClass.getResourceAsStream("/config.yml")
            val outputStream = configFile.outputStream()
            outputStream.write(inputStream.readBytes())
            inputStream.close()
            outputStream.close()
            log("${configFile.absolutePath} created")
            return
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
            storage = storageSettings.let {
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
            servers.addAll(CoreServer(storage, coreServerPort), FilesServer(storage, filesServerPort) {
                log("JMessenger started in " + (Date().time - startTime) + "ms")
            })
            executeOnAllServers { startThread { it.start() } }
        } catch (e: Exception) {
            log("Invalid config.yml file")
            e.printStackTrace()
            return
        }
        Runtime.getRuntime().addShutdownHook(Thread {
            try {
                println()
                stop()
            } catch (e: InterruptedException) {

            }
        })
        val scanner = Scanner(System.`in`)
        while (true) {
            val args = scanner.nextLine().trim().split(" ")
            if(args.isEmpty()) continue
            val command = args[0]
            if(stopped) break
            logInput(command)
            if (command == "online") {
                log("Online: ")
                for (server in servers) {
                    log(" ${server.serverName}: ${server.online}")
                }
            }
            if(command == "user") {
                if(args.size < 2) {
                    log("Input user login.")
                    continue
                }
                val login = args[1]
                val user = storage.getUser(login)
                log("User ${user.login}:")
                log("  id: ${user.id}")
            }
            if(command == "stop" || command == "shutdown") {
                stop()
                exitProcess(0)
            }
        }
    }

    private inline fun executeOnAllServers(command: (Server) -> Unit) {
        servers.forEach { command.invoke(it) }
    }

    fun stop() {
        val beginShutdown = Date().time
        stopped = true
        executeOnAllServers { it.stop() }
        storage.stop()
        log("JMessenger Server stopped in " + (Date().time-beginShutdown) + "ms")
        LogsManager.save()
    }

}