package com.oneclean.android.booster


import org.junit.Assert.*
import org.junit.Test
import java.text.SimpleDateFormat
import java.util.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
data class TES(var number: Int)
class ExampleUnitTest {
    val fakeDataList = mutableListOf<Long>()
    @Test
    fun addition_isCorrect() {
        val formatter = SimpleDateFormat("yyyy-MM-dd")
        System.out.println(formatter.format(Date()))
    }



    fun generateData() {
        /*
        模拟数据不加入集合 随机100m - 2048m   1024 * 1024 * 1024 * 100  ～  1024 * 1024 * 1024 * 1024 * 2
        生成一个就通知 _totalSize+= 更新
        点击事件单独处理，也是去更新 _totalSize
        btn显示逻辑
        1. 当真实加载完成时不更新ui，而是调用此方法生成假数据
        2. 每次生成整点延迟
        3. _totalSize更新fakeDateSize / fileNumber
        4. 生成完成在发起一个完成通知ui更新
        5. 假数据的点击事件： 不
        */
        val m = 1024 * 1024 * 1L
        val fileNumber = (10..23).random()
        println("fileNumber 总大小为： ${fileNumber}")
        val fakeDateSize = (100 * m..2048 * m).random()
        println("fakeDateSize： ${formatSize(fakeDateSize)}")
        for (i in 0 until fileNumber) {
            fakeDataList.add(fakeDateSize / fileNumber)
        }
        var temp = 0L
        fakeDataList.forEach {
            temp += it
        }
        println("fake 总大小为： ${formatSize(temp)}")
    }
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
}