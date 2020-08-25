package jmessenger.jlanguage

import jmessenger.jlanguage.messages.JMessage
import jmessenger.jlanguage.utils.DataInputStream
import jmessenger.jlanguage.utils.MessagesUtils
import jmessenger.jlanguage.utils.ReflectUtils
import jmessenger.jlanguage.utils.TypesUtils
import jmessenger.jlanguage.utils.TypesUtils.INT
import jmessenger.jlanguage.utils.TypesUtils.LIST
import jmessenger.jlanguage.utils.TypesUtils.LONG
import jmessenger.jlanguage.utils.TypesUtils.MESSAGE
import jmessenger.jlanguage.utils.TypesUtils.SHORT
import jmessenger.jlanguage.utils.TypesUtils.STRING
import java.io.InputStream
import java.io.OutputStream
import java.lang.reflect.Modifier

internal class JLanguageInputStream(inputStream: InputStream) {

    private val stream = DataInputStream(inputStream)

    fun parseLastMessage(): JMessage {
        val messageType = stream.readShort()
        val map = mutableMapOf<String, Any>()
        while (true) {
            val type = stream.readByte()
            if (type == TypesUtils.END) {
                break
            }
            val name = stream.readUTF()
            map[name] = readNextValue(type)
        }
        val message = MessagesUtils.getMessage(messageType)
        for (field in ReflectUtils.getFields(message)) {
            if (field.isAnnotationPresent(Ignore::class.java)) continue
            if (Modifier.isStatic(field.modifiers)) continue
            val value = map[field.name] ?: continue
            if (field.isAccessible) field.set(message, value)
            else ReflectUtils.getMethod(message, "set" + field.name.capitalize(), field.type).invoke(message, value)
        }
        return message
    }

    private fun readNextValue(type: Byte): Any = when (type) {
        INT -> stream.readInt()
        LONG -> stream.readLong()
        SHORT -> stream.readShort()
        STRING -> stream.readUTF()
        LIST -> {
            val listMessages = mutableListOf<JMessage>()
            val size = stream.readInt()
            for (i in 0 until size) {
                listMessages.add(parseLastMessage())
            }
            listMessages
        }
        MESSAGE -> parseLastMessage()
        else -> throw Exception()
    }

    fun available() = stream.available()

    fun readInt() = stream.readInt()

    fun readLong() = stream.readLong()

    fun readUTF() = stream.readUTF()

    fun readByte() = stream.readByte()

    fun readShort() = stream.readShort()

    fun close() = stream.close()

    fun skip(n: Long) = stream.skip(n)

    fun readBytesToStream(outputStream: OutputStream, bufferSize: Int, bytesCount: Int) {
        val bufferSize = bufferSize * 1024
        var bytesCount = bytesCount
        val buffer = ByteArray(bufferSize)
        var flag = false
        var bytesToRead = bufferSize.coerceAtMost(bytesCount)
        while (true) {
            while (stream.available() >= bytesToRead) {
                if (0 >= bytesToRead) {
                    flag = true
                    break
                }
                stream.read(buffer, 0, bytesToRead)
                outputStream.write(buffer, 0, bytesToRead)
                bytesCount -= bufferSize
                bytesToRead = bufferSize.coerceAtMost(bytesCount)
            }
            if (flag) break
        }
    }

}