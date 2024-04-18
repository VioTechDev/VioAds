package com.ads.admob.helper.reward.params

import com.google.android.gms.ads.interstitial.InterstitialAd

/**
 * Created by ViO on 16/03/2024.
 */
sealed class AdRewardState {
    data object None : AdRewardState()
    data object Fail : AdRewardState()
    data object Loading : AdRewardState()
    data object Loaded : AdRewardState()
    data object ShowFail : AdRewardState()
    data object Showed : AdRewardState()
    data object Cancel : AdRewardState()
    class Show(val interstitialAd: InterstitialAd) : AdRewardState()
}