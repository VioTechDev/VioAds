package com.ads.admob.helper.interstitial

import android.app.Activity
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.ads.admob.AdmobManager
import com.ads.admob.admob.AdmobFactory
import com.ads.admob.data.ContentAd
import com.ads.admob.dialog.LoadingAdsDialog
import com.ads.admob.helper.AdsHelper
import com.ads.admob.helper.interstitial.params.AdInterstitialState
import com.ads.admob.helper.interstitial.params.InterstitialAdParam
import com.ads.admob.listener.InterstitialAdCallback
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.LoadAdError
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.util.concurrent.CopyOnWriteArrayList

/**
 * Created by ViO on 16/03/2024.
 */
class InterstitialAdHelper(
    private val activity: Activity,
    private val lifecycleOwner: LifecycleOwner,
    private val config: InterstitialAdConfig
) : AdsHelper<InterstitialAdConfig, InterstitialAdParam>(activity, lifecycleOwner, config) {
    private val listAdCallback: CopyOnWriteArrayList<InterstitialAdCallback> =
        CopyOnWriteArrayList()
    private val adInterstitialState: MutableStateFlow<AdInterstitialState> =
        MutableStateFlow(if (canRequestAds()) AdInterstitialState.None else AdInterstitialState.Fail)
    var interstitialAdValue: ContentAd? = null
        private set
    private var requestShowCount = 0

    private val dialogLoading by lazy { LoadingAdsDialog(activity) }
    private var loadingJob: Job? = null

    init {
        requestShowCount = config.currentTime
    }

    override fun requestAds(param: InterstitialAdParam) {
        lifecycleOwner.lifecycleScope.launch {
            if (canRequestAds()) {
                when (param) {
                    is InterstitialAdParam.Request -> {
                        flagActive.compareAndSet(false, true)
                        createInterAds(activity)
                    }

                    is InterstitialAdParam.Show -> {
                        flagActive.compareAndSet(false, true)
                        interstitialAdValue = param.interstitialAd
                        showInterAds(activity)
                    }

                    is InterstitialAdParam.ShowAd -> {
                        flagActive.compareAndSet(false, true)
                        showInterAds(activity)
                    }

                    else -> {

                    }
                }
            } else {
                lifecycleOwner.lifecycleScope.launch {
                    adInterstitialState.emit(AdInterstitialState.Fail)
                }
                when (param) {
                    is InterstitialAdParam.Request -> {
                        invokeAdListener {
                            it.onAdFailedToLoad(
                                LoadAdError(
                                    99,
                                    "Request Invalid",
                                    "",
                                    null,
                                    null
                                )
                            )
                        }
                    }

                    is InterstitialAdParam.Show, is InterstitialAdParam.ShowAd -> {
                        invokeAdListener { it.onNextAction() }
                        invokeAdListener {
                            it.onAdFailedToShow(
                                AdError(
                                    1999,
                                    "Show ads InValid",
                                    ""
                                )
                            )
                        }
                    }

                    else -> {

                    }
                }
            }
        }
    }

    private fun showInterAds(activity: Activity) {
        if (config.showByTime != 1) {
            requestShowCount++
        }
        if (requestShowCount % config.showByTime == 0 && interstitialAdValue != null && adInterstitialState.value == AdInterstitialState.Loaded) {
            logZ("Show Interstitial")
            lifecycleOwner.lifecycleScope.launch {
                AdmobManager.adsShowFullScreen()
                showDialogLoading()
                delay(800)
                AdmobFactory.INSTANCE
                    .showInterstitial(activity, interstitialAdValue, invokeListenerAdCallback())
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
            && adInterstitialState.value != AdInterstitialState.Loading
        ) {
            invokeAdListener { it.onNextAction() }
            invokeAdListener { it.onAdFailedToShow(AdError(1999, "Show ads InValid", "")) }
            requestAds(InterstitialAdParam.Request)
        } else {
            invokeAdListener { it.onNextAction() }
            invokeAdListener { it.onAdFailedToShow(AdError(1999, "Show ads InValid", "")) }
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
            (interstitialAdValue == null
                    && (adInterstitialState.value != AdInterstitialState.Loading && adInterstitialState.value != AdInterstitialState.Loaded)
                    )
                    || adInterstitialState.value == AdInterstitialState.Showed
        return canRequestAds() && showConfigValid && valueValid
    }

    private fun createInterAds(activity: Activity) {
        if (requestValid()) {
            logZ("Create Interstitial")
            lifecycleOwner.lifecycleScope.launch {
                adInterstitialState.emit(AdInterstitialState.Loading)
                AdmobFactory.INSTANCE
                    .requestInterstitialAds(
                        activity,
                        config.idAds,
                        invokeListenerAdCallback()
                    )
            }
        }
    }

    override fun cancel() {
    }

    fun registerAdListener(adCallback: InterstitialAdCallback) {
        this.listAdCallback.add(adCallback)
    }

    fun unregisterAdListener(adCallback: InterstitialAdCallback) {
        this.listAdCallback.remove(adCallback)
    }

    fun unregisterAllAdListener() {
        this.listAdCallback.clear()
    }

    private fun invokeAdListener(action: (adCallback: InterstitialAdCallback) -> Unit) {
        listAdCallback.forEach(action)
    }

    private fun invokeListenerAdCallback(): InterstitialAdCallback {
        return object : InterstitialAdCallback {
            override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                logZ("onAdFailedToLoad ${loadAdError.message}")
                invokeAdListener { it.onAdFailedToLoad(loadAdError) }
                lifecycleOwner.lifecycleScope.launch {
                    adInterstitialState.emit(AdInterstitialState.Fail)
                }
            }

            override fun onAdLoaded(data: ContentAd) {
                logZ("onAdLoaded")
                interstitialAdValue = data
                lifecycleOwner.lifecycleScope.launch {
                    adInterstitialState.emit(AdInterstitialState.Loaded)
                }
                invokeAdListener { it.onAdLoaded(data) }
            }


            override fun onAdFailedToShow(adError: AdError) {
                logZ("onAdFailedToShow ${adError.message}")
                AdmobManager.adsFullScreenDismiss()
                invokeAdListener { it.onNextAction() }
                invokeAdListener { it.onAdFailedToShow(adError) }
                dialogLoading.dismiss()
                cancelLoadingJob()
                lifecycleOwner.lifecycleScope.launch {
                    adInterstitialState.emit(AdInterstitialState.ShowFail)
                }
            }

            override fun onNextAction() {
                logZ("onNextAction")
                AdmobManager.adsFullScreenDismiss()
                dialogLoading.dismiss()
                cancelLoadingJob()
                invokeAdListener { it.onNextAction() }
            }

            override fun onAdClose() {
                logZ("onAdClose")
                AdmobManager.adsFullScreenDismiss()
                dialogLoading.dismiss()
                cancelLoadingJob()
                invokeAdListener { it.onAdClose() }
            }

            override fun onInterstitialShow() {
                logZ("onInterstitialShow")
                AdmobManager.adsShowFullScreen()
                lifecycleOwner.lifecycleScope.launch {
                    adInterstitialState.emit(AdInterstitialState.Showed)
                }
                if (config.canReloadAds) {
                    requestAds(InterstitialAdParam.Request)
                }
            }

            override fun onAdClicked() {
                logZ("onAdClicked")
                invokeAdListener { it.onAdClicked() }
            }

            override fun onAdImpression() {
                logZ("onAdImpression")
                invokeAdListener { it.onAdImpression() }
            }

        }
    }

    private fun cancelLoadingJob() {
        loadingJob?.cancel()
        loadingJob = null
    }

    companion object {
        private val TAG = InterstitialAdHelper::class.simpleName
    }
}