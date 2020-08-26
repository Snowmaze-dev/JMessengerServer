package jmessenger.utils

import java.security.SecureRandom

object RandomStringGenerator {

    private const val symbols = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz"
    private const val symbolsLength = symbols.length
    private var rnd = SecureRandom()

    fun randomString(len: Int): String {
        val sb = StringBuilder(len)
        for (i in 0 until len) sb.append(symbols[rnd.nextInt(symbolsLength)])
        return sb.toString()
    }

}