package com.ads.admob.admob

import android.app.Activity
import android.app.Application
import android.content.Context
import android.widget.FrameLayout
import androidx.annotation.LayoutRes
import com.ads.admob.BannerInlineStyle
import com.ads.admob.config.VioAdConfig
import com.ads.admob.data.ContentAd
import com.ads.admob.listener.BannerAdCallBack
import com.ads.admob.listener.InterstitialAdCallback
import com.ads.admob.listener.NativeAdCallback
import com.ads.admob.listener.RewardAdCallBack
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.gms.ads.nativead.NativeAd

/**
 * Created by ViO on 16/03/2024.
 */
interface AdmobFactory {
    fun initAdmob(context: Application, vioAdConfig: VioAdConfig)

    fun requestBannerAd(
        context: Context,
        adId: String,
        collapsibleGravity: String? = null,
        bannerInlineStyle: Int = BannerInlineStyle.SMALL_STYLE,
        useInlineAdaptive: Boolean = false,
        adCallback: BannerAdCallBack,
        adPlacement: String? = null
    )

    fun requestNativeAd(context: Context, adId: String, adCallback: NativeAdCallback, adPlacement: String? = null)

    fun populateNativeAdView(
        activity: Context,
        nativeAd: ContentAd,
        @LayoutRes nativeAdViewId: Int,
        adPlaceHolder: FrameLayout,
        containerShimmerLoading: ShimmerFrameLayout?,
        adCallback: NativeAdCallback
    )

    fun requestInterstitialAds(context: Context, adId: String, adCallback: InterstitialAdCallback, adPlacement: String? = null)

    fun showInterstitial(
        context: Context,
        interstitialAd: ContentAd?,
        adCallback: InterstitialAdCallback
    )

    fun requestRewardAd(context: Context, adId: String, adCallback: RewardAdCallBack, adPlacement: String? = null)
    fun showRewardAd(
        activity: Activity,
        rewardedAd: ContentAd,
        adCallback: RewardAdCallBack
    )
    companion object {
        val INSTANCE: AdmobFactory by lazy(LazyThreadSafetyMode.SYNCHRONIZED) { AdmobFactoryImpl() }
    }
}