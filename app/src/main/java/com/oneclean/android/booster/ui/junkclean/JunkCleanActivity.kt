package com.oneclean.android.booster.ui.junkclean

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Environment
import androidx.lifecycle.ViewModelProvider
import com.hi.dhl.binding.viewbind
import com.oneclean.android.booster.R
import com.oneclean.android.booster.databinding.ActivityJunkCleanBinding
import com.oneclean.android.booster.ui.animation.AnimationActivity
import com.oneclean.android.booster.ui.base.BaseActivity
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
        binding.apply {
            slvSystemCache.setStatusChangedListener(this@JunkCleanActivity)
            slvResidualJunks.setStatusChangedListener(this@JunkCleanActivity)
            slvAdJunks.setStatusChangedListener(this@JunkCleanActivity)
            slvObsoleteApk.setStatusChangedListener(this@JunkCleanActivity)
            slvThumbPhoto.setStatusChangedListener(this@JunkCleanActivity)
            slvTempFiles.setStatusChangedListener(this@JunkCleanActivity)
            slvCleanMemory.setStatusChangedListener(this@JunkCleanActivity)

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
                binding.tvTotalSize.text = it
                val str = getString(R.string.clean) + it
                binding.tvClean.text = str
            }
            keySystemCache.observe(this@JunkCleanActivity) { binding.slvSystemCache.number = it }
            keyResidualJunks.observe(this@JunkCleanActivity) {
                binding.slvResidualJunks.number = it
            }
            keyAdJunks.observe(this@JunkCleanActivity) { binding.slvAdJunks.number = it }
            keyObsoleteApk.observe(this@JunkCleanActivity) { binding.slvObsoleteApk.number = it }
            keyThumbPhoto.observe(this@JunkCleanActivity) { binding.slvThumbPhoto.number = it }
            keyTempFiles.observe(this@JunkCleanActivity) { binding.slvTempFiles.number = it }
            keyCleanMemory.observe(this@JunkCleanActivity) { binding.slvCleanMemory.number = it }

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
                        "SystemCache" -> {
                            slvSystemCache.notifyStatusChangeBySize(it.value)
                        }
                        "ResidualJunks" -> {
                            slvResidualJunks.notifyStatusChangeBySize(it.value)
                        }
                        "AdJunks" -> {
                            slvAdJunks.notifyStatusChangeBySize(it.value)
                        }
                        "ObsoleteApk" -> {
                            slvObsoleteApk.notifyStatusChangeBySize(it.value)
                        }
                        "ThumbPhoto" -> {
                            slvThumbPhoto.notifyStatusChangeBySize(it.value)
                        }
                        "TempFiles" -> {
                            slvTempFiles.notifyStatusChangeBySize(it.value)
                        }
                        "CleanMemory" -> {
                            slvCleanMemory.notifyStatusChangeBySize(it.value)
                        }
                    }
                }
            }
        } else {
            viewModel.keys.forEach {
                binding.apply {
                    when (it) {
                        "SystemCache" -> {
                            slvSystemCache.notifyStatusChangeBySize(0)
                        }
                        "ResidualJunks" -> {
                            slvResidualJunks.notifyStatusChangeBySize(0)
                        }
                        "AdJunks" -> {
                            slvAdJunks.notifyStatusChangeBySize(0)
                        }
                        "ObsoleteApk" -> {
                            slvObsoleteApk.notifyStatusChangeBySize(0)
                        }
                        "ThumbPhoto" -> {
                            slvThumbPhoto.notifyStatusChangeBySize(0)
                        }
                        "TempFiles" -> {
                            slvTempFiles.notifyStatusChangeBySize(0)
                        }
                        "CleanMemory" -> {
                            slvCleanMemory.notifyStatusChangeBySize(0)
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
        val str2 = getString(R.string.clean) + str1
        binding.tvClean.text = str2
        binding.tvClean.isEnabled = viewModel.fileMap.isNotEmpty()
    }

    override fun changed(isChecked: Boolean, id: Int) {
        val type = when (id) {
            R.id.slv_system_cache -> "SystemCache"
            R.id.slv_residual_junks -> "ResidualJunks"
            R.id.slv_ad_junks -> "AdJunks"
            R.id.slv_obsolete_apk -> "ObsoleteApk"
            R.id.slv_thumb_photo -> "ThumbPhoto"
            R.id.slv_temp_files -> "TempFiles"
            R.id.slv_clean_memory -> "CleanMemory"
            else -> ""
        }

        viewModel.updateData(type, isChecked)
        cleanBtnRefresh()
    }

}