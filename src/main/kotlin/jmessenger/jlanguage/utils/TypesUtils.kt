package jmessenger.jlanguage.utils

import jmessenger.jlanguage.messages.JMessage

internal object TypesUtils {

    const val CANCELLED = 0

    const val END = 1

    const val INT = 2

    const val LONG = 3

    const val SHORT = 4

    const val STRING = 5

    const val LIST = 6

    const val MAP = 7

    const val MESSAGE = 8


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