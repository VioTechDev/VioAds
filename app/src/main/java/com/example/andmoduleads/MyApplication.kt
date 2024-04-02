package com.example.andmoduleads

import android.app.Application
import androidx.lifecycle.ProcessLifecycleOwner
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
            listClassInValid = listClassInValid
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
        initAppOpenAd()
    }

    companion object {
        var application: MyApplication? = null
            private set
    }
}
