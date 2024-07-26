package com.ads.admob.helper.adnative.factory.max

import android.content.Context
import android.util.Log
import android.widget.FrameLayout
import androidx.annotation.LayoutRes
import com.adjust.sdk.Adjust
import com.adjust.sdk.AdjustAdRevenue
import com.adjust.sdk.AdjustConfig
import com.ads.admob.data.ContentAd
import com.ads.admob.listener.NativeAdCallback
import com.ads.admob.toLoadAdError
import com.ads.control.R
import com.applovin.mediation.MaxAd
import com.applovin.mediation.MaxError
import com.applovin.mediation.nativeAds.MaxNativeAdListener
import com.applovin.mediation.nativeAds.MaxNativeAdLoader
import com.applovin.mediation.nativeAds.MaxNativeAdView
import com.applovin.mediation.nativeAds.MaxNativeAdViewBinder
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.gms.ads.AdError

/**
 * Created by ViO on 16/03/2024.
 */
class MaxNativeFactoryImpl : MaxNativeFactory {
    private val TAG = MaxNativeFactoryImpl::class.java.simpleName
    override fun requestNativeAd(context: Context, adId: String, adCallback: NativeAdCallback) {
        val nativeAdLoader = MaxNativeAdLoader(adId, context)
        nativeAdLoader.setRevenueListener { ad ->
            val adjustAdRevenue = AdjustAdRevenue(AdjustConfig.AD_REVENUE_APPLOVIN_MAX)
            adjustAdRevenue.setRevenue(ad.revenue, "USD")
            adjustAdRevenue.setAdRevenueNetwork(ad.networkName)
            adjustAdRevenue.setAdRevenueUnit(ad.adUnitId)
            adjustAdRevenue.setAdRevenuePlacement(ad.placement)

            Adjust.trackAdRevenue(adjustAdRevenue)
        }
        nativeAdLoader.setNativeAdListener(object : MaxNativeAdListener() {
            override fun onNativeAdLoaded(nativeAdView: MaxNativeAdView?, ad: MaxAd) {
                Log.e(TAG, "onNativeAdLoaded: ", )
                adCallback.onAdLoaded(ContentAd.MaxContentAd.ApNativeAd(nativeAdLoader, ad))
            }

            override fun onNativeAdLoadFailed(adUnitId: String, error: MaxError) {
                Log.e(TAG, "onNativeAdLoadFailed: ${error.message}", )
                adCallback.onAdFailedToLoad(error.toLoadAdError())
            }

            override fun onNativeAdClicked(ad: MaxAd) {
                adCallback.onAdClicked()
            }

            override fun onNativeAdExpired(nativeAd: MaxAd) {
            }
        })
        nativeAdLoader.loadAd()
    }

    override fun populateNativeAdView(
        activity: Context,
        apNativeAd: ContentAd.MaxContentAd.ApNativeAd,
        @LayoutRes nativeAdViewId: Int,
        adPlaceHolder: FrameLayout,
        containerShimmerLoading: ShimmerFrameLayout?,
        adCallback: NativeAdCallback
    ) {
        val binder: MaxNativeAdViewBinder = MaxNativeAdViewBinder.Builder(nativeAdViewId)
            .setTitleTextViewId(R.id.ad_headline)
            .setBodyTextViewId(R.id.ad_body)
            .setAdvertiserTextViewId(R.id.ad_advertiser)
            .setIconImageViewId(R.id.ad_app_icon)
            .setMediaContentViewGroupId(R.id.ad_media)
            .setOptionsContentViewGroupId(R.id.ad_options_view)
            .setCallToActionButtonId(R.id.ad_call_to_action)
            .build()
        adPlaceHolder.removeAllViews()
        val nativeAdView = MaxNativeAdView(binder, activity)
        apNativeAd.maxNativeAdLoader.render(nativeAdView, apNativeAd.nativeAd)
        adPlaceHolder.addView(nativeAdView)
    }
}