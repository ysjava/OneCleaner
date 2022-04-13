package com.oneclean.android.booster.utils

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Rect
import android.os.Build
import android.util.DisplayMetrics
import android.util.Log
import android.view.WindowManager
import com.oneclean.android.booster.OneCleanerApplication
import com.oneclean.android.booster.OneCleanerApplication.Companion.context

/**
 * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
 */
fun dp2px(dpValue: Float,context: Context = OneCleanerApplication.context): Int {
    val scale: Float = context.resources.displayMetrics.density
    return (dpValue * scale + 0.5f).toInt()
}

/**
 * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
 */
fun px2dp(context: Context, pxValue: Float): Int {
    val scale = context.resources.displayMetrics.density
    return (pxValue / scale + 0.5f).toInt()
}

/**
 * 将px值转换为sp值，保证文字大小不变
 *
 * @param pxValue
 * @param fontScale
 * （DisplayMetrics类中属性scaledDensity）
 * @return
 */
fun px2sp(context: Context, pxValue: Float): Int {
    val fontScale = context.resources.displayMetrics.scaledDensity
    return (pxValue / fontScale + 0.5f).toInt()
}

/**
 * 将sp值转换为px值，保证文字大小不变
 *
 * @param spValue
 * @param fontScale
 * （DisplayMetrics类中属性scaledDensity）
 * @return
 */
fun sp2px(spValue: Float,context: Context = OneCleanerApplication.context): Int {
    val fontScale = context.resources.displayMetrics.scaledDensity
    return (spValue * fontScale + 0.5f).toInt()
}


/**
 * 获得屏幕高度
 *
 * @param context
 * @return
 */
fun getScreenWidth(context: Context): Int {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        wm.currentWindowMetrics.bounds.width()
    } else {
        val outMetrics = context.resources.displayMetrics
        outMetrics.widthPixels
    }

}

/**
 * 获得屏幕宽度
 *
 * @param context
 * @return
 */
fun getScreenHeight(context: Context): Int {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        wm.currentWindowMetrics.bounds.height()
    } else {
        val outMetrics = context.resources.displayMetrics
        outMetrics.heightPixels
    }
}

/**
 * 获得状态栏的高度
 *
 * @param context
 * @return
 */
fun getStatusHeight(): Int {
    var height = -1
    val id = context.resources.getIdentifier("status_bar_height", "dimen", "android")
    if (id > 0) {
        //根据资源ID获取响应的尺寸值
        height = context.resources.getDimensionPixelSize(id)
    }

    return height
}

/**
 * 获取底部navigation bar高度
 *
 * @return
 */
fun getNavigationBarHeight(context: Context): Int {
    val resources = context.resources
    val resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android")
    val height = resources.getDimensionPixelSize(resourceId)
    Log.v("dbw", "Navi height:$height")
    return height
}

/**
 * 某些设备手机底部使用虚拟导航按键，某些设备使用物理导航按键
 * 判断是否含有底部navigation bar
 *
 * @return
 */
fun checkDeviceHasNavigationBar(context: Context): Boolean {
    var hasNavigationBar = false
    val rs = context.resources
    val id = rs.getIdentifier("config_showNavigationBar", "bool", "android")
    if (id > 0) {
        hasNavigationBar = rs.getBoolean(id)
    }
    try {
        val systemPropertiesClass = Class.forName("android.os.SystemProperties")
        val m = systemPropertiesClass.getMethod("get", String::class.java)
        val navBarOverride = m.invoke(systemPropertiesClass, "qemu.hw.mainkeys") as String
        if ("1" == navBarOverride) {
            hasNavigationBar = false
        } else if ("0" == navBarOverride) {
            hasNavigationBar = true
        }
    } catch (e: Exception) {
        Log.w("android", e)
    }
    return hasNavigationBar
}

/**
 * 获取当前屏幕截图，包含状态栏
 *
 * @param activity
 * @return
 */
fun snapShotWithStatusBar(activity: Activity): Bitmap? {
    val view = activity.window.decorView
    view.isDrawingCacheEnabled = true
    view.buildDrawingCache()
    val bmp = view.drawingCache
    val width = getScreenWidth(activity)
    val height = getScreenHeight(activity)
    var bp: Bitmap? = null
    bp = Bitmap.createBitmap(bmp, 0, 0, width, height)
    view.destroyDrawingCache()
    return bp
}

/**
 * 获取当前屏幕截图，不包含状态栏
 *
 * @param activity
 * @return
 */
fun snapShotWithoutStatusBar(activity: Activity): Bitmap? {
    val view = activity.window.decorView
    view.isDrawingCacheEnabled = true
    view.buildDrawingCache()
    val bmp = view.drawingCache
    val frame = Rect()
    activity.window.decorView.getWindowVisibleDisplayFrame(frame)
    val statusBarHeight = frame.top
    val width = getScreenWidth(activity)
    val height = getScreenHeight(activity)
    var bp: Bitmap? = null
    bp = Bitmap.createBitmap(
        bmp, 0, statusBarHeight, width, height
                - statusBarHeight
    )
    view.destroyDrawingCache()
    return bp
}