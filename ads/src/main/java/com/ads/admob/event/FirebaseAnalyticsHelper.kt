package com.ads.admob.event

import android.content.Context
import android.util.Log
import androidx.core.os.bundleOf
import com.applovin.mediation.MaxAd
import com.google.firebase.analytics.FirebaseAnalytics

object FirebaseAnalyticsHelper {
    fun logEventWithAds(context: Context, impressionData: MaxAd) {
        Log.e("TAG", "logEventWithAds: ${impressionData.placement}", )
        FirebaseAnalytics.getInstance(context).logEvent(FirebaseAnalytics.Event.AD_IMPRESSION,
            bundleOf().apply {
                impressionData.placement
                putString(FirebaseAnalytics.Param.AD_PLATFORM, "appLovin")
                putString(FirebaseAnalytics.Param.AD_UNIT_NAME, impressionData.adUnitId)
                putString(FirebaseAnalytics.Param.AD_FORMAT, impressionData.format.label)
                putString(FirebaseAnalytics.Param.AD_SOURCE, impressionData.networkName)
                putDouble(FirebaseAnalytics.Param.VALUE, impressionData.revenue)
                putString(
                    FirebaseAnalytics.Param.CURRENCY,
                    "USD"
                ) // All Applovin revenue is sent in USD
            }
        )
    }
    fun logEventAdPlacement(context: Context, placement: String?, adUnitId: String) {
        placement?.let {
            Log.e("TAG", "logEventAdPlacement: $placement", )
            FirebaseAnalytics.getInstance(context).logEvent("ad_placement",
                bundleOf().apply {
                    putString("placement", placement)
                    putString(FirebaseAnalytics.Param.AD_UNIT_NAME, adUnitId)            }
            )
        } ?:run {
            Log.e("TAG", "logEventAdPlacement: 3213", )
        }
    }
}