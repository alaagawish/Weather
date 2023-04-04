package eg.gov.iti.jets.kotlin.weather.utils

import java.text.SimpleDateFormat
import java.util.*

class ConvertTime {
    companion object {
        fun getDateFormat(pattern: String, date: Long): String =
            SimpleDateFormat(pattern).format(Date(date ))
    }
}