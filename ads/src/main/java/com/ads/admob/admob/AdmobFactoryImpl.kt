package com.ads.admob.admob

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.webkit.WebView
import android.widget.FrameLayout
import androidx.annotation.IntDef
import com.adjust.sdk.Adjust
import com.adjust.sdk.AdjustAdRevenue
import com.adjust.sdk.AdjustConfig
import com.adjust.sdk.LogLevel
import com.ads.admob.config.NetworkProvider
import com.ads.admob.config.VioAdConfig
import com.ads.admob.config.VioAdjustConfig
import com.ads.admob.data.ContentAd
import com.ads.admob.helper.adnative.factory.AdmobNativeFactory
import com.ads.admob.helper.banner.factory.admob.AdmobBannerFactory
import com.ads.admob.helper.banner.factory.max.MaxBannerFactory
import com.ads.admob.helper.interstitial.factory.admob.AdmobInterstitialAdFactory
import com.ads.admob.helper.interstitial.factory.max.MaxInterstitialAdFactory
import com.ads.admob.helper.reward.factory.AdmobRewardAdFactory
import com.ads.admob.listener.BannerAdCallBack
import com.ads.admob.listener.InterstitialAdCallback
import com.ads.admob.listener.NativeAdCallback
import com.ads.admob.listener.RewardAdCallBack
import com.applovin.sdk.AppLovinSdk
import com.applovin.sdk.AppLovinSdkConfiguration
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdapterResponseInfo
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import com.google.android.gms.ads.initialization.InitializationStatus
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.rewarded.RewardItem
import com.google.android.gms.ads.rewarded.RewardedAd

/**
 * Created by ViO on 16/03/2024.
 */
