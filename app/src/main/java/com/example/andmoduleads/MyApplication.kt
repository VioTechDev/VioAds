package com.example.andmoduleads

import android.app.Application
import androidx.lifecycle.ProcessLifecycleOwner
import com.adjust.sdk.AdjustConfig
import com.ads.admob.admob.AdmobFactory
import com.ads.admob.config.NetworkProvider
import com.ads.admob.config.VioAdConfig
import com.ads.admob.config.VioAdjustConfig
import com.ads.admob.helper.appoppen.AppResumeAdConfig
import com.ads.admob.helper.appoppen.AppResumeAdHelper
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
            idAds = "ca-app-pub-3940256099942544/9257395921",
            listClassInValid = listClassInValid,
            canShowAds = false
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
        initAppOpenAd().setRequestAppResumeValid(false)
        val vioAdjustConfig = VioAdjustConfig.Build("mpuaogf4tngg",  AdjustConfig.ENVIRONMENT_SANDBOX).build()
        val vioAdConfig = VioAdConfig.Builder(vioAdjustConfig = vioAdjustConfig)
            .buildVariantProduce(false)
            .mediationProvider(NetworkProvider.ADMOB)
            .listTestDevices(ArrayList())
            .build()
        AdmobFactory.getInstance().initAdmob(this, vioAdConfig)
    }
    companion object {
        var application: MyApplication? = null
            private set
    }
}
