package com.oneclean.android.booster.ui.base

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.os.Build
import android.os.Bundle
import android.os.Environment
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import com.oneclean.android.booster.ui.popup.RequestPermissionPopup
import com.oneclean.android.booster.utils.initWindow
import com.permissionx.guolindev.PermissionX

abstract class BaseActivity : AppCompatActivity {

    companion object{
        var lastActivity: AppCompatActivity? = null
    }

    constructor() : super()

    constructor(@LayoutRes contentLayoutId: Int) : super(contentLayoutId)


    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initWindow(window)
        lastActivity = this
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    }

    /**
     * 请求权限
     *
     * @param success 当权限获取成功时执行
     * @param fail 获取权限失败执行
     * */
    protected fun requestPermission(
        success: (grantedList: List<String>) -> Unit,
        fail: (deniedList: List<String>) -> Unit,
        for11RequestFail: (() -> Unit)? = null
    ) {
        PermissionX.init(this)
            .permissions(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.INTERNET
            )
            .explainReasonBeforeRequest()
            .onExplainRequestReason { scope, deniedList ->
                scope.showRequestReasonDialog(RequestPermissionPopup(this, "message", deniedList))
            }
            .onForwardToSettings { scope, deniedList ->
                scope.showForwardToSettingsDialog(
                    deniedList,
                    "You need to manually open the permission in the application settings",
                    "I understand",
                    "Cancel"
                )
            }
            .request { allGranted, grantedList, deniedList ->
                if (allGranted) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && !Environment.isExternalStorageManager()) {
                        if (for11RequestFail != null)
                            for11RequestFail()
                    } else {
                        success(grantedList)
                    }
                } else {
                    fail(deniedList)
                }
            }
    }
}