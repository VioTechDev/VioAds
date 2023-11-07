package com.example.andmoduleads.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.ads.control.ads.VioAdmob;
import com.ads.control.ads.VioAdmobCallback;
import com.ads.control.ads.VioAdmobInitCallback;
import com.ads.control.ads.wrapper.ApAdError;
import com.ads.control.ads.wrapper.ApNativeAd;
import com.ads.control.billing.AppPurchase;
import com.ads.control.config.VioAdmobConfig;
import com.ads.control.funtion.BillingListener;
import com.example.andmoduleads.BuildConfig;
import com.example.andmoduleads.R;

import java.util.ArrayList;
import java.util.List;

public class SplashActivity extends AppCompatActivity {

    private static final String TAG = "VioAds";
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
                        load3InterSplash();
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

    private void load3InterSplash() {
        VioAdmob.getInstance().loadSplashInterPriority3SameTime(this,
                BuildConfig.ad_inter_splash_piority,
                BuildConfig.ad_inter_splash_medium,
                BuildConfig.ad_interstitial_splash,
                30000,
                3000,
                true,
                new VioAdmobCallback() {
                    @Override
                    public void onAdSplashPriorityReady() {
                        super.onAdSplashPriorityReady();
                        Log.i(TAG, "onAdSplashHighFloorReady: ");
                        showInter3Sametime();
                    }

                    @Override
                    public void onAdPriorityFailedToLoad(@Nullable ApAdError adError) {
                        super.onAdPriorityFailedToLoad(adError);
                        Log.e(TAG, "onAdHighFloorFailedToLoad: " + adError);
                    }

                    @Override
                    public void onAdSplashPriorityMediumReady() {
                        super.onAdSplashPriorityMediumReady();
                        Log.i(TAG, "onAdSplashHighMediumReady: ");
                        showInter3Sametime();
                    }

                    @Override
                    public void onAdPriorityMediumFailedToLoad(@Nullable ApAdError adError) {
                        super.onAdPriorityMediumFailedToLoad(adError);
                        Log.i(TAG, "onAdMediumFailedToLoad: " + adError);
                    }

                    @Override
                    public void onAdSplashReady() {
                        super.onAdSplashReady();
                        Log.i(TAG, "onAdSplashReady: ");
                        showInter3Sametime();
                    }

                    @Override
                    public void onAdFailedToLoad(@Nullable ApAdError adError) {
                        super.onAdFailedToLoad(adError);
                        Log.e(TAG, "onAdFailedToLoad: ");
                    }

                    @Override
                    public void onNextAction() {
                        super.onNextAction();
                        startMain();
                        Log.i(TAG, "onNextAction:1 ");
                    }
                });
    }

    private void showInter3Sametime() {
        VioAdmob.getInstance().onShowSplashPriority3(SplashActivity.this, new VioAdmobCallback() {
            @Override
            public void onAdClosed() {
                super.onAdClosed();
                startMain();
                Log.e(TAG, "onAdClosed: ");
            }

            @Override
            public void onAdPriorityFailedToShow(@Nullable ApAdError adError) {
                super.onAdPriorityFailedToShow(adError);
                Log.e(TAG, "onAdPriorityFailedToShow: ");
            }

            @Override
            public void onAdPriorityMediumFailedToShow(@Nullable ApAdError adError) {
                super.onAdPriorityMediumFailedToShow(adError);
                Log.e(TAG, "onAdPriorityMediumFailedToShow: ");
            }

            @Override
            public void onAdFailedToShow(@Nullable ApAdError adError) {
                super.onAdFailedToShow(adError);
                Log.e(TAG, "onAdFailedToShow: ");
            }

            @Override
            public void onNextAction() {
                super.onNextAction();
                //startMain();
                Log.e(TAG, "onNextAction: ");
            }
        });
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

        VioAdmob.getInstance().onCheckShowSplashPriority3WhenFail(this, adCallback, 1000);
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