package jmessenger.jmessengerfilesserver

import jmessenger.SocketUser

interface FileSocketUser: SocketUser {

    fun sendFileToUser(filename: String)

}