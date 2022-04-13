package com.oneclean.android.booster.utils

import android.content.Context

fun getLong(context: Context, key: String, defValue: Long = -1L): Long {
    val sp = context.getSharedPreferences("time", Context.MODE_PRIVATE)
    return sp.getLong(key, defValue)
}

fun putLong(context: Context, key: String, value: Long) {
    val editor = context.getSharedPreferences("time", Context.MODE_PRIVATE).edit()
    editor.putLong(key,value)
    editor.apply()
}

fun getBoolean(context: Context, key: String, defValue: Boolean = false): Boolean {
    val sp = context.getSharedPreferences("time", Context.MODE_PRIVATE)
    return sp.getBoolean(key, defValue)
}

fun putBoolean(context: Context, key: String, value: Boolean) {
    val editor = context.getSharedPreferences("time", Context.MODE_PRIVATE).edit()
    editor.putBoolean(key,value)
    editor.apply()
}