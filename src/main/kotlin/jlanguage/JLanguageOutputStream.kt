package jlanguage

import jlanguage.TypesUtils.END
import jlanguage.TypesUtils.INT
import jlanguage.TypesUtils.LIST
import jlanguage.TypesUtils.LONG
import jlanguage.TypesUtils.MESSAGE
import jlanguage.TypesUtils.STRING
import jlanguage.messages.JMessage
import java.io.DataOutputStream
import java.io.OutputStream
import java.lang.reflect.Modifier

class JLanguageOutputStream(outputStream: OutputStream) {

    private val stream = DataOutputStream(outputStream)

    private fun writeMessage(message: JMessage) {
        stream.writeShort(message.type.toInt())
        for (field in ReflectUtils.getFields(message)) {
            if (field.isAnnotationPresent(Ignore::class.java)) continue
            if (Modifier.isStatic(field.modifiers)) continue
            val value = if (field.isAccessible) field.get(message)
            else ReflectUtils.getMethod(message, "get" + field.name.capitalize()).invoke(message)
            val type = TypesUtils.getType(value)
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

}