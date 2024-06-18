package com.ads.admob.listener

import com.ads.admob.data.ContentAd

/**
 * Created by ViO on 16/03/2024.
 */
interface AppOpenAdCallBack : ViOAdCallback<ContentAd> {
    fun onAppOpenAdShow()
    fun onAppOpenAdClose()
}