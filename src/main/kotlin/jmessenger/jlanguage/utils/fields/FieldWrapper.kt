package jmessenger.jlanguage.utils.fields

class FieldWrapper(private val javaField: java.lang.reflect.Field): Field {

    override val name: String = javaField.name

    override fun get(obj: Any): Any? = javaField.get(obj)

    override fun set(obj: Any, value: Any?) = javaField.set(obj, value)

}