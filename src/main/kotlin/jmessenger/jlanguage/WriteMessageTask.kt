package jmessenger.jlanguage

import jmessenger.jlanguage.messages.JMessage
import jmessenger.jlanguage.utils.DataOutputStream
import jmessenger.jlanguage.utils.JMessagesUtils
import jmessenger.jlanguage.utils.TypesUtils

class WriteMessageTask(private val message: JMessage, private val stream: DataOutputStream,
                       resultCallback: () -> Unit = {}): Task(resultCallback) {

    override fun run() {
        writeMessage(message)
        stream.flush()
    }

    private fun writeMessage(message: JMessage) {
        stream.writeShort(message.type)
        for (field in JMessagesUtils.getMessageFields(message)) {
            if(cancelled) {
                stream.writeByte(TypesUtils.CANCELLED)
                break
            }
            val value = field.get(message) ?: continue
            val type = TypesUtils.getType(value)
            if(type == TypesUtils.LIST && (value as List<*>).size==0) continue // Skip field if list is empty
            stream.writeByte(type)
            stream.writeString(field.name)
            when (type) {
                TypesUtils.INT -> stream.writeInt(value as Int)
                TypesUtils.STRING -> stream.writeString(value as String)
                TypesUtils.LONG -> stream.writeLong(value as Long)
                TypesUtils.LIST -> {
                    val list = value as List<JMessage>
                    stream.writeInt(list.size)
                    for (msg in list) {
                        writeMessage(msg)
                    }
                }
                TypesUtils.MESSAGE -> writeMessage(value as JMessage)
            }
        }
        stream.writeByte(TypesUtils.END)
    }

}