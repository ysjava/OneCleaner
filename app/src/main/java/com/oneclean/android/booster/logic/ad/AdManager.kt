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
import com.oneclean.android.booster.R
import com.oneclean.android.booster.logic.enums.AdName
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

object AdManager {
    var interstitialAd: InterstitialAd? = null
    var interstitialAd2: InterstitialAd? = null
    var nativeAd: NativeAd? = null
    var nativeAd2: NativeAd? = null
    private var hashSet = hashSetOf<AppCompatActivity>()
    fun checkReceiver(receiver: AppCompatActivity):Boolean{
        return hashSet.contains(receiver)
    }
    /**
     * 当请求加载广告的页面执行stop（切换到其他页面）时执行
     *
     * */
    fun remove(activity: AppCompatActivity) {
        hashSet.remove(activity)
    }

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


    /**
     * 加载广告
     *
     * */
    fun loadInterstitialAd(
        receiver: AppCompatActivity,
        success: (interstitialAd: InterstitialAd) -> Unit,
        fail: (() -> Unit)?,
        closeAd: (() -> Unit)? = null,
        adUnitId: String = "ca-app-pub-3940256099942544/1033173712",
        adIndex: Int
    ) {

        if (isLoadingInterstitialAd) {
            "当前正在加载原生广告 位置  $adIndex".logd("UGWIUGEIWEUQ")
            return
        }
        isLoadingInterstitialAd = true
        //添加接收者
        hashSet.add(receiver)
        val cacheAd = if (adIndex == 1) interstitialAd else interstitialAd2
        if (cacheAd != null && hashSet.contains(receiver)) {
            "缓存有的".logd("TTTESTE")
            //请求广告的接收者还未到其他界面 返回缓存的广告，并销毁
            success(cacheAd)
            //显示的页面不是接受页面就直接不管
            isLoadingInterstitialAd = false
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
                    if (adIndex == 1) {
                        AdManager.interstitialAd = interstitialAd
                    } else {
                        interstitialAd2 = interstitialAd
                    }

                    isLoadingInterstitialAd = false
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
                                if (adIndex == 1) {
                                    AdManager.interstitialAd = null
                                } else {
                                    interstitialAd2 = null
                                }

                                "插屏广告开始展示： 位置: $adIndex".logd("LJWBNFfjqfn")
                                setNumber(adIndex, 1)
                            }

                            override fun onAdClicked() {
                                super.onAdClicked()
                                OneCleanerApplication.adActivity?.finish()
                                setNumber(adIndex, 2)
                            }
                        }
                }

                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    // Handle the error
                    "插屏广告加载失败  位置: $adIndex".logd("LJWBNFfjqfn")
