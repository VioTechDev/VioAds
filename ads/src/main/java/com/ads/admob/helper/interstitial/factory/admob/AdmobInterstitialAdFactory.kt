package com.ads.admob.helper.interstitial.factory.admob

import android.content.Context
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.ads.admob.listener.InterstitialAdCallback
/**
 * Created by ViO on 16/03/2024.
 */
interface AdmobInterstitialAdFactory {
    fun requestInterstitialAd(context: Context, adId: String, adCallback: InterstitialAdCallback)
    fun showInterstitial(
        context: Context,
        interstitialAd: InterstitialAd?,
        adCallback: InterstitialAdCallback
    )

    companion object {
        @JvmStatic
        fun getInstance(): AdmobInterstitialAdFactory = AdmobInterstitialAdFactoryImpl()
    }
}