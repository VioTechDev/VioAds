package com.example.andmoduleads.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.ads.admob.BannerInlineStyle
import com.ads.admob.data.ContentAd
import com.ads.admob.helper.adnative.AdmobNativeAdAdapter
import com.ads.admob.helper.adnative.NativeAdapterConfig
import com.ads.admob.helper.banner.BannerAdConfig
import com.ads.admob.helper.banner.BannerAdHelper
import com.ads.admob.helper.banner.params.BannerAdParam
import com.ads.admob.helper.reward.RewardAdConfig
import com.ads.admob.helper.reward.RewardAdHelper
import com.ads.admob.helper.reward.params.RewardAdParam
import com.ads.admob.listener.RewardAdCallBack
import com.example.andmoduleads.Contact
import com.example.andmoduleads.ContactsAdapter
import com.example.andmoduleads.R
import com.example.andmoduleads.databinding.ActivityMainBinding
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.rewarded.RewardItem

class MainActivity : AppCompatActivity() {
    private var binding: ActivityMainBinding? = null
    protected var isBackgroundRunning = false
    var contacts: java.util.ArrayList<Contact>? = null

    private val rewardAdHelper by lazy {
        val rewardAdConfig = RewardAdConfig("ca-app-pub-3940256099942544/5224354917", 1, true, true)
        RewardAdHelper(
            this, this, rewardAdConfig
        ).apply {

        }
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
            rewardAdHelper.registerAdListener(object  : RewardAdCallBack{
                override fun onAdClose() {
                    rewardAdHelper.requestAds(RewardAdParam.Request)
                }

                override fun onUserEarnedReward(rewardItem: RewardItem?) {
                }

                override fun onRewardShow() {
                }

                override fun onAdLoaded(data: ContentAd.AdmobAd.ApRewardAd) {
                }

                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    Log.e("TAG", "onAdFailedToLoad: ", )
                }

                override fun onAdClicked() {
                }

                override fun onAdImpression() {
                }

                override fun onAdFailedToShow(adError: AdError) {
                    Log.e("TAG", "onAdFailedToShow: ", )
                }

            })
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
        val cats: List<Contact> = List(20/4) {
            Log.e("TAG", "onCreate: $it", )
            Contact("vinhvh $it",true)
        }

        //Build the native adapter from the current adapter

        //Build the native adapter from the current adapter

        val listData = contacts?.chunked(20/4)?.mapIndexed { index, contacts ->
            Log.e("TAG", "onCreate: ", )
            contacts.toMutableList().apply {
                add(1, cats[index]) }
        }?.flatten()
        val adapter = contacts?.let { ContactsAdapter(it) }


        val gridLayoutManager = GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false)
        val nativeAdapterConfig = adapter?.let {
            NativeAdapterConfig.Builder(
                "ca-app-pub-3940256099942544/2247696110",
                it,
                R.layout.item_native_ad,
                R.layout.native_exit1,
                1,
                gridLayoutManager,
                2,
                false,
                false
            ).build()
        }
        val admobNativeAdAdapter = nativeAdapterConfig?.let { AdmobNativeAdAdapter(it) }


        // Attach the new adapter to the recyclerview to populate items


        // Attach the new adapter to the recyclerview to populate items
        binding?.rvContacts?.adapter = admobNativeAdAdapter

        // Set layout manager to position the items

        // Set layout manager to position the items
        binding?.rvContacts?.layoutManager = LinearLayoutManager(this)
    }

}