//                    interstitialAd = null
                    isLoadingInterstitialAd = false
                    fail?.let { it() }
                }
            })
    }

    private var isLoadingNativeAd = false
    private var isLoadingInterstitialAd = false
    fun loadNativeAd(
        receiver: AppCompatActivity,
        @LayoutRes adCellId: Int,
        adClicked: (() -> Unit)? = null,
        adIndex: Int
    ) {
        if (isLoadingNativeAd) {
            "当前正在加载原生广告 位置  $adIndex".logd("UGWIUGEIWEUQ")
            return
        }
        isLoadingNativeAd = true
        //添加接收者
        hashSet.add(receiver)

        val cacheAd = if (adIndex == 2) nativeAd else nativeAd2

        if (hashSet.contains(receiver) && cacheAd != null) {
            val adView =
                LayoutInflater.from(receiver).inflate(adCellId, null) as NativeAdView
            populateNativeAdView(cacheAd, adView)
            val adFrame = receiver.findViewById<FrameLayout>(R.id.lay_frame)
            adFrame.removeAllViews()
            "原生广告加载成功 ==有缓存= 显示： 位置: $adIndex".logd("LJWBNFfjqfn")

            adFrame.addView(adView)
            setNumber(adIndex, 1)

            if (adIndex == 2) {
                nativeAd = null
            } else {
                nativeAd2 = null
            }
            isLoadingNativeAd = false
            return
        }

        val adLoader = AdLoader.Builder(receiver, "ca-app-pub-3940256099942544/2247696110")
            .forNativeAd { ad: NativeAd ->
                "原生广告加载成功 位置: $adIndex".logd("LJWBNFfjqfn")
                //缓存

                if (adIndex == 2) {
                    nativeAd = ad
                } else {
                    nativeAd2 = ad
                }
                isLoadingNativeAd = false
                if (!hashSet.contains(receiver)) return@forNativeAd

                val adView =
                    LayoutInflater.from(receiver).inflate(adCellId, null) as NativeAdView
                populateNativeAdView(ad, adView)
                val adFrame = receiver.findViewById<FrameLayout>(R.id.lay_frame)
                adFrame.removeAllViews()
                "原生广告==开始显示： 位置: $adIndex".logd("LJWBNFfjqfn")
                adFrame.addView(adView)
                setNumber(adIndex, 1)

                if (adIndex == 2) {
                    nativeAd = null
                } else {
                    nativeAd2 = null
                }
            }
            .withAdListener(object : AdListener() {
                override fun onAdFailedToLoad(p0: LoadAdError) {
                    //nativeAd = null
                    isLoadingNativeAd = false
                }

                override fun onAdClicked() {
                    super.onAdClicked()
                    "原生广告点击： 位置: $adIndex".logd("LJWBNFfjqfn")
                    adClicked?.let { adClicked() }
                    setNumber(adIndex, 2)
                }
            })
            .withNativeAdOptions(NativeAdOptions.Builder().build())
            .build()

        "开始加载原生广告： 位置: $adIndex".logd("LJWBNFfjqfn")
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


    private fun setNumber(adIndex: Int, type: Int) {
        when (adIndex) {
            1 -> if (type == 1) launcherAdShowNumber++ else launcherAdClickNumber++
            2 -> if (type == 1) homeAdShowNumber++ else homeAdClickNumber++
            3 -> if (type == 1) animAdShowNumber++ else animAdClickNumber++
            4 -> if (type == 1) cleanedAdShowNumber++ else cleanedAdClickNumber++
        }
        if (type == 1) totalAdShowNumber++ else totalAdClickNumber++

        save()

    }

    private fun save() {
        val editor =
            OneCleanerApplication.context.getSharedPreferences("sp_name_ad", Context.MODE_PRIVATE)
                .edit()
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

    fun checkCache(index: Int): Boolean {
        return when (index) {
            1 -> interstitialAd != null
            2 -> interstitialAd2 != null
            3 -> nativeAd != null
            4 -> nativeAd2 != null
            else -> false
        }
    }


    /**
     * 预加载广告
     *
     * @param adName 预加载的广告名字
     * */
    fun preloadAd(adName: AdName) {
        if (adName == AdName.LauncherInterstitialAd || adName == AdName.AnimInterstitialAd)
            preloadInterstitialAd(adName)
        else
            preloadNativeAd(adName)
    }

    /**
     * 预加载原生广告
     * 加载完了缓存就行，其他不用干
     * */
    private fun preloadNativeAd(adName: AdName) {
//        val adLoader = AdLoader.Builder(
//            OneCleanerApplication.context,
//            "ca-app-pub-3940256099942544/2247696110"
//        )
//            .forNativeAd { ad: NativeAd ->
//                "原生广告预加载成功 名字: ${adName.name}".logd("LJWBNFfjqfn")
//                //缓存起来即可
//                if (adName == AdName.HomeNativeAd) {
//                    nativeAd = ad
//                } else {
//                    nativeAd2 = ad
//                }
//            }
//            .withAdListener(object : AdListener() {
//                override fun onAdFailedToLoad(p0: LoadAdError) {
//                    //nativeAd = null
//                    isLoadingNativeAd = false
//                }
//
//                override fun onAdClicked() {
//                    super.onAdClicked()
//                    "原生广告点击： 位置: $adIndex".logd("LJWBNFfjqfn")
//                    adClicked?.let { adClicked() }
//                    setNumber(adIndex, 2)
//                }
//            })
//            .withNativeAdOptions(NativeAdOptions.Builder().build())
//            .build()
//
//        "开始加载原生广告： 位置: $adIndex".logd("LJWBNFfjqfn")
//        adLoader.loadAds(AdRequest.Builder().build(), 1)
    }

    /**
     * 预加载插屏广告
     *
     * */
    private fun preloadInterstitialAd(adName: AdName) {

    }
}