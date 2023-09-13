package com.example.andmoduleads;

import com.ads.control.admob.Admob;
import com.ads.control.admob.AppOpenManager;
import com.ads.control.ads.VioAdmob;
import com.ads.control.application.VioAdmobMultiDexApplication;
import com.ads.control.applovin.AppLovin;
import com.ads.control.applovin.AppOpenMax;
import com.ads.control.billing.AppPurchase;
import com.ads.control.billing.PurchaseItem;
import com.ads.control.config.AdjustConfig;
import com.ads.control.config.VioAdmobConfig;
import com.example.andmoduleads.activity.MainActivity;
import com.example.andmoduleads.activity.SplashActivity;

import java.util.ArrayList;
import java.util.List;


public class MyApplication extends VioAdmobMultiDexApplication {

    private final String APPSFLYER_TOKEN = "2PUNpdyDTkedZTgeKkWCyB";
    private final String ADJUST_TOKEN = "cc4jvudppczk";
    private final String EVENT_PURCHASE_ADJUST = "gzel1k";
    private final String EVENT_AD_IMPRESSION_ADJUST = "gzel1k";
    private final String TAG = "MainApplication";

    protected StorageCommon storageCommon;
    private static MyApplication context;

    public static MyApplication getApplication() {
        return context;
    }

    public StorageCommon getStorageCommon() {
        return storageCommon;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
        Admob.getInstance().setNumToShowAds(0);

        storageCommon = new StorageCommon();
        initBilling();
        initAds();
    }

    private void initAds() {
        String environment = BuildConfig.env_dev ? VioAdmobConfig.ENVIRONMENT_DEVELOP : VioAdmobConfig.ENVIRONMENT_PRODUCTION;
        vioAdmobConfig = new VioAdmobConfig(this, VioAdmobConfig.PROVIDER_ADMOB, environment);

        // Optional: setup Adjust event
        AdjustConfig adjustConfig = new AdjustConfig(ADJUST_TOKEN);
        adjustConfig.setEventAdImpression(EVENT_AD_IMPRESSION_ADJUST);
        adjustConfig.setEventNamePurchase(EVENT_PURCHASE_ADJUST);
        vioAdmobConfig.setAdjustConfig(adjustConfig);

        // Optional: setup Appsflyer event
//        AppsflyerConfig appsflyerConfig = new AppsflyerConfig(true,APPSFLYER_TOKEN);
//        aperoAdConfig.setAppsflyerConfig(appsflyerConfig);

        // Optional: enable ads resume
        vioAdmobConfig.setIdAdResume(BuildConfig.ads_open_app);
        vioAdmobConfig.setIdAdResumeMedium(BuildConfig.ad_resume_medium);
        vioAdmobConfig.setIdAdResumeHigh(BuildConfig.ad_resume_high);
        vioAdmobConfig.setNumberOfTimesReloadAds(3);

        // Optional: setup list device test - recommended to use
        listTestDevice.add("EC25F576DA9B6CE74778B268CB87E431");
        vioAdmobConfig.setListDeviceTest(listTestDevice);
        vioAdmobConfig.setIntervalInterstitialAd(0);

        VioAdmob.getInstance().init(this, vioAdmobConfig, false);

        // Auto disable ad resume after user click ads and back to app
        Admob.getInstance().setDisableAdResumeWhenClickAds(true);
        AppLovin.getInstance().setDisableAdResumeWhenClickAds(true);
        // If true -> onNextAction() is called right after Ad Interstitial showed
        Admob.getInstance().setOpenActivityAfterShowInterAds(false);

        if (VioAdmob.getInstance().getMediationProvider() == VioAdmobConfig.PROVIDER_ADMOB) {
            AppOpenManager.getInstance().disableAppResumeWithActivity(SplashActivity.class);
        } else {
            AppOpenMax.getInstance().disableAppResumeWithActivity(SplashActivity.class);
        }
        NetworkUtil.initNetwork(this);
    }

    private void initBilling() {
        List<PurchaseItem> listPurchaseItem = new ArrayList<>();
        listPurchaseItem.add(new PurchaseItem(MainActivity.PRODUCT_ID, AppPurchase.TYPE_IAP.PURCHASE));
        AppPurchase.getInstance().initBilling(this, listPurchaseItem);
    }
}
