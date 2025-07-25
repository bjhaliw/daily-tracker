package com.hollowlog.dailytracker.core

import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale
import java.util.TimeZone

val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

fun convertMillisToDate(millis: Long): String {
    val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    formatter.timeZone = TimeZone.getTimeZone("UTC")
    return formatter.format(Date(millis))
}

fun convertDateToFormattedString(date: LocalDate): String {
    return date.format(formatter)
}