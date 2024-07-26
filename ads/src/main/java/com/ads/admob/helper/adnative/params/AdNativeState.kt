package com.ads.admob.helper.adnative.params

import com.ads.admob.data.ContentAd
import com.google.android.gms.ads.nativead.NativeAd

/**
 * Created by ViO on 16/03/2024.
 */
sealed class AdNativeState {
    object None : AdNativeState()
    object Fail : AdNativeState()
    object Loading : AdNativeState()
    object Cancel : AdNativeState()
    data class Loaded(val adNative: ContentAd) : AdNativeState()
}