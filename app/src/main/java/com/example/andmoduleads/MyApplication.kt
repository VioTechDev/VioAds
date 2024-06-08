package com.example.andmoduleads

import android.app.Application
import android.util.Log
import androidx.lifecycle.ProcessLifecycleOwner
import com.ads.admob.admob.AdmobFactory
import com.ads.admob.config.NetworkProvider
import com.ads.admob.config.VioAdConfig
import com.ads.admob.config.VioAdjustConfig
import com.ads.admob.helper.appoppen.AppResumeAdConfig
import com.ads.admob.helper.appoppen.AppResumeAdHelper
import com.applovin.sdk.AppLovinSdk
import com.applovin.sdk.AppLovinSdkConfiguration
import com.example.andmoduleads.activity.SplashActivity
import com.google.android.gms.ads.AdActivity


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
            canShowAds = false,
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
        initAppOpenAd()
        val vioAdjustConfig = VioAdjustConfig.Build("mpuaogf4tngg",  false).build()
        val vioAdConfig = VioAdConfig.Builder(vioAdjustConfig = vioAdjustConfig)
            .buildVariantProduce(false)
            .mediationProvider(NetworkProvider.MAX)
            .listTestDevices(arrayListOf("FBDA72C75E0671544A38367B5AACCEC7"))
            .build()
        AdmobFactory.INSTANCE.initAdmob(this, vioAdConfig)
    }
}
