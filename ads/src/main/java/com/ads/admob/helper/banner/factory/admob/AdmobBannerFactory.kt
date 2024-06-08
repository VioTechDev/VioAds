package com.ads.admob.helper.banner.factory.admob

import android.content.Context
import com.ads.admob.BannerInlineStyle
import com.ads.admob.listener.BannerAdCallBack

/**
 * Created by ViO on 16/03/2024.
 */

interface AdmobBannerFactory {
    fun requestBannerAd(
        context: Context,
        adId: String,
        collapsibleGravity: String? = null,
        bannerInlineStyle: Int = BannerInlineStyle.SMALL_STYLE,
        useInlineAdaptive: Boolean,
        adCallback: BannerAdCallBack
    )

    companion object {
        @JvmStatic
        fun getInstance(): AdmobBannerFactory = AdmobBannerFactoryImpl()
    }
}