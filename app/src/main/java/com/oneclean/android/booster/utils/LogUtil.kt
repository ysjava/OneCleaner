@file:Suppress("unused", "SpellCheckingInspection")

package com.oneclean.android.booster.utils

import android.util.Log
const val TAG = "LOG_UTIL"

fun Any.logv(tag: String = TAG){
    Log.i(tag, "$this")
}

fun Any.logd(tag: String = TAG){
    Log.d(tag, "$this")
}

fun Any.logi(tag: String = TAG){
    Log.i(tag, "$this")
}

fun Any.logw(tag: String = TAG){
    Log.w(tag, "$this")
}

fun Any.loge(tag: String = TAG){
    Log.e(tag, "$this")
}