package com.ads.admob.helper.reward.factory.max

import android.app.Activity
import android.content.Context
import com.ads.admob.listener.RewardAdCallBack
import com.ads.admob.listener.RewardInterAdCallBack
import com.applovin.mediation.ads.MaxRewardedAd

interface MaxRewardAdFactory {
    fun requestRewardAd(
        context: Context,
        adId: String,
        adCallback: RewardAdCallBack,
        adPlacement: String? = null,
        )
    fun requestRewardInterAd(context: Context, adId: String, adCallback: RewardInterAdCallBack, adPlacement: String? = null)
    fun showRewardAd(
        activity: Activity,
        rewardedAd: MaxRewardedAd,
        adCallback: RewardAdCallBack,
        adPlacement: String? = null,
    )

    companion object {
        @JvmStatic
        fun getInstance(): MaxRewardAdFactory = MaxRewardAdFactoryImpl()
    }
}