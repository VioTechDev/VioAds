package com.ads.admob.helper.banner

import com.ads.admob.helper.IAdsConfig


/**
 * Created by ViO on 16/03/2024.
 */
data class BannerAdConfig(
    override val idAds: String,
    override val canShowAds: Boolean,
    override val canReloadAds: Boolean,
) : IAdsConfig {
    var collapsibleGravity: String? = null
}