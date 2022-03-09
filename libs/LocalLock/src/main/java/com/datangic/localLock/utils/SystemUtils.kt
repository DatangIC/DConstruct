package com.datangic.localLock.utils

import android.app.Service
import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.view.WindowManager

object SystemUtils {
    /**
     * 获取屏幕的宽和高
     *
     * @param context 参数为上下文对象Context
     * @return 返回值为长度为2int型数组, 其中
     * int[0] -- 表示屏幕的宽度
     * int[1] -- 表示屏幕的高度
     */
    fun getSystemDisplay(context: Context): IntArray {
        //创建保存屏幕信息类
        val dm = DisplayMetrics()
        //获取窗口管理类
        val wm = context.getSystemService(
                Context.WINDOW_SERVICE) as WindowManager
        //获取屏幕信息并保存到DisplayMetrics中
        wm.defaultDisplay.getMetrics(dm)
        //声明数组保存信息
        val displays = IntArray(2)
        displays[0] = dm.widthPixels //屏幕宽度(单位:px)
        displays[1] = dm.heightPixels //屏幕高度
        return displays
    }

    /**
     * @param context
     * @param mills 震动频率
     * @param isRepeat 是否循环执行震动
     */
    fun starVibrate(context: Context, mills: LongArray = longArrayOf(70, 50, 70), isRepeat: Boolean = false) {
        val vib = context.getSystemService(Service.VIBRATOR_SERVICE) as Vibrator
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vib.vibrate(VibrationEffect.createWaveform(mills, intArrayOf(70, 0, 30), -1))
        } else {
            vib.vibrate(mills, if (isRepeat) 1 else -1)
        }
    }



}