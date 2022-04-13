package com.oneclean.android.booster.ui.cleaned

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.core.widget.NestedScrollView
import com.hi.dhl.binding.viewbind
import com.oneclean.android.booster.R
import com.oneclean.android.booster.databinding.ActivityCleanedBinding
import com.oneclean.android.booster.ui.animation.AnimationActivity
import com.oneclean.android.booster.ui.base.BaseActivity
import com.oneclean.android.booster.ui.junkclean.JunkCleanActivity
import kotlin.math.min

class CleanedActivity : BaseActivity(R.layout.activity_cleaned), View.OnClickListener {
    private val binding: ActivityCleanedBinding by viewbind()

    companion object {
        fun startActivity(packageContext: Context, cleanType: Int) {
            val intent = Intent(packageContext, CleanedActivity::class.java)
            intent.putExtra("CleanType", cleanType)
            packageContext.startActivity(intent)
        }
    }

    private var cleanType = -1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        cleanType = intent.getIntExtra("CleanType", -1)
        initView()

        binding.scrollView.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { _, _, scrollY, _, _ ->
            binding.toolbar.setBackgroundColor(Color.argb(min(255, scrollY), 255, 255, 255))
        })

        binding.apply {
            when (cleanType) {
                0 -> layJunk.visibility = View.GONE
                1 -> layBooster.visibility = View.GONE
                2 -> layBattery.visibility = View.GONE
                3 -> layCpu.visibility = View.GONE
            }
        }
    }

    private fun initView() {
        binding.apply {
            tvCleanJunk.setOnClickListener(this@CleanedActivity)
            tvCleanBooster.setOnClickListener(this@CleanedActivity)
            tvCleanBattery.setOnClickListener(this@CleanedActivity)
            tvCleanCpu.setOnClickListener(this@CleanedActivity)
            ivBack.setOnClickListener { finish() }
            tvTitle.text = getTitleText(cleanType)
        }
    }

    private fun getTitleText(cleanType: Int): String {
        return when (cleanType) {
            0 -> getString(R.string.junk_clean)
            1 -> getString(R.string.phone_booster)
            2 -> getString(R.string.battery_saver)
            3 -> getString(R.string.cpu_cooler)
            else -> "haha ys"
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.tv_clean_junk -> JunkCleanActivity.startActivity(this@CleanedActivity, 0)
            R.id.tv_clean_booster -> AnimationActivity.startActivity(this@CleanedActivity, 1)
            R.id.tv_clean_battery -> AnimationActivity.startActivity(this@CleanedActivity, 2)
            R.id.tv_clean_cpu -> AnimationActivity.startActivity(this@CleanedActivity, 3)
        }
        finish()
    }

}