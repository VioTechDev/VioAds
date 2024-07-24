package com.ads.admob.helper.reward

import com.ads.admob.helper.IAdsConfig
/**
 * Created by ViO on 16/03/2024.
 */
class RewardAdConfig(
    override val idAds: String,
    val showByTime: Int = 1,
    override val canShowAds: Boolean,
    override val canReloadAds: Boolean,
    val currentTime: Int = 0,
    val adPlacement: String? = null
) : IAdsConfig