package com.oneclean.android.booster.ui.home

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import com.hi.dhl.binding.viewbind
import com.lxj.xpopup.XPopup
import com.oneclean.android.booster.R
import com.oneclean.android.booster.databinding.ActivityHomeBinding
import com.oneclean.android.booster.ui.PrivacyPolicyActivity
import com.oneclean.android.booster.ui.animation.AnimationActivity
import com.oneclean.android.booster.ui.base.BaseActivity
import com.oneclean.android.booster.ui.junkclean.JunkCleanActivity
import com.oneclean.android.booster.ui.popup.RatingUsPopup
import com.oneclean.android.booster.utils.*

class HomeActivity : BaseActivity(R.layout.activity_home) {
    private val binding: ActivityHomeBinding by viewbind()

    //Junk Clean-> Phone Booster-> Battery Saver-> CPU Cooler
    /**当前已清理类型的位置 -1即一个没清理*/
    private var index = -1

    /**当前主按钮显示的清理类别  index对应cleanTypes  默认主页显示的第一个清理类别*/
    private var curHomeBtnShowIndex = 0

    companion object {
        const val BROADCAST_ACTION_DISC = "nnsmanys0098"
    }

    private val cleanedIcons = arrayOf(
        R.drawable.ic_junk_clean,
        R.drawable.ic_phone_booster,
        R.drawable.ic_battery,
        R.drawable.ic_cpu_cooler
    )
    private val unCleanIcons = arrayOf(
        R.drawable.ic_junk_clean_un,
        R.drawable.ic_phone_booster_un,
        R.drawable.ic_battery_un,
        R.drawable.ic_cpu_cooler_un
    )

    /**清理的类别*/
    private val cleanTypeStrArray = arrayOf("CLEAN", "BOOSTER", "SAVER", "COOLER")
    val cleanTypes = mutableListOf<Clean>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initCleanType()
        initView()
        registerReceiver(CleanCheckedBroadcastReceiver(), IntentFilter(BROADCAST_ACTION_DISC))
        //Junk Clean-> Phone Booster-> Battery Saver-> CPU Cooler

