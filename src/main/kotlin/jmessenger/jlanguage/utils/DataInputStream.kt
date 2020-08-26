package jmessenger.jlanguage.utils

import java.io.DataInputStream
import java.io.FilterInputStream
import java.io.InputStream

class DataInputStream(inputStream: InputStream) : FilterInputStream(inputStream) {

    private val stream = DataInputStream(inputStream)

    private fun waitBytes(count: Int) {
        while (stream.available() < count) {
            Thread.sleep(1)
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

    fun readUTF(): String {
        waitBytes(2) // TODO
        return stream.readUTF()
    }

}