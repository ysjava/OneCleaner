package com.oneclean.android.booster.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Path
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView

/**
 * 简单的圆角ImageView
 * */
class RoundAngleImageView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : AppCompatImageView(context, attrs, defStyleAttr) {
    private var width: Float = 0f
    private var height: Float = 0f
    private val path = Path()

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        this.width = getWidth().toFloat()
        this.height = getHeight().toFloat()
    }

    override fun onDraw(canvas: Canvas) {
        if (width >= 24 && height > 24) {
            val path = path
            //四个圆角
            //移动起点位置
            path.moveTo(24f, 0f)
            //连接直线
            path.lineTo(width - 24, 0f)
            //贝塞尔曲线
            path.quadTo(width, 0f, width, 24f)
            path.lineTo(width, height - 24)
            path.quadTo(width, height, width - 24, height)
            path.lineTo(24f, height)
            path.quadTo(0f, height, 0f, height - 24)
            path.lineTo(0f, 24f)
            path.quadTo(0f, 0f, 24f, 0f)
            canvas.clipPath(path)
        }
        super.onDraw(canvas)
    }

}