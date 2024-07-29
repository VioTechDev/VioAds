package com.ads.admob.helper.interstitial.factory.max

import android.content.Context
import com.ads.admob.listener.InterstitialAdCallback
import com.applovin.mediation.ads.MaxInterstitialAd

interface MaxInterstitialAdFactory {
    fun requestInterstitialAd(context: Context, adId: String, adCallback: InterstitialAdCallback, adPlacement: String? = null)
    fun showInterstitial(
        context: Context,
        interstitialAd: MaxInterstitialAd?,
        adCallback: InterstitialAdCallback,
        adPlacement: String? = null
    )

    companion object {
        @JvmStatic
        fun getInstance(): MaxInterstitialAdFactory = MaxInterstitialAdFactoryImpl()
    }
}