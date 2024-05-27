package com.ads.admob.helper.interstitial

import com.ads.admob.helper.IAdsConfig
/**
 * Created by ViO on 16/03/2024.
 */
class InterstitialAdConfig(
    override val idAds: String,
    val showByTime: Int = 1,
    override val canShowAds: Boolean,
    override val canReloadAds: Boolean,
    val currentTime: Int = 0
) : IAdsConfig