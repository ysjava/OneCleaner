package com.oneclean.android.booster.ui.junkclean

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.oneclean.android.booster.utils.FileUtil
import java.io.File
import java.io.FileInputStream
import kotlin.collections.ArrayList

class JunkCleanViewModel : ViewModel() {
    //存放删除后文件的type
    private val _keys = mutableListOf<String>()
    val keys: List<String> = _keys
    private val handler = Handler(Looper.getMainLooper()) {
        when (it.what) {
            1 -> {
                val oldValue = _totalSize.value
                _totalSize.value = (it.obj as Long) + oldValue!!
            }
            2 -> {
                val type = it.data.getString("type")!!
                val size = it.obj as Long
                when (type) {
                    "SystemJunk" -> _keySystemJunk.value = size
                    "ObsoleteFiles" -> _keyObsoleteFiles.value = size
                    "ApkJunk" -> _keyApkJunk.value = size
                    "ResidualJunk" -> _keyResidualJunk.value = size
                    "TempFiles" -> _keyTempFiles.value = size
                    "DeepCleanJunk" -> _keyDeepCleanJunk.value = size
                }
            }
            3 -> _handleStatus.value = "ScanComplete"
            4 -> _handleStatus.value = "Removed"
        }
        true
    }

    /**
     * 文件大小计算
     *
     * @param file 计算的文件
     * @return 计算结果
     * */
    private fun fileSize(file: File): Long {
        val size: Long
        var fis: FileInputStream? = null
        size = try {
            fis = FileInputStream(file)
            fis.available().toLong()
        } catch (e: Exception) {
            e.printStackTrace()
            0
        } finally {
            fis?.close()
        }

        return size
    }

    /**
     * 遍历整个文件夹，匹配指定文件
     *
     * @param files 文件列表
     * */
    fun handleFiles(files: Array<File>?) {
        Thread {
            val startScanningTime = System.currentTimeMillis()
            //生成假数据
            generateData()
            realHandleFiles(files)
            val endScanningTime = System.currentTimeMillis()
            val time = (endScanningTime - startScanningTime) / 1000
            //小于10s就随机4-8s，否则就原时间
            val delay = if (time < 10) ((4L - time)..(8L - time)).random() else 0
            handler.sendEmptyMessageDelayed(3, delay * 1000)
        }.start()
    }

    private fun realHandleFiles(files: Array<File>?) {
        files?.forEach {
            //匹配忽略大小写
            val name = it.name.lowercase()

            //name.contains("xxx")  xxx即为指定类型的文件
            when {
                name.contains("cache") -> addFile(it, "SystemJunk")
                name.contains("log") -> addFile(it, "ObsoleteFiles")
                name.contains("apk") -> addFile(it, "ApkJunk")
                name.contains("apk") -> addFile(it, "ResidualJunk")
                name.contains("temp") -> addFile(it, "TempFiles")
                name.contains("自动生产的垃圾") -> addFile(it, "DeepCleanJunk")
                else -> {
                    //如果未匹配并且是文件夹就继续遍历
                    if (it.isDirectory) realHandleFiles(it.listFiles())
                }
            }
        }
    }

    //总垃圾文件大小
    private val _totalSize = MutableLiveData<Long>(0)
    val totalSize = Transformations.map(_totalSize) {
        formatSize(it).trim()
    }

    //要清理的垃圾文件集合  <垃圾类型,垃圾文件集合>
    private val _fileMap = hashMapOf<String, ArrayList<File>>()
    val fileMap: Map<String, ArrayList<File>> = _fileMap

    /**
     * 匹配到文件后添加到集合中
     *
     * @param file 匹配到的要清理的文件或文件夹
     * @param type 垃圾文件的类型
     * */
    private fun addFile(file: File, type: String) {
        if (file.isDirectory) {

            //如果是文件夹类型就继续遍历下去
            file.listFiles()?.let {
                it.forEach { inFile ->
                    addFile(inFile, type)
                }
            }
        } else {
            //文件大小
            val fileSize = fileSize(file)
            //'type'类型的垃圾文件大小
            val sv = _typeSizeMap[type]
            //总大小显示动态更新
            var message = Message.obtain()
            message.what = 1
            message.obj = fileSize
            handler.sendMessageDelayed(message, 0)

            _typeSizeMap[type] = if (sv == null) fileSize else sv + fileSize
            //更新某个type的总大小
            message = Message.obtain()
            message.what = 2
            message.obj = _typeSizeMap[type]
            val bundle = Bundle()
            bundle.putString("type", type)
            message.data = bundle
            handler.sendMessage(message)

            //添加垃圾文件
            if (_fileMap[type] == null)
                _fileMap[type] = arrayListOf(file)
            else
                _fileMap[type]!!.add(file)

        }
    }

    private val _typeSizeMap = hashMapOf(
        Pair("SystemJunk", 0L),
        Pair("ObsoleteFiles", 0L),
        Pair("ApkJunk", 0L),
        Pair("ResidualJunk", 0L),
        Pair("TempFiles", 0L)
    )

