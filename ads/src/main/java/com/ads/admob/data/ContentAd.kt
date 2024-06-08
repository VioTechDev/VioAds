package com.ads.admob.data

import com.applovin.mediation.ads.MaxAdView
import com.applovin.mediation.ads.MaxInterstitialAd
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.appopen.AppOpenAd
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAd

/**
 * Created by ViO on 16/03/2024.
 */
sealed class ContentAd {
    sealed class AdmobAd : ContentAd() {
        data class ApNativeAd(val nativeAd: NativeAd) : AdmobAd()
        data class ApRewardAd(val rewardAd: RewardedAd) : AdmobAd()
        data class ApRewardInterAd(val rewardedInterstitialAd: RewardedInterstitialAd) : AdmobAd()
        data class ApInterstitialAd(val interstitialAd: InterstitialAd) : AdmobAd()
        data class ApAppOpenAd(val appOpenAd: AppOpenAd) : AdmobAd()
        data class ApBannerAd(val adView: AdView) : AdmobAd()
    }

    sealed class MaxContentAd : ContentAd() {
        data class ApInterstitialAd(val interstitialAd: MaxInterstitialAd) : MaxContentAd()
        data class ApBannerAd(val adView: MaxAdView) : MaxContentAd()
    }
}