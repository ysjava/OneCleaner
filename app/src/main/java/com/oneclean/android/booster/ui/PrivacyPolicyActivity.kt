package com.oneclean.android.booster.ui

import android.os.Bundle
import android.widget.ImageView
import com.oneclean.android.booster.R
import com.oneclean.android.booster.ui.base.BaseActivity

class PrivacyPolicyActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_privacy_policy)

        findViewById<ImageView>(R.id.iv_back).setOnClickListener { finish() }
    }
}