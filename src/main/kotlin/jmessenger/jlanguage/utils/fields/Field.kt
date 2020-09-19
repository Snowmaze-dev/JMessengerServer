package jmessenger.jlanguage.utils.fields

interface Field {

    val name: String

    fun get(obj: Any): Any?

    fun set(obj: Any, value: Any?)

}