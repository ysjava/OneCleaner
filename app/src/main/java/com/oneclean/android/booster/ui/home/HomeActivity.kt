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
import com.oneclean.android.booster.logic.enums.CleanType
import com.oneclean.android.booster.ui.animation.AnimationActivity
import com.oneclean.android.booster.ui.base.BaseActivity
import com.oneclean.android.booster.ui.junkclean.JunkCleanActivity
import com.oneclean.android.booster.ui.popup.RatingUsPopup
import com.oneclean.android.booster.utils.*

class HomeActivity : BaseActivity(R.layout.activity_home) {
    private val binding: ActivityHomeBinding by viewbind()

    //Junk Clean-> Phone Booster-> Battery Saver-> CPU Cooler
    /**当前已清理类型 默认未清理*/
    private var cleanedType = CleanType.NOTHING

    /**当前主按钮显示的清理类别  index对应cleanTypes  默认主页显示的第一个清理类别*/
    private var curHomeBtnShowIndex = CleanType.CLEAN

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

    private val homeBtnStyleGroup = arrayOf(
        Pair(R.drawable.ic_home_circle_in_red, R.color.home_btn_red),
        Pair(R.drawable.ic_home_circle_in_orange, R.color.home_btn_orange),
        Pair(R.drawable.ic_home_circle_in_pink, R.color.home_btn_pink)
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
            temp = 1
            requestPermission(reqSuccess, reqFail, by11RequestFail)
        }

        initAnimation()
    }

    /**为了让点击页面下方四个按钮能权限验证完成后，能正常的执行跳转*/
    private var requestPermissionIndex = CleanType.NOTHING

    private fun initView() {
        binding.apply {
            cvPhoneBooster.setOnClickListener { performStartActivity(CleanType.BOOSTER) }
            cvBatterySaver.setOnClickListener { performStartActivity(CleanType.SAVER) }
            cvCpuCooler.setOnClickListener { performStartActivity(CleanType.COOLER) }
            cvJunkClean.setOnClickListener { performStartActivity(CleanType.CLEAN) }
            ivDrawer.setOnClickListener { layDrawer.openDrawer(GravityCompat.START) }
            ivClose.setOnClickListener { layDrawer.close() }
            cvRatingUs.setOnClickListener {
                XPopup.Builder(this@HomeActivity)
                    .asCustom(RatingUsPopup(this@HomeActivity))
                    .show()

                layDrawer.closeDrawer(GravityCompat.START, false)
            }
            cvShare.setOnClickListener {
                val intent = Intent()
                intent.action = Intent.ACTION_SEND
                intent.putExtra(Intent.EXTRA_TEXT, "文本内容")
                intent.type = "text/plain"
                startActivity(intent)
            }
            cvPrivacyPolicy.setOnClickListener {
                val intent = Intent(Intent.ACTION_VIEW).apply {
                    data = Uri.parse(
                        "https://onecleanerr.com/policy")
                }
                layDrawer.close()
                startActivity(intent)
            }
        }
    }

    /**
     * 点击主页下方四个按钮的启动activity，这时候还没启动，先做权限验证
     * @param type 清理类型的位置  0-3  对应 cleanTypeStrArray
     * */
    private fun performStartActivity(type: CleanType) {
        temp = 1
        requestPermissionIndex = type
        requestPermission(reqSuccess, reqFail,by11RequestFail)
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

    /**防止重复去做StorageManager权限请求*/
    private var temp = 1
    private val by11RequestFail = {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (temp == 1) {
                val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                val uri = Uri.fromParts("package", packageName, null)
                intent.data = uri
                startForResult.launch(intent)
                temp = 0
            }
        }
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
        val b = index.value > CleanType.NOTHING.value

        val ix = if (b) index else curIndex
        if (ix == CleanType.CLEAN) {
            JunkCleanActivity.startActivity(this, ix.value)
        } else {
            AnimationActivity.startActivity(this, ix.value)
        }
        requestPermissionIndex = CleanType.NOTHING

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
        "isRefresh $isRefresh".logd("JBNJWQNDN")
        if (isRefresh) {
            //需要刷新主界面  全部恢复默认状态
            this.cleanedType = CleanType.NOTHING
            this.curHomeBtnShowIndex = CleanType.CLEAN
            this.cleanTypes.forEach {
                it.cleaned = false
            }
            //恢复
            putBoolean(this, "is_refresh", false)
            //时间重置
            putLong(this, "start_time", -1L)
            //更新主按钮
            updateHomeBtn(
                Pair(R.drawable.ic_home_circle_un_2, R.color.un_clean),
                unCleanIcons[0],
                cleanTypeStrArray[0]
            )
            return
        }

        val curCleaned = this.cleanedType
        if (curCleaned == CleanType.NOTHING) return
        var curIndex = curCleaned.value
        //下一个应清理的
        var nextIndex = CleanType.NOTHING
        val size = cleanTypes.size

        for (i in 0..size - 2) {
            val index = (curIndex + 1) % size
            if (!cleanTypes[index].cleaned) {
                nextIndex = CleanType.switchToTypeByValue(index)
                break
            }
            curIndex++
        }

        //最开始啥都没清理默认状态
        //全部清理完成显示绿框加booster
        //有未清理的就显示红框加未清理的按钮
        if (nextIndex == CleanType.NOTHING) {
            // 全部清理完成
            updateHomeBtn(
                Pair(R.drawable.ic_home_circle_2, R.color.cleaned),
                cleanedIcons[0],
                cleanTypeStrArray[0]
            )
            curHomeBtnShowIndex = CleanType.CLEAN
        } else {
            val pair = homeBtnStyleGroup.random()
            updateHomeBtn(pair, unCleanIcons[nextIndex.value], cleanTypeStrArray[nextIndex.value])
            curHomeBtnShowIndex = nextIndex
        }
    }

    private fun updateHomeBtn(
        pair: Pair<Int, Int>, centerIconResId: Int, centerName: String,
    ) {
        binding.apply {
            val color = ContextCompat.getColor(this@HomeActivity, pair.second)
            ivHomeOuterCircle.setColorFilter(color)
            ivHomeInnerCircle.setImageResource(pair.first)
            ivCenterIcon.setImageResource(centerIconResId)
            ivCenterIcon.setColorFilter(color)
            tvCenterName.text = centerName
            tvCenterName.setTextColor(color)
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
                this@HomeActivity.cleanedType = CleanType.switchToTypeByValue(index)

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