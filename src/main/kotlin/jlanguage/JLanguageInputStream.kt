package jlanguage

import jlanguage.TypesUtils.INT
import jlanguage.TypesUtils.LIST
import jlanguage.TypesUtils.LONG
import jlanguage.TypesUtils.MESSAGE
import jlanguage.TypesUtils.SHORT
import jlanguage.TypesUtils.STRING
import jlanguage.messages.JMessage
import java.io.DataInputStream
import java.io.InputStream
import java.lang.reflect.Modifier

internal class JLanguageInputStream(inputStream: InputStream) {

    private val stream = DataInputStream(inputStream)

    fun parseLastMessage(): JMessage {
        val map = mutableMapOf<String, Any>()
        var end = false
        val messageType = stream.readShort()
        while (true) {
            while (stream.available() > 0) {
                val type = stream.readByte()
                if (type == TypesUtils.END) {
                    end = true
                    break
                }
                val name = stream.readUTF()
                map[name] = readNextValue(type)
            }
            if (end) break
        }
        val message = MessagesUtils.getMessage(messageType)
        for (field in ReflectUtils.getFields(message)) { // TODO родительские поля
            if (field.isAnnotationPresent(Ignore::class.java)) continue
            if (Modifier.isStatic(field.modifiers)) continue
            val value = map[field.name]
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

}