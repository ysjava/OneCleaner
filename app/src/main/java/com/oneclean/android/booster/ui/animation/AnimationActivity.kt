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
import androidx.core.animation.doOnCancel
import androidx.core.animation.doOnEnd
import com.bumptech.glide.Glide
//import com.google.android.gms.ads.interstitial.InterstitialAd
import com.hi.dhl.binding.viewbind
import com.oneclean.android.booster.R
import com.oneclean.android.booster.databinding.ActivityAnimationBinding
//import com.oneclean.android.booster.logic.ad.AdManager

import com.oneclean.android.booster.logic.enums.CleanType
import com.oneclean.android.booster.ui.base.BaseActivity

import com.oneclean.android.booster.ui.cleaned.CleanedActivity
import com.oneclean.android.booster.ui.home.HomeActivity
import com.oneclean.android.booster.utils.dp2px
import com.oneclean.android.booster.utils.getStatusHeight
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

            //toolbar更新下高度，加上状态栏的高度，这个操作可以定义个父类来做
            val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
            val lp = toolbar.layoutParams
            lp.height = lp.height + getStatusHeight()
            toolbar.layoutParams = lp
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
        //开始加载广告
//        if (AdManager.adLoadCheck()) {
//            AdManager.loadInterstitialAd(this, success, fail, adIndex = 3)
//        }
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

    //    private var adManager2: AdManager2? = null
    private var animatorSetCancelTag = false
    private fun scanningAnim(cleanType: CleanType) {

        //AdManager.loadInterstitialAd(this, success, fail)

        showAppsIcon()
        val height = dp2px(330f).toFloat()
        val dp60 = dp2px(60f).toFloat()

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
        animatorSet.doOnCancel {
            animatorSetCancelTag = true
        }
        animatorSet.doOnEnd {
            if (animatorSetCancelTag) {
                animatorSetCancelTag = false
                return@doOnEnd
            }

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

    private var showIconsTimer: Timer? = null
    private fun showAppsIcon() {
        val infoList = getShowAppInfoList()
        binding.layIconContainer.removeAllViews()
        for (i in infoList.indices) {
            val info = infoList[i]
            val timer = Timer("hh")
            this.showIconsTimer = timer
            timer.schedule(object : TimerTask() {
                override fun run() {
                    if (showIconsTimer == null) return
                    val cell = layoutInflater.inflate(
                        R.layout.cell_app_icon,
                        binding.layIconContainer,
                        false
                    ) as ImageView
                    val icon = info.loadIcon(packageManager)
                    runOnUiThread {
                        if (showIconsTimer == null) return@runOnUiThread
                        Glide.with(this@AnimationActivity).load(icon).centerCrop().into(cell)
                        binding.layIconContainer.addView(cell)
                    }
                }

            }, kotlin.math.min(3000, ((i + 1) * (200L..500L).random())))
        }
    }


    @SuppressLint("QueryPermissionsNeeded")
    private fun getShowAppInfoList(): List<ApplicationInfo> {
        val result = mutableListOf<ApplicationInfo>()
        val randomSize = (5..8).random()
        //11开始非系统应用不让访问
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

        //显示广告
        val timer = Timer()
        this.timer = timer
        adTag = true
        timer.schedule(showAdTimerTask, 500)

        //跳转到加速完成页面
    }

    private val showAdTimerTask = object : TimerTask() {
        override fun run() {
            if (timer == null) return
            runOnUiThread {
                if (timer == null) return@runOnUiThread
                showAd()
            }
        }
    }

    private fun showAd() {
        CleanedActivity.startActivity(this@AnimationActivity, cleanType.value)
        finish()

//        ad?.show(this)
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
        timer = null

//        AdManager.remove(this)
        isStop = true
    }

    override fun onDestroy() {
        super.onDestroy()
        showIconsTimer?.cancel()
        showIconsTimer = null
    }

    private var isStop = false
    private var adTag = false
    override fun onResume() {
        super.onResume()

        if (isStop) {
            animatorSet?.start()
        }

//        if (ad == null && timer == null && adTag) {
//            val timer = Timer()
//            this.timer = timer
//            timer.schedule(showAdTimerTask, 500)
//        }
    }

    //private var ad: InterstitialAd? = null
//    private val success: (ad: InterstitialAd) -> Unit = {
//        ad = it
//    }
//    private val fail: () -> Unit = {
//        //AdManager.loadInterstitialAd(this, success, null, adIndex = 3)
//    }

}