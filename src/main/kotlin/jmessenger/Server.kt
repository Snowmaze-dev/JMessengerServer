package jmessenger

import jmessenger.utils.LogsManager.log

interface Server {

    val serverName: String

    val online: Int

    fun start() {
        log("$serverName started")
    }

}