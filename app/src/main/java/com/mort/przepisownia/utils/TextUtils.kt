package com.mort.przepisownia.utils

fun Float?.inTextFormatter(): Any {
    if (this == null) {
        return ""
    } else if (this % 1F == 0F) {
        return this.toInt()
    } else {
        return this
    }
}