package com.oneclean.android.booster.ui.launcher

import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.animation.LinearInterpolator
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.animation.doOnCancel
import androidx.core.animation.doOnEnd
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.oneclean.android.booster.R
import com.oneclean.android.booster.ui.base.BaseActivity
import com.oneclean.android.booster.ui.home.HomeActivity
import com.oneclean.android.booster.utils.logd

class LauncherActivity : AppCompatActivity(R.layout.activity_launcher) {

    private lateinit var tvProgress: TextView
    private lateinit var progressBar: ProgressBar
    private var animator: ObjectAnimator? = null
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
        val animator = ObjectAnimator.ofInt(progressBar, "progress", 0, 100)
        animator.duration = 3000
        animator.interpolator = LinearInterpolator()
        animator.start()
        this.animator = animator
//        animator.addUpdateListener {
//            val progress = it.animatedValue as Int
//            "progress $progress".logd("HWUHIQUFG")
//            if (progress > 50){
//                animator.cancel()
//
//
//                val animator2 = ObjectAnimator.ofInt(progressBar, "progress", progress, 100)
//                animator2.duration = 10000
//                animator2.interpolator = LinearInterpolator()
//                animator2.start()
//            }
//        }

        animator.addUpdateListener {
            val str = getString(R.string.loading___) + it.animatedValue + "%"
            tvProgress.text = str
        }
        animator.doOnCancel {
            //取消动画
        }
        val temp = true
//        animator.doOnEnd {
//            if (BaseActivity.lastActivity == null) {
//                startActivity(Intent(this@LauncherActivity, HomeActivity::class.java))
//            }
//            finish()
//        }
    }

    override fun onStart() {
        super.onStart()

        if (jindu > 0) {
            this.animator = ObjectAnimator.ofInt(progressBar, "progress", jindu, 100)
            this.animator!!.duration = 3000
            this.animator!!.interpolator = LinearInterpolator()
            this.animator!!.start()

            this.animator!!.addUpdateListener {
                val str = getString(R.string.loading___) + it.animatedValue + "%"
                tvProgress.text = str
            }
        }

    }

    private var jindu: Int = 0
    override fun onStop() {
        super.onStop()
        jindu = this.animator?.animatedValue as Int
        //在加载页点了home就取消动画
        this.animator?.cancel()
    }
}