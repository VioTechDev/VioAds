package com.ads.admob.helper.banner.factory.max

import android.content.Context
import android.util.Log
import com.adjust.sdk.Adjust
import com.adjust.sdk.AdjustAdRevenue
import com.adjust.sdk.AdjustConfig
import com.ads.admob.data.ContentAd
import com.ads.admob.event.FirebaseAnalyticsHelper
import com.ads.admob.listener.BannerAdCallBack
import com.ads.admob.toAdError
import com.ads.admob.toLoadAdError
import com.applovin.mediation.MaxAd
import com.applovin.mediation.MaxAdViewAdListener
import com.applovin.mediation.MaxError
import com.applovin.mediation.ads.MaxAdView
import com.google.android.gms.ads.LoadAdError


/**
 * Created by ViO on 16/03/2024.
 */
class MaxBannerFactoryImpl : MaxBannerFactory {
    private val TAG = "MaxBannerFactory"
    override fun requestBannerAd(
        context: Context,
        adId: String,
        adCallback: BannerAdCallBack
    ) {
        try {
            val adView = MaxAdView(adId, context)
            adView.setListener(object : MaxAdViewAdListener {
                override fun onAdLoaded(p0: MaxAd) {
                    Log.e(TAG, "onAdLoaded: ")
                    adCallback.onAdLoaded(ContentAd.MaxContentAd.ApBannerAd(adView))
                }

                override fun onAdDisplayed(p0: MaxAd) {
                }

                override fun onAdHidden(p0: MaxAd) {
                }

                override fun onAdClicked(p0: MaxAd) {
                    adCallback.onAdClicked()
                }

                override fun onAdLoadFailed(p0: String, p1: MaxError) {
                    Log.e(TAG, "onAdLoadFailed: ${p1.message}")
                    adCallback.onAdFailedToLoad(p1.toLoadAdError())
                }

                override fun onAdDisplayFailed(p0: MaxAd, p1: MaxError) {
                    adCallback.onAdFailedToShow(p1.toAdError())
                }

                override fun onAdExpanded(p0: MaxAd) {
                }

                override fun onAdCollapsed(p0: MaxAd) {
                }

            })
            adView.setRevenueListener {ad->
                val adjustAdRevenue = AdjustAdRevenue(AdjustConfig.AD_REVENUE_APPLOVIN_MAX)
                adjustAdRevenue.setRevenue(ad.revenue, "USD")
                adjustAdRevenue.setAdRevenueNetwork(ad.networkName)
                adjustAdRevenue.setAdRevenueUnit(ad.adUnitId)
                adjustAdRevenue.setAdRevenuePlacement(ad.placement)

                Adjust.trackAdRevenue(adjustAdRevenue)
                FirebaseAnalyticsHelper.logEventWithAds(context, ad)
            }
            adView.loadAd()
        } catch (ex: Exception) {
            adCallback.onAdFailedToLoad(
                LoadAdError(
                    1999,
                    ex.message.toString(),
                    "",
                    null,
                    null
                )
            )
        }
    }

}