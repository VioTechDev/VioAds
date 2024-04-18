package com.example.andmoduleads.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        rewardAdHelper.requestAds(RewardAdParam.Request)
        binding?.button3?.setOnClickListener {
            rewardAdHelper.requestAds(RewardAdParam.ShowAd)
        }
    }
}