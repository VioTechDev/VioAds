package com.ads.admob.helper.interstitial.factory.max

import android.app.Activity
import android.content.Context
import android.util.Log
import com.adjust.sdk.Adjust
import com.adjust.sdk.AdjustAdRevenue
import com.adjust.sdk.AdjustConfig
import com.ads.admob.data.ContentAd
import com.ads.admob.listener.InterstitialAdCallback
import com.ads.admob.toAdError
import com.ads.admob.toLoadAdError
import com.applovin.mediation.MaxAd
import com.applovin.mediation.MaxAdListener
import com.applovin.mediation.MaxError
import com.applovin.mediation.ads.MaxInterstitialAd

class MaxInterstitialAdFactoryImpl : MaxInterstitialAdFactory {
    private val TAG = "MaxInterstitialAd"
    override fun requestInterstitialAd(
        context: Context,
        adId: String,
        adCallback: InterstitialAdCallback
    ) {

        val interstitialAd = MaxInterstitialAd(adId, context as Activity)
        Log.d(TAG, "requestInterstitialAd: id $adId")
        interstitialAd.setListener(object : MaxAdListener {
            override fun onAdLoaded(p0: MaxAd) {
                Log.d(TAG, "onAdLoaded: id $adId")
                adCallback.onAdLoaded(ContentAd.MaxContentAd.ApInterstitialAd(interstitialAd))
            }

            override fun onAdDisplayed(p0: MaxAd) {
            }

            override fun onAdHidden(p0: MaxAd) {
            }

            override fun onAdClicked(p0: MaxAd) {
            }

            override fun onAdLoadFailed(p0: String, p1: MaxError) {
                Log.d(TAG, "onAdLoadFailed: id $adId  ${p1.message}")
                adCallback.onAdFailedToLoad(p1.toLoadAdError())
            }

            override fun onAdDisplayFailed(p0: MaxAd, p1: MaxError) {

            }

        })
        interstitialAd.setRevenueListener { ad ->
            val adjustAdRevenue = AdjustAdRevenue(AdjustConfig.AD_REVENUE_APPLOVIN_MAX)
            adjustAdRevenue.setRevenue(ad.revenue, "USD")
            adjustAdRevenue.setAdRevenueNetwork(ad.networkName)
            adjustAdRevenue.setAdRevenueUnit(ad.adUnitId)
            adjustAdRevenue.setAdRevenuePlacement(ad.placement)

            Adjust.trackAdRevenue(adjustAdRevenue)
        }
        interstitialAd.loadAd()
    }

    override fun showInterstitial(
        context: Context,
        interstitialAd: MaxInterstitialAd?,
        adCallback: InterstitialAdCallback
    ) {
        Log.d(TAG, "showInterstitial: ")
        interstitialAd?.let {
            it.showAd(context as Activity)
        }

        interstitialAd?.setListener(object : MaxAdListener {
            override fun onAdLoaded(p0: MaxAd) {

            }

            override fun onAdDisplayed(p0: MaxAd) {
                adCallback.onInterstitialShow()
                Log.e(TAG, "onAdDisplayed: ", )
                adCallback.onAdImpression()
            }

            override fun onAdHidden(p0: MaxAd) {
                adCallback.onAdClose()
                Log.e(TAG, "onAdHidden: ", )
            }

            override fun onAdClicked(p0: MaxAd) {
                adCallback.onAdClicked()
            }

            override fun onAdLoadFailed(p0: String, p1: MaxError) {
            }

            override fun onAdDisplayFailed(p0: MaxAd, p1: MaxError) {
                adCallback.onAdFailedToShow(p1.toAdError())
            }

        })
    }

}