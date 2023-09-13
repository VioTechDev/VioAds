package com.example.andmoduleads.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.ads.control.admob.Admob;
import com.ads.control.admob.AppOpenManager;
import com.ads.control.ads.VioAdmob;
import com.ads.control.ads.VioAdmobCallback;
import com.ads.control.ads.nativeAds.VioNativeAdView;
import com.ads.control.ads.wrapper.ApAdError;
import com.ads.control.ads.wrapper.ApInterstitialAd;
import com.ads.control.ads.wrapper.ApNativeAd;
import com.ads.control.ads.wrapper.ApRewardAd;
import com.ads.control.billing.AppPurchase;
import com.ads.control.config.VioAdmobConfig;
import com.ads.control.dialog.InAppDialog;
import com.ads.control.event.VioAdjust;
import com.ads.control.funtion.AdCallback;
import com.ads.control.funtion.PurchaseListener;
import com.example.andmoduleads.BuildConfig;
import com.example.andmoduleads.MyApplication;
import com.example.andmoduleads.NetworkUtil;
import com.example.andmoduleads.R;
import com.example.andmoduleads.databinding.ActivityMainBinding;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.nativead.NativeAd;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    public static final String PRODUCT_ID = "android.test.purchased";
    private static final String TAG = "MAIN_TEST";
    //adjust
    private static final String EVENT_TOKEN_SIMPLE = "g3mfiw";
    private static final String EVENT_TOKEN_REVENUE = "a4fd35";


    private FrameLayout frAds;
    private NativeAd unifiedNativeAd;
    private ApInterstitialAd mInterstitialAd;
    private ApRewardAd rewardAd;

    private boolean isShowDialogExit = false;

    private String idBanner = "";
    private String idNative = "";
    private String idInter = "";

    private int layoutNativeCustom;
    private VioNativeAdView vioNativeAdView;

    private ActivityMainBinding binding;
    protected boolean isBackgroundRunning = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        vioNativeAdView = findViewById(R.id.aperoNativeAds);


        configMediationProvider();
        VioAdmob.getInstance().setCountClickToShowAds(1);

        AppOpenManager.getInstance().setEnableScreenContentCallback(true);
        AppOpenManager.getInstance().setFullScreenContentCallback(new FullScreenContentCallback() {
            @Override
            public void onAdShowedFullScreenContent() {
                super.onAdShowedFullScreenContent();

            }
        });
        startLoadBanner();
        /**
         * Sample integration native ads
         */
        /*
        AperoAd.getInstance().loadNativeAd(this, idNative, layoutNativeCustom);
        aperoNativeAdView.setLayoutLoading(R.layout.loading_native_medium);
        aperoNativeAdView.setLayoutCustomNativeAd(layoutNativeCustom);
        aperoNativeAdView.loadNativeAd(this, idNative,layoutNativeCustom,R.layout.loading_native_medium);
        */
        vioNativeAdView.loadNativeAd(this, idNative, new VioAdmobCallback() {
            @Override
            public void onAdImpression() {
                super.onAdImpression();
            }
        });


        AppPurchase.getInstance().setPurchaseListener(new PurchaseListener() {
            @Override
            public void onProductPurchased(String productId, String transactionDetails) {
                Log.e("PurchaseListioner", "ProductPurchased:" + productId);
                Log.e("PurchaseListioner", "transactionDetails:" + transactionDetails);
                startActivity(new Intent(MainActivity.this, MainActivity.class));
                finish();
            }

            @Override
            public void displayErrorMessage(String errorMsg) {
                Log.e("PurchaseListioner", "displayErrorMessage:" + errorMsg);
            }

            @Override
            public void onUserCancelBilling() {

            }
        });

        loadAdInterstitial();

        findViewById(R.id.btShowAds).setOnClickListener(v -> {
                    if (mInterstitialAd.isReady()) {

                        ApInterstitialAd inter = VioAdmob.getInstance().getInterstitialAds(this, idInter);

                        VioAdmob.getInstance().showInterstitialAdByTimes(this, mInterstitialAd, new VioAdmobCallback() {
                            @Override
                            public void onNextAction() {
                                Log.i(TAG, "onNextAction: start content and finish main");
                                startActivity(new Intent(MainActivity.this, ContentActivity.class));
                            }

                            @Override
                            public void onAdFailedToShow(@Nullable ApAdError adError) {
                                super.onAdFailedToShow(adError);
                                Log.i(TAG, "onAdFailedToShow:" + adError.getMessage());
                            }

                            @Override
                            public void onInterstitialShow() {
                                super.onInterstitialShow();
                                Log.d(TAG, "onInterstitialShow");
                            }
                        }, true);
                    } else {
                        Toast.makeText(this, "start loading ads", Toast.LENGTH_SHORT).show();
                        loadAdInterstitial();
                    }
                }
        );

        findViewById(R.id.btnShowAdsSametime).setOnClickListener(view ->
                VioAdmob.getInstance().forceShowInterstitialPriority(
                        MainActivity.this,
                        MyApplication.getApplication().getStorageCommon().getApInterstitialPriorityAd(),
                        new VioAdmobCallback() {
                            @Override
                            public void onNextAction() {
                                Log.i(TAG, "onAdClosed: start content and finish main");
                                startActivity(new Intent(MainActivity.this, SimpleListActivity.class));
                            }

                            @Override
                            public void onAdFailedToShow(@Nullable ApAdError adError) {
                                super.onAdFailedToShow(adError);
                                assert adError != null;
                                Log.e(TAG, "onAdFailedToShow: " + adError.getMessage());
                            }
                        },
                        true
                )
        );

        findViewById(R.id.btForceShowAds).setOnClickListener(v -> {
            if (mInterstitialAd.isReady()) {
                VioAdmob.getInstance().forceShowInterstitial(this, mInterstitialAd, new VioAdmobCallback() {
                    @Override
                    public void onNextAction() {
                        Log.i(TAG, "onAdClosed: start content and finish main");
                        startActivity(new Intent(MainActivity.this, SimpleListActivity.class));
                    }

                    @Override
                    public void onAdFailedToShow(@Nullable ApAdError adError) {
                        super.onAdFailedToShow(adError);
                        Log.i(TAG, "onAdFailedToShow:" + adError.getMessage());
                    }

                    @Override
                    public void onInterstitialShow() {
                        super.onInterstitialShow();
                        Log.d(TAG, "onInterstitialShow");
                    }

                    @Override
                    public void onAdImpression() {
                        super.onAdImpression();
                        Log.e(TAG, "onAdImpression:");
                    }
                }, true);
            } else {
                loadAdInterstitial();
            }

        });

        findViewById(R.id.btnShowReward).setOnClickListener(v -> {
            if (rewardAd != null && rewardAd.isReady()) {
                VioAdmob.getInstance().forceShowRewardAd(this, rewardAd, new VioAdmobCallback());
                return;
            }
            rewardAd = VioAdmob.getInstance().getRewardAd(this, BuildConfig.ad_reward);
        });

        Button btnIAP = findViewById(R.id.btIap);
        if (AppPurchase.getInstance().isPurchased()) {
            btnIAP.setText("Consume Purchase");
        } else {
            btnIAP.setText("Purchase");
        }
        btnIAP.setOnClickListener(v -> {
            if (AppPurchase.getInstance().isPurchased()) {
                AppPurchase.getInstance().consumePurchase(AppPurchase.PRODUCT_ID_TEST);
            } else {
                InAppDialog dialog = new InAppDialog(this);
                dialog.setCallback(() -> {
                    AppPurchase.getInstance().purchase(this, PRODUCT_ID);
                    dialog.dismiss();
                });
                dialog.show();
            }
        });


    }

    protected Timer bannerTimer;
    protected final Handler bannerHandler = new Handler();
    protected boolean fistRequestBanner = true;

    private void startLoadBanner() {
        bannerTimer = new Timer();
        TimerTask bannerTimerTask = new TimerTask() {
            @Override
            public void run() {
                bannerHandler.post(() -> {
                    if (isBackgroundRunning) {
                        return;
                    }
                    loadBannerPriority();

                });
            }
        };
        bannerTimer.schedule(
                bannerTimerTask,
                0,
                10000
        );
    }

    private void loadBannerPriority() {
        if (!NetworkUtil.isOnline()) {
            if (fistRequestBanner) {
                binding.frAds.setVisibility(View.GONE);
            }
            return;
        }
        binding.frAds.setVisibility(View.VISIBLE);
        VioAdmob.getInstance().loadBannerPriority(this,
                "ca-app-pub-3940256099942544/6300978111ư",
                "ca-app-pub-3940256099942544/630097811132",
                "ca-app-pub-3940256099942544/6300978111",
                binding.frAds,
                VioAdmob.REQUEST_TYPE.SAME_TIME,
                fistRequestBanner,
                new VioAdmobCallback() {
                }
        );
        fistRequestBanner = false;
    }



    private void configMediationProvider() {
        if (VioAdmob.getInstance().getMediationProvider() == VioAdmobConfig.PROVIDER_ADMOB) {
            idBanner = BuildConfig.ad_banner;
            idNative = BuildConfig.ad_native;
            idInter = BuildConfig.ad_interstitial_splash;
            layoutNativeCustom = com.ads.control.R.layout.custom_native_admod_medium_rate;
        }
    }

    private void loadAdInterstitial() {
        VioAdmob.getInstance().loadPriorityInterstitialAds(
                this,
                MyApplication.getApplication().getStorageCommon().getApInterstitialPriorityAd(),
                new VioAdmobCallback() {
                    @Override
                    public void onInterPriorityLoaded(@Nullable ApInterstitialAd interstitialAd) {
                        super.onInterPriorityLoaded(interstitialAd);
                        Log.e(TAG, "onInterPriorityLoaded: " + interstitialAd + " " + MyApplication.getApplication().getStorageCommon().getApInterstitialPriorityAd().getHighPriorityInterstitialAd());
                    }

                    @Override
                    public void onInterPriorityMediumLoaded(@Nullable ApInterstitialAd interstitialAd) {
                        super.onInterPriorityMediumLoaded(interstitialAd);
                        Log.e(TAG, "onInterPriorityMediumLoaded: " + interstitialAd + " " + MyApplication.getApplication().getStorageCommon().getApInterstitialPriorityAd().getMediumPriorityInterstitialAd());
                    }

                    @Override
                    public void onInterstitialLoad(@Nullable ApInterstitialAd interstitialAd) {
                        super.onInterstitialLoad(interstitialAd);
                        Log.e(TAG, "onInterstitialLoad: " + interstitialAd + " " + MyApplication.getApplication().getStorageCommon().getApInterstitialPriorityAd().getNormalPriorityInterstitialAd());
                    }
                });
    }


    public void onTrackSimpleEventClick(View v) {
        VioAdjust.onTrackEvent(EVENT_TOKEN_SIMPLE);
    }

    public void onTrackRevenueEventClick(View v) {
        VioAdjust.onTrackRevenue(EVENT_TOKEN_REVENUE, 1f, "EUR");
    }


    @Override
    protected void onResume() {
        super.onResume();
        isBackgroundRunning = false;
        loadNativeExit();
    }

    @Override
    protected void onPause() {
        super.onPause();
        isBackgroundRunning = true;
    }

    private void loadNativeExit() {

        if (unifiedNativeAd != null)
            return;
        Admob.getInstance().loadNativeAd(this, BuildConfig.ad_native, new AdCallback() {
            @Override
            public void onUnifiedNativeAdLoaded(NativeAd unifiedNativeAd) {
                MainActivity.this.unifiedNativeAd = unifiedNativeAd;
            }

            @Override
            public void onAdImpression() {
                super.onAdImpression();
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (unifiedNativeAd == null)
            return;

//        DialogExitApp1 dialogExitApp1 = new DialogExitApp1(this, unifiedNativeAd, 1);
//        dialogExitApp1.setDialogExitListener(new DialogExitListener() {
//            @Override
//            public void onExit(boolean exit) {
//                MainActivity.super.onBackPressed();
//            }
//        });
//        dialogExitApp1.setCancelable(false);
//        dialogExitApp1.show();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        AppPurchase.getInstance().handleActivityResult(requestCode, resultCode, data);
        Log.e("onActivityResult", "ProductPurchased:" + data.toString());
        if (AppPurchase.getInstance().isPurchased(this)) {
            findViewById(R.id.btIap).setVisibility(View.GONE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void stopLoadBanner() {
        if (bannerTimer != null) {
            bannerTimer.cancel();
            bannerTimer.purge();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopLoadBanner();
    }
}