package com.ads.admob.helper.banner.params

import com.ads.admob.data.ContentAd
import com.ads.admob.helper.params.IAdsParam

/**
 * Created by ViO on 16/03/2024.
 */
sealed class BannerAdParam: IAdsParam {
    data class Ready(val bannerAds: ContentAd) : BannerAdParam()
    object Request : BannerAdParam() {
        @JvmStatic
        fun create(): Request {
            return this
        }
    }

    data class Clickable(
        val minimumTimeKeepAdsDisplay: Long
    ) : BannerAdParam()
}