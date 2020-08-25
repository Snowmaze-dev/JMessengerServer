package jmessenger.jlanguage.utils

import java.lang.reflect.Field
import java.lang.reflect.Method

object ReflectUtils {

    fun getFields(obj: Any): List<Field> {
        val fields = mutableListOf<Field>()
        var clazz: Class<in Any> = obj.javaClass
        while(clazz != Any::class.java) {
            fields.addAll(clazz.declaredFields)
            clazz = clazz.superclass
        }
        return fields.toList()
    }

    fun getMethod(obj: Any, methodName: String, vararg parameterTypes: Class<*>): Method {
        var clazz: Class<in Any> = obj.javaClass
        while(clazz != Any::class.java) {
            try {
                return clazz.getDeclaredMethod(methodName, *parameterTypes)
            }
            catch (e: NoSuchMethodException) { }
            clazz = clazz.superclass
        }
        throw NoSuchMethodException()
    }

}