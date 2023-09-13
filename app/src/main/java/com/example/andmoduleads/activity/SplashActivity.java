package com.example.andmoduleads.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.ads.control.admob.AppOpenManager;
import com.ads.control.ads.VioAdmob;
import com.ads.control.ads.VioAdmobCallback;
import com.ads.control.ads.wrapper.ApInterstitialAd;
import com.ads.control.config.VioAdmobConfig;
import com.ads.control.ads.VioAdmobInitCallback;
import com.ads.control.ads.wrapper.ApAdError;
import com.ads.control.ads.wrapper.ApNativeAd;
import com.ads.control.billing.AppPurchase;
import com.ads.control.funtion.BillingListener;
import com.example.andmoduleads.BuildConfig;
import com.example.andmoduleads.R;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.appopen.AppOpenAd;

import java.util.ArrayList;
import java.util.List;

public class SplashActivity extends AppCompatActivity {

    private static final String TAG = "AperoAd";
    private List<String> list = new ArrayList<>();
    private String idAdSplash;
    private Boolean isFirst = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        if (VioAdmob.getInstance().getMediationProvider() == VioAdmobConfig.PROVIDER_ADMOB)
            idAdSplash = BuildConfig.ad_interstitial_splash;
        AppPurchase.getInstance().setBillingListener(new BillingListener() {
            @Override
            public void onInitBillingFinished(int code) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        loadSplash();
                        //loadOpenAppAds();
                        //loadInterPriorityAd();
                        //load3InterSplash();
//                        loa3InterAlterlate();
                    }
                });
            }
        }, 5000);

        AppPurchase.getInstance().setEventConsumePurchaseTest(findViewById(R.id.txtLoading));
    }


    VioAdmobCallback adCallback = new VioAdmobCallback() {
        @Override
        public void onAdFailedToLoad(@Nullable ApAdError i) {
            super.onAdFailedToLoad(i);
            Log.d(TAG, "onAdLoaded");
        }

        @Override
        public void onAdLoaded() {
            super.onAdLoaded();
            Log.d(TAG, "onAdLoaded");
        }

        @Override
        public void onNativeAdLoaded(@NonNull ApNativeAd nativeAd) {
            super.onNativeAdLoaded(nativeAd);
        }

        @Override
        public void onNextAction() {
            super.onNextAction();
            Log.d(TAG, "onNextAction");
            startMain();
        }

        @Override
        public void onAdSplashReady() {
            super.onAdSplashReady();

        }

        @Override
        public void onAdClosed() {
            super.onAdClosed();
            Log.d(TAG, "onAdClosed");
        }

        @Override
        public void onAdImpression() {
            super.onAdImpression();
            Log.e(TAG, "onAdImpression: ");
        }
    };

    private void loadSplash() {
        Log.d(TAG, "onCreate: show splash ads");
        VioAdmob.getInstance().setInitCallback(new VioAdmobInitCallback() {
            @Override
            public void initAdSuccess() {
                VioAdmob.getInstance().loadSplashInterstitialAds(SplashActivity.this, idAdSplash, 30000, 5000, true, adCallback);
            }
        });

        //loadAdmobAd();
    }

    private void startMain() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isFirst) {
            isFirst  = false;
            return;
        }

        VioAdmob.getInstance().onCheckShowSplashWhenFail(this, adCallback, 1000);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.e(TAG, "Splash onPause: ");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.e(TAG, "Splash onStop: ");
    }

    @Override
    protected void onDestroy() {
//        AppOpenManager.getInstance().removeFullScreenContentCallback();
        super.onDestroy();
    }
}