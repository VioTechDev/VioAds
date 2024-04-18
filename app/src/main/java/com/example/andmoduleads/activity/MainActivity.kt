package com.example.andmoduleads.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.ads.admob.BannerInlineStyle
import com.ads.admob.helper.banner.BannerAdConfig
import com.ads.admob.helper.banner.BannerAdHelper
import com.ads.admob.helper.banner.params.BannerAdParam
import com.ads.admob.helper.reward.RewardAdConfig
import com.ads.admob.helper.reward.RewardAdHelper
import com.ads.admob.helper.reward.params.RewardAdParam
import com.example.andmoduleads.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private var binding: ActivityMainBinding? = null
    protected var isBackgroundRunning = false
    private val rewardAdHelper by lazy {
        val rewardAdConfig = RewardAdConfig("ca-app-pub-3940256099942544/5224354917", 1, true, true)
        RewardAdHelper(
            this, this, rewardAdConfig
        )
    }

    private val bannerAdHelper by lazy { initBannerAd() }
    private fun initBannerAd(): BannerAdHelper {
        val config = BannerAdConfig(
            idAds = "ca-app-pub-3940256099942544/6300978111",
            canShowAds = true,
            canReloadAds = true,
            bannerInlineStyle = BannerInlineStyle.LARGE_STYLE,
            useInlineAdaptive = false
        )
        return BannerAdHelper(activity = this, lifecycleOwner = this, config = config)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        rewardAdHelper.requestAds(RewardAdParam.Request)
        binding?.button3?.setOnClickListener {
            rewardAdHelper.requestAds(RewardAdParam.ShowAd)
        }
        binding?.frAds?.let {
            bannerAdHelper.setBannerContentView(it)
        }
        binding?.shimmerBanner?.shimmerContainerBanner?.let { bannerAdHelper.setShimmerLayoutView(it) }
        if (bannerAdHelper.bannerAdView == null) {
            bannerAdHelper.requestAds(BannerAdParam.Request)
        }
    }

}