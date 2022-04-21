package com.oneclean.android.booster.ui.popup

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.oneclean.android.booster.OneCleanerApplication
import com.oneclean.android.booster.R
import com.oneclean.android.booster.utils.logd
import com.permissionx.guolindev.dialog.RationaleDialog

class RequestPermissionPopup(
    context: Context,
    val message: String,
    private val permissions: List<String>
) : RationaleDialog(
    context,
    R.style.CustomDialog
) {

    private lateinit var tvClose: ImageView
    private lateinit var tvRequest: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.popup_permission)

        tvClose = findViewById(R.id.tv_close)
        tvRequest = findViewById(R.id.tv_request)

        window?.let {
            val param = it.attributes
            val width = (context.resources.displayMetrics.widthPixels * 0.8).toInt()
            val height = param.height
            it.setLayout(width, height)
        }
    }

    override fun getNegativeButton(): View {
        return tvClose
    }

    override fun getPositiveButton(): View {
        return tvRequest
    }

    override fun getPermissionsToRequest(): List<String> {
        return permissions
    }
}