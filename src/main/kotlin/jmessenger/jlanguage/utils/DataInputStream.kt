package jmessenger.jlanguage.utils

import jmessenger.jlanguage.utils.exceptions.TimeoutException
import jmessenger.jlanguage.utils.exceptions.UnknownMessage
import java.io.DataInputStream
import java.io.FilterInputStream
import java.io.InputStream

class DataInputStream(inputStream: InputStream, timeout: Int) : FilterInputStream(inputStream) {

    private val stream = DataInputStream(inputStream)
    private val sleepTime = 10L
    private val timeout = (timeout/sleepTime).toInt()

    private fun waitBytes(count: Int) {
        var passed = 0
        while (stream.available() < count) {
            Thread.sleep(sleepTime)
            passed++
            if(passed == timeout) {
                throw TimeoutException()
            }
        }
    }

    fun readByte(): Byte {
        waitBytes(1)
        return stream.read().toByte()
    }

    fun readShort(): Short {
        waitBytes(2)
        return stream.readShort()
    }

    fun readInt(): Int {
        waitBytes(4)
        return stream.readInt()
    }

    fun readLong(): Long {
        waitBytes(8)
        return stream.readLong()
    }

    fun readString(): String {
        val length = readShort().toInt()
        if(length < 0) {
            throw UnknownMessage("Received invalid string")
        }
        val array = ByteArray(length)
        waitBytes(length)
        read(array, 0, length)
        return String(array)
    }

}