package jmessenger.jlanguage

import jmessenger.jlanguage.messages.JMessage
import jmessenger.jlanguage.utils.JMessagesUtils
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

internal class JLanguageOutputStream(outputStream: OutputStream) {

    private val stream = DataOutputStream(outputStream)

    private fun writeMessage(message: JMessage) {
        stream.writeShort(message.type.toInt())
        for (field in JMessagesUtils.getMessageFields(message)) {
            val value = field.get(message) ?: continue
            val type = TypesUtils.getType(value)
            if(type == LIST && (value as List<*>).size==0) continue // Skip field if list is empty
            stream.writeByte(type)
            stream.writeUTF(field.name)
            when (type) {
                INT -> stream.writeInt(value as Int)
                STRING -> stream.writeUTF(value as String)
                LONG -> stream.writeLong(value as Long)
                LIST -> {
                    val list = value as List<JMessage>
                    stream.writeInt(list.size)
                    for (msg in list) {
                        writeMessage(msg)
                    }
                }
                MESSAGE -> writeMessage(value as JMessage)
            }
        }
        stream.writeByte(END)
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