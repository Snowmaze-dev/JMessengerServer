package jmessenger.jlanguage.utils

class FieldWrapper(private val javaField: java.lang.reflect.Field): Field {

    override val name: String
        get() = javaField.name

    override fun get(obj: Any): Any? = javaField.get(obj)

    override fun set(obj: Any, value: Any?) = javaField.set(obj, value)

}