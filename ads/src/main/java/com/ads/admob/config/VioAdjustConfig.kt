package com.ads.admob.config

class VioAdjustConfig private constructor(
    val adjustToken: String,
    val environment: String,
    val adRevenueKey: String,
) {
    class Build(
        private var adjustToken: String,
        private var environment: String,
        private var adRevenueKey: String = "",
    ) {
        fun adRevenueKey(key: String) = apply {
            adRevenueKey = key
        }
        fun build() = VioAdjustConfig(adjustToken, environment, adRevenueKey)
    }
}