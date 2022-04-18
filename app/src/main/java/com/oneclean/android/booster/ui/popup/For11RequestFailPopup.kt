package com.oneclean.android.booster.ui.popup

import android.content.Context
import com.lxj.xpopup.core.CenterPopupView
import com.oneclean.android.booster.R

class For11RequestFailPopup(context: Context): CenterPopupView(context) {

    override fun getImplLayoutId(): Int {
        return R.layout.popup_for_11_request_fail
    }

    override fun onCreate() {
        super.onCreate()

    }
}