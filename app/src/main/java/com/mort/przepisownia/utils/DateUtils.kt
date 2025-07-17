package com.mort.przepisownia.utils

import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

fun formatDate(timestamp: Long): String {
    val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
    val date = Instant.ofEpochMilli(timestamp).atZone(ZoneId.systemDefault()).toLocalDate()

    return formatter.format(date)
}