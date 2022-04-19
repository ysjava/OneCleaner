package com.oneclean.android.booster

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.content.Intent
import androidx.lifecycle.*
import com.oneclean.android.booster.ui.base.BaseActivity
import com.oneclean.android.booster.ui.launcher.LauncherActivity
import com.oneclean.android.booster.utils.getLong
import com.oneclean.android.booster.utils.putBoolean

class OneCleanerApplication : Application() {
    private var stopTime: Long = 0

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

                //热启动 超过5s就加载启动页
                BaseActivity.lastActivity?.let {
                    val t = System.currentTimeMillis() - stopTime
                    if ((t / 1000) > 5)
                        it.startActivity(Intent(it, LauncherActivity::class.java))
                }

                //每次启动应用就去判断时间是否超过15
                //超过就SharedPreference记录
                val startTime = getLong(this@OneCleanerApplication, "start_time", -1)
                if (startTime == -1L) return
                val t = System.currentTimeMillis() - startTime

                //超过15分钟 1000 * 60 * 15
                if (t >= 1000 * 60 * 15) {
                    putBoolean(this@OneCleanerApplication, "is_refresh", true)
                }
            }

            override fun onStop(owner: LifecycleOwner) {
                super.onStop(owner)
                stopTime = System.currentTimeMillis()
            }
        })
    }

}