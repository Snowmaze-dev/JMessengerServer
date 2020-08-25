package jmessenger

import jmessenger.utils.LogManager.log

interface Server {

    val serverName: String

    val online: Int

    fun start() {
        log("$serverName started")
    }

}