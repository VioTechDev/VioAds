package com.ads.admob.cmp.interfaces

interface OnConsentResponse {
    fun onResponse(errorMessage: String? = null)
    fun onPolicyRequired(isRequired: Boolean)
}