class AdmobFactoryImpl : AdmobFactory {
    private lateinit var vioAdConfig: VioAdConfig
    private val TAG = AdmobFactoryImpl::class.simpleName
    override fun initAdmob(
        context: Application,
        adConfig: VioAdConfig
    ) {

        this.vioAdConfig = adConfig
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val processName = Application.getProcessName()
            val packageName = context.packageName
            if (packageName != processName) {
                WebView.setDataDirectorySuffix(processName)
            }
        }
        if (vioAdConfig.provider == NetworkProvider.MAX) {
            AppLovinSdk.getInstance(context).mediationProvider = "max"
            AppLovinSdk.initializeSdk(context) { configuration: AppLovinSdkConfiguration? ->
                // AppLovin SDK is initialized, start loading ads
            }
        } else {
            MobileAds.initialize(context) { initializationStatus: InitializationStatus ->
                val statusMap = initializationStatus.adapterStatusMap
                for (adapterClass in statusMap.keys) {
                    val status = statusMap[adapterClass]
                    Log.d(
                        TAG, String.format(
                            "Adapter name: %s, Description: %s, Latency: %d",
                            adapterClass, status!!.description, status.latency
                        )
                    )
                }
            }
            MobileAds.setRequestConfiguration(
                RequestConfiguration.Builder().setTestDeviceIds(adConfig.listDevices).build()
            )
        }
        setupAdjust(context, adConfig.vioAdjustConfig)
    }

    private fun setupAdjust(application: Application, adjustConfig: VioAdjustConfig) {
        val environment = if (adjustConfig.environmentProduct) {
            AdjustConfig.ENVIRONMENT_PRODUCTION
        } else {
            AdjustConfig.ENVIRONMENT_SANDBOX
        }
        val config = AdjustConfig(application, adjustConfig.adjustToken, environment)

        // Change the log level.
        config.setLogLevel(LogLevel.VERBOSE)
        config.setPreinstallTrackingEnabled(true)
        config.setOnAttributionChangedListener { attribution ->
            Log.d(TAG, "Attribution callback called!")
            Log.d(TAG, "Attribution: $attribution")
        }

        // Set event success tracking delegate.
        config.setOnEventTrackingSucceededListener { eventSuccessResponseData ->
            Log.d(TAG, "Event success callback called!")
            Log.d(
                TAG,
                "Event success data: $eventSuccessResponseData"
            )
        }
        // Set event failure tracking delegate.
        config.setOnEventTrackingFailedListener { eventFailureResponseData ->
            Log.d(TAG, "Event failure callback called!")
            Log.d(
                TAG,
                "Event failure data: $eventFailureResponseData"
            )
        }

        // Set session success tracking delegate.
        config.setOnSessionTrackingSucceededListener { sessionSuccessResponseData ->
            Log.d(
                TAG,
                "Session success callback called!"
            )
            Log.d(
                TAG,
                "Session success data: $sessionSuccessResponseData"
            )
        }

        // Set session failure tracking delegate.
        config.setOnSessionTrackingFailedListener { sessionFailureResponseData ->
            Log.d(
                TAG,
                "Session failure callback called!"
            )
            Log.d(
                TAG,
                "Session failure data: $sessionFailureResponseData"
            )
        }
        application.registerActivityLifecycleCallbacks(AdjustLifecycleCallbacks())
        config.setSendInBackground(true)
        Adjust.onCreate(config)
    }

    private class AdjustLifecycleCallbacks : Application.ActivityLifecycleCallbacks {
        override fun onActivityResumed(activity: Activity) {
            Adjust.onResume()
        }

        override fun onActivityPaused(activity: Activity) {
            Adjust.onPause()
        }

        override fun onActivityStopped(activity: Activity) {
        }

        override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
        }

        override fun onActivityDestroyed(activity: Activity) {
        }

        override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        }

        override fun onActivityStarted(activity: Activity) {
        }
    }


    override fun requestBannerAd(
        context: Context,
        adId: String,
        collapsibleGravity: String?,
        bannerInlineStyle: Int,
        useInlineAdaptive: Boolean,
        adCallback: BannerAdCallBack
    ) {
        when (vioAdConfig.provider) {
            NetworkProvider.ADMOB -> {
                AdmobBannerFactory.getInstance()
                    .requestBannerAd(
                        context,
                        adId,
                        collapsibleGravity,
                        bannerInlineStyle,
                        useInlineAdaptive,
                        object : BannerAdCallBack {
                            override fun onAdLoaded(data: ContentAd) {
                                adCallback.onAdLoaded(data)
                                if (data is ContentAd.AdmobAd.ApBannerAd) {
                                    data.adView.setOnPaidEventListener { adValue ->
                                        val loadedAdapterResponseInfo: AdapterResponseInfo? =
                                            data.adView.responseInfo?.loadedAdapterResponseInfo
                                        val adRevenue =
                                            AdjustAdRevenue(AdjustConfig.AD_REVENUE_ADMOB)
                                        adRevenue.setRevenue(
                                            adValue.valueMicros / 1000000.0,
                                            adValue.currencyCode
                                        )
                                        adRevenue.setAdRevenuePlacement("Banner")
                                        if (loadedAdapterResponseInfo != null) {
                                            adRevenue.setAdRevenueNetwork(loadedAdapterResponseInfo.adSourceName)
                                        }
                                        Adjust.trackAdRevenue(adRevenue)
                                    }
                                }
                            }

                            override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                                adCallback.onAdFailedToLoad(loadAdError)
                            }

                            override fun onAdClicked() {
                                adCallback.onAdClicked()
                            }

                            override fun onAdImpression() {
                                adCallback.onAdImpression()
                            }

                            override fun onAdFailedToShow(adError: AdError) {
                                adCallback.onAdFailedToShow(adError)
                            }

                        }
                    )
            }

            NetworkProvider.MAX -> {
                MaxBannerFactory.getInstance()
                    .requestBannerAd(
                        context,
                        adId,
                        object : BannerAdCallBack {
                            override fun onAdLoaded(data: ContentAd) {
                                adCallback.onAdLoaded(data)
                            }

                            override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                                adCallback.onAdFailedToLoad(loadAdError)
                            }

                            override fun onAdClicked() {
                                adCallback.onAdClicked()
                            }

                            override fun onAdImpression() {
                                adCallback.onAdImpression()
                            }

                            override fun onAdFailedToShow(adError: AdError) {
                                adCallback.onAdFailedToShow(adError)
                            }
                        }
                    )
            }
        }

    }

    override fun requestNativeAd(
        context: Context,
        adId: String,
        adCallback: NativeAdCallback
    ) {
        AdmobNativeFactory.getInstance().requestNativeAd(context, adId, object : NativeAdCallback{
            override fun populateNativeAd() {
                adCallback.populateNativeAd()
            }

            override fun onAdLoaded(data: ContentAd.AdmobAd.ApNativeAd) {
               adCallback.onAdLoaded(data)
                data.nativeAd.setOnPaidEventListener {adValue ->
                    val loadedAdapterResponseInfo: AdapterResponseInfo? = data.nativeAd.responseInfo?.loadedAdapterResponseInfo
                    val adRevenue = AdjustAdRevenue(AdjustConfig.AD_REVENUE_ADMOB)
                    adRevenue.setRevenue(adValue.valueMicros / 1000000.0, adValue.currencyCode)
                    adRevenue.setAdRevenuePlacement("Native")
                    if (loadedAdapterResponseInfo != null) {
                        adRevenue.setAdRevenueNetwork(loadedAdapterResponseInfo.adSourceName)
                    }
                    Adjust.trackAdRevenue(adRevenue)
                }
            }

            override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                adCallback.onAdFailedToLoad(loadAdError)
            }

            override fun onAdClicked() {
               adCallback.onAdClicked()
            }

            override fun onAdImpression() {
                adCallback.onAdImpression()
            }

            override fun onAdFailedToShow(adError: AdError) {
               adCallback.onAdFailedToShow(adError)
            }

        })
    }

    override fun populateNativeAdView(
        context: Context,
        nativeAd: NativeAd,
        nativeAdViewId: Int,
        adPlaceHolder: FrameLayout,
        containerShimmerLoading: ShimmerFrameLayout?,
        adCallback: NativeAdCallback
    ) {
        AdmobNativeFactory.getInstance().populateNativeAdView(
            context,
            nativeAd,
            nativeAdViewId,
            adPlaceHolder,
            containerShimmerLoading,
            adCallback
        )
    }

    override fun requestInterstitialAds(
        context: Context,
        adId: String,
        adCallback: InterstitialAdCallback
    ) {
        when (vioAdConfig.provider) {
            NetworkProvider.ADMOB -> {
                AdmobInterstitialAdFactory.getInstance()
                    .requestInterstitialAd(context, adId, object : InterstitialAdCallback {
                        override fun onNextAction() {
                            adCallback.onNextAction()
                        }

                        override fun onAdClose() {
                            adCallback.onAdClose()
                        }

                        override fun onInterstitialShow() {
                            adCallback.onInterstitialShow()
                        }

                        override fun onAdLoaded(data: ContentAd) {
                            if (data is ContentAd.AdmobAd.ApInterstitialAd) {
                                adCallback.onAdLoaded(data)
                                data.interstitialAd.setOnPaidEventListener { adValue ->
                                    val loadedAdapterResponseInfo: AdapterResponseInfo? =
                                        data.interstitialAd.responseInfo.loadedAdapterResponseInfo
                                    val adRevenue = AdjustAdRevenue(AdjustConfig.AD_REVENUE_ADMOB)
                                    adRevenue.setRevenue(
                                        adValue.valueMicros / 1000000.0,
                                        adValue.currencyCode
                                    )
                                    adRevenue.setAdRevenuePlacement("Interstitial")
                                    if (loadedAdapterResponseInfo != null) {
                                        adRevenue.setAdRevenueNetwork(loadedAdapterResponseInfo.adSourceName)
                                    }
                                    Adjust.trackAdRevenue(adRevenue)
                                }
                            }
                        }

                        override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                            adCallback.onAdFailedToLoad(loadAdError)
                        }

                        override fun onAdClicked() {
                            adCallback.onAdClicked()
                        }

                        override fun onAdImpression() {
                            adCallback.onAdImpression()
                        }

                        override fun onAdFailedToShow(adError: AdError) {
                            adCallback.onAdFailedToShow(adError)
                        }

                    })
            }

            NetworkProvider.MAX -> {
                MaxInterstitialAdFactory.getInstance()
                    .requestInterstitialAd(context, adId, object : InterstitialAdCallback {
                        override fun onNextAction() {
                            adCallback.onNextAction()
                        }

                        override fun onAdClose() {
                            adCallback.onAdClose()
                        }

                        override fun onInterstitialShow() {
                            adCallback.onInterstitialShow()
                        }

                        override fun onAdLoaded(data: ContentAd) {
                            adCallback.onAdLoaded(data)
                        }

                        override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                            adCallback.onAdFailedToLoad(loadAdError)
                        }

                        override fun onAdClicked() {
                            adCallback.onAdClicked()
                        }

                        override fun onAdImpression() {
                            adCallback.onAdImpression()
                        }

                        override fun onAdFailedToShow(adError: AdError) {
                            adCallback.onAdFailedToShow(adError)
                        }

                    })
            }
        }
    }

    override fun showInterstitial(
        context: Context,
        interstitialAd: ContentAd?,
        adCallback: InterstitialAdCallback
    ) {
        when (interstitialAd) {
            is ContentAd.AdmobAd.ApInterstitialAd -> {
                AdmobInterstitialAdFactory.getInstance()
                    .showInterstitial(context, interstitialAd.interstitialAd, adCallback)
            }

            is ContentAd.MaxContentAd.ApInterstitialAd -> {
                MaxInterstitialAdFactory.getInstance()
                    .showInterstitial(context, interstitialAd.interstitialAd, adCallback)
            }

            else -> {
                adCallback.onAdFailedToShow(AdError(1999, "Ad Not support", ""))
            }
        }

    }

    override fun requestRewardAd(context: Context, adId: String, adCallback: RewardAdCallBack) {
        AdmobRewardAdFactory.getInstance().requestRewardAd(context, adId, object : RewardAdCallBack{
            override fun onAdClose() {
                adCallback.onAdClose()
            }

            override fun onUserEarnedReward(rewardItem: RewardItem?) {
                adCallback.onUserEarnedReward(rewardItem)
            }

            override fun onRewardShow() {
               adCallback.onRewardShow()
            }

            override fun onAdLoaded(data: ContentAd.AdmobAd.ApRewardAd) {
                adCallback.onAdLoaded(data)
                data.rewardAd.setOnPaidEventListener {adValue ->
                    val loadedAdapterResponseInfo: AdapterResponseInfo? = data.rewardAd.responseInfo.loadedAdapterResponseInfo
                    val adRevenue = AdjustAdRevenue(AdjustConfig.AD_REVENUE_ADMOB)
                    adRevenue.setRevenue(adValue.valueMicros / 1000000.0, adValue.currencyCode)
                    adRevenue.setAdRevenuePlacement("RewardAd")
                    if (loadedAdapterResponseInfo != null) {
                        adRevenue.setAdRevenueNetwork(loadedAdapterResponseInfo.adSourceName)
                    }
                    Adjust.trackAdRevenue(adRevenue)
                }
            }

            override fun onAdFailedToLoad(loadAdError: LoadAdError) {
               adCallback.onAdFailedToLoad(loadAdError)
            }

            override fun onAdClicked() {
               adCallback.onAdClicked()
            }

            override fun onAdImpression() {
                adCallback.onAdImpression()
            }

            override fun onAdFailedToShow(adError: AdError) {
                adCallback.onAdFailedToShow(adError)
            }
        })
    }

    override fun showRewardAd(
        activity: Activity,
        rewardedAd: RewardedAd,
        adCallback: RewardAdCallBack
    ) {
        AdmobRewardAdFactory.getInstance().showRewardAd(activity, rewardedAd, adCallback)
    }

    companion object {
        private val TAG = AdmobFactoryImpl::class.simpleName
    }
}

@IntDef(BannerInlineStyle.SMALL_STYLE, BannerInlineStyle.LARGE_STYLE)
annotation class BannerInlineStyle {
    companion object {
        const val SMALL_STYLE = 0
        const val LARGE_STYLE = 1
    }
}