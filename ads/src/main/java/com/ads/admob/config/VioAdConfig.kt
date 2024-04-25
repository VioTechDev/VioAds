package com.ads.admob.config

import android.app.Application

/**
 * Created by ViO on 16/03/2024.
 */
class VioAdConfig private constructor(
    val application: Application? = null,
    val isVariantProduce: Boolean = false,
    val provider: Int = NetworkProvider.ADMOB,
    val listDevices: List<String> = arrayListOf(),
    val disableAdsResumeByAd: Boolean = true,
    val vioAdjustConfig: VioAdjustConfig
) {

    class Builder(
        private var application: Application? = null,
        private var isBuildVariantProduce: Boolean = false,
        private var mediationProvider: Int = NetworkProvider.ADMOB,
        private var listTestDevices: List<String> = arrayListOf(),
        private var disableAdsResumeByAd: Boolean = true,
        private var vioAdjustConfig: VioAdjustConfig
    ) {
        fun application(application: Application) = apply { this.application = application }

        fun buildVariantProduce(variantProduce: Boolean) =
            apply { this.isBuildVariantProduce = variantProduce }

        fun mediationProvider(mediation: Int) = apply { this.mediationProvider = mediation }

        fun listTestDevices(listDevices: List<String>) =
            apply { this.listTestDevices = listDevices }

        fun disableAdsResumeByAd(isDisabled: Boolean) =
            apply { this.disableAdsResumeByAd = isDisabled }

        fun build() =
            VioAdConfig(
                application,
                isBuildVariantProduce,
                mediationProvider,
                listTestDevices,
                disableAdsResumeByAd,
                vioAdjustConfig
            )
    }
}
