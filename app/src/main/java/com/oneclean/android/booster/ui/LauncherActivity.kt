package com.oneclean.android.booster.ui

import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.view.animation.LinearInterpolator
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.animation.doOnEnd
import com.oneclean.android.booster.R
import com.oneclean.android.booster.ui.home.HomeActivity

class LauncherActivity : AppCompatActivity(R.layout.activity_launcher) {

    private lateinit var tvProgress: TextView
    private lateinit var progressBar: ProgressBar
    override fun onCreate(savedInstanceState: Bundle?) {
        //系统启动页有图片
        window.setBackgroundDrawable(null)

        super.onCreate(savedInstanceState)
        progressBar = findViewById(R.id.progress_bar)
        tvProgress = findViewById(R.id.tv_progress)
        //Thread.sleep(3000)
        val animator = ObjectAnimator.ofInt(progressBar, "progress", 0, 100)
        animator.duration = 1500
        animator.interpolator = LinearInterpolator()
        animator.start()

        animator.addUpdateListener {
            val str = getString(R.string.loading___) + it.animatedValue + "%"
            tvProgress.text = str
        }
        animator.doOnEnd {
            startActivity(Intent(this@LauncherActivity, HomeActivity::class.java))
            finish()
        }
    }
}