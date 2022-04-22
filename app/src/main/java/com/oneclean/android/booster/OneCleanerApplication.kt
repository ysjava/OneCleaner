package com.oneclean.android.booster

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.*
import com.google.android.gms.ads.AdActivity
import com.oneclean.android.booster.logic.ad.AdManager
import com.oneclean.android.booster.ui.base.BaseActivity
import com.oneclean.android.booster.ui.launcher.LauncherActivity
import com.oneclean.android.booster.utils.getBoolean
import com.oneclean.android.booster.utils.getLong
import com.oneclean.android.booster.utils.logd
import com.oneclean.android.booster.utils.putBoolean
import java.util.*

class OneCleanerApplication : Application() {
    private var stopTime: Long = 0

    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context

        @SuppressLint("StaticFieldLeak")
        lateinit var instance: OneCleanerApplication

        val activityList = LinkedList<BaseActivity?>()
        var adActivity: AdActivity? = null

        //当去获取权限时取消热启动计时
        var cancelTime = false
        //是否从权限请求页面回来
        var isRequestPermissionBack = false

    }

    override fun onCreate() {
        super.onCreate()
        context = applicationContext
        instance = this
        AdManager.initData()
        cancelTime = getBoolean(this,"cancelTime",false,"hot_load")
        isRequestPermissionBack = getBoolean(this,"isRequestPermissionBack",false,"hot_load")
        var launcherActivity: AppCompatActivity? = null

        registerActivityLifecycleCallbacks(object : ActivityLifecycleCallbacks {
            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
                if (activity is LauncherActivity) {
                    //if (launcherActivity!=null) launcherActivity?.finish()
                    launcherActivity = activity
                }
                if (activity is AdActivity)
                    adActivity = activity
            }

            override fun onActivityStarted(activity: Activity) {

            }

            override fun onActivityResumed(activity: Activity) {

            }

            override fun onActivityPaused(activity: Activity) {
            }

            override fun onActivityStopped(activity: Activity) {

            }

            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
            }

            override fun onActivityDestroyed(activity: Activity) {
                if (activity === launcherActivity)
                    launcherActivity = null
                if (activity is AdActivity)
                    adActivity = null
            }

        })

        ProcessLifecycleOwner.get().lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onStart(owner: LifecycleOwner) {
                super.onStart(owner)
                cancelTime = getBoolean(this@OneCleanerApplication,"cancelTime",false,"hot_load")
                isRequestPermissionBack = getBoolean(this@OneCleanerApplication,"isRequestPermissionBack",false,"hot_load")
                //热启动 超过5s就加载启动页
                if (activityList.size > 0) {
                    activityList.last?.let {
                        //如果是去获取权限，则不走热启动

                        if (isRequestPermissionBack){
                            isRequestPermissionBack = false
                            cancelTime = false
                            putBoolean(instance,"cancelTime",false,"hot_load")
                            putBoolean(instance,"isRequestPermissionBack",false,"hot_load")
                            return
                        }
                        if (cancelTime) return

                        if (adActivity != null) return

                        val t = System.currentTimeMillis() - stopTime

                        if ((t / 1000) > 5) {
                            "执行热启动 。。。 ".logd("WHEUWHWHEQ")
                            //launcherActivity?.let { launcherActivity!!.finish() }

                            adActivity?.finish()
                            if (launcherActivity!=null){
                                launcherActivity?.startActivity(Intent(launcherActivity, LauncherActivity::class.java))
                                launcherActivity?.finish()
                            }else{
                                it.startActivity(Intent(it, LauncherActivity::class.java))
                            }
                        }
                    }
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
                "开始计时 $cancelTime    $isRequestPermissionBack 。。。 ".logd("WHEUWHWHEQ")
                if (adActivity != null) return
                //如果是去获取权限，则不走热启动
                if (cancelTime) return
                stopTime = System.currentTimeMillis()
            }
        })
    }

}