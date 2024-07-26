package com.ads.admob.helper.reward.factory.max

import android.app.Activity
import android.content.Context
import com.ads.admob.listener.RewardAdCallBack
import com.ads.admob.listener.RewardInterAdCallBack
import com.applovin.mediation.ads.MaxRewardedAd
import com.google.android.gms.ads.rewarded.RewardedAd

interface MaxRewardAdFactory {
    fun requestRewardAd(context: Context, adId: String, adCallback: RewardAdCallBack)
    fun requestRewardInterAd(context: Context, adId: String, adCallback: RewardInterAdCallBack)
    fun showRewardAd(
        activity: Activity,
        rewardedAd: MaxRewardedAd,
        adCallback: RewardAdCallBack
    )

    companion object {
        @JvmStatic
        fun getInstance(): MaxRewardAdFactory = MaxRewardAdFactoryImpl()
    }
}