package com.ads.admob.helper.banner

import com.ads.admob.BannerInlineStyle
import com.ads.admob.helper.IAdsConfig


/**
 * Created by ViO on 16/03/2024.
 */
data class BannerAdConfig(
    override val idAds: String,
    override val canShowAds: Boolean,
    override val canReloadAds: Boolean,
    val bannerInlineStyle: Int = BannerInlineStyle.SMALL_STYLE,
    val useInlineAdaptive: Boolean = false,
    val adPlacement: String? = null
) : IAdsConfig {
    var collapsibleGravity: String? = null
}