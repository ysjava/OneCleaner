package com.oneclean.android.booster.logic.ad

import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.oneclean.android.booster.OneCleanerApplication

class AdManager2 {
    var interstitialAd: InterstitialAd? = null
    var receiver: AppCompatActivity? = null

    fun loadInterstitialAd(
        receiver: AppCompatActivity,
        success: (interstitialAd: InterstitialAd) -> Unit,
        fail: (() -> Unit)?,
        adUnitId: String = "ca-app-pub-3940256099942544/1033173712",
        closeAd: (() -> Unit)? = null
    ) {
        this.receiver = receiver

        if (interstitialAd!=null && this.receiver!=null){
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
                    //成功加载广告  当前显示页面是请求的页面就正常返回，否则就缓存起来即可
                    this@AdManager2.interstitialAd = interstitialAd
                    if (this@AdManager2.receiver!=null){
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
                            }

                            override fun onAdClicked() {
                                OneCleanerApplication.adActivity?.finish()
                            }
                        }
                }

                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    // Handle the error
                    this@AdManager2.interstitialAd = null
                    fail?.let { it() }
                }
            })
    }
}