package com.ads.admob.helper.interstitial.params

import com.ads.admob.data.ContentAd
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.ads.admob.helper.params.IAdsParam

/**
 * Created by ViO on 16/03/2024.
 */
sealed class InterstitialAdParam : IAdsParam {
    data class Show(val interstitialAd: ContentAd) : InterstitialAdParam()
    object ShowAd : InterstitialAdParam()
    object Request : InterstitialAdParam() {
        @JvmStatic
        fun create(): Request {
            return this
        }
    }

    data class Clickable(
        val minimumTimeKeepAdsDisplay: Long
    ) : InterstitialAdParam()
}