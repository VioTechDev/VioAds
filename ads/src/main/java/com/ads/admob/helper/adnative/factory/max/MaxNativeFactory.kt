package com.ads.admob.helper.adnative.factory.max

import android.content.Context
import android.widget.FrameLayout
import androidx.annotation.LayoutRes
import com.ads.admob.data.ContentAd
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.gms.ads.nativead.NativeAd
import com.ads.admob.listener.NativeAdCallback
import com.applovin.mediation.nativeAds.MaxNativeAdView

/**
 * Created by ViO on 16/03/2024.
 */
interface MaxNativeFactory {
    fun requestNativeAd(context: Context, adId: String, adCallback: NativeAdCallback, adPlacement: String? = null)

    fun populateNativeAdView(
        activity: Context,
        nativeAd: ContentAd.MaxContentAd.ApNativeAd,
        @LayoutRes nativeAdViewId: Int,
        adPlaceHolder: FrameLayout,
        containerShimmerLoading: ShimmerFrameLayout?,
        adCallback: NativeAdCallback
    )

    companion object {
        @JvmStatic
        fun getInstance(): MaxNativeFactory = MaxNativeFactoryImpl()
    }
}