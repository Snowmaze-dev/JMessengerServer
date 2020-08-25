package jmessenger.jlanguage.utils

import jmessenger.jlanguage.messages.JMessage

internal object TypesUtils {

    const val INT: Byte = 0

    const val LONG: Byte = 1

    const val SHORT: Byte = 2

    const val STRING: Byte = 3

    const val LIST: Byte = 4

    const val MAP: Byte = 5

    const val MESSAGE: Byte = 6

    const val END: Byte = 7


    fun getType(obj: Any) = when (obj) {
        is Int -> INT
        is Long -> LONG
        is Short -> SHORT
        is String -> STRING
        is List<*> -> LIST
        is Map<*, *> -> MAP
        is JMessage -> MESSAGE
        else -> throw Exception()
    }

}