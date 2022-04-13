package com.oneclean.android.booster.ui.base

import android.content.pm.ApplicationInfo
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.oneclean.android.booster.R
import com.oneclean.android.booster.utils.logd

/**
 * 效果测试
 *
 * */
enum class CleanType(value: Int){
    CLEAN(0),BOOSTER(1),SAVER(2),COOLER(3)
}
class TestActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)


//        val container = findViewById<LinearLayout>(R.id.container)
//        findViewById<Button>(R.id.button).setOnClickListener {
//            val randomSize = (5..10).random()
//            val list = packageManager.getInstalledPackages(0)
//            list.forEach {
//                if (it.applicationInfo.loadLabel(packageManager).contains("Go")){
//                    "系统应用 : ${it.applicationInfo.loadLabel(packageManager)}".logd("JBHWJEGHQfw")
//                }
////                if ((it.applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM) != 0) {
////                    "系统应用 : ${it.applicationInfo.loadLabel(packageManager)}".logd("JBHWJEGHQfw")
////                }else{
////                    "===========非系统应用 : ${it.applicationInfo.loadLabel(packageManager)} ==========".logd("JBHWJEGHQfw")
////                }
//            }
////            val list = getShowAppInfoList(randomSize)
////            container.removeAllViews()
////            list.forEach {
////                "name : ${it.loadLabel(packageManager)}".logd("JBHWJEGHQfw")
////                val dw = it.loadIcon(packageManager)
////                val cell = layoutInflater.inflate(R.layout.cell_test, container, false) as ImageView
////
////                Glide.with(this).load(dw).centerCrop().into(cell)
////
////                container.addView(cell)
////            }
//        }

    }


//    private fun getShowAppInfoList(randomSize: Int): List<ApplicationInfo> {
//        val result = mutableListOf<ApplicationInfo>()
//
//        val list = packageManager.getInstalledPackages(0)
//        list.forEach {
//            if ((it.applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM) == 0) {
//                "系统应用 : ${it.applicationInfo.loadLabel(packageManager)}".logd("JBHWJEGHQfw")
//            }else{
//                "===========非系统应用 : ${it.applicationInfo.loadLabel(packageManager)} ==========".logd("JBHWJEGHQfw")
//            }
//        }
//
//        "randomSize : $randomSize".logd("JBHWJEGHQfw")
//        for (info in (1..20).random() / 2 until list.size step (1..5).random()) {
//            if (result.size >= randomSize) return result
//
//            result.add(list[info].applicationInfo)
//        }
//
//        return result
//    }
}