package com.ads.admob.helper.adnative

import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ads.admob.admob.AdmobFactory
import com.ads.admob.data.ContentAd
import com.ads.admob.listener.NativeAdCallback
import com.ads.admob.widget.RecyclerViewAdapterWrapper
import com.ads.control.R
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.LoadAdError


class AdmobNativeAdAdapter(private val nativeAdapterConfig: NativeAdapterConfig) :
    RecyclerViewAdapterWrapper(nativeAdapterConfig.adapter) {
    init {
        setSpanAds()
        //assertConfig()
    }

    private fun assertConfig() {
        //if user set span ads
        val nCol: Int = nativeAdapterConfig.gridLayoutManager.spanCount
        require(nativeAdapterConfig.adItemInterval % nCol == 0) {
            String.format(
                "The adItemInterval (%d) is not divisible by number of columns in GridLayoutManager (%d)",
                nativeAdapterConfig.adItemInterval,
                nCol
            )
        }
    }

    private fun convertAdPosition2OrgPosition(position: Int): Int {
        return if (nativeAdapterConfig.isRepeat) {
            position - (position + (nativeAdapterConfig.adItemInterval - nativeAdapterConfig.firstPositionNativeApp)) / (nativeAdapterConfig.adItemInterval + 1)
        } else {
            (position) - (nativeAdapterConfig.firstPositionNativeApp + position) / (nativeAdapterConfig.adapter.itemCount + 1)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (isAdPosition(position)) {
            TYPE_FB_NATIVE_ADS
        } else super.getItemViewType(
            convertAdPosition2OrgPosition(position)
        )
    }

    private fun isAdPosition(position: Int): Boolean {
        return if (nativeAdapterConfig.isRepeat) {
            if (position <= nativeAdapterConfig.firstPositionNativeApp) {
                position == nativeAdapterConfig.firstPositionNativeApp
            } else {
                (position - nativeAdapterConfig.firstPositionNativeApp) % (nativeAdapterConfig.adItemInterval + 1) == 0
            }
        } else {
            position == nativeAdapterConfig.firstPositionNativeApp
        }
    }

    override fun getItemCount(): Int {
        val realCount = super.getItemCount()
        return if (nativeAdapterConfig.isRepeat) {
            realCount + realCount / nativeAdapterConfig.adItemInterval
        } else {
            realCount
        }
    }

    private fun onBindAdViewHolder(holder: RecyclerView.ViewHolder) {
        val adHolder = holder as AdViewHolder
        if (nativeAdapterConfig.forceReloadAdOnBind || !adHolder.loaded) {
            AdmobFactory.INSTANCE.requestNativeAd(
                holder.itemView.context,
                nativeAdapterConfig.nativeAdId,
                object : NativeAdCallback {
                    override fun populateNativeAd() {

                    }

                    override fun onAdLoaded(data: ContentAd) {
                        AdmobFactory.INSTANCE.populateNativeAdView(
                            holder.itemView.context,
                            data,
                            nativeAdapterConfig.nativeContentView,
                            holder.nativeContentView,
                            holder.shimmerLayoutView,
                            object : NativeAdCallback {
                                override fun populateNativeAd() {

                                }

                                override fun onAdLoaded(data: ContentAd) {
                                }

                                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                                }

                                override fun onAdClicked() {
                                }

                                override fun onAdImpression() {
                                }

                                override fun onAdFailedToShow(adError: AdError) {
                                }

                            }
                        )
                    }

                    override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    }

                    override fun onAdClicked() {
                    }

                    override fun onAdImpression() {
                    }

                    override fun onAdFailedToShow(adError: AdError) {
                    }

                },
                nativeAdapterConfig.adPlacement)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (getItemViewType(position) == TYPE_FB_NATIVE_ADS) {
            onBindAdViewHolder(holder)
        } else {
            super.onBindViewHolder(holder, convertAdPosition2OrgPosition(position))
        }
    }

    private fun onCreateAdViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val adLayoutOutline = inflater
            .inflate(nativeAdapterConfig.itemNativeAd, parent, false)
        return AdViewHolder(adLayoutOutline)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == TYPE_FB_NATIVE_ADS) {
            onCreateAdViewHolder(parent)
        } else super.onCreateViewHolder(parent, viewType)
    }

    private fun setSpanAds() {
        val spl = nativeAdapterConfig.gridLayoutManager.spanSizeLookup
        nativeAdapterConfig.gridLayoutManager.spanSizeLookup =
            object : GridLayoutManager.SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int {
                    return if (isAdPosition(position)) {
                        spl.getSpanSize(position)
                    } else 1
                }
            }
    }

    private class AdViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var loaded = false
        var shimmerLayoutView: ShimmerFrameLayout
        var nativeContentView: FrameLayout

        init {
            shimmerLayoutView = view.findViewById(R.id.shimmer_container_native)
            nativeContentView = view.findViewById(R.id.frAds)
        }
    }

    companion object {
        const val TYPE_FB_NATIVE_ADS = 900
        private const val DEFAULT_AD_ITEM_INTERVAL = 4
        fun isValidPhoneNumber(target: CharSequence): Boolean {
            return if (target.length != 10) {
                false
            } else {
                Patterns.PHONE.matcher(target).matches()
            }
        }
    }
}
