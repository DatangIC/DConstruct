package com.datangic.zxing

import android.app.Service
import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator

object SystemUtils {
    fun starVibrate(context: Context, mills: LongArray = longArrayOf(70, 50, 70), isRepeat: Boolean = false) {
        val vib = context.getSystemService(Service.VIBRATOR_SERVICE) as Vibrator
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vib.vibrate(VibrationEffect.createWaveform(mills, intArrayOf(70, 0, 30), -1))
        } else {
            vib.vibrate(mills, if (isRepeat) 1 else -1)
        }
    }
}