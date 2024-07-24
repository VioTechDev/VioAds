package com.ads.admob.helper.interstitial.factory.max

import android.content.Context
import com.ads.admob.listener.InterstitialAdCallback
import com.applovin.mediation.MaxAd
import com.applovin.mediation.ads.MaxInterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAd

interface MaxInterstitialAdFactory {
    fun requestInterstitialAd(context: Context, adId: String, adCallback: InterstitialAdCallback, adPlacement: String? = null)
    fun showInterstitial(
        context: Context,
        interstitialAd: MaxInterstitialAd?,
        adCallback: InterstitialAdCallback
    )

    companion object {
        @JvmStatic
        fun getInstance(): MaxInterstitialAdFactory = MaxInterstitialAdFactoryImpl()
    }
}