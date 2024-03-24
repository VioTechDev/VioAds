package com.ads.admob.listener

import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.LoadAdError
import com.ads.admob.data.ContentAd
/**
 * Created by ViO on 16/03/2024.
 */
interface ViOAdCallback<T : ContentAd> {
    fun onAdLoaded(data: T)
    fun onAdFailedToLoad(loadAdError: LoadAdError)
    fun onAdClicked()
    fun onAdImpression()
    fun onAdFailedToShow(adError: AdError)
}