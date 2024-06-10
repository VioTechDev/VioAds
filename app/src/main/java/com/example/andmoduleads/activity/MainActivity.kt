package com.example.andmoduleads.activity

import android.annotation.SuppressLint
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.ads.admob.data.ContentAd
import com.ads.admob.helper.AdOptionVisibility
import com.ads.admob.helper.adnative.AdmobNativeAdAdapter
import com.ads.admob.helper.adnative.NativeAdConfig
import com.ads.admob.helper.adnative.NativeAdHelper
import com.ads.admob.helper.adnative.NativeAdapterConfig
import com.ads.admob.helper.adnative.params.NativeAdParam
import com.ads.admob.helper.banner.BannerAdConfig
import com.ads.admob.helper.banner.BannerAdHelper
import com.ads.admob.helper.banner.params.BannerAdParam
import com.ads.admob.helper.interstitial.InterstitialAdConfig
import com.ads.admob.helper.interstitial.InterstitialAdHelper
import com.ads.admob.helper.interstitial.params.InterstitialAdParam
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
import com.google.android.gms.ads.identifier.AdvertisingIdClient
import com.google.android.gms.ads.rewarded.RewardItem
import com.google.android.gms.common.GooglePlayServicesNotAvailableException
import com.google.android.gms.common.GooglePlayServicesRepairableException
import java.io.IOException

class MainActivity : AppCompatActivity() {
    private var binding: ActivityMainBinding? = null
    protected var isBackgroundRunning = false
    var contacts: java.util.ArrayList<Contact>? = null
    val interAdHelper by lazy { initInterAdAd() }

    private fun initInterAdAd(): InterstitialAdHelper {
        val config = InterstitialAdConfig(
            idAds = "7172848836d13826",
            canShowAds = true,
            canReloadAds = true,
            showByTime = 1,
            currentTime = 1
        )
        return InterstitialAdHelper(
            activity = this,
            lifecycleOwner = this,
            config = config
        )
    }
    private val rewardAdHelper by lazy {
        val rewardAdConfig = RewardAdConfig("f67842c7460f9215", 1, true, true)
        RewardAdHelper(
            this, this, rewardAdConfig
        ).apply {

        }
    }
    private val nativeAdHelper by lazy {
        val config = NativeAdConfig(
            "c2b390cda8403c0a",
            true,
            true,
           R.layout.native_exit1
        )

        NativeAdHelper(
            this,
            this,
            config
        ).apply {
            adVisibility = AdOptionVisibility.INVISIBLE
        }
    }

    private val bannerAdHelper by lazy { initBannerAd() }
    private fun initBannerAd(): BannerAdHelper {
        val config = BannerAdConfig(
            idAds = "ca-app-pub-4584260126367940/4345254018",
            canShowAds = true,
            canReloadAds = true
        )
        return BannerAdHelper(activity = this, lifecycleOwner = this, config = config)
    }

    override fun onResume() {
        super.onResume()
        interAdHelper.requestAds(InterstitialAdParam.Request)
    }
    @SuppressLint("ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        binding?.frAds?.let {
            nativeAdHelper.setNativeContentView(it)
        }
        binding?.flShimemr?.let {
            nativeAdHelper.setShimmerLayoutView(it.shimmerContainerNative)
        }
        //nativeAdHelper.requestAds(NativeAdParam.Request)
        rewardAdHelper.requestAds(RewardAdParam.Request)
        interAdHelper.requestAds(InterstitialAdParam.Request)
        rewardAdHelper.registerAdListener(object : RewardAdCallBack {
                override fun onAdClose() {
                    rewardAdHelper.requestAds(RewardAdParam.Request)
                }

                override fun onUserEarnedReward(rewardItem: RewardItem?) {
                }

                override fun onRewardShow() {
                }

                override fun onAdLoaded(data: ContentAd) {
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
            Log.e("TAG", "onCreate: ")
            bannerAdHelper.setBannerContentView(it)
        }
        bannerAdHelper.requestAds(BannerAdParam.Request)
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
            Contact("vinhvh $it",true)
        }

        //Build the native adapter from the current adapter

        //Build the native adapter from the current adapter

        val listData = contacts?.chunked(20/4)?.mapIndexed { index, contacts ->
            contacts.toMutableList().apply {
                add(1, cats[index]) }
        }?.flatten()
        val adapter = contacts?.let { ContactsAdapter(it) }


        val gridLayoutManager = GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false)
        val nativeAdapterConfig = adapter?.let {
            NativeAdapterConfig.Builder(
                "c2b390cda8403c0a",
                it,
                R.layout.item_native_ad,
                R.layout.native_exit1,
                1,
                gridLayoutManager,
                4,
                true,
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