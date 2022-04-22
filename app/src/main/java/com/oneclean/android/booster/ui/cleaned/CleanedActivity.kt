package com.oneclean.android.booster.ui.cleaned

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.hi.dhl.binding.viewbind
import com.oneclean.android.booster.R
import com.oneclean.android.booster.databinding.ActivityCleanedBinding
import com.oneclean.android.booster.logic.ad.AdManager
//import com.oneclean.android.booster.logic.ad.AdManager2
import com.oneclean.android.booster.logic.enums.CleanType
import com.oneclean.android.booster.ui.animation.AnimationActivity
import com.oneclean.android.booster.ui.base.BaseActivity
import com.oneclean.android.booster.ui.junkclean.JunkCleanActivity
import com.oneclean.android.booster.utils.getStatusHeight
import com.oneclean.android.booster.utils.logd

class CleanedActivity : BaseActivity(R.layout.activity_cleaned), View.OnClickListener {
    private val binding: ActivityCleanedBinding by viewbind()

    companion object {
        fun startActivity(packageContext: Context, cleanTypeValue: Int) {
            val intent = Intent(packageContext, CleanedActivity::class.java)
            intent.putExtra("CleanTypeValue", cleanTypeValue)
            packageContext.startActivity(intent)
        }
    }

    private var cleanType = CleanType.NOTHING
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val value = intent.getIntExtra("CleanTypeValue", -1)
        cleanType = CleanType.switchToTypeByValue(value)
        initView()
        "CleanedActivity onCreate1111 ".logd("LJWBNFfjqfn")
        if (AdManager.adLoadCheck()) {
            "CleanedActivity onCreate22222 ".logd("LJWBNFfjqfn")
            //加载广告
            AdManager.loadNativeAd(this, R.layout.cell_cleaned_ad_native, adClickedListener, 4)
        }

        binding.apply {
            when (cleanType) {
                CleanType.CLEAN -> layJunk.visibility = View.GONE
                CleanType.BOOSTER -> layBooster.visibility = View.GONE
                CleanType.SAVER -> layBattery.visibility = View.GONE
                CleanType.COOLER -> layCpu.visibility = View.GONE
                CleanType.NOTHING -> {}
            }
            val centerNames =
                arrayOf("\nJunk Cleaned", "\nPhone Boosted", "\nBattery Saved", "\nCPU Cooled")
            tvCenterName.text = centerNames[cleanType.value]
        }

    }

//    private var adManager2: AdManager2? = null

    private val adClickedListener: () -> Unit = {
        //先销毁广告
        binding.layFrame.removeAllViews()
        "CleanedActivity adClickedListener : ${AdManager.adLoadCheck()}".logd("LJWBNFfjqfn")
        if (AdManager.adLoadCheck())
//            adManager2?.loadNativeAd(this, R.layout.cell_cleaned_ad_native, adClickedListener2, 4)
            AdManager.loadNativeAd(this, R.layout.cell_cleaned_ad_native, adClickedListener2, 4)
    }

    private val adClickedListener2: () -> Unit = {
        //先销毁广告
        binding.layFrame.removeAllViews()
//        if (AdManager.adLoadCheck())
//            AdManager.loadNativeAd(this, R.layout.cell_cleaned_ad_native,null, 4)

    }

    private fun initView() {
        binding.apply {
            tvCleanJunk.setOnClickListener(this@CleanedActivity)
            tvCleanBooster.setOnClickListener(this@CleanedActivity)
            tvCleanBattery.setOnClickListener(this@CleanedActivity)
            tvCleanCpu.setOnClickListener(this@CleanedActivity)
            ivBack.setOnClickListener { finish() }
            tvTitle.text = getTitleText(cleanType.value)
        }

        //toolbar更新下高度，加上状态栏的高度，这个操作可以定义个父类来做
        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        val lp = toolbar.layoutParams
        lp.height = lp.height + getStatusHeight()
        toolbar.layoutParams = lp
    }

    private fun getTitleText(cleanType: Int): String {
        return when (cleanType) {
            0 -> getString(R.string.junk_clean)
            1 -> getString(R.string.phone_booster)
            2 -> getString(R.string.battery_saver)
            3 -> getString(R.string.cpu_cooler)
            else -> "haha ys"
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.tv_clean_junk -> JunkCleanActivity.startActivity(this@CleanedActivity, 0)
            R.id.tv_clean_booster -> AnimationActivity.startActivity(this@CleanedActivity, 1)
            R.id.tv_clean_battery -> AnimationActivity.startActivity(this@CleanedActivity, 2)
            R.id.tv_clean_cpu -> AnimationActivity.startActivity(this@CleanedActivity, 3)
        }
        finish()
    }

    override fun onStop() {
        super.onStop()
//        adManager2?.receiver = null
        AdManager.remove(this)
    }

    override fun onRestart() {
        super.onRestart()
        if (AdManager.adLoadCheck()){
            AdManager.loadNativeAd(this,R.layout.cell_cleaned_ad_native,adClickedListener,4)
        }
    }
}