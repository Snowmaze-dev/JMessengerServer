package jmessenger.jlanguage.utils

class MethodsField(clazz: Class<*>, private val javaField: java.lang.reflect.Field): Field {

    override val name: String
        get() = javaField.name

    private val getter = ReflectUtils.getMethod(clazz, "get" + javaField.name.capitalize())
    private val setter = ReflectUtils.getMethod(clazz, "set" + javaField.name.capitalize(), javaField.type)

    override fun get(obj: Any): Any? = getter?.invoke(obj)

    override fun set(obj: Any, value: Any?) {
        setter?.invoke(obj, value)
    }


}