        binding.ivHomeOuterCircle.setOnClickListener {
            requestPermission(reqSuccess, reqFail)
        }
        initAnimation()
    }

    /**为了让点击页面下方四个按钮能权限验证完成后，能正常的执行跳转*/
    private var requestPermissionIndex = -1

    private fun initView() {

        binding.apply {
            cvPhoneBooster.setOnClickListener { performStartActivity(1) }
            cvBatterySaver.setOnClickListener { performStartActivity(2) }
            cvCpuCooler.setOnClickListener { performStartActivity(3) }
            cvJunkClean.setOnClickListener { performStartActivity(0) }
            ivDrawer.setOnClickListener { layDrawer.openDrawer(GravityCompat.START) }
            ivClose.setOnClickListener { layDrawer.close() }
            cvRatingUs.setOnClickListener {
                XPopup.Builder(this@HomeActivity)
                    .asCustom(RatingUsPopup(this@HomeActivity))
                    .show()

                layDrawer.closeDrawer(GravityCompat.START, false)
            }
            cvShare.setOnClickListener { }
            cvPrivacyPolicy.setOnClickListener {
                startActivity(
                    Intent(
                        this@HomeActivity,
                        PrivacyPolicyActivity::class.java
                    )
                )
            }
        }
    }

    /**
     * 点击主页下方四个按钮的启动activity，这时候还没启动，先做权限验证
     * @param index 清理类型的位置  0-3  对应 cleanTypeStrArray
     * */
    private fun performStartActivity(index: Int) {
        requestPermissionIndex = index
        requestPermission(reqSuccess, reqFail)
    }

    /**启动activity*/
    private val startForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            requestPermission(reqSuccess, reqFail)
        }

    /**未得到权限执行*/
    private val reqFail: (deniedList: List<String>) -> Unit = {
        Toast.makeText(this, "需要你同意权限获取", Toast.LENGTH_SHORT).show()
    }

    /**成功得到权限执行*/
    private val reqSuccess: (grantedList: List<String>) -> Unit = {
        //android 11获取特殊权限需要用户手动去开启
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && !Environment.isExternalStorageManager()) {
            val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
            val uri = Uri.fromParts("package", packageName, null)
            intent.data = uri
            startForResult.launch(intent)
        } else {
            performRealStartActivity()
        }
    }

    /**
     * 真实的执行activity跳转
     * */
    private fun performRealStartActivity() {
        val index = requestPermissionIndex
        val curIndex = curHomeBtnShowIndex
        //requestPermissionIndex（index） > -1 则表示执行该方法的是页面下方的四个按钮点击
        val b = index > -1
        if (b || curIndex != 0) {
            val ix = if (b) index else curIndex
            //处理后就恢复
            if (b) requestPermissionIndex = -1
            AnimationActivity.startActivity(this, ix)
        } else {
            //清理类型为CLEAN时
            JunkCleanActivity.startActivity(this, 0)
        }
    }

    /**
     * 主按钮外圈旋转动画
     * */
    lateinit var animation: Animation
    private fun initAnimation() {
        animation = RotateAnimation(
            0f,
            360f,
            Animation.RELATIVE_TO_SELF,
            0.5f,
            Animation.RELATIVE_TO_SELF,
            0.5f
        )
        animation.duration = 6000
        animation.repeatCount = -1
        binding.ivHomeOuterCircle.animation = animation
        animation.start()
    }

    override fun onStart() {
        super.onStart()

        // 通过SharedPreference获取是否刷新
        val isRefresh = getBoolean(this, "is_refresh", false)
        if (isRefresh) {
            //需要刷新主界面  全部恢复默认状态
            this.index = -1
            this.curHomeBtnShowIndex = 0
            this.cleanTypes.forEach {
                it.cleaned = false
            }
            //恢复
            putBoolean(this, "is_refresh", false)
            //时间重置
            putLong(this, "start_time", -1L)
            //更新主按钮
            val color = ContextCompat.getColor(this@HomeActivity, R.color.un_clean)
            updateHomeBtn(
                R.drawable.ic_home_circle_un_1,
                R.drawable.ic_home_circle_un_2,
                unCleanIcons[0],
                cleanTypeStrArray[0],
                color
            )
        }

        var curIndex = this.index
        if (curIndex < 0) return

        var nextIndex = -1
        val size = cleanTypes.size

        for (i in 0..size - 2) {
            val index = (curIndex + 1) % size
            if (!cleanTypes[index].cleaned) {
                nextIndex = index
                break
            }
            curIndex++
        }

        //最开始啥都没清理默认状态
        //全部清理完成显示绿框加booster
        //有未清理的就显示红框加未清理的按钮
        if (nextIndex == -1) {
            // 全部清理完成
            val color = ContextCompat.getColor(this@HomeActivity, R.color.cleaned)
            updateHomeBtn(
                R.drawable.ic_home_circle_1,
                R.drawable.ic_home_circle_2,
                R.drawable.ic_phone_booster,
                cleanTypeStrArray[1],
                color
            )
            curHomeBtnShowIndex = 0
        } else {
            val color = ContextCompat.getColor(this@HomeActivity, R.color.un_clean)
            updateHomeBtn(
                R.drawable.ic_home_circle_un_1,
                R.drawable.ic_home_circle_un_2,
                unCleanIcons[nextIndex],
                cleanTypeStrArray[nextIndex],
                color
            )
            curHomeBtnShowIndex = nextIndex
        }
    }

    private fun updateHomeBtn(
        outCircleResId: Int,
        innerCircleResId: Int,
        centerIconResId: Int,
        centerName: String,
        centerNameColor: Int
    ) {
        binding.apply {
            ivHomeOuterCircle.setImageResource(outCircleResId)
            ivHomeInnerCircle.setImageResource(innerCircleResId)
            ivCenterIcon.setImageResource(centerIconResId)
            tvCenterName.text = centerName
            tvCenterName.setTextColor(centerNameColor)
        }
    }

    override fun onResume() {
        super.onResume()
        //恢复主按钮动画
        binding.ivHomeOuterCircle.animation.start()
    }

    private fun initCleanType() {
        cleanTypes.add(Clean("Junk Clean"))
        cleanTypes.add(Clean("Phone Booster"))
        cleanTypes.add(Clean("Battery Saver"))
        cleanTypes.add(Clean("CPU Cooler"))
    }

    /**
     * 通过广播来告知主页当前完成的 '优化类型'
     * 然后记录下来，当回到主页时，在onStart方法中判断主按钮应显示的状态
     * */
    inner class CleanCheckedBroadcastReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val index = intent.getIntExtra("CheckedIndex", -1)
            if (index >= 0) {
                //收到第一个清理完成的广播后开始计时
                val startTime = getLong(this@HomeActivity, "start_time")
                if (startTime == -1L) //不等于-1即已经有记录时间了，就不管了
                    putLong(this@HomeActivity, "start_time", System.currentTimeMillis())

                this@HomeActivity.cleanTypes[index].cleaned = true
                this@HomeActivity.index = index

                cleanTypes.forEach {
                    LogUtil.d(it.toString())
                }
            }
        }
    }

    override fun onStop() {
        super.onStop()
        //界面不可见时取消动画
        binding.ivHomeOuterCircle.animation.cancel()
    }

    /**
     * 清理类型的对象类
     * @param type 清理的类型
     * @param cleaned 该类型是否清理
     * */
    data class Clean(val type: String, var cleaned: Boolean = false)
}