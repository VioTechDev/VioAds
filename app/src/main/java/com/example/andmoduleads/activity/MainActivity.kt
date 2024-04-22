package com.example.andmoduleads.activity

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.ads.admob.BannerInlineStyle
import com.ads.admob.helper.adnative.adapter.AdLayoutContext
import com.ads.admob.helper.adnative.adapter.AdmobRecyclerAdapterWrapper
import com.ads.admob.helper.adnative.adapter.NativeAdLayoutContext
import com.ads.admob.helper.banner.BannerAdConfig
import com.ads.admob.helper.banner.BannerAdHelper
import com.ads.admob.helper.banner.params.BannerAdParam
import com.ads.admob.helper.reward.RewardAdConfig
import com.ads.admob.helper.reward.RewardAdHelper
import com.ads.admob.helper.reward.params.RewardAdParam
import com.example.andmoduleads.Contact
import com.example.andmoduleads.ContactsAdapter
import com.example.andmoduleads.R
import com.example.andmoduleads.RecyclerExampleAdapter
import com.example.andmoduleads.databinding.ActivityMainBinding
import com.google.android.gms.ads.AdRequest
import java.util.Timer
import java.util.TimerTask

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
    var adapterWrapper: AdmobRecyclerAdapterWrapper? = null
    var updateAdsTimer: Timer? = null

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

        /*//Build the native adapter from the current adapter
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
        binding?.rvContacts?.layoutManager = LinearLayoutManager(this)*/
        initRecyclerViewItems()
    }

    /**
     * Inits an adapter with items, wrapping your adapter with a [AdmobRecyclerAdapterWrapper] and setting the recyclerview to this wrapper
     * FIRST OF ALL Please notice that the following code will work on a real devices but emulator!
     */
    private fun initRecyclerViewItems() {
        binding?.rvContacts?.setLayoutManager(LinearLayoutManager(this))

        //creating your adapter, it could be a custom adapter as well
        contacts = Contact.createContactsList(20)

        // Create adapter passing in the sample user data

        // Create adapter passing in the sample user data

        //creating your adapter, it could be a custom adapter as well
        val adapter = RecyclerExampleAdapter(this)
        //your test devices' ids
        //when you'll be ready for release please use another ctor with admobReleaseUnitId instead.
        adapterWrapper = AdmobRecyclerAdapterWrapper(this, "ca-app-pub-3940256099942544/2247696110")
        //By default both types of ads are loaded by wrapper.
        // To set which of them to show in the list you should use an appropriate ctor
        //adapterWrapper = new AdmobRecyclerAdapterWrapper(this, testDevicesIds, EnumSet.of(EAdType.ADVANCED_INSTALLAPP));

        //wrapping your adapter with a AdmobAdapterWrapper.
        adapterWrapper?.setAdapter(adapter)
        val nativeAdLayoutContext = AdLayoutContext(com.ads.control.R.layout.native_exit3);
        adapterWrapper?.adsLayoutContext= nativeAdLayoutContext
        //inject your custom layout and strategy of binding for installapp/content  ads
        //here you should pass the extended NativeAdLayoutContext
        //by default it has a value InstallAppAdLayoutContext.getDefault()
        //adapterWrapper.setInstallAdsLayoutContext(...);
        //by default it has a value ContentAdLayoutContext.getDefault()
        //adapterWrapper.setContentAdsLayoutContext(...);

        //Sets the max count of ad blocks per dataset, by default it equals to 3 (according to the Admob's policies and rules)
        adapterWrapper?.setLimitOfAds(100)
        adapterWrapper?.setCanRequestAd(true)

        //Sets the number of your data items between ad blocks, by default it equals to 10.
        //You should set it according to the Admob's policies and rules which says not to
        //display more than one ad block at the visible part of the screen,
        // so you should choose this parameter carefully and according to your item's height and screen resolution of a target devices
        adapterWrapper?.setNoOfDataBetweenAds(3)
        adapterWrapper?.setFirstAdIndex(2)

        //if you use several view types in your source adapter then you have to set the biggest view type value with the following method
        //adapterWrapper.setViewTypeBiggestSource(100);
        binding?.rvContacts?.setAdapter(adapterWrapper) // setting an AdmobRecyclerAdapterWrapper to a RecyclerView

        //preparing the collection of data
        val sItem = "item #"
        val lst = java.util.ArrayList<String>(100)
        for (i in 1..100) lst.add(sItem + Integer.toString(i))

        //adding a collection of data to your adapter and rising the data set changed event
        adapter.addAll(lst)
        adapter.notifyDataSetChanged()
        adapterWrapper?.requestUpdateAd()
    }

    /*
    * Could be omitted. It's only for updating an ad blocks in each 60 seconds without refreshing the list
     */
    private fun initUpdateAdsTimer() {
        updateAdsTimer = Timer()
        updateAdsTimer?.schedule(object : TimerTask() {
            override fun run() {
                runOnUiThread { adapterWrapper?.requestUpdateAd() }
            }
        }, (60 * 1000).toLong(), (60 * 1000).toLong())
    }


}