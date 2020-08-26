package jmessenger.utils

import kotlin.math.pow
import kotlin.math.round

fun Double.round(decimals: Int): Double {
    val multiplier = 10.0.pow(decimals)
    return round(this * multiplier) / multiplier
}

fun <T> MutableList<T>.addAll(vararg elements: T) {
    addAll(elements)
}

inline fun startThread(crossinline runnable: () -> Unit) = Thread { runnable() }.apply {
    start()
}

fun StringBuilder.println(string: String) {
   append(string + System.getProperty("line.separator"))
}