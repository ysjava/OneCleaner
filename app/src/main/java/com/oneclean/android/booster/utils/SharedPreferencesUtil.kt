package com.oneclean.android.booster.utils

import android.content.Context
import com.oneclean.android.booster.OneCleanerApplication

fun getLong(context: Context, key: String, defValue: Long = -1L, name: String = "time"): Long {
    val sp = context.getSharedPreferences(name, Context.MODE_PRIVATE)
    return sp.getLong(key, defValue)
}

fun putLong(context: Context, key: String, value: Long, name: String = "time") {
    val editor = context.getSharedPreferences(name, Context.MODE_PRIVATE).edit()
    editor.putLong(key, value)
    editor.apply()
}

fun getBoolean(
    context: Context,
    key: String,
    defValue: Boolean = false,
    name: String = "time"
): Boolean {
    val sp = context.getSharedPreferences(name, Context.MODE_PRIVATE)
    return sp.getBoolean(key, defValue)
}

fun putBoolean(context: Context, key: String, value: Boolean, name: String = "sp_name_time") {
    val editor = context.getSharedPreferences(name, Context.MODE_PRIVATE).edit()
    editor.putBoolean(key, value)
    editor.apply()
}

fun getInt(key: String, name: String = "sp_name_ad", context: Context = OneCleanerApplication.instance): Int {
    val sp = context.getSharedPreferences(name, Context.MODE_PRIVATE)
    return sp.getInt(key, 0)
}

fun getString(
    key: String,
    name: String,
    context: Context = OneCleanerApplication.instance
): String {
    val sp = context.getSharedPreferences(name, Context.MODE_PRIVATE)
    return sp.getString(key, "") ?: ""
}

fun setString(
    key: String,
    name: String,
    value: String,
    context: Context = OneCleanerApplication.instance
) {
    val editor = context.getSharedPreferences(name, Context.MODE_PRIVATE).edit()
    editor.putString(key, value)
    editor.apply()
}