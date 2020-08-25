package jmessenger.utils

import java.text.SimpleDateFormat
import java.util.*

object LogManager {

    private val dateFormat = SimpleDateFormat("[yyyy-MM-dd HH:mm:ss]", Locale.ROOT)

    fun log(text: String) {
        val date = dateFormat.format(Date())
        println("$date $text")
    }

}