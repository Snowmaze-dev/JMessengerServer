package jmessenger.utils

import java.security.SecureRandom

object RandomString {

    private val symbols = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz"
    private var rnd = SecureRandom()

    fun randomString(len: Int): String {
        val sb = StringBuilder(len)
        for (i in 0 until len) sb.append(symbols[rnd.nextInt(symbols.length)])
        return sb.toString()
    }

}