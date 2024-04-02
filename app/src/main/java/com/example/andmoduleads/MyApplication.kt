package com.example.andmoduleads;

import android.app.Application;


public class MyApplication extends Application {

    private final String APPSFLYER_TOKEN = "2PUNpdyDTkedZTgeKkWCyB";
    private final String ADJUST_TOKEN = "cc4jvudppczk";
    private final String EVENT_PURCHASE_ADJUST = "gzel1k";
    private final String EVENT_AD_IMPRESSION_ADJUST = "gzel1k";
    private final String TAG = "MainApplication";
    private static MyApplication context;

    public static MyApplication getApplication() {
        return context;
    }



    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
    }
}
