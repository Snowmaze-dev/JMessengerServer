package jmessenger.jlanguage.utils

import java.lang.reflect.Field
import java.lang.reflect.Method

object ReflectUtils {

    fun getFields(initialClass: Class<*>): List<Field> {
        val fields = mutableListOf<Field>()
        var clazz = initialClass
        while(clazz != Any::class.java) {
            fields.addAll(clazz.declaredFields)
            clazz = clazz.superclass
        }
        return fields.toList()
    }

    fun getMethod(initialClass: Class<*>, methodName: String, vararg parameterTypes: Class<*>): Method? {
        var clazz = initialClass
        while(clazz != Any::class.java) {
            try {
                return clazz.getDeclaredMethod(methodName, *parameterTypes)
            }
            catch (e: NoSuchMethodException) { }
            clazz = clazz.superclass
        }
        return null
    }

}