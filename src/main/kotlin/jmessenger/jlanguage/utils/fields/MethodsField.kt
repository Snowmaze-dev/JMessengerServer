package jmessenger.jlanguage.utils.fields

import jmessenger.jlanguage.utils.ReflectUtils
import jmessenger.jlanguage.utils.fields.exceptions.FieldGetterAndSetterNotAccessibleException
import jmessenger.jlanguage.utils.fields.exceptions.FieldGetterNotAccessibleException
import jmessenger.jlanguage.utils.fields.exceptions.FieldSetterNotAccessibleException

class MethodsField(private val clazz: Class<*>, javaField: java.lang.reflect.Field): Field {

    override val name: String = javaField.name

    private val setter = ReflectUtils.getMethod(clazz, "set" + name.capitalize(), javaField.type)
    private val getter = ReflectUtils.getMethod(clazz, "get" + name.capitalize())
    private var getterAccessible: Boolean
    private var setterAccessible: Boolean

    init {
        getterAccessible = getter != null
        setterAccessible = setter != null
        if(!(getterAccessible && setterAccessible)) {
            println("error for class " + clazz.name + " field $name " + getter?.name + " " + setter?.name + " " + getter?.isAccessible + " " + setter?.isAccessible)
            throw FieldGetterAndSetterNotAccessibleException("Getter and setter for field $name not accessible. Class ${clazz.canonicalName}")
        }
    }

    override fun get(obj: Any): Any {
        if(!getterAccessible) throw FieldGetterNotAccessibleException("Getter for field $name not accessible. Class ${clazz.name}")
        return getter!!.invoke(obj)
    }

    override fun set(obj: Any, value: Any?) {
        if(!setterAccessible) throw FieldSetterNotAccessibleException("Setter for field $name not accessible. Class ${clazz.name}")
        setter?.invoke(obj, value)
    }


}