package com.oneclean.android.booster

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import androidx.lifecycle.*
import com.oneclean.android.booster.utils.getLong
import com.oneclean.android.booster.utils.putBoolean

class OneCleanerApplication : Application() {

    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context

        @SuppressLint("StaticFieldLeak")
        lateinit var instance: OneCleanerApplication
    }

    override fun onCreate() {
        super.onCreate()
        context = applicationContext
        instance = this

        ProcessLifecycleOwner.get().lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onStart(owner: LifecycleOwner) {
                super.onStart(owner)
                //每次启动应用就去判断时间是否超过15
                //超过就SharedPreference记录
                val startTime = getLong(this@OneCleanerApplication, "start_time", -1)
                val t = System.currentTimeMillis() - startTime
                //超过15分钟
                if (t >= 1000 * 15 && startTime != -1L) {
                    putBoolean(this@OneCleanerApplication, "is_refresh", true)
                }
            }

            override fun onStop(owner: LifecycleOwner) {
                super.onStop(owner)
            }
        })
    }
}