package com.oneclean.android.booster.ui.junkclean

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.text.Spannable
import android.text.SpannableString
import android.text.style.RelativeSizeSpan
import androidx.lifecycle.ViewModelProvider
import com.hi.dhl.binding.viewbind
import com.oneclean.android.booster.R
import com.oneclean.android.booster.databinding.ActivityJunkCleanBinding
import com.oneclean.android.booster.ui.animation.AnimationActivity
import com.oneclean.android.booster.ui.base.BaseActivity
import com.oneclean.android.booster.utils.getStatusHeight
import com.oneclean.android.booster.utils.logd
import com.oneclean.android.booster.widget.ScanLoadingView

class JunkCleanActivity : BaseActivity(R.layout.activity_junk_clean),
    ScanLoadingView.StatusChangedListener {
    private val binding: ActivityJunkCleanBinding by viewbind()
    private var cleanType = -1
    private val viewModel by lazy { ViewModelProvider(this).get(JunkCleanViewModel::class.java) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initObserve()
        initView()
        cleanType = intent.getIntExtra("CleanType", -1)

        viewModel.handleFiles(Environment.getExternalStorageDirectory().listFiles())
    }

    companion object {
        fun startActivity(packageContext: Context, cleanType: Int) {
            val intent = Intent(packageContext, JunkCleanActivity::class.java)
            intent.putExtra("CleanType", cleanType)
            packageContext.startActivity(intent)
        }
    }

    private fun initView() {

        //toolbar更新下高度，加上状态栏的高度，这个操作可以定义个父类来做
        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        val lp = toolbar.layoutParams
        lp.height = lp.height + getStatusHeight()
        toolbar.layoutParams = lp

        binding.apply {
            slvSystemJunk.setStatusChangedListener(this@JunkCleanActivity)
            slvObsoleteFiles.setStatusChangedListener(this@JunkCleanActivity)
            slvApkJunk.setStatusChangedListener(this@JunkCleanActivity)
            slvResidualJunk.setStatusChangedListener(this@JunkCleanActivity)
            slvTempFiles.setStatusChangedListener(this@JunkCleanActivity)
            slvDeepCleanJunk.setStatusChangedListener(this@JunkCleanActivity)

            tvClean.setOnClickListener {
                //开启清理动画
                AnimationActivity.startActivity(this@JunkCleanActivity, cleanType)
                //清理
                viewModel.clean()
            }

            ivBack.setOnClickListener { finish() }
        }
    }


    private fun initObserve() {
        viewModel.apply {
            totalSize.observe(this@JunkCleanActivity) {
                val index = it.indexOf(" ")

                val spannable = SpannableString(it)
                spannable.setSpan(
                    RelativeSizeSpan(0.4F),
                    index,
                    it.length,
                    Spannable.SPAN_EXCLUSIVE_INCLUSIVE
                )

                binding.tvTotalSize.text = spannable
                val str = "Clean  $it"
                binding.tvClean.text = str
            }
            keySystemJunk.observe(this@JunkCleanActivity) { binding.slvSystemJunk.number = it }
            keyObsoleteFiles.observe(this@JunkCleanActivity) {
                binding.slvObsoleteFiles.number = it
            }
            keyApkJunk.observe(this@JunkCleanActivity) { binding.slvApkJunk.number = it }
            keyResidualJunk.observe(this@JunkCleanActivity) { binding.slvResidualJunk.number = it }
            keyTempFiles.observe(this@JunkCleanActivity) { binding.slvTempFiles.number = it }
            keyDeepCleanJunk.observe(this@JunkCleanActivity) {
                "observe========observe=========: $it".logd("IYGWUYGUYWGYQG")
                binding.slvDeepCleanJunk.number = it
            }

            handleStatus.observe(this@JunkCleanActivity) {
                when (it) {
                    "ScanComplete" -> updateView()
                    "Removed" -> finish()
                    //updateView(true)
                }
            }
        }
    }

    private fun updateView(cleaned: Boolean = false) {
        if (!cleaned) {
            viewModel.typeSizeMap.forEach {
                binding.apply {
                    when (it.key) {
                        "SystemJunk" -> {
                            slvSystemJunk.notifyStatusChangeBySize(it.value)
                        }
                        "ObsoleteFiles" -> {
                            slvObsoleteFiles.notifyStatusChangeBySize(it.value)
                        }
                        "ApkJunk" -> {
                            slvApkJunk.notifyStatusChangeBySize(it.value)
                        }
                        "ResidualJunk" -> {
                            slvResidualJunk.notifyStatusChangeBySize(it.value)
                        }
                        "TempFiles" -> {
                            slvTempFiles.notifyStatusChangeBySize(it.value)
                        }
                        "DeepCleanJunk" -> {
                            slvDeepCleanJunk.notifyStatusChangeBySize(it.value)
                        }
                    }
                }
            }
            val str = viewModel.totalSize.value + "  Junk Scanned"
            binding.tvSecond.text = str
            //假数据不会参与到真实的垃圾集合中，只是一个显示效果，所以，需要动态的去改变状态
            binding.slvDeepCleanJunk.notifyStatusChangeBySize(viewModel.backFakeDataSize)
        } else {
            viewModel.keys.forEach {
                binding.apply {
                    when (it) {
                        "SystemJunk" -> {
                            slvSystemJunk.notifyStatusChangeBySize(0)
                        }
                        "ObsoleteFiles" -> {
                            slvObsoleteFiles.notifyStatusChangeBySize(0)
                        }
                        "ApkJunk" -> {
                            slvApkJunk.notifyStatusChangeBySize(0)
                        }
                        "ResidualJunk" -> {
                            slvResidualJunk.notifyStatusChangeBySize(0)
                        }
                        "TempFiles" -> {
                            slvTempFiles.notifyStatusChangeBySize(0)
                        }
                        "DeepCleanJunk" -> {
                            slvDeepCleanJunk.notifyStatusChangeBySize(0)
                        }
                    }
                }
            }
        }
        //清除按钮刷新
        cleanBtnRefresh()
    }

    private fun cleanBtnRefresh() {
        val str1 = binding.tvTotalSize.text
        val str2 = "Clean  $str1"
        binding.tvClean.text = str2
        binding.tvClean.isEnabled =
            viewModel.fileMap.isNotEmpty() || binding.slvDeepCleanJunk.loadingStatus == ScanLoadingView.LOADED
    }

    override fun changed(isChecked: Boolean, id: Int) {
        val type = when (id) {
            R.id.slv_system_junk -> "SystemJunk"
            R.id.slv_obsolete_files -> "ObsoleteFiles"
            R.id.slv_apk_junk -> "ApkJunk"
            R.id.slv_residual_junk -> "ResidualJunk"
            R.id.slv_temp_files -> "TempFiles"
            R.id.slv_deep_clean_junk -> "DeepCleanJunk"
            else -> ""
        }

        viewModel.updateData(type, isChecked)
        cleanBtnRefresh()
    }

}