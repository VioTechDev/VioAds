package com.ads.admob.config

import androidx.annotation.IntDef
/**
 * Created by ViO on 16/03/2024.
 */
@IntDef(NetworkProvider.ADMOB, NetworkProvider.MAX)
annotation class NetworkProvider {
    companion object {
        const val ADMOB = 0
        const val MAX = 1
        const val MIX = 3
    }
}