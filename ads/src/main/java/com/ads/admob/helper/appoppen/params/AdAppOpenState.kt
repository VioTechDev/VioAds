package com.ads.admob.helper.appoppen.params

/**
 * Created by ViO on 16/03/2024.
 */
open class AdAppOpenState {
     object None : AdAppOpenState()
     object Fail : AdAppOpenState()
     object Loading : AdAppOpenState()
     object Loaded : AdAppOpenState()
     object ShowFail : AdAppOpenState()
     object Showed : AdAppOpenState()
     object Cancel : AdAppOpenState()
}