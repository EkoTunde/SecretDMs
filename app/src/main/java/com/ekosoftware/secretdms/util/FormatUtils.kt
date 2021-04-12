package com.ekosoftware.secretdms.util

import org.joda.time.LocalDateTime
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

/**
 * Returns a human-readable-friendly date [String] from a [Long] representing time in milliseconds.
 */
fun Long?.asDateTimeString(): String {
    if (this == null) return ""
    val startingDateTime = LocalDateTime(this)
    val now = LocalDateTime()
    val date = if (now.dayOfMonth != startingDateTime.dayOfMonth) {
        if (startingDateTime.isBefore(now.minusWeeks(1)))
            DateFormat.getDateInstance(DateFormat.SHORT).format(startingDateTime.toDate()) ?: ""
        else {
            val sdf = SimpleDateFormat("EEE", Locale.getDefault())
            sdf.format(startingDateTime.toDate())
        } + " "
    } else ""
    val time = DateFormat.getTimeInstance(DateFormat.SHORT).format(startingDateTime.toDate())
    return "$date$time"
}