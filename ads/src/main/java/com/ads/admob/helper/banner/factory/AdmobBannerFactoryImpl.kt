package com.ads.admob.helper.banner.factory

import android.app.Activity
import android.content.Context
import android.view.View
import com.adjust.sdk.Adjust
import com.adjust.sdk.AdjustAdRevenue
import com.adjust.sdk.AdjustConfig
import com.ads.admob.AdmobManager
import com.ads.admob.data.ContentAd
import com.ads.admob.getAdRequest
import com.ads.admob.getAdSize
import com.ads.admob.getCollapsibleAdRequest
import com.ads.admob.listener.BannerAdCallBack
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.AdapterResponseInfo
import com.google.android.gms.ads.LoadAdError


/**
 * Created by ViO on 16/03/2024.
 */
class AdmobBannerFactoryImpl : AdmobBannerFactory {
    override fun requestBannerAd(
        context: Context,
        adId: String,
        collapsibleGravity: String?,
        bannerInlineStyle: Int,
        useInlineAdaptive: Boolean,
        adCallback: BannerAdCallBack
    ) {
        try {
            val adView = AdView(context)
            adView.adUnitId = adId
            val adSize = getAdSize(context as Activity, useInlineAdaptive, bannerInlineStyle)
            adView.setAdSize(adSize)
            adView.setLayerType(View.LAYER_TYPE_SOFTWARE, null)
            adView.adListener = object : AdListener() {
                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    adCallback.onAdFailedToLoad(loadAdError)
                }

                override fun onAdLoaded() {
                    adCallback.onAdLoaded(ContentAd.AdmobAd.ApBannerAd(adView))

                    try{
                        adView.setOnPaidEventListener {
                            val loadedAdapterResponseInfo: AdapterResponseInfo? = adView.responseInfo?.loadedAdapterResponseInfo
                            val adRevenue = AdjustAdRevenue(AdjustConfig.AD_REVENUE_ADMOB)
                            adRevenue.setRevenue(it.valueMicros / 1000000.0, it.currencyCode)
                            adRevenue.setAdRevenuePlacement("Banner")
                            if (loadedAdapterResponseInfo != null) {
                                adRevenue.setAdRevenueNetwork(loadedAdapterResponseInfo.adSourceName)
                            }
                            Adjust.trackAdRevenue(adRevenue)
                        }

                    }catch (e:Exception){}

                }

                override fun onAdClicked() {
                    super.onAdClicked()
                    AdmobManager.adsClicked()
                    adCallback.onAdClicked()
                }

                override fun onAdImpression() {
                    super.onAdImpression()
                    adCallback.onAdImpression()
                }
            }
            adView.loadAd(
                if (collapsibleGravity == null) {
                    getAdRequest()
                } else {
                    getCollapsibleAdRequest(collapsibleGravity)
                }
            )
        } catch (ex: Exception) {
            adCallback.onAdFailedToLoad(
                LoadAdError(
                    1991,
                    ex.message.toString(),
                    "",
                    null,
                    null
                )
            )
        }
    }
}