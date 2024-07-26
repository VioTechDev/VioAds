package com.ads.admob.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.WindowManager
import com.ads.control.R

/**
 * Created by ViO on 16/03/2024.
 */
class LoadingAdsDialog(context: Context) : Dialog(context, R.style.Dialog_FullScreen_Light) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT
        )
        setContentView(R.layout.dialog_prepair_loading_ads)
    }
}