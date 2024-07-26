package com.example.andmoduleads

import android.app.Application
import android.util.Log
import androidx.lifecycle.ProcessLifecycleOwner
import com.ads.admob.admob.AdmobFactory
import com.ads.admob.config.NetworkProvider
import com.ads.admob.config.VioAdConfig
import com.ads.admob.config.VioAdjustConfig
import com.ads.admob.data.ContentAd
import com.ads.admob.helper.appoppen.AppResumeAdConfig
import com.ads.admob.helper.appoppen.AppResumeAdHelper
import com.ads.admob.listener.AppOpenAdCallBack
import com.applovin.sdk.AppLovinSdk
import com.applovin.sdk.AppLovinSdkConfiguration
import com.example.andmoduleads.activity.SplashActivity
import com.google.android.gms.ads.AdActivity
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.LoadAdError


class MyApplication : Application() {
    private val APPSFLYER_TOKEN = "2PUNpdyDTkedZTgeKkWCyB"
    private val ADJUST_TOKEN = "cc4jvudppczk"
    private val EVENT_PURCHASE_ADJUST = "gzel1k"
    private val EVENT_AD_IMPRESSION_ADJUST = "gzel1k"
    private val TAG = "MainApplication"


    private fun initAppOpenAd(): AppResumeAdHelper {
        val listClassInValid = mutableListOf<Class<*>>()
        listClassInValid.add(AdActivity::class.java)
        listClassInValid.add(SplashActivity::class.java)
        val config = AppResumeAdConfig(
            idAds = "c2026e2d6ea47670",
            listClassInValid = listClassInValid,
            canShowAds = true,
            networkProvider = NetworkProvider.MAX
        )
        return AppResumeAdHelper(
            application = this,
            lifecycleOwner = ProcessLifecycleOwner.get(),
            config = config
        )
    }

    override fun onCreate() {
        super.onCreate()
        application = this
        appResumeAdHelper = initAppOpenAd()
        appResumeAdHelper?.registerAdListener(object : AppOpenAdCallBack{
            override fun onAppOpenAdShow() {
                Log.e(TAG, "onAppOpenAdShow: ", )
            }

            override fun onAppOpenAdClose() {
                Log.e(TAG, "onAppOpenAdClose: ", )
            }

            override fun onAdLoaded(data: ContentAd) {
                Log.e(TAG, "onAdLoaded: ", )
            }

            override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                Log.e(TAG, "onAdFailedToLoad: ", )
            }

            override fun onAdClicked() {
                Log.e(TAG, "onAdClicked: ", )
            }

            override fun onAdImpression() {
                Log.e(TAG, "onAdImpression: ", )
            }

            override fun onAdFailedToShow(adError: AdError) {
                Log.e(TAG, "onAdFailedToShow: ", )
            }

        })
        val vioAdjustConfig = VioAdjustConfig.Build("mpuaogf4tngg",  false).build()
        val vioAdConfig = VioAdConfig.Builder(vioAdjustConfig = vioAdjustConfig)
            .buildVariantProduce(false)
            .mediationProvider(NetworkProvider.MIX)
            .listTestDevices(arrayListOf("FBDA72C75E0671544A38367B5AACCEC7"))
            .build()
        AdmobFactory.INSTANCE.initAdmob(this, vioAdConfig)
    }
    companion object {
        var application: MyApplication? = null
            private set
        var appResumeAdHelper : AppResumeAdHelper? = null
            private set
    }
}
