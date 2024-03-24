package com.ads.admob.helper.adnative

import androidx.annotation.LayoutRes
import com.ads.admob.helper.IAdsConfig

/**
 * Created by ViO on 16/03/2024.
 */
class NativeAdConfig(
    override val idAds: String,
    override val canShowAds: Boolean,
    override val canReloadAds: Boolean,
    @LayoutRes val layoutId: Int,
) : IAdsConfig
