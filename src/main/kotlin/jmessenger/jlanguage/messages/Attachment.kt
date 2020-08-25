package jmessenger.jlanguage.messages

open class Attachment(type: Short, var id: Int = 0) : JMessage(type) {

    override fun equals(other: Any?) = if (other is Attachment) id == other.id
    else false

    override fun hashCode() = id

}