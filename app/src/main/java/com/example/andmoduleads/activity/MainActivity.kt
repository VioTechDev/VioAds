package com.example.andmoduleads.activity

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.ads.admob.BannerInlineStyle
import com.ads.admob.helper.adnative.AdmobNativeAdAdapter
import com.ads.admob.helper.banner.BannerAdConfig
import com.ads.admob.helper.banner.BannerAdHelper
import com.ads.admob.helper.banner.params.BannerAdParam
import com.ads.admob.helper.reward.RewardAdConfig
import com.ads.admob.helper.reward.RewardAdHelper
import com.ads.admob.helper.reward.params.RewardAdParam
import com.example.andmoduleads.Contact
import com.example.andmoduleads.ContactsAdapter
import com.example.andmoduleads.R
import com.example.andmoduleads.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private var binding: ActivityMainBinding? = null
    protected var isBackgroundRunning = false
    var contacts: java.util.ArrayList<Contact>? = null

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
            useInlineAdaptive = true
        )
        return BannerAdHelper(activity = this, lifecycleOwner = this, config = config)
    }

    @SuppressLint("ResourceType")
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

        val list: MutableList<String> = ArrayList()
        for (i in 0..29) {
            list.add("Let's save the world $i")
        }


        // Initialize contacts

        // Initialize contacts
        contacts = Contact.createContactsList(20)

        // Create adapter passing in the sample user data

        // Create adapter passing in the sample user data
        val adapter = ContactsAdapter(contacts)

        //Build the native adapter from the current adapter

        //Build the native adapter from the current adapter
        val admobNativeAdAdapter: AdmobNativeAdAdapter = AdmobNativeAdAdapter.Builder.with(
            "ca-app-pub-3940256099942544/2247696110",  //admob native ad id
            adapter,  //current adapter
            0 //Set the size "small", "medium" or "custom"
        ).adPosition(1)
            .adItemInterval(5) //Repeat interval
            .adLayout(R.layout.native_exit1, 1)
            .adLayoutView(R.layout.item_native_ad, 1)
            .build()


        // Attach the new adapter to the recyclerview to populate items


        // Attach the new adapter to the recyclerview to populate items
        binding?.rvContacts?.adapter = admobNativeAdAdapter

        // Set layout manager to position the items

        // Set layout manager to position the items
        binding?.rvContacts?.layoutManager = LinearLayoutManager(this)
    }

}