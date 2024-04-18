package com.ads.admob.admob

import android.app.Activity
import android.content.Context
import android.widget.FrameLayout
import androidx.annotation.LayoutRes
import com.ads.admob.BannerInlineStyle
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.nativead.NativeAd
import com.ads.admob.config.VioAdConfig
import com.ads.admob.listener.BannerAdCallBack
import com.ads.admob.listener.InterstitialAdCallback
import com.ads.admob.listener.NativeAdCallback
import com.ads.admob.listener.RewardAdCallBack
import com.google.android.gms.ads.rewarded.RewardedAd

/**
 * Created by ViO on 16/03/2024.
 */
interface AdmobFactory {
    fun initAdmob(context: Context, vioAdConfig: VioAdConfig)

    fun requestBannerAd(
        context: Context,
        adId: String,
        collapsibleGravity: String? = null,
        bannerInlineStyle: Int = BannerInlineStyle.SMALL_STYLE,
        useInlineAdaptive: Boolean = false,
        adCallback: BannerAdCallBack
    )

    fun requestNativeAd(context: Context, adId: String, adCallback: NativeAdCallback)

    fun populateNativeAdView(
        activity: Context,
        nativeAd: NativeAd,
        @LayoutRes nativeAdViewId: Int,
        adPlaceHolder: FrameLayout,
        containerShimmerLoading: ShimmerFrameLayout?,
        adCallback: NativeAdCallback
    )

    fun requestInterstitialAds(context: Context, adId: String, adCallback: InterstitialAdCallback)

    fun showInterstitial(
        context: Context,
        interstitialAd: InterstitialAd?,
        adCallback: InterstitialAdCallback
    )

    fun requestRewardAd(context: Context, adId: String, adCallback: RewardAdCallBack)
    fun showRewardAd(
        activity: Activity,
        rewardedAd: RewardedAd,
        adCallback: RewardAdCallBack
    )
    companion object {
        @JvmStatic
        fun getInstance(): AdmobFactory = AdmobFactoryImpl()
    }
}