package com.ads.admob.helper.adnative

import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ads.admob.data.ContentAd
import com.ads.admob.helper.adnative.factory.AdmobNativeFactory
import com.ads.admob.helper.adnative.params.NativeAdAdapterParam
import com.ads.admob.listener.NativeAdCallback
import com.ads.admob.widget.RecyclerViewAdapterWrapper
import com.ads.control.R
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.LoadAdError


class AdmobNativeAdAdapter private constructor(private val mParam: NativeAdAdapterParam) :
    RecyclerViewAdapterWrapper(mParam.adapter!!) {
    init {
        assertConfig()
        setSpanAds()
    }

    private fun assertConfig() {
        if (mParam.gridLayoutManager != null) {
            //if user set span ads
            val nCol = mParam.gridLayoutManager!!.spanCount
            require(mParam.adItemInterval % nCol == 0) {
                String.format(
                    "The adItemInterval (%d) is not divisible by number of columns in GridLayoutManager (%d)",
                    mParam.adItemInterval,
                    nCol
                )
            }
        }
    }

    private fun convertAdPosition2OrgPosition(position: Int): Int {
        return position - (position + 1) / (mParam.adItemInterval + 1)
    }

    override fun getItemViewType(position: Int): Int {
        return if (isAdPosition(position)) {
            TYPE_FB_NATIVE_ADS
        } else super.getItemViewType(convertAdPosition2OrgPosition(position))
    }

    private fun isAdPosition(position: Int): Boolean {
        /*if(position==1|| position==4)return true;*/
        return (position + 1) % (mParam.adItemInterval + 1) == 0
    }

    private fun onBindAdViewHolder(holder: RecyclerView.ViewHolder) {
        val adHolder = holder as AdViewHolder
        if (mParam.forceReloadAdOnBind || !adHolder.loaded) {
            AdmobNativeFactory.getInstance().requestNativeAd(
                holder.itemView.context,
                mParam.admobNativeId,
                object : NativeAdCallback {
                    override fun populateNativeAd() {

                    }

                    override fun onAdLoaded(data: ContentAd.AdmobAd.ApNativeAd) {
                        AdmobNativeFactory.getInstance().populateNativeAdView(
                            holder.itemView.context,
                            data.nativeAd,
                            mParam.itemContainerLayoutRes,
                            holder.nativeContentView,
                            holder.shimmerLayoutView,
                            object : NativeAdCallback {
                                override fun populateNativeAd() {

                                }

                                override fun onAdLoaded(data: ContentAd.AdmobAd.ApNativeAd) {
                                }

                                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                                }

                                override fun onAdClicked() {
                                }

                                override fun onAdImpression() {
                                }

                                override fun onAdFailedToShow(adError: AdError) {
                                }

                            })
                    }

                    override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    }

                    override fun onAdClicked() {
                    }

                    override fun onAdImpression() {
                    }

                    override fun onAdFailedToShow(adError: AdError) {
                    }

                })
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
            .inflate(mParam.nativeContentView, parent, false)
        return AdViewHolder(adLayoutOutline)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == TYPE_FB_NATIVE_ADS) {
            onCreateAdViewHolder(parent)
        } else super.onCreateViewHolder(parent, viewType)
    }

    private fun setSpanAds() {
        if (mParam.gridLayoutManager == null) {
            return
        }
        val spl = mParam.gridLayoutManager!!.spanSizeLookup
        mParam.gridLayoutManager?.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return if (isAdPosition(position)) {
                    spl.getSpanSize(position)
                } else 1
            }
        }
    }


    class Builder private constructor(private val mParam: NativeAdAdapterParam) {
        fun adItemInterval(interval: Int): Builder {
            mParam.adItemInterval = interval
            return this
        }

        fun adLayout(@LayoutRes layoutContainerRes: Int, @IdRes itemContainerId: Int): Builder {
            mParam.itemContainerLayoutRes = layoutContainerRes
            mParam.itemContainerId = itemContainerId
            return this
        }
        fun adLayoutView(@LayoutRes layoutContainerRes: Int, @IdRes itemContainerId: Int): Builder {
            mParam.nativeContentView = layoutContainerRes
            mParam.itemContainerId = itemContainerId
            return this
        }

        fun build(): AdmobNativeAdAdapter {
            return AdmobNativeAdAdapter(mParam)
        }

        fun enableSpanRow(layoutManager: GridLayoutManager?): Builder {
            mParam.gridLayoutManager = layoutManager
            return this
        }

        fun adItemIterval(i: Int): Builder {
            mParam.adItemInterval = i
            return this
        }

        fun forceReloadAdOnBind(forced: Boolean): Builder {
            mParam.forceReloadAdOnBind = forced
            return this
        }

        companion object {
            fun with(
                placementId: String = "",
                wrapped: RecyclerView.Adapter<*>?,
                layout: Int
            ): Builder {
                val param = NativeAdAdapterParam()
                param.admobNativeId = placementId
                param.adapter = wrapped as RecyclerView.Adapter<RecyclerView.ViewHolder>
                param.layout = layout

                //default value
                param.adItemInterval = DEFAULT_AD_ITEM_INTERVAL
                param.itemContainerLayoutRes = R.layout.item_admob_native_ad_outline
                param.itemContainerId = R.id.ad_container
                param.forceReloadAdOnBind = true
                return Builder(param)
            }
        }
    }

    private class AdViewHolder internal constructor(view: View) : RecyclerView.ViewHolder(view) {
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
