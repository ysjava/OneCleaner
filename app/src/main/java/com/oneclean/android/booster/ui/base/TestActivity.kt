package com.oneclean.android.booster.ui.base

import android.content.Intent
import android.content.pm.ApplicationInfo
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.oneclean.android.booster.OneCleanerApplication.Companion.context
import com.oneclean.android.booster.R
import com.oneclean.android.booster.utils.logd
import java.util.*

/**
 * 效果测试
 *
 * */

class TestActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)
        val lay = findViewById<LinearLayout>(R.id.container)
        val button = findViewById<Button>(R.id.button)
        button.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse(
                    "https://onecleanerr.com/policy"
                )
            }
            startActivity(intent)
            // 做跳转到谷歌play做好评的业务逻辑
            //这里开始执行一个应用市场跳转逻辑，默认this为Context上下文对象

//    Log.d("HOmeFragment", "intent = $intent")
//    //intent.data = Uri.parse("market://details?id=" + "com.xunmeng.pinduoduo") //跳转到应用市场，非Google Play市场一般情况也实现了这个接口
//    //存在手机里没安装应用市场的情况，跳转会包异常，做一个接收判断
//    if (intent.resolveActivity(this.packageManager) != null) { //可以接收
//        startActivity(intent)
//    } else { //没有应用市场，我们通过浏览器跳转到Google Play
//        intent.data = Uri.parse("https://play.google.com/store/apps/details?id=" + "com.blossom.ripple");
//        //这里存在一个极端情况就是有些用户浏览器也没有，再判断一次
//        if (intent.resolveActivity(this.packageManager) == null) { //有浏览器
//            startActivity(intent)
//        } else { //天哪，这还是智能手机吗？
//            Toast.makeText(
//                this,
//                "You don't have an app market installed, not even a browser!",
//                Toast.LENGTH_SHORT
//            ).show()
//        }
//    }


        }


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