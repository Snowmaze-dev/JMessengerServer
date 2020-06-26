package jlanguage

import java.io.DataInputStream
import java.io.InputStream

class DataInputStream(stream: InputStream) {

    private val stream = DataInputStream(stream)

    fun readInt(): Int {
        while(true) {
            if(available()>0) {
                return stream.readInt()
            }
        }
    }

    fun readUTF(): String {
        while(true) {
            if(available()>0) {
                return stream.readUTF()
            }
        }
    }

    fun readLong(): Long {
        while (true) {
            if(available()>0) {
                return stream.readLong()
            }
        }
    }
    fun readShort(): Short {
        while(true) {
            if(available()>0) {
                return stream.readShort()
            }
        }
    }

    fun readByte(): Byte {
        while(true) {
            if(available()>0) {
                return stream.readByte()
            }
        }
    }

    fun available() = stream.available()


}