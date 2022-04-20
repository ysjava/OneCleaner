package com.oneclean.android.booster.ui.launcher

import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.view.animation.LinearInterpolator
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.oneclean.android.booster.OneCleanerApplication
import com.oneclean.android.booster.R

import com.oneclean.android.booster.logic.ad.AdManager2
import com.oneclean.android.booster.ui.home.HomeActivity

class LauncherActivity : AppCompatActivity(R.layout.activity_launcher) {

    private lateinit var tvProgress: TextView
    private lateinit var progressBar: ProgressBar
    private var animator: ObjectAnimator? = null
    private var adManager2: AdManager2? = null
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
        //开始请求广告
        val adManager2 = AdManager2()
        this.adManager2 = adManager2
        adManager2.loadInterstitialAd(this, loadAdSuccess, loadAdFail)

        val animator = ObjectAnimator.ofInt(progressBar, "progress", 0, 100)
        animator.duration = 10000
        animator.interpolator = LinearInterpolator()
        animator.start()
        this.animator = animator
        //开始请求广告
//        AdManager.loadInterstitialAd(this, loadAdSuccess, loadAdFail)

        animator.addUpdateListener {
            val str = getString(R.string.loading___) + it.animatedValue + "%"
            tvProgress.text = str
            val progress = it.animatedValue as Int
            if (progress >= 100) {
                //加载到100还没请求到广告就直接跳转了
                end()
            }
        }


    }

    override fun onBackPressed() {
        cancel()
        super.onBackPressed()
    }

    override fun onStop() {
        //activity不在主页面了，就从集合中移去，这样就不会在加载广告到该页面了
        //AdManager.remove(this)
        cancel()
        super.onStop()
    }

    override fun onUserLeaveHint() {
        cancel()
        super.onUserLeaveHint()

    }

    private fun cancel() {
        animator?.cancel()
        adManager2?.receiver = null
        newAnimator?.cancel()
        resumeAnimator?.cancel()
    }

    override fun onResume() {
        val b = animator?.isRunning ?: false
        if (!b) {
            val progress = progressBar.progress
            val resumeAnimator = ObjectAnimator.ofInt(progressBar, "progress", progress, 100)
            resumeAnimator.duration = ((3..6).random() * 1000).toLong()
            resumeAnimator.start()
            this.resumeAnimator = resumeAnimator

            resumeAnimator.addUpdateListener {
                val str = getString(R.string.loading___) + it.animatedValue + "%"
                tvProgress.text = str
                val progress2 = it.animatedValue as Int
                if (progress2 >= 100) {
                    //加载到100还没请求到广告就直接跳转了
                    end()
                    adManager2?.interstitialAd?.show(this)
                }
            }
        }

        super.onResume()
    }

    private var newAnimator: ObjectAnimator? = null
    private var resumeAnimator: ObjectAnimator? = null

    private val loadAdSuccess: (interstitialAd: InterstitialAd) -> Unit = { ad ->
        //显示广告前先让loading在1s 结束

        animator?.cancel()
        val progress = progressBar.progress
        val newAnimator = ObjectAnimator.ofInt(progressBar, "progress", progress, 100)
        newAnimator.duration = 1000
        newAnimator.start()
        this.newAnimator = newAnimator
        newAnimator.addUpdateListener {
            val str = getString(R.string.loading___) + it.animatedValue + "%"
            tvProgress.text = str

            val v = it.animatedValue as Int
            if (v >= 100) {
                end()
                //动画结束，开始show广告
                ad.show(this)
            }
        }

    }


    private val loadAdFail: () -> Unit = {
        //重试一次
        adManager2?.loadInterstitialAd(this, loadAdSuccess, null)
    }

    private fun end() {
        if (OneCleanerApplication.activityList.size == 0) {
            startActivity(Intent(this@LauncherActivity, HomeActivity::class.java))
        }
        finish()
    }
}