package com.ads.admob.data

import com.applovin.mediation.MaxAd
import com.applovin.mediation.ads.MaxAdView
import com.applovin.mediation.ads.MaxAppOpenAd
import com.applovin.mediation.ads.MaxInterstitialAd
import com.applovin.mediation.ads.MaxRewardedAd
import com.applovin.mediation.nativeAds.MaxNativeAdLoader
import com.applovin.mediation.nativeAds.MaxNativeAdView
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
        data class ApAppResumeAd(val appOpenAd: AppOpenAd) : AdmobAd()
        data class ApBannerAd(val adView: AdView) : AdmobAd()
    }

    sealed class MaxContentAd : ContentAd() {
        data class ApInterstitialAd(val interstitialAd: MaxInterstitialAd) : MaxContentAd()
        data class ApBannerAd(val adView: MaxAdView) : MaxContentAd()
        data class ApRewardAd(val rewardAd: MaxRewardedAd) : MaxContentAd()
        data class ApNativeAd(val maxNativeAdLoader: MaxNativeAdLoader, val nativeAd: MaxAd) : MaxContentAd()
        data class ApAppOpenAd(val appOpenAd: MaxAppOpenAd) : MaxContentAd()
        data class ApAppResumeAd(val appOpenAd: MaxAd) : MaxContentAd()

    }
}