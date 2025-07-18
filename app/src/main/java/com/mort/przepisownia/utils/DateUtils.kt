package com.mort.przepisownia.utils

import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale

val locale: Locale = Locale.forLanguageTag("pl-PL")

fun formatDate(timestamp: Long): String {
    val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
    val date = Instant.ofEpochMilli(timestamp).atZone(ZoneId.systemDefault()).toLocalDate()

    return formatter.format(date)
}

fun formatDateMonth(timestamp: Long): String {
    val formatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM).withLocale(locale)
    val date = Instant.ofEpochMilli(timestamp).atZone(ZoneId.systemDefault()).toLocalDate()

    return formatter.format(date)
}