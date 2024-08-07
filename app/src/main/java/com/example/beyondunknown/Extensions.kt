package com.example.beyondunknown

import android.os.Build
import android.os.Bundle
import android.content.Intent
import java.util.Calendar
import java.io.Serializable
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.Locale

inline fun <reified T : Serializable> Bundle.serializable(key: String): T? = when {
    Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> getSerializable(key, T::class.java)
    else -> @Suppress("DEPRECATION") getSerializable(key) as? T
}

inline fun <reified T : Serializable> Intent.serializable(key: String): T? = when {
    Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> getSerializableExtra(key, T::class.java)
    else -> @Suppress("DEPRECATION") getSerializableExtra(key) as? T
}

fun getDateAsString(): String = when {
    Build.VERSION.SDK_INT >= Build.VERSION_CODES.O -> LocalDate.now().toString()
    else -> SimpleDateFormat("yyyy-MM-dd", Locale.UK).format(Calendar.getInstance().time).toString()
}