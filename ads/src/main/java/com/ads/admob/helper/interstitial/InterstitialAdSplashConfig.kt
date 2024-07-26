package com.ads.admob.helper.interstitial

import com.ads.admob.helper.IAdsConfig
/**
 * Created by ViO on 16/03/2024.
 */
class InterstitialAdSplashConfig(
    override val idAds: String,
    val timeOut: Long,
    val timeDelay: Long,
    val showReady: Boolean = false,
    override val canShowAds: Boolean,
    override val canReloadAds: Boolean
) : IAdsConfig