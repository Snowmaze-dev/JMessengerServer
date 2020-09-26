package jmessenger.jlanguage.utils

import java.io.DataOutputStream
import java.io.OutputStream

class DataOutputStream(out: OutputStream) : DataOutputStream(out) {

    fun writeString(string: String) {
        val array = string.toByteArray()
        writeShort(array.size)
        write(array, 0, array.size)
    }

}