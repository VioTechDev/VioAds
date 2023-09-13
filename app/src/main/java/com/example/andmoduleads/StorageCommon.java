package com.example.andmoduleads;

import com.ads.control.ads.wrapper.ApInterstitialPriorityAd;

public class StorageCommon {
    private volatile ApInterstitialPriorityAd apInterstitialPriorityAd;

    synchronized public ApInterstitialPriorityAd getApInterstitialPriorityAd() {
        if (apInterstitialPriorityAd == null)
            apInterstitialPriorityAd = new ApInterstitialPriorityAd(
                    BuildConfig.ad_inter_priority,
                    BuildConfig.ad_inter_medium,
                    BuildConfig.ad_inter_normal);
        return apInterstitialPriorityAd;
    }
}
