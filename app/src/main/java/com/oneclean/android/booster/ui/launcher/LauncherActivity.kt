package com.oneclean.android.booster.ui.launcher

import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.view.animation.LinearInterpolator
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
//import com.google.android.gms.ads.interstitial.InterstitialAd
import com.oneclean.android.booster.OneCleanerApplication
import com.oneclean.android.booster.R
//import com.oneclean.android.booster.logic.ad.AdManager

import com.oneclean.android.booster.ui.home.HomeActivity
import com.oneclean.android.booster.utils.logd

class LauncherActivity : AppCompatActivity(R.layout.activity_launcher) {

    private lateinit var tvProgress: TextView
    private lateinit var progressBar: ProgressBar
    private var animator: ObjectAnimator? = null

    //    private var adManager2: AdManager2? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        //系统启动页有图片
        window.setBackgroundDrawable(null)

        super.onCreate(savedInstanceState)
        progressBar = findViewById(R.id.progress_bar)
        tvProgress = findViewById(R.id.tv_progress)
        //开始请求广告  失败重试一次  loading 2s-10s
        //成功请求后  先loading至100  然后展示广告， 3秒跳转并销毁广告
        //若是已经存在广告 则不请求，loading100后展示广告，3s跳转并销毁
        //若请求到广告已不再请求页面，则缓存备用
        //进入后台超过5s就重复操作

        //没有缓存 初始时长10s，否则就3s。 收到成功通知0.5s执行完loading， 收到失败通知，先重试一次： 成功->0.5s加载完成loading； 失败->等待正常的10s走完
        //收到成功的通知，取消原动画，执行新动画

        //val isLoadAd = AdManager.adLoadCheck()
//        if (isLoadAd) {
//            "开始加载".logd("TTTESTE")
//            AdManager.loadInterstitialAd(this, loadAdSuccess, loadAdFail, adIndex = 1)
//        }


        val animator = ObjectAnimator.ofInt(progressBar, "progress", 0, 100)
        animator.duration =  3000
        animator.interpolator = LinearInterpolator()
        animator.start()
        this.animator = animator
        animator.addUpdateListener {
            "动画监听1111 ${it.animatedValue}".logd("TTTESTE")
            val str = getString(R.string.loading___) + progressBar.progress + "%"
            tvProgress.text = str
            val progress = it.animatedValue as Int
            if (progress >= 100) {
                //加载到100还没请求到广告就直接跳转了
                end(1)
            }
        }

//        if (!AdManager.checkCache(1)) {
//            "动画执行1111".logd("TTTESTE")
//            val animator = ObjectAnimator.ofInt(progressBar, "progress", 0, 100)
//            animator.duration = if (isLoadAd) 10000 else 3000
//            animator.interpolator = LinearInterpolator()
//            animator.start()
//            this.animator = animator
//            animator.addUpdateListener {
//                "动画监听1111 ${it.animatedValue}".logd("TTTESTE")
//                val str = getString(R.string.loading___) + progressBar.progress + "%"
//                tvProgress.text = str
//                val progress = it.animatedValue as Int
//                if (progress >= 100) {
//                    //加载到100还没请求到广告就直接跳转了
//                    end(1)
//                }
//            }
//        }

    }

    override fun onBackPressed() {
        cancel()
        super.onBackPressed()
    }

    override fun onStop() {
        cancel()
        super.onStop()
    }

    override fun onUserLeaveHint() {
        cancel()
        super.onUserLeaveHint()

    }

    private fun cancel() {
        animator?.cancel()
        //AdManager.remove(this)
        newAnimator?.cancel()
        this.newAnimator = null
        resumeAnimator?.cancel()
    }

    override fun onResume() {


            //"onResume   ${AdManager.interstitialAd}".logd("kugqkfgkabak")
            val b = animator?.isRunning ?: false
            if (!b) {
                "动画执行2222".logd("TTTESTE")
                val progress = progressBar.progress
                val resumeAnimator = ObjectAnimator.ofInt(progressBar, "progress", progress, 100)
                resumeAnimator.duration = ((3..6).random() * 1000).toLong()
                resumeAnimator.start()
                this.resumeAnimator = resumeAnimator

                resumeAnimator.addUpdateListener {
                    "动画监听2222 ${it.animatedValue}".logd("TTTESTE")
                    val str = getString(R.string.loading___) + it.animatedValue + "%"
                    tvProgress.text = str
                    val progress2 = it.animatedValue as Int
                    if (progress2 >= 100) {
                        //加载到100还没请求到广告就直接跳转了

                        end(2)
                        //if (AdManager.checkReceiver(this))
                        //AdManager.interstitialAd?.show(this)
                    }
                }
            }

//        if (!AdManager.checkCache(1)) {
//            "onResume   ${AdManager.interstitialAd}".logd("kugqkfgkabak")
//            val b = animator?.isRunning ?: false
//            if (!b) {
//                "动画执行2222".logd("TTTESTE")
//                val progress = progressBar.progress
//                val resumeAnimator = ObjectAnimator.ofInt(progressBar, "progress", progress, 100)
//                resumeAnimator.duration = ((3..6).random() * 1000).toLong()
//                resumeAnimator.start()
//                this.resumeAnimator = resumeAnimator
//
//                resumeAnimator.addUpdateListener {
//                    "动画监听2222 ${it.animatedValue}".logd("TTTESTE")
//                    val str = getString(R.string.loading___) + it.animatedValue + "%"
//                    tvProgress.text = str
//                    val progress2 = it.animatedValue as Int
//                    if (progress2 >= 100) {
//                        //加载到100还没请求到广告就直接跳转了
//
//                        end(2)
//                        //if (AdManager.checkReceiver(this))
//                            AdManager.interstitialAd?.show(this)
//                    }
//                }
//            }
//        } else {
//            "onResume   ${AdManager.interstitialAd}".logd("kugqkfgkabak")
//            AdManager.interstitialAd?.let { loadAdSuccess(AdManager.interstitialAd!!) }
//        }

        super.onResume()
    }

    private var newAnimator: ObjectAnimator? = null
    private var resumeAnimator: ObjectAnimator? = null

//    private val loadAdSuccess: (interstitialAd: InterstitialAd) -> Unit = { ad ->
//        //显示广告前先让loading在1s 结束
//        animator?.cancel()
//        val progress = progressBar.progress
//        val newAnimator = ObjectAnimator.ofInt(progressBar, "progress", progress, 100)
//        newAnimator.duration = 2000
//        newAnimator.start()
//        "动画执行333333".logd("TTTESTE")
//        this.newAnimator = newAnimator
//        newAnimator.addUpdateListener {
//            val str = getString(R.string.loading___) + progressBar.progress + "%"
//            tvProgress.text = str
//            val v = newAnimator.animatedValue as Int
//            "动画监听333333".logd("TTTESTE")
//            if (v >= 100 && this.newAnimator?.isRunning == true) {
//
//                end(3)
//                //动画结束，开始show广告
//                //if (AdManager.checkReceiver(this))
//                    ad.show(this@LauncherActivity)
//            }
//        }
//
//    }

//    private val loadAdFail: () -> Unit = {
//        //重试一次
//        //adManager2?.loadInterstitialAd(this, loadAdSuccess, null, adIndex = 1)
//        AdManager.loadInterstitialAd(this, loadAdSuccess, null, adIndex = 1)
//    }

    private fun end(int: Int) {
        if (OneCleanerApplication.activityList.size == 0) {
            startActivity(Intent(this@LauncherActivity, HomeActivity::class.java))
        }
        finish()
    }
}