    val typeSizeMap: Map<String, Long> = _typeSizeMap

    private val _handleStatus = MutableLiveData("")
    val handleStatus: LiveData<String> = _handleStatus

    private val _keySystemJunk = MutableLiveData<Long>(0)
    private val _keyObsoleteFiles = MutableLiveData<Long>(0)
    private val _keyApkJunk = MutableLiveData<Long>(0)
    private val _keyResidualJunk = MutableLiveData<Long>(0)
    private val _keyTempFiles = MutableLiveData<Long>(0)
    private val _keyDeepCleanJunk = MutableLiveData<Long>(0)

    val keySystemJunk = Transformations.map(_keySystemJunk) { it }
    val keyObsoleteFiles = Transformations.map(_keyObsoleteFiles) { it }
    val keyApkJunk = Transformations.map(_keyApkJunk) { it }
    val keyResidualJunk = Transformations.map(_keyResidualJunk) { it }
    val keyTempFiles = Transformations.map(_keyTempFiles) { it }
    val keyDeepCleanJunk = Transformations.map(_keyDeepCleanJunk) { it }

    private fun formatSize(size: Long): String {

        val kb = size / 1024.0
        val mb = kb / 1024.0
        val gb = mb / 1024.0

        return when {
            gb >= 1.0 -> {
                "${String.format("%.2f", gb)} GB  "
            }
            mb >= 1.0 -> {
                "${String.format("%.2f", mb)} MB  "
            }
            kb >= 1.0 -> {
                "${String.format("%.2f", kb)} KB  "
            }
            else -> {
                "$size B  "
            }
        }
    }

    fun clean() {
        Thread {
            _fileMap.entries.forEach {
                it.value.forEach { file ->
                    //删除文件
                    FileUtil.deleteFile(file)
//=========== 要执行动画，不进行数据动态更新了，删除文件即可====================
                    /*  val fileSize = fileSize(file)
                        //'type'类型的垃圾文件大小
                        val sv = _typeSizeMap[it.key]

                        //总大小显示动态更新
                        var message = Message.obtain()
                        message.what = 1
                        backgroundTotalSize -= fileSize
                        message.obj = backgroundTotalSize
                        handler.sendMessage(message)
                        handler.sendMessageDelayed(message, delayTime)

                        _typeSizeMap[it.key] = if (sv == null) 0 else sv - fileSize
                        message = Message.obtain()
                        message.what = 2
                        message.obj = _typeSizeMap[it.key]
                        val bundle = Bundle()
                        bundle.putString("type", it.key)
                        message.data = bundle
                        handler.sendMessage(message)
                        handler.sendMessageDelayed(message, delayTime)
                    */
                }
                //删除完一种类型的垃圾文件后移除集合
                _typeSizeMap.remove(it.key)
                //记录删除了的type,用于更新界面
                //_keys.add(it.key)
            }

            _keys.forEach {
                _fileMap.remove(it)
            }

            //删除所选中的垃圾后，进行界面更新
            handler.sendEmptyMessage(4)
        }.start()
    }


    private val unCheckedFileHashMap = hashMapOf<String, ArrayList<File>>()

    /**
     * 选中或者取消选中后更新总数显示信息
     * @param type 操作的类型
     * @param checked 选中结果
     * */
    fun updateData(type: String, checked: Boolean) {
        val size = _typeSizeMap[type] ?: backFakeDataSize
        val oldTotalSize = _totalSize.value!!
        val reSize = if (checked) oldTotalSize + size else oldTotalSize - size
        _totalSize.value = reSize

        if (type == "DeepCleanJunk") return
        if (_fileMap[type] == null && unCheckedFileHashMap[type] == null) return

        if (checked) {
            val temp = unCheckedFileHashMap[type]!!
            _fileMap[type] = temp
            unCheckedFileHashMap.remove(type)
        } else {
            val temp = _fileMap[type]!!
            unCheckedFileHashMap[type] = temp
            _fileMap.remove(type)
        }
    }

    private val fakeDataList = mutableListOf<Long>()
    var backFakeDataSize = 0L
        private set

    private fun generateData() {

        val m = 1024 * 1024 * 1L
        val fileNumber = (10..23).random()
        val fakeDateSize = (100 * m..2048 * m).random()

        for (i in 0 until fileNumber) {

            val size = fakeDateSize / fileNumber
            fakeDataList.add(size)

            //进行更新
            var message = Message.obtain()
            message.what = 1
            message.obj = size
            val delay = (100L * i) + (50..300).random()
            handler.sendMessageDelayed(message, delay)

            //假数据单独更新
            message = Message.obtain()
            message.what = 2
            backFakeDataSize += size
            message.obj = backFakeDataSize
            val bundle = Bundle()
            bundle.putString("type", "DeepCleanJunk")
            message.data = bundle
            handler.sendMessageDelayed(message, delay)
        }
    }
}