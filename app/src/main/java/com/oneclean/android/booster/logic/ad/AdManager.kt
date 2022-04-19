package com.oneclean.android.booster.logic.ad

import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.oneclean.android.booster.OneCleanerApplication
import com.oneclean.android.booster.ui.base.BaseActivity

interface InterstitialAdLoadedListener{
    fun success(interstitialAd: InterstitialAd)
    fun fail(loadAdError: LoadAdError)
}

object AdManager {
    //插屏广告
    private var interstitialAd: InterstitialAd? = null

    /**
     * 加载广告
     *
     * */
    fun loadInterstitialAd(
        listener: InterstitialAdLoadedListener?,
        success: (interstitialAd: InterstitialAd) -> Unit,
        fail: () -> Unit
    ) {
        if (interstitialAd != null) {


            //TODO 销毁
            return
        }

        val adRequest: AdRequest = AdRequest.Builder().build()
        InterstitialAd.load(OneCleanerApplication.context,
            "ca-app-pub-3940256099942544/1033173712",
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    this@AdManager.interstitialAd = interstitialAd

                    interstitialAd.setImmersiveMode(true)
                    interstitialAd.fullScreenContentCallback =
                        object : FullScreenContentCallback() {
                            override fun onAdDismissedFullScreenContent() {
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
                    fail()
                }
            })
    }
}