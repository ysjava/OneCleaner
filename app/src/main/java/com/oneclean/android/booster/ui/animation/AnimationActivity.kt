package com.oneclean.android.booster.ui.animation

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.annotation.StringRes
import androidx.core.animation.doOnEnd
import com.bumptech.glide.Glide
import com.hi.dhl.binding.viewbind
import com.oneclean.android.booster.R
import com.oneclean.android.booster.databinding.ActivityAnimationBinding
import com.oneclean.android.booster.ui.base.BaseActivity
import com.oneclean.android.booster.ui.cleaned.CleanedActivity
import com.oneclean.android.booster.ui.home.HomeActivity
import java.util.*

class AnimationActivity : BaseActivity(R.layout.activity_animation) {
    private val binding: ActivityAnimationBinding by viewbind()
    private val animationRes = arrayOf(
        "clean.json",
        "Phone Booster .json",
        "Battery Saver.json",
        "CPU Cooler.json",
        "end.json"
    )

    companion object {
        fun startActivity(packageContext: Context, cleanType: Int) {
            val intent = Intent(packageContext, AnimationActivity::class.java)
            intent.putExtra("CleanType", cleanType)
            packageContext.startActivity(intent)
        }
    }

    private var cleanType = -1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        cleanType = intent.getIntExtra("CleanType", -1)

        if (cleanType > -1) {
            initView(cleanType)
            startAnimationByType(cleanType)
        }
    }

    private var count = 0
    private val animatorListener = object : AnimatorListenerImpl() {
        override fun onAnimationEnd(animation: Animator?, isReverse: Boolean) {
            if (count > 0) return
            animationComplete()
            count++
        }
    }

    private fun startAnimationByType(cleanType: Int) {
        if (cleanType == 0) {
            binding.ivScanningView.visibility = View.GONE
            binding.lavAnimation.visibility = View.VISIBLE
            updateTipsText(tipsStrArray[0])
            animationRealPlay(cleanType, (2..7).random())
        } else {
            scanningAnim(cleanType)
        }
    }

    private val tipsStrArray = arrayOf(
        R.string.cleaning___,
        R.string.accelerating___,
        R.string.optimizing___,
        R.string.cooling___
    )

    private fun scanningAnim(cleanType: Int) {
        showAppsIcon()
        val height = resources.getDimensionPixelSize(R.dimen.dp_330).toFloat()
        val dp60 = resources.getDimensionPixelSize(R.dimen.dp_60).toFloat()

        val animatorSet = AnimatorSet()

        val alpha2 = ObjectAnimator.ofFloat(binding.ivScanningView, "alpha", 0f, 1f)
        alpha2.duration = 500
        alpha2.startDelay = 200
        val translationY1 =
            ObjectAnimator.ofFloat(binding.ivScanningView, "translationY", dp60, -height)
        translationY1.duration = 1500
        val alpha1 = ObjectAnimator.ofFloat(binding.ivScanningView, "alpha", 1f, 0f)
        alpha1.duration = 500
        alpha1.startDelay = 900

        animatorSet.playTogether(alpha2, translationY1, alpha1)
        animatorSet.start()

        var count = 1
        animatorSet.doOnEnd {
            if (count <= 2) {
                animatorSet.start()
                count++
            } else {
                //扫描动画结束
                //隐藏icon列表展示
                binding.layIconContainer.visibility = View.GONE
                //执行第二段动画
                animationRealPlay(cleanType, (3..8).random())
                updateTipsText(tipsStrArray[cleanType])
                binding.ivScanningView.visibility = View.GONE
                binding.lavAnimation.visibility = View.VISIBLE
            }
        }
    }

    private fun showAppsIcon() {
        val infoList = getShowAppInfoList()

        infoList.forEach {
            val cell = layoutInflater.inflate(
                R.layout.cell_app_icon,
                binding.layIconContainer,
                false
            ) as ImageView
            val icon = it.loadIcon(packageManager)
            Glide.with(this).load(icon).centerCrop().into(cell)
            binding.layIconContainer.addView(cell)
        }

    }

    private fun getShowAppInfoList(): List<ApplicationInfo> {
        val result = mutableListOf<ApplicationInfo>()
        val randomSize = (5..8).random()
        val list = packageManager.getInstalledPackages(0)

        for (info in (1..20).random() / 2 until list.size step (1..5).random()) {
            if (result.size >= randomSize) return result

            result.add(list[info].applicationInfo)
        }

        return result
    }

    private fun animationComplete() {
        animationRealPlay(4, 0)
        updateTipsText(R.string.complete)
        //发送广播 告知清理完成
        val intent = Intent()
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.action = HomeActivity.BROADCAST_ACTION_DISC
        intent.putExtra("CheckedIndex", cleanType)
        sendBroadcast(intent)
        //跳转到加速完成页面  （或者直接写一起试试)
        Timer().schedule(object : TimerTask() {
            override fun run() {
                CleanedActivity.startActivity(this@AnimationActivity, cleanType)
                finish()
            }
        }, 1500)

    }

    private fun initView(cleanType: Int) {
        binding.apply {
            ivBack.setOnClickListener { finish() }

            val titleStr = when (cleanType) {
                0 -> "Junk Clean"
                1 -> "Phone Booster"
                2 -> "Battery Saver"
                3 -> "CPU Cooler"
                else -> ""
            }
            binding.tvTitle.text = titleStr
        }
    }

    /**
     * 执行动画的代码，提出来的公共部分
     *
     * @param resIndex 动画资源的index
     * @param repeatCount 循环次数
     * */
    private fun animationRealPlay(resIndex: Int, repeatCount: Int) {

        binding.lavAnimation.apply {
            setAnimation(animationRes[resIndex])
            this.repeatCount = if (resIndex == 1) 0 else repeatCount
            playAnimation()
            addAnimatorListener(animatorListener)
        }
    }

    private fun updateTipsText(@StringRes resId: Int) {
        binding.tvAnimationTips.text = getString(resId)
    }
}