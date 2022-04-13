package com.oneclean.android.booster.widget

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.text.BoringLayout
import android.text.TextPaint
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator
import androidx.core.content.ContextCompat
import com.oneclean.android.booster.R
import com.oneclean.android.booster.utils.dp2px
import kotlin.math.max


class ScanLoadingView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    companion object {
        const val LOADED = 200
        const val LOADING = 1
        const val UNCHECK = -1
    }

    private var changedListener: StatusChangedListener? = null
    private var textColor: Int = 0
    private var drawableSize = 0
    private var textSize = 0
    var enable = false

    var number = 0L
        set(value) {
            field = value
            text = formatSize(value)
            requestLayout()
        }
    var loadingStatus = LOADING
        set(value) {
            if (value != LOADED && value != LOADING && value != UNCHECK) return
            field = value
            invalidate()
        }
    private var text = "864 KB   "

    private var defHeight = 0
    private var startAngle = 0F
        set(value) {
            field = value
            invalidate()
        }
    private var paintText = TextPaint()
    private var paintLoading = Paint()
    private var iconPaint = Paint()
    private var bounds = Rect()
    private val drawables =
        intArrayOf(R.drawable.ic_scanning_status_selected, R.drawable.ic_scanning_status_deselect)

    init {
        val animator = ObjectAnimator.ofFloat(this, "startAngle", 0f, 360f)
        animator.duration = 600
        animator.repeatCount = -1
        animator.repeatMode = ValueAnimator.RESTART
        animator.interpolator = LinearInterpolator()
        animator.start()

        val strokeWidth = dp2px(1.5f, context)
        defHeight = dp2px(40f, context)


        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.ScanLoadingView)
        textColor = typedArray.getColor(
            R.styleable.ScanLoadingView_color,
            ContextCompat.getColor(context, R.color.scanning_size)
        )
        drawableSize =
            typedArray.getDimension(R.styleable.ScanLoadingView_drawable_size, 30f).toInt()
        textSize = typedArray.getDimension(R.styleable.ScanLoadingView_text_size, 14f).toInt()
        typedArray.recycle()

        paintText.textSize = textSize.toFloat()
        paintText.getTextBounds(text, 0, text.length, bounds)
        paintLoading.strokeWidth = strokeWidth.toFloat()
        paintLoading.shader = LinearGradient(
            0f,
            0f,
            200f,
            strokeWidth.toFloat(),
            Color.parseColor("#F0F8F6"),
            Color.parseColor("#C2D9D9"),
            Shader.TileMode.CLAMP
        )
        paintText.isAntiAlias = true


        setOnClickListener { }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val boring = BoringLayout.isBoring(text, paintText)

        var relWidth = -1

        boring?.let {
            relWidth = (it.width + drawableSize + paintLoading.strokeWidth * 2).toInt()
        }
        setMeasuredDimension(if (relWidth < 0) 0 else relWidth, defHeight)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        paintLoading.apply {
            isAntiAlias = true
            style = Paint.Style.STROKE
            strokeCap = Paint.Cap.ROUND
        }

        val right = measuredWidth - paintLoading.strokeWidth / 2
        val left = right - drawableSize
        val top = measuredHeight / 2 - drawableSize / 2f
        val bottom = top + drawableSize

        val bgRight = measuredWidth.toFloat()
        val bgLeft = bgRight - drawableSize
        val bgTop = measuredHeight / 2f - drawableSize / 2f

        val bitmap: Bitmap

        when (loadingStatus) {
            LOADING -> {
                canvas.drawArc(left, top, right, bottom, startAngle, 200f, false, paintLoading)
            }
            LOADED -> {
                bitmap = drawableToBitmap(
                    ContextCompat.getDrawable(context, drawables[0])!!,
                    drawableSize
                )
                canvas.drawBitmap(bitmap, bgLeft, bgTop, iconPaint)
            }
            UNCHECK -> {
                bitmap = drawableToBitmap(
                    ContextCompat.getDrawable(context, drawables[1])!!,
                    drawableSize
                )
                canvas.drawBitmap(bitmap, bgLeft, bgTop, iconPaint)
            }
        }
        canvas.drawText(
            text,
            0F,
            measuredHeight.toFloat() / 2 + (bounds.bottom - bounds.top) / 2,
            paintText
        )
    }

    private fun formatSize(size: Long): String {

        val kb = size / 1024.0
        val mb = kb / 1024.0
        val gb = mb / 1024.0

        return when {
            gb >= 1.0 -> {
                "${String.format("%.2f", gb)} GB   "
            }
            mb >= 1.0 -> {
                "${String.format("%.2f", mb)} MB   "
            }
            kb >= 1.0 -> {
                "${String.format("%.2f", kb)} KB   "
            }
            else -> {
                "$size B  "
            }
        }
    }

    override fun performClick(): Boolean {
        if (!enable || loadingStatus == LOADING) return false
        loadingStatus = if (loadingStatus == LOADED) UNCHECK else LOADED
        changedListener?.changed(loadingStatus > 1, this.id)
        return super.performClick()
    }

    fun setStatusChangedListener(listener: StatusChangedListener) {
        changedListener = listener
    }

    /**
     * 根据该类型的垃圾文件大小显示状态
     * size=0： enable = false  loadingStatus = UNCHECKED
     * size>0： enable = true loadingStatus = LOADED
     * @param size 该类型的垃圾文件大小
     * */
    fun notifyStatusChangeBySize(size: Long) {
        val aSize = max(0L, size)
        enable = aSize > 0L
        loadingStatus = if (aSize > 0) LOADED else UNCHECK
    }

    interface StatusChangedListener {
        fun changed(isChecked: Boolean, id: Int)
    }
}

/**
 * Drawable转换成一个Bitmap
 * 方法一
 * @param drawable drawable对象
 * @return
 */
fun drawableToBitmap(drawable: Drawable, size: Int): Bitmap {
    @Suppress("DEPRECATION")
    val bitmap = Bitmap.createBitmap(
        size, size,
        if (drawable.opacity != PixelFormat.OPAQUE) Bitmap.Config.ARGB_8888 else Bitmap.Config.RGB_565
    )
    val canvas = Canvas(bitmap)
    drawable.setBounds(0, 0, size, size)
    drawable.draw(canvas)
    return bitmap
}

