package com.ads.admob.helper.adnative.params

import com.ads.admob.data.ContentAd
import com.google.android.gms.ads.nativead.NativeAd
import com.ads.admob.helper.params.IAdsParam

/**
 * Created by ViO on 16/03/2024.
 */
sealed class NativeAdParam : IAdsParam {
    data class Ready(val nativeAd: ContentAd) : NativeAdParam()
    object Request : NativeAdParam() {
        @JvmStatic
        fun create(): Request {
            return this
        }
    }
}
