package com.ads.admob.helper.reward

import android.app.Activity
import android.util.Log
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.ads.admob.AdmobManager
import com.ads.admob.admob.AdmobFactory
import com.ads.admob.data.ContentAd
import com.ads.admob.dialog.LoadingAdsDialog
import com.ads.admob.helper.AdsHelper
import com.ads.admob.helper.reward.params.AdRewardState
import com.ads.admob.helper.reward.params.RewardAdParam
import com.ads.admob.listener.RewardAdCallBack
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.rewarded.RewardItem
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.util.concurrent.CopyOnWriteArrayList

/**
 * Created by ViO on 16/03/2024.
 */
class RewardAdHelper(
    private val activity: Activity,
    private val lifecycleOwner: LifecycleOwner,
    private val config: RewardAdConfig
) : AdsHelper<RewardAdConfig, RewardAdParam>(activity, lifecycleOwner, config) {
    private val listAdCallback: CopyOnWriteArrayList<RewardAdCallBack> =
        CopyOnWriteArrayList()
    private val adRewardState: MutableStateFlow<AdRewardState> =
        MutableStateFlow(if (canRequestAds()) AdRewardState.None else AdRewardState.Fail)
    var rewardAdValue: ContentAd? = null
        private set
    private var requestShowCount = 0

    private val dialogLoading by lazy { LoadingAdsDialog(activity) }
    private var loadingJob: Job? = null

    init {
        requestShowCount = config.currentTime
    }
    override fun requestAds(param: RewardAdParam) {
        lifecycleOwner.lifecycleScope.launch {
            if (canRequestAds()) {
                when (param) {
                    is RewardAdParam.Request -> {
                        flagActive.compareAndSet(false, true)
                        createInterAds(activity)
                    }

                    is RewardAdParam.Show -> {
                        flagActive.compareAndSet(false, true)
                        rewardAdValue = param.rewardedAd
                        showRewardAds(activity)
                    }

                    is RewardAdParam.ShowAd -> {
                        flagActive.compareAndSet(false, true)
                        showRewardAds(activity)
                    }

                    else -> {

                    }
                }
            } else {
                invokeAdListener { it.onAdClose() }
            }
        }
    }

    private fun showRewardAds(activity: Activity) {
        if (config.showByTime != 1) {
            requestShowCount++
        }
        if (requestShowCount % config.showByTime == 0 && rewardAdValue != null && adRewardState.value == AdRewardState.Loaded) {
            lifecycleOwner.lifecycleScope.launch {
                AdmobManager.adsShowFullScreen()
                showDialogLoading()
                delay(800)
                AdmobFactory.INSTANCE
                    .showRewardAd(activity, rewardAdValue!!, invokeRewardAdCallback())
                loadingJob = lifecycleOwner.lifecycleScope.launch {
                    delay(2000)
                    dialogLoading.dismiss()
                }
            }
        } else if (requestShowCount % config.showByTime ==
            if (config.showByTime <= 2) {
                1
            } else {
                config.showByTime - 1
            }
            && adRewardState.value != AdRewardState.Loading
        ) {
            invokeAdListener { it.onAdFailedToShow(AdError(1, "Ads Empty", "" )) }
            requestAds(RewardAdParam.Request)
        } else {
            invokeAdListener { it.onAdFailedToShow(AdError(1, "Ads Empty", "" )) }
        }
    }

    private fun showDialogLoading() {
        dialogLoading.show()
    }

    private fun requestValid(): Boolean {
        val showConfigValid = (config.showByTime == 1 || requestShowCount % config.showByTime ==
                if (config.showByTime <= 2) {
                    1
                } else {
                    config.showByTime - 1
                })
        val valueValid =
            (rewardAdValue == null
                    && (adRewardState.value != AdRewardState.Loading && adRewardState.value != AdRewardState.Loaded)
                    )
                    || adRewardState.value == AdRewardState.Showed
        return canRequestAds() && showConfigValid && valueValid
    }

    private fun createInterAds(activity: Activity) {
        if (requestValid()) {
            lifecycleOwner.lifecycleScope.launch {
                adRewardState.emit(AdRewardState.Loading)
               logZ("createRewardAds")
                AdmobFactory.INSTANCE
                    .requestRewardAd(
                        activity,
                        config.idAds,
                        invokeRewardAdCallback(),
                        config.adPlacement
                    )
            }
        }
    }

    override fun cancel() {
    }

    fun registerAdListener(adCallback: RewardAdCallBack) {
        this.listAdCallback.add(adCallback)
    }

    fun unregisterAdListener(adCallback: RewardAdCallBack) {
        this.listAdCallback.remove(adCallback)
    }

    fun unregisterAllAdListener() {
        this.listAdCallback.clear()
    }

    private fun invokeAdListener(action: (adCallback: RewardAdCallBack) -> Unit) {
        listAdCallback.forEach(action)
    }

    private fun invokeRewardAdCallback(): RewardAdCallBack {
        return object : RewardAdCallBack {
            override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                logZ("onAdFailedToLoad ${loadAdError.message}")
                Log.e(TAG, "onAdFailedToLoad: $loadAdError", )
                invokeAdListener { it.onAdFailedToLoad(loadAdError) }
                lifecycleOwner.lifecycleScope.launch {
                    adRewardState.emit(AdRewardState.Fail)
                }
            }

            override fun onAdLoaded(data: ContentAd) {
                logZ("onRewardLoad")
                Log.d(TAG, "onRewardLoad: ")
                rewardAdValue = data
                lifecycleOwner.lifecycleScope.launch {
                    adRewardState.emit(AdRewardState.Loaded)
                }
                invokeAdListener { it.onAdLoaded(data) }
            }


            override fun onAdFailedToShow(adError: AdError) {
                logZ("onAdFailedToShow ${adError.message}")
                Log.e(TAG, "onAdFailedToShow: $adError", )
                AdmobManager.adsFullScreenDismiss()
                invokeAdListener { it.onAdClose() }
                dialogLoading.dismiss()
                cancelLoadingJob()
                lifecycleOwner.lifecycleScope.launch {
                    adRewardState.emit(AdRewardState.ShowFail)
                }
            }

            override fun onAdClose() {
                logZ("onAdClose")
                AdmobManager.adsFullScreenDismiss()
                dialogLoading.dismiss()
                cancelLoadingJob()
                invokeAdListener { it.onAdClose() }
            }

            override fun onRewardShow() {
                logZ("onRewardShow")
                lifecycleOwner.lifecycleScope.launch {
                    adRewardState.emit(AdRewardState.Showed)
                }
                AdmobManager.adsShowFullScreen()
            }

            override fun onUserEarnedReward(rewardItem: RewardItem?) {
                logZ("onUserEarnedReward")
                Log.d(TAG, "onUserEarnedReward: ")
            }

            override fun onAdClicked() {
                logZ("onAdClicked")
                invokeAdListener { it.onAdClicked() }
            }

            override fun onAdImpression() {
                logZ("onAdImpression")
                Log.d(TAG, "onAdImpression: ")
                invokeAdListener { it.onAdImpression() }
            }

        }
    }

    private fun cancelLoadingJob() {
        loadingJob?.cancel()
        loadingJob = null
    }

    companion object {
        private val TAG = RewardAdHelper::class.simpleName
    }
}