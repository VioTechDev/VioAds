package com.ads.admob.helper.reward.factory.max

import android.app.Activity
import android.content.Context
import android.util.Log
import com.adjust.sdk.Adjust
import com.adjust.sdk.AdjustAdRevenue
import com.adjust.sdk.AdjustConfig
import com.ads.admob.data.ContentAd
import com.ads.admob.event.FirebaseAnalyticsHelper
import com.ads.admob.listener.RewardAdCallBack
import com.ads.admob.listener.RewardInterAdCallBack
import com.ads.admob.toAdError
import com.ads.admob.toLoadAdError
import com.applovin.mediation.MaxAd
import com.applovin.mediation.MaxError
import com.applovin.mediation.MaxReward
import com.applovin.mediation.MaxRewardedAdListener
import com.applovin.mediation.ads.MaxRewardedAd

class MaxRewardAdFactoryImpl : MaxRewardAdFactory {
    private val TAG = "MaxRewardAdFactory"
    override fun requestRewardAd(context: Context, adId: String, adCallback: RewardAdCallBack) {
        Log.e(TAG, "requestRewardAd: ", )
        val rewardedAd = MaxRewardedAd.getInstance(adId, context as Activity)
        rewardedAd.setListener(object : MaxRewardedAdListener {
            override fun onAdLoaded(p0: MaxAd) {
                Log.d(TAG, "onAdLoaded: ", )
                adCallback.onAdLoaded(ContentAd.MaxContentAd.ApRewardAd(rewardedAd))
            }

            override fun onAdDisplayed(p0: MaxAd) {

            }

            override fun onAdHidden(p0: MaxAd) {

            }

            override fun onAdClicked(p0: MaxAd) {

            }

            override fun onAdLoadFailed(p0: String, p1: MaxError) {
                Log.d(TAG, "onAdLoadFailed: ${p1.message}", )
                adCallback.onAdFailedToLoad(p1.toLoadAdError())
            }

            override fun onAdDisplayFailed(p0: MaxAd, p1: MaxError) {

            }

            override fun onUserRewarded(p0: MaxAd, p1: MaxReward) {
                adCallback.onUserEarnedReward(null)
            }

        })
        rewardedAd.setRevenueListener {ad ->
            val adjustAdRevenue = AdjustAdRevenue(AdjustConfig.AD_REVENUE_APPLOVIN_MAX)
            adjustAdRevenue.setRevenue(ad.revenue, "USD")
            adjustAdRevenue.setAdRevenueNetwork(ad.networkName)
            adjustAdRevenue.setAdRevenueUnit(ad.adUnitId)
            adjustAdRevenue.setAdRevenuePlacement(ad.placement)
            FirebaseAnalyticsHelper.logEventWithAds(context, ad)
            Adjust.trackAdRevenue(adjustAdRevenue)
        }
        rewardedAd.loadAd()
    }

    override fun requestRewardInterAd(
        context: Context,
        adId: String,
        adCallback: RewardInterAdCallBack
    ) {

    }

    override fun showRewardAd(
        activity: Activity,
        rewardedAd: MaxRewardedAd,
        adCallback: RewardAdCallBack
    ) {
        rewardedAd.setListener(object : MaxRewardedAdListener {
            override fun onAdLoaded(p0: MaxAd) {

            }

            override fun onAdDisplayed(p0: MaxAd) {
                adCallback.onRewardShow()
            }

            override fun onAdHidden(p0: MaxAd) {
                adCallback.onAdClose()
            }

            override fun onAdClicked(p0: MaxAd) {
                adCallback.onAdClicked()
            }

            override fun onAdLoadFailed(p0: String, p1: MaxError) {
            }

            override fun onAdDisplayFailed(p0: MaxAd, p1: MaxError) {
                adCallback.onAdFailedToShow(p1.toAdError())
            }

            override fun onUserRewarded(p0: MaxAd, p1: MaxReward) {
                adCallback.onUserEarnedReward(null)
            }

        })
        rewardedAd.showAd(activity)
    }

}