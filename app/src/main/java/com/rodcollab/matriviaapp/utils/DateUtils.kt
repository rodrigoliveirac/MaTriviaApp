package com.rodcollab.matriviaapp.utils

import java.text.SimpleDateFormat
import java.util.Date

object DateUtils {
    fun getDateFormatted(time: Long) : String {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm:ss")
        val date = Date(time)
        return dateFormat.format(date)
    }
}