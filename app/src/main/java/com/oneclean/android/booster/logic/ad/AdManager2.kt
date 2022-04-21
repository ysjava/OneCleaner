package com.oneclean.android.booster.logic.ad

import android.content.Context
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdOptions
import com.google.android.gms.ads.nativead.NativeAdView
import com.oneclean.android.booster.OneCleanerApplication
import com.oneclean.android.booster.OneCleanerApplication.Companion.context
import com.oneclean.android.booster.R
import com.oneclean.android.booster.utils.getInt
import com.oneclean.android.booster.utils.getString
import com.oneclean.android.booster.utils.logd
import com.oneclean.android.booster.utils.setString
import java.text.SimpleDateFormat
import java.util.*


private var launcherAdShowNumber = 0
private var launcherAdClickNumber = 0

private var homeAdShowNumber = 0
private var homeAdClickNumber = 0

private var animAdShowNumber = 0
private var animAdClickNumber = 0

private var cleanedAdShowNumber = 0
private var cleanedAdClickNumber = 0

private var totalAdShowNumber = 0
private var totalAdClickNumber = 0


class AdManager2 {
    var interstitialAd: InterstitialAd? = null
    var receiver: AppCompatActivity? = null

    companion object {
        /**
         * 检查是否去加载广告
         *
         * @return true:  加载  false： 不加载
         * */
        fun adLoadCheck(): Boolean {

            return !((launcherAdShowNumber >= 10 || launcherAdClickNumber >= 2) ||
                    (homeAdShowNumber >= 10 || homeAdClickNumber >= 2) ||
                    (animAdShowNumber >= 20 || animAdClickNumber >= 3) ||
                    (cleanedAdShowNumber >= 20 || cleanedAdClickNumber >= 3) ||
                    (totalAdShowNumber >= 40 || totalAdClickNumber >= 4))
        }

        fun initData() {
            val newToday = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            val oldToday = getString("today", "sp_name_today")
            if (oldToday == "" || oldToday != newToday) {
                launcherAdShowNumber = 0
                launcherAdClickNumber = 0

                homeAdShowNumber = 0
                homeAdClickNumber = 0

                animAdShowNumber = 0
                animAdClickNumber = 0

                cleanedAdShowNumber = 0
                cleanedAdClickNumber = 0

                totalAdShowNumber = 0
                totalAdClickNumber = 0

                setString("today", "sp_name_today", newToday)
            } else {
                launcherAdShowNumber = getInt("launcherAdShowNumber")
                launcherAdClickNumber = getInt("launcherAdClickNumber")

                homeAdShowNumber = getInt("homeAdShowNumber")
                homeAdClickNumber = getInt("homeAdClickNumber")

                animAdShowNumber = getInt("animAdShowNumber")
                animAdClickNumber = getInt("animAdClickNumber")

                cleanedAdShowNumber = getInt("cleanedAdShowNumber")
                cleanedAdClickNumber = getInt("cleanedAdClickNumber")

                totalAdShowNumber = getInt("totalAdShowNumber")
                totalAdClickNumber = getInt("totalAdClickNumber")
            }
        }
    }

