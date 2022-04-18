package com.oneclean.android.booster.widget.viewgroup

import android.content.Context
import android.util.AttributeSet
import com.airbnb.lottie.LottieAnimationView

class EquilateralLottieAnimationView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LottieAnimationView(context, attrs, defStyleAttr) {


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val width = MeasureSpec.getSize(widthMeasureSpec)
        setMeasuredDimension(width,width)
    }
}