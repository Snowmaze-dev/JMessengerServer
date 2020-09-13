package jmessenger.jlanguage.utils

interface Field {

    val name: String

    fun get(obj: Any): Any?

    fun set(obj: Any, value: Any?)

}