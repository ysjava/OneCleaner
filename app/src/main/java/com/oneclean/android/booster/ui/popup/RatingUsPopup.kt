package com.oneclean.android.booster.ui.popup

import android.content.Context
import android.content.Intent
import android.net.Uri
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
            val intent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse(
                    "https://play.google.com/store/apps/details?id=com.example.android")
                setPackage("com.android.vending")
            }
            context.startActivity(intent)

            delayDismiss(300)
        }

        findViewById<ImageView>(R.id.iv_cancel).setOnClickListener {
            delayDismiss(200)
        }
    }
}