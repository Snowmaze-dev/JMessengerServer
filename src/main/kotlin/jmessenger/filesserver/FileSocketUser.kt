package jmessenger.filesserver

import jmessenger.SocketUser

interface FileSocketUser: SocketUser {

    fun sendFileToUser(filename: String)

}