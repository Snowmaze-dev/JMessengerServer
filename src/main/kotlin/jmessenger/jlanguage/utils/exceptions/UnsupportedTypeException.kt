package jmessenger.jlanguage.utils.exceptions

class UnsupportedTypeException(clazz: Class<*>) : RuntimeException("Unsupported type ${clazz.canonicalName}")