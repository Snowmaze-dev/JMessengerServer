package jmessenger.jlanguage

import jmessenger.jlanguage.messages.JMessage
import jmessenger.jlanguage.utils.ReflectUtils
import jmessenger.jlanguage.utils.TypesUtils
import jmessenger.jlanguage.utils.TypesUtils.END
import jmessenger.jlanguage.utils.TypesUtils.INT
import jmessenger.jlanguage.utils.TypesUtils.LIST
import jmessenger.jlanguage.utils.TypesUtils.LONG
import jmessenger.jlanguage.utils.TypesUtils.MESSAGE
import jmessenger.jlanguage.utils.TypesUtils.STRING
import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.lang.reflect.Modifier

internal class JLanguageOutputStream(outputStream: OutputStream) {

    private val stream = DataOutputStream(outputStream)

    private fun writeMessage(message: JMessage) {
        stream.writeShort(message.type.toInt())
        for (field in ReflectUtils.getFields(message)) {
            if (field.isAnnotationPresent(Ignore::class.java)) continue
            if (Modifier.isStatic(field.modifiers)) continue
            val value = (if (field.isAccessible) field.get(message)
            else ReflectUtils.getMethod(message, "get" + field.name.capitalize()).invoke(message)) ?: continue
            val type = TypesUtils.getType(value)
            if(type == LIST && (value as List<*>).size==0) continue // Skip field if list is empty
            stream.writeByte(type.toInt())
            stream.writeUTF(field.name)
            if (type == INT) {
                stream.writeInt(value as Int)
            }
            if (type == STRING) {
                stream.writeUTF(value as String)
            }
            if (type == LONG) {
                stream.writeLong(value as Long)
            }
            if (type == LIST) {
                val list = value as List<JMessage>
                stream.writeInt(list.size)
                for (msg in list) {
                    writeMessage(msg)
                }
            }
            if (type == MESSAGE) {
                writeMessage(value as JMessage)
            }
        }
        stream.writeByte(END.toInt())
    }

    fun sendMessage(message: JMessage) {
        writeMessage(message)
        stream.flush()
    }

    fun writeStream(inputStream: InputStream) {
        val inputStreamToSend = DataInputStream(inputStream)
        var bytesCount = inputStream.available()
        val bufferSizeValue = 8
        val bufferSize = bufferSizeValue*1024
        val buffer = ByteArray(bufferSize)
        var bytesToWrite = bufferSize.coerceAtMost(bytesCount)
        stream.writeInt(bufferSizeValue)
        stream.writeInt(bytesCount)
        while (bytesToWrite >= 0) {
            inputStreamToSend.read(buffer, 0, bytesToWrite)
            stream.write(buffer, 0, bytesToWrite)
            bytesCount -= bufferSize
            bytesToWrite = bufferSize.coerceAtMost(bytesCount)
        }
        stream.flush()
    }

    fun close() = stream.close()

}