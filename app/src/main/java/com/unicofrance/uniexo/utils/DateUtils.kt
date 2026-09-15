package com.unicofrance.uniexo.utils

import java.text.SimpleDateFormat

fun getDateTime(ts: Long): String {
    try {
        val date = SimpleDateFormat("dd/MM/yyyy")
        return date.format(ts)
    } catch (e: Exception) {
        return e.toString()
    }
}