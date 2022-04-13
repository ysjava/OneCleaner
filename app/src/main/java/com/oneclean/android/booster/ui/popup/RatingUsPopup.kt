package com.oneclean.android.booster.ui.popup

import android.content.Context
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.lxj.xpopup.core.CenterPopupView
import com.oneclean.android.booster.R

class RatingUsPopup(context: Context) : CenterPopupView(context) {
    override fun getImplLayoutId(): Int {
        return R.layout.dialog_rating_us
    }

    override fun onCreate() {
        super.onCreate()
        findViewById<TextView>(R.id.tv_sure).setOnClickListener {
            Toast.makeText(context, "感谢评价！", Toast.LENGTH_SHORT).show()
            delayDismiss(300)
        }

        findViewById<ImageView>(R.id.iv_cancel).setOnClickListener {
            delayDismiss(200)
        }
    }
}