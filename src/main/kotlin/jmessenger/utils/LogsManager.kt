package jmessenger.utils

import java.io.File
import java.io.PrintWriter
import java.nio.charset.Charset
import java.text.SimpleDateFormat
import java.util.*

object LogsManager {

    private val dateFormat = SimpleDateFormat("[yyyy-MM-dd HH:mm:ss]", Locale.ROOT)
    private val logsFolder = File("logs");
    private lateinit var logsOutputStream: PrintWriter
    private var logsEnabled = false

    fun init(logsEnabled: Boolean) {
        this.logsEnabled = logsEnabled
        if(logsEnabled) {
            val folderCreated = logsFolder.mkdir()
            if(logsFolder.isDirectory) {
                val dateFormat = SimpleDateFormat("yyyy-MM-dd-HH:mm:ss", Locale.ROOT)
                val logFile = File(logsFolder, "log-" + dateFormat.format(Date()) + ".txt")
                logsOutputStream = PrintWriter(logFile.outputStream().bufferedWriter(Charset.defaultCharset()))
                if (folderCreated) {
                    log("Logs directory created at " + logsFolder.absolutePath)
                }
            }
            else {
                this.logsEnabled = false
                log("Failed to create logs folder")
            }
        }
    }

    fun log(text: String) {
        val date = dateFormat.format(Date())
        val log = "$date $text"
        if(logsEnabled) {
            logsOutputStream.println(log)
            logsOutputStream.flush()
        }
        println(log)
    }

    fun logInput(text: String) {
        if (logsEnabled) {
            val date = dateFormat.format(Date())
            logsOutputStream.println("$date >> $text")
            logsOutputStream.flush()
        }
    }

}