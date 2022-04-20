package com.oneclean.android.booster.logic.ad

import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback


object AdManager {
    //插屏广告
    var interstitialAd: InterstitialAd? = null
        private set

    private var hashSet = hashSetOf<AppCompatActivity>()

    /**
     * 当请求加载广告的页面执行stop（切换到其他页面）时执行
     *
     * */
    fun remove(activity: AppCompatActivity) {
        hashSet.remove(activity)
    }

    /**
     * 加载广告
     *
     * */
    fun loadInterstitialAd(
        receiver: AppCompatActivity,
        success: (interstitialAd: InterstitialAd) -> Unit,
        fail: (() -> Unit)?,
        closeAd: (() -> Unit)? = null
    ) {
        //添加接收者
        hashSet.add(receiver)
        if (interstitialAd != null) {
            if (hashSet.contains(receiver)) {
                //请求广告的接收者还未到其他界面 返回缓存的广告，并销毁
                success(interstitialAd!!)
            }
            //显示的页面不是接受页面就直接不管
            return
        }

        //不存在缓存  发起广告请求
        val adRequest: AdRequest = AdRequest.Builder().build()

        InterstitialAd.load(receiver,
            "ca-app-pub-3940256099942544/1033173712",
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    //成功加载广告  当前显示页面是请求的页面就正常返回，否则就缓存起来即可
                    this@AdManager.interstitialAd = interstitialAd
                    if (hashSet.contains(receiver)) {
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
                                this@AdManager.interstitialAd = null
                            }
                        }
                }

                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    // Handle the error
                    interstitialAd = null
                    fail?.let { it() }
                }
            })
    }
}