    /** 那个位置的广告  便于后面广告记录  */
    private var adIndex: Int = -1
    fun loadInterstitialAd(
        receiver: AppCompatActivity,
        success: (interstitialAd: InterstitialAd) -> Unit,
        fail: (() -> Unit)?,
        adUnitId: String = "ca-app-pub-3940256099942544/1033173712",
        closeAd: (() -> Unit)? = null,
        adIndex: Int
    ) {
        "开始加载插屏广告： 位置: $adIndex".logd("LJWBNFfjqfn")
        this.adIndex = adIndex
        this.receiver = receiver
        if (interstitialAd != null && this.receiver != null) {
            success(interstitialAd!!)
            return
        }
        //不存在缓存  发起广告请求
        val adRequest: AdRequest = AdRequest.Builder().build()

        InterstitialAd.load(receiver,
            adUnitId,
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    "插屏广告加载完成： 位置: $adIndex".logd("LJWBNFfjqfn")
                    //成功加载广告  当前显示页面是请求的页面就正常返回，否则就缓存起来即可
                    this@AdManager2.interstitialAd = interstitialAd
                    if (this@AdManager2.receiver != null) {
                        success(interstitialAd)
                    }

                    interstitialAd.setImmersiveMode(true)
                    interstitialAd.fullScreenContentCallback =
                        object : FullScreenContentCallback() {
                            override fun onAdDismissedFullScreenContent() {
                                closeAd?.let { closeAd() }
                            }

                            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                            }

                            override fun onAdShowedFullScreenContent() {
                                this@AdManager2.interstitialAd = null
                                "插屏广告开始展示： 位置: $adIndex".logd("LJWBNFfjqfn")
                                setNumber(1)
                            }

                            override fun onAdClicked() {
                                "插屏广告点击： 位置: $adIndex".logd("LJWBNFfjqfn")
                                OneCleanerApplication.adActivity?.finish()
                                setNumber(2)
                            }
                        }
                }

                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    // Handle the error
                    "插屏广告加载失败 失败： 位置: $adIndex".logd("LJWBNFfjqfn")
                    this@AdManager2.interstitialAd = null
                    fail?.let { it() }
                }
            })
    }

    private fun setNumber(type: Int) {
        when (adIndex) {
            1 -> if (type == 1) launcherAdShowNumber++ else launcherAdClickNumber++
            2 -> if (type == 1) homeAdShowNumber++ else homeAdClickNumber++
            3 -> if (type == 1) animAdShowNumber++ else animAdClickNumber++
            4 -> if (type == 1) cleanedAdShowNumber++ else cleanedAdClickNumber++
        }
        if (type ==1) totalAdShowNumber++ else totalAdClickNumber++

        save()

    }

    private fun save() {
        val editor = context.getSharedPreferences("sp_name_ad", Context.MODE_PRIVATE).edit()
        editor.putInt("launcherAdShowNumber", launcherAdShowNumber)
        editor.putInt("launcherAdClickNumber", launcherAdClickNumber)
        editor.putInt("homeAdShowNumber", homeAdShowNumber)
        editor.putInt("homeAdClickNumber", homeAdClickNumber)
        editor.putInt("animAdShowNumber", animAdShowNumber)
        editor.putInt("animAdClickNumber", animAdClickNumber)
        editor.putInt("cleanedAdShowNumber", cleanedAdShowNumber)
        editor.putInt("cleanedAdClickNumber", cleanedAdClickNumber)
        editor.putInt("totalAdShowNumber", totalAdShowNumber)
        editor.putInt("totalAdClickNumber", totalAdClickNumber)

        editor.apply()
    }

    private lateinit var adLoader: AdLoader
    fun loadNativeAd(
        receiver: AppCompatActivity,
        @LayoutRes adCellId: Int,
        adClicked: (() -> Unit)? = null,
        adIndex: Int
    ) {
        "开始加载原生广告： 位置: $adIndex".logd("LJWBNFfjqfn")
        this.adIndex = adIndex
        val adLoader = AdLoader.Builder(receiver, "ca-app-pub-3940256099942544/2247696110")
            .forNativeAd { ad: NativeAd ->
                if (!adLoader.isLoading) {
                    val adView =
                        LayoutInflater.from(receiver).inflate(adCellId, null) as NativeAdView
                    populateNativeAdView(ad, adView)
                    val adFrame = receiver.findViewById<FrameLayout>(R.id.lay_frame)
                    adFrame.removeAllViews()
                    "原生广告加载成功 开始显示： 位置: $adIndex".logd("LJWBNFfjqfn")
                    adFrame.addView(adView)

                    setNumber(1)
                }
            }
            .withAdListener(object : AdListener() {
                override fun onAdFailedToLoad(p0: LoadAdError) {
                    super.onAdFailedToLoad(p0)
                }

                override fun onAdClicked() {
                    super.onAdClicked()
                    "原生广告点击： 位置: $adIndex".logd("LJWBNFfjqfn")
                    adClicked?.let { adClicked() }
                    setNumber(2)
                }


            })
            .withNativeAdOptions(NativeAdOptions.Builder().build())
            .build()
        this.adLoader = adLoader
        adLoader.loadAds(AdRequest.Builder().build(), 1)
    }

    private fun populateNativeAdView(ad: NativeAd, adView: NativeAdView) {
        adView.headlineView = adView.findViewById(R.id.tv_head_line)
        adView.bodyView = adView.findViewById(R.id.tv_body)
        adView.imageView = adView.findViewById(R.id.iv_ad_icon)
        adView.callToActionView = adView.findViewById(R.id.tv_call_action)
        adView.headlineView?.let {
            (it as TextView).text = ad.headline
        }
        adView.bodyView?.let {
            (it as TextView).text = ad.body
        }
        adView.imageView?.let {
            it.isEnabled = false
            (it as ImageView).setImageDrawable(ad.icon?.drawable)
        }
        adView.callToActionView?.let {
            (it as TextView).text = ad.callToAction
        }

        adView.setNativeAd(ad)
    }

}
