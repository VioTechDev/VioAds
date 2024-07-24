package com.ads.admob.event

import android.content.Context
import android.util.Log
import androidx.core.os.bundleOf
import com.applovin.mediation.MaxAd
import com.google.firebase.analytics.FirebaseAnalytics

object FirebaseAnalyticsHelper {
    fun logEventWithAds(context: Context, impressionData: MaxAd) {
        Log.e("TAG", "logEventWithAds: ${impressionData.revenue}", )
        FirebaseAnalytics.getInstance(context).logEvent(FirebaseAnalytics.Event.AD_IMPRESSION,
            bundleOf().apply {
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
}