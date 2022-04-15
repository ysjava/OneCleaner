package com.oneclean.android.booster.ui.animation

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
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
import com.oneclean.android.booster.logic.enums.CleanType
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
        fun startActivity(packageContext: Context, cleanTypeValue: Int) {
            val intent = Intent(packageContext, AnimationActivity::class.java)
            intent.putExtra("CleanTypeValue", cleanTypeValue)
            packageContext.startActivity(intent)
        }
    }

    private var cleanType = CleanType.NOTHING
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val value = intent.getIntExtra("CleanTypeValue", -1)
        val cleanType = CleanType.switchToTypeByValue(value)
        this.cleanType = cleanType

        if (cleanType != CleanType.NOTHING) {
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

    private fun startAnimationByType(cleanType: CleanType) {
        if (cleanType == CleanType.CLEAN) {
            binding.ivScanningView.visibility = View.GONE
            binding.lavAnimation.visibility = View.VISIBLE
            updateTipsText(tipsStrArray[0])
            animationRealPlay(cleanType.value, (2..7).random())
        } else {
            scanningAnim(cleanType)
        }
    }

    private val tipsStrArray = arrayOf(
        R.string.cleaning___,
        R.string.optimizing___,
        R.string.optimizing___,
        R.string.optimizing___,
    )
    private var animatorSet: AnimatorSet? = null
    private fun scanningAnim(cleanType: CleanType) {
        showAppsIcon()
        val height = resources.getDimensionPixelSize(R.dimen.dp_330).toFloat()
        val dp60 = resources.getDimensionPixelSize(R.dimen.dp_60).toFloat()

        val animatorSet = AnimatorSet()
        this.animatorSet = animatorSet
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
                animationRealPlay(cleanType.value, (3..8).random())
                updateTipsText(tipsStrArray[cleanType.value])
                binding.ivScanningView.visibility = View.GONE
                binding.lavAnimation.visibility = View.VISIBLE
            }
        }
    }

    private fun showAppsIcon() {
        val infoList = getShowAppInfoList()
        for (i in infoList.indices) {
            val info = infoList[i]

            Timer("hh").schedule(object : TimerTask() {
                override fun run() {
                    val cell = layoutInflater.inflate(
                        R.layout.cell_app_icon,
                        binding.layIconContainer,
                        false
                    ) as ImageView
                    val icon = info.loadIcon(packageManager)
                    runOnUiThread {
                        Glide.with(this@AnimationActivity).load(icon).centerCrop().into(cell)
                        binding.layIconContainer.addView(cell)
                    }
                }

            }, i * 100L)
        }
    }


    @SuppressLint("QueryPermissionsNeeded")
    private fun getShowAppInfoList(): List<ApplicationInfo> {
        val result = mutableListOf<ApplicationInfo>()
        val randomSize = (5..8).random()
        //11开始不非系统应用不让访问
        val list = packageManager.getInstalledPackages(0)

        for (info in (1..20).random() / 2 until list.size step (1..5).random()) {
            if (result.size >= randomSize) return result

            result.add(list[info].applicationInfo)
        }

        return result
    }

    private var timer: Timer? = null

    private fun animationComplete() {
        binding.tvAnimationTips.visibility = View.GONE

        animationRealPlay(4, 0)
        binding.tvComplete.visibility = View.VISIBLE
        updateTipsText(R.string.complete)
        //发送广播 告知清理完成
        val intent = Intent()
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.action = HomeActivity.BROADCAST_ACTION_DISC
        intent.putExtra("CheckedIndex", cleanType.value)
        sendBroadcast(intent)
        //跳转到加速完成页面  （或者直接写一起试试)
        this.timer = Timer()
        this.timer?.schedule(object : TimerTask() {
            override fun run() {
                CleanedActivity.startActivity(this@AnimationActivity, cleanType.value)
                finish()
            }
        }, 1500)

    }

    private fun initView(cleanType: CleanType) {
        binding.apply {
            ivBack.setOnClickListener { finish() }

            val titleStr = when (cleanType) {
                CleanType.CLEAN -> "Junk Clean"
                CleanType.BOOSTER -> "Phone Booster"
                CleanType.SAVER -> "Battery Saver"
                CleanType.COOLER -> "CPU Cooler"
                else -> ""
            }
            binding.tvTitle.text = titleStr
        }
    }

    /**
     * 执行动画的代码，提出来的公共部分
     *
     * @param resIndex 清理类型
     * @param repeatCount 循环次数
     * */
    private fun animationRealPlay(resIndex: Int, repeatCount: Int) {

        binding.lavAnimation.apply {
            setAnimation(animationRes[resIndex])
            this.repeatCount = if (resIndex == CleanType.BOOSTER.value) 0 else repeatCount
            playAnimation()
            addAnimatorListener(animatorListener)
        }
    }

    private fun updateTipsText(@StringRes resId: Int) {
        binding.tvAnimationTips.text = getString(resId)
    }

    override fun onStop() {
        super.onStop()
        animatorSet?.cancel()
        timer?.cancel()
    }
}