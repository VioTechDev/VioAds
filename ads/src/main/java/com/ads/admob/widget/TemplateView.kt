// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
package com.ads.admob.widget

import android.content.Context
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.text.TextUtils
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import com.ads.control.R
import com.google.android.gms.ads.nativead.MediaView
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdView

/**
 * Base class for a template view. *
 */
class TemplateView : FrameLayout {
    private var templateType = 0
    private var styles: NativeTemplateStyle? = null
    private var nativeAd: NativeAd? = null
    var nativeAdView: NativeAdView? = null
        private set
    private var primaryView: TextView? = null
    private var secondaryView: TextView? = null
    private var ratingBar: RatingBar? = null
    private var tertiaryView: TextView? = null
    private var iconView: ImageView? = null
    private var mediaView: MediaView? = null
    private var callToActionView: Button? = null
    private var background: ConstraintLayout? = null

    constructor(context: Context?) : super(context!!)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initView(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        initView(context, attrs)
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    constructor(
        context: Context,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes) {
        initView(context, attrs)
    }

    fun setStyles(styles: NativeTemplateStyle?) {
        this.styles = styles
        applyStyles()
    }

    private fun applyStyles() {
        val mainBackground: ColorDrawable? = styles?.mainBackgroundColor
        if (mainBackground != null) {
            background!!.background = mainBackground
            if (primaryView != null) {
                primaryView!!.background = mainBackground
            }
            if (secondaryView != null) {
                secondaryView!!.background = mainBackground
            }
            if (tertiaryView != null) {
                tertiaryView!!.background = mainBackground
            }
        }
        val primary: Typeface? = styles?.primaryTextTypeface
        if (primary != null && primaryView != null) {
            primaryView!!.setTypeface(primary)
        }
        val secondary: Typeface? = styles?.secondaryTextTypeface
        if (secondary != null && secondaryView != null) {
            secondaryView!!.setTypeface(secondary)
        }
        val tertiary: Typeface? = styles?.tertiaryTextTypeface
        if (tertiary != null && tertiaryView != null) {
            tertiaryView!!.setTypeface(tertiary)
        }
        val ctaTypeface: Typeface? = styles?.callToActionTextTypeface
        if (ctaTypeface != null && callToActionView != null) {
            callToActionView!!.setTypeface(ctaTypeface)
        }
        val primaryTypefaceColor: Int? = styles?.primaryTextTypefaceColor
        if (primaryTypefaceColor != null) {
            if (primaryTypefaceColor > 0 && primaryView != null) {
                primaryView!!.setTextColor(primaryTypefaceColor)
            }
        }
        val secondaryTypefaceColor: Int? = styles?.secondaryTextTypefaceColor
        if (secondaryTypefaceColor != null) {
            if (secondaryTypefaceColor > 0 && secondaryView != null) {
                secondaryView!!.setTextColor(secondaryTypefaceColor)
            }
        }
        val tertiaryTypefaceColor: Int? = styles?.tertiaryTextTypefaceColor
        if (tertiaryTypefaceColor != null) {
            if (tertiaryTypefaceColor > 0 && tertiaryView != null) {
                tertiaryView!!.setTextColor(tertiaryTypefaceColor)
            }
        }
        val ctaTypefaceColor: Int? = styles?.callToActionTypefaceColor
        if (ctaTypefaceColor != null) {
            if ((ctaTypefaceColor > 0) && (callToActionView != null)) {
                callToActionView!!.setTextColor(ctaTypefaceColor)
            }
        }
        val ctaTextSize: Float? = styles?.callToActionTextSize
        if (ctaTextSize != null) {
            if (ctaTextSize > 0 && callToActionView != null) {
                callToActionView!!.textSize = ctaTextSize
            }
        }
        val primaryTextSize: Float? = styles?.primaryTextSize
        if (primaryTextSize != null) {
            if (primaryTextSize > 0 && primaryView != null) {
                primaryView!!.textSize = primaryTextSize
            }
        }
        val secondaryTextSize: Float? = styles?.secondaryTextSize
        if (secondaryTextSize != null) {
            if (secondaryTextSize > 0 && secondaryView != null) {
                secondaryView!!.textSize = secondaryTextSize
            }
        }
        val tertiaryTextSize: Float? = styles?.tertiaryTextSize
        if (tertiaryTextSize != null) {
            if (tertiaryTextSize > 0 && tertiaryView != null) {
                tertiaryView!!.textSize = tertiaryTextSize
            }
        }
        val ctaBackground: ColorDrawable? = styles?.callToActionBackgroundColor
        if (ctaBackground != null && callToActionView != null) {
            callToActionView!!.background = ctaBackground
        }
        val primaryBackground: ColorDrawable? = styles?.primaryTextBackgroundColor
        if (primaryBackground != null && primaryView != null) {
            primaryView!!.background = primaryBackground
        }
        val secondaryBackground: ColorDrawable? = styles?.secondaryTextBackgroundColor
        if (secondaryBackground != null && secondaryView != null) {
            secondaryView!!.background = secondaryBackground
        }
        val tertiaryBackground: ColorDrawable? = styles?.tertiaryTextBackgroundColor
        if (tertiaryBackground != null && tertiaryView != null) {
            tertiaryView!!.background = tertiaryBackground
        }
        invalidate()
        requestLayout()
    }

    private fun adHasOnlyStore(nativeAd: NativeAd): Boolean {
        val store = nativeAd.store
        val advertiser = nativeAd.advertiser
        return !TextUtils.isEmpty(store) && TextUtils.isEmpty(advertiser)
    }

    fun setNativeAd(nativeAd: NativeAd) {
        this.nativeAd = nativeAd
        val store = nativeAd.store
        val advertiser = nativeAd.advertiser
        val headline = nativeAd.headline
        val body = nativeAd.body
        val cta = nativeAd.callToAction
        val starRating = nativeAd.starRating
        val icon = nativeAd.icon
        val secondaryText: String?
        nativeAdView!!.callToActionView = callToActionView
        nativeAdView!!.headlineView = primaryView
        nativeAdView!!.mediaView = mediaView
        secondaryView!!.visibility = VISIBLE
        if (adHasOnlyStore(nativeAd)) {
            nativeAdView!!.storeView = secondaryView
            secondaryText = store
        } else if (!TextUtils.isEmpty(advertiser)) {
            nativeAdView!!.advertiserView = secondaryView
            secondaryText = advertiser
        } else {
            secondaryText = ""
        }
        primaryView!!.text = headline
        callToActionView!!.text = cta

        //  Set the secondary view to be the star rating if available.
        if (starRating != null && starRating > 0) {
            secondaryView!!.visibility = GONE
            ratingBar!!.visibility = VISIBLE
            ratingBar!!.max = 5
            nativeAdView!!.starRatingView = ratingBar
        } else {
            secondaryView!!.text = secondaryText
            secondaryView!!.visibility = VISIBLE
            ratingBar!!.visibility = GONE
        }
        if (icon != null) {
            iconView!!.visibility = VISIBLE
            iconView!!.setImageDrawable(icon.drawable)
        } else {
            iconView!!.visibility = GONE
        }
        if (tertiaryView != null) {
            tertiaryView!!.text = body
            nativeAdView!!.bodyView = tertiaryView
        }
        nativeAdView!!.setNativeAd(nativeAd)
    }

    /**
     * To prevent memory leaks, make sure to destroy your ad when you don't need it anymore. This
     * method does not destroy the template view.
     * https://developers.google.com/admob/android/native-unified#destroy_ad
     */
    fun destroyNativeAd() {
        nativeAd!!.destroy()
    }

    val templateTypeName: String
        get() {
            if (templateType == R.layout.gnt_custom_medium_template_view) {
                return MEDIUM_TEMPLATE
            } else if (templateType == R.layout.gnt_small_template_view) {
                return SMALL_TEMPLATE
            }
            return ""
        }

    private fun initView(context: Context, attributeSet: AttributeSet?) {
        val attributes =
            context.theme.obtainStyledAttributes(attributeSet, R.styleable.TemplateView, 0, 0)
        templateType = try {
            attributes.getResourceId(
                R.styleable.TemplateView_gnt_template_type, R.layout.gnt_custom_medium_template_view
            )
        } finally {
            attributes.recycle()
        }
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        inflater.inflate(templateType, this)
    }

    public override fun onFinishInflate() {
        super.onFinishInflate()
        nativeAdView = findViewById<NativeAdView>(R.id.native_ad_view)
        primaryView = findViewById<TextView>(R.id.primary)
        secondaryView = findViewById<TextView>(R.id.secondary)
        tertiaryView = findViewById<TextView>(R.id.body)
        ratingBar = findViewById<RatingBar>(R.id.rating_bar)
        ratingBar?.setEnabled(false)
        callToActionView = findViewById<Button>(R.id.cta)
        iconView = findViewById<ImageView>(R.id.icon)
        mediaView = findViewById<MediaView>(R.id.media_view)
        background = findViewById<ConstraintLayout>(R.id.background)
    }

    companion object {
        private const val MEDIUM_TEMPLATE = "medium_template"
        private const val SMALL_TEMPLATE = "small_template"
    }
}
