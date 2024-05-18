package com.ads.admob.config

class VioAdjustConfig private constructor(
    val adjustToken: String,
    val environmentProduct: Boolean,
    val adRevenueKey: String,
) {
    class Build(
        private var adjustToken: String,
        private var environmentProduct: Boolean = true,
        private var adRevenueKey: String = "",
    ) {
        fun adRevenueKey(key: String) = apply {
            adRevenueKey = key
        }
        fun environmentProduct(product: Boolean) = apply { environmentProduct = product }
        fun build() = VioAdjustConfig(adjustToken, environmentProduct, adRevenueKey)
    }
}