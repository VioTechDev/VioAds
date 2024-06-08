package com.example.andmoduleads.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.ads.admob.data.ContentAd
import com.ads.admob.helper.interstitial.InterstitialAdSplashConfig
import com.ads.admob.helper.interstitial.InterstitialAdSplashHelper
import com.ads.admob.helper.interstitial.params.InterstitialAdParam
import com.ads.admob.listener.InterstitialAdCallback
import com.example.andmoduleads.MyApplication
import com.example.andmoduleads.R
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.LoadAdError

class SplashActivity : AppCompatActivity() {
    private val list: List<String> = ArrayList()
    private val idAdSplash: String? = null
    private val isFirst = true
    private val interAdSplashHelper by lazy { initInterAdSplash() }
    private fun initInterAdSplash(): InterstitialAdSplashHelper {
        val config = InterstitialAdSplashConfig(
            idAds = "7172848836d13826",
            canShowAds = true,
            canReloadAds = true,
            timeDelay = 5000L,
            timeOut = 30000L,
            showReady = true
        )
        return InterstitialAdSplashHelper(
            activity = this,
            lifecycleOwner = this,
            config = config
        ).apply {
            registerAdListener(interAdCallBack)

        }
    }

    private val interAdCallBack = object : InterstitialAdCallback {
        override fun onNextAction() {
            startActivity(Intent(this@SplashActivity, MainActivity::class.java))
            finish()
        }

        override fun onAdClose() {
            startActivity(Intent(this@SplashActivity, MainActivity::class.java))
            finish()
        }

        override fun onInterstitialShow() {
        }

        override fun onAdLoaded(data: ContentAd) {
        }

        override fun onAdFailedToLoad(loadAdError: LoadAdError) {
        }

        override fun onAdClicked() {
        }

        override fun onAdImpression() {
        }

        override fun onAdFailedToShow(adError: AdError) {
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        MyApplication.appResumeAdHelper?.setDisableAppResumeOnScreen()
        interAdSplashHelper.requestAds(InterstitialAdParam.Request)
    }

    override fun onDestroy() {
        super.onDestroy()
        MyApplication.appResumeAdHelper?.setEnableAppResumeOnScreen()
    }
    companion object {
        private const val TAG = "VioAds"
    }
}