package com.ads.admob.helper.adnative

import android.app.Activity
import android.view.View
import android.widget.FrameLayout
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.ads.admob.admob.AdmobFactory
import com.ads.admob.data.ContentAd
import com.ads.admob.helper.AdOptionVisibility
import com.ads.admob.helper.AdsHelper
import com.ads.admob.helper.adnative.params.AdNativeState
import com.ads.admob.helper.adnative.params.NativeAdParam
import com.ads.admob.listener.NativeAdCallback
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.nativead.NativeAd
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.atomic.AtomicInteger

/**
 * Created by ViO on 16/03/2024.
 */
class NativeAdHelper(
    private val activity: Activity,
    private val lifecycleOwner: LifecycleOwner,
    private val config: NativeAdConfig
) : AdsHelper<NativeAdConfig, NativeAdParam>(activity, lifecycleOwner, config) {
    private val adNativeState: MutableStateFlow<AdNativeState> =
        MutableStateFlow(if (canRequestAds()) AdNativeState.None else AdNativeState.Fail)
    private val resumeCount: AtomicInteger = AtomicInteger(0)
    private val listAdCallback: CopyOnWriteArrayList<NativeAdCallback> = CopyOnWriteArrayList()
    private var flagEnableReload = config.canReloadAds
    private var shimmerLayoutView: ShimmerFrameLayout? = null
    private var nativeContentView: FrameLayout? = null
    private var isRequestValid = true
    var adVisibility: AdOptionVisibility = AdOptionVisibility.GONE

    var nativeAd: ContentAd? = null
        private set

    init {
        registerAdListener(getDefaultCallback())
        lifecycleEventState.onEach {
            if (it == Lifecycle.Event.ON_CREATE) {
                if (!canRequestAds()) {
                    nativeContentView?.checkAdVisibility(false)
                    shimmerLayoutView?.checkAdVisibility(false)
                }
            }
            if (it == Lifecycle.Event.ON_RESUME) {
                if (!canShowAds() && isActiveState()) {
                    cancel()
                }
            }
        }.launchIn(lifecycleOwner.lifecycleScope)
        //Request when resume
        lifecycleEventState.debounce(300).onEach { event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                resumeCount.incrementAndGet()
                logZ("Resume repeat ${resumeCount.get()} times")
            }
            if (event == Lifecycle.Event.ON_RESUME && resumeCount.get() > 1 && nativeAd != null && canRequestAds() && canReloadAd() && isActiveState()) {
                if (!isRequestValid) {
                    isRequestValid = true
                    return@onEach
                }
                requestAds(NativeAdParam.Request)
            }
        }.launchIn(lifecycleOwner.lifecycleScope)
        //for action resume or init
        adNativeState
            .onEach { logZ("adNativeState(${it::class.java.simpleName})") }
            .launchIn(lifecycleOwner.lifecycleScope)
        adNativeState.onEach { adsParam ->
            handleShowAds(adsParam)
        }.launchIn(lifecycleOwner.lifecycleScope)
    }

    fun setShimmerLayoutView(shimmerLayoutView: ShimmerFrameLayout) = apply {
        kotlin.runCatching {
            this.shimmerLayoutView = shimmerLayoutView
            if (lifecycleOwner.lifecycle.currentState in Lifecycle.State.CREATED..Lifecycle.State.RESUMED) {
                if (!canRequestAds()) {
                    shimmerLayoutView.checkAdVisibility(false)
                }
            }
        }
    }

    fun setNativeContentView(nativeContentView: FrameLayout) = apply {
        kotlin.runCatching {
            this.nativeContentView = nativeContentView
            if (lifecycleOwner.lifecycle.currentState in Lifecycle.State.CREATED..Lifecycle.State.RESUMED) {
                if (!canRequestAds()) {
                    nativeContentView.checkAdVisibility(false)
                }
            }
        }
    }

    @Deprecated("replace with flagEnableReload")
    fun setEnableReload(isEnable: Boolean) {
        flagEnableReload = isEnable
    }

    private fun handleShowAds(adsParam: AdNativeState) {
        nativeContentView?.checkAdVisibility(adsParam !is AdNativeState.Cancel && canShowAds())
        shimmerLayoutView?.checkAdVisibility(adsParam is AdNativeState.Loading)
        when (adsParam) {
            is AdNativeState.Loaded -> {
                if (nativeContentView != null && shimmerLayoutView != null) {
                    AdmobFactory.INSTANCE.populateNativeAdView(
                        activity,
                        adsParam.adNative,
                        config.getLayoutIdByMediationNativeAd(adsParam.adNative),
                        nativeContentView!!,
                        shimmerLayoutView,
                        invokeListenerAdCallback()
                    )
                }
            }

            else -> Unit
        }
    }

    @Deprecated("Using cancel()")
    fun resetState() {
        logZ("resetState()")
        cancel()
    }

    fun getAdNativeState(): Flow<AdNativeState> {
        return adNativeState.asStateFlow()
    }

    private fun createNativeAds(activity: Activity) {
        if (canRequestAds()) {
            AdmobFactory.INSTANCE
                .requestNativeAd(context = activity, config.idAds, invokeListenerAdCallback())
        }
    }


    private fun getDefaultCallback(): NativeAdCallback {
        return object : NativeAdCallback {
            override fun populateNativeAd() {
            }

            override fun onAdLoaded(data: ContentAd) {
                if (isActiveState()) {
                    this@NativeAdHelper.nativeAd = data
                    lifecycleOwner.lifecycleScope.launch {
                        adNativeState.emit(AdNativeState.Loaded(data))
                    }
                    logZ("onNativeAdLoaded")
                } else {
                    logInterruptExecute("onNativeAdLoaded")
                }
            }

            override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                if (isActiveState()) {
                    if (nativeAd == null) {
                        lifecycleOwner.lifecycleScope.launch {
                            adNativeState.emit(AdNativeState.Fail)
                        }
                    }
                    logZ("onAdFailedToLoad")
                } else {
                    logInterruptExecute("onAdFailedToLoad")
                }
            }

            override fun onAdClicked() {
                logZ("Native onAdClick")
            }

            override fun onAdImpression() {
                isRequestValid = lifecycleOwner.lifecycle.currentState == Lifecycle.State.RESUMED
                logZ("Native onAdImpression")
            }

            override fun onAdFailedToShow(adError: AdError) {
                logZ("Native onAdFailedToShow")
            }
        }
    }

    override fun requestAds(param: NativeAdParam) {
        lifecycleOwner.lifecycleScope.launch {
            if (canRequestAds()) {
                if (adNativeState.value == AdNativeState.Loading) return@launch
                logZ("requestAds($param)")
                when (param) {
                    is NativeAdParam.Request -> {
                        flagActive.compareAndSet(false, true)
                        if (nativeAd == null) {
                            adNativeState.emit(AdNativeState.Loading)
                        }
                        createNativeAds(activity)
                    }

                    is NativeAdParam.Ready -> {
                        flagActive.compareAndSet(false, true)
                        nativeAd = param.nativeAd
                        adNativeState.emit(AdNativeState.Loaded(param.nativeAd))
                    }
                }
            } else {
                if (!isOnline() && nativeAd == null) {
                    cancel()
                }
            }
        }
    }

    override fun cancel() {
        logZ("cancel() called")
        flagActive.compareAndSet(true, false)
        lifecycleOwner.lifecycleScope.launch {
            adNativeState.emit(AdNativeState.Cancel)
        }
    }

    fun registerAdListener(adCallback: NativeAdCallback) {
        this.listAdCallback.add(adCallback)
    }

    fun unregisterAdListener(adCallback: NativeAdCallback) {
        this.listAdCallback.remove(adCallback)
    }

    fun unregisterAllAdListener() {
        this.listAdCallback.clear()
    }

    private fun invokeAdListener(action: (adCallback: NativeAdCallback) -> Unit) {
        listAdCallback.forEach(action)
    }

    private fun invokeListenerAdCallback(): NativeAdCallback {
        return object : NativeAdCallback {
            override fun populateNativeAd() {
                invokeAdListener { it.populateNativeAd() }
            }

            override fun onAdLoaded(data: ContentAd) {
                invokeAdListener {
                    it.onAdLoaded(data)
                }
            }

            override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                invokeAdListener { it.onAdFailedToLoad(loadAdError) }
            }

            override fun onAdClicked() {
                invokeAdListener { it.onAdClicked() }
            }

            override fun onAdImpression() {
                invokeAdListener { it.onAdImpression() }
            }

            override fun onAdFailedToShow(adError: AdError) {
                invokeAdListener { it.onAdFailedToShow(adError) }
            }

        }
    }

    /**
     * Adjusts the visibility of the [View] based on the provided visibility state and
     * the configured ad visibility option.
     *
     * @param isVisible A boolean indicating whether the view should be set to visible.
     * @see AdOptionVisibility
     */
    private fun View.checkAdVisibility(isVisible: Boolean) {
        visibility = if (isVisible) View.VISIBLE
        else when (adVisibility) {
            AdOptionVisibility.GONE -> View.GONE
            AdOptionVisibility.INVISIBLE -> View.INVISIBLE
        }
    }
}
