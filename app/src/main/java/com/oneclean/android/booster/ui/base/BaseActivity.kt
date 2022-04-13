package com.oneclean.android.booster.ui.base

import android.Manifest
import android.os.Bundle
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import com.oneclean.android.booster.ui.popup.RequestPermissionPopup
import com.oneclean.android.booster.utils.initWindow
import com.permissionx.guolindev.PermissionX

 abstract class BaseActivity : AppCompatActivity {
    constructor():super()

    constructor(@LayoutRes contentLayoutId: Int) : super(contentLayoutId)

     override fun onCreate(savedInstanceState: Bundle?) {
         super.onCreate(savedInstanceState)
         initWindow(window)
     }
     /**
      * 请求权限
      *
      * @param success 当权限获取成功时执行
      * @param fail 获取权限失败执行
      * */
    protected fun requestPermission(
        success: (grantedList: List<String>) -> Unit,
        fail: (deniedList: List<String>) -> Unit
    ) {
        PermissionX.init(this)
            .permissions(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.INTERNET
            )
            .explainReasonBeforeRequest()
            .onExplainRequestReason { scope, deniedList ->
//                scope.showRequestReasonDialog(deniedList, "应用需要用到以下权限才能正常使用", "同意", "取消")
                scope.showRequestReasonDialog(RequestPermissionPopup(this,"message", deniedList))
            }
            .onForwardToSettings{ scope, deniedList ->
                scope.showForwardToSettingsDialog(deniedList,"您需要去应用程序设置当中手动开启权限","我已明白","取消")
            }
            .request { allGranted, grantedList, deniedList ->
                if (allGranted) {
                    success(grantedList)
                } else {
                    fail(deniedList)
                }
            }
    }
}