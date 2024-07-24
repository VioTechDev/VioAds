package com.ads.admob.helper.banner.factory.max

import android.content.Context
import com.ads.admob.listener.BannerAdCallBack

/**
 * Created by ViO on 16/03/2024.
 */

interface MaxBannerFactory {
    fun requestBannerAd(
        context: Context,
        adId: String,
        adCallback: BannerAdCallBack,
        adPlacement: String? = null,
        )

    companion object {
        @JvmStatic
        fun getInstance(): MaxBannerFactory = MaxBannerFactoryImpl()
    }
}