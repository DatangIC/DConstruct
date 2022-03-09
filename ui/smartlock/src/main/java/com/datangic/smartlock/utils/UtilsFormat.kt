package com.datangic.smartlock.utils

import android.annotation.SuppressLint
import android.os.Build
import android.text.Html
import android.text.Spanned
import java.text.ParsePosition
import java.text.SimpleDateFormat
import java.util.*

object UtilsFormat {

    const val DATE_WITH_YEAR = "yyyy-MM-dd HH:mm"
    const val DATE_WITH_YEAR_SECOND = "yyyy-MM-dd HH:mm:SS"
    const val DATA_WITHOUT_TIME = "yyy-MM-dd"

    @SuppressLint("SimpleDateFormat")
    fun Int.toDateString(format: String = "MM-dd HH:mm"): String {
        val sdf = SimpleDateFormat(format)
        return sdf.format(Date(this * 1000L))
    }

    @SuppressLint("SimpleDateFormat")
    fun Long.toDateString(format: String = "MM-dd HH:mm"): String {
        val sdf = SimpleDateFormat(format)
        return sdf.format(Date(this * 1000L))
    }

    @SuppressLint("SimpleDateFormat")
    fun Int.toTimeString(format: String = "HH:mm"): String {
        val sdf = SimpleDateFormat(format)
        return sdf.format(Date(this * 1000L))
    }

    @SuppressLint("SimpleDateFormat")
    fun Int.toStringWithTime(): String {
        val t = this.toString()
        return if (t.length == 1) {
            "0$t"
        } else {
            t
        }
    }

    fun <E> List<E>.getIndex(index: Int): E? {
        return if (this.size > index) {
            this[index]
        } else {
            null
        }
    }

    fun String.hexStringToBytes(): ByteArray? {
        if (this == "") {
            return null
        }
        val up = this.uppercase(Locale.getDefault())
        val length = up.length / 2
        val hexChars = up.toCharArray()
        val d = ByteArray(length)
        for (i in 0 until length) {
            val pos = i * 2
            d[i] = ("0123456789ABCDEF".indexOf(hexChars[pos]) * 16 or "0123456789ABCDEF".indexOf(hexChars[pos + 1])).toByte()
        }
        return d
    }

    @SuppressLint("SimpleDateFormat")
    fun String.toTimeStamp(format: String = "yy-MM-dd HH:mm"): Long {
        return if (this.isNotEmpty()) {
            SimpleDateFormat(format).parse(this, ParsePosition(0)).time
        } else {
            0
        }
    }

    fun String.toHtml(): Spanned {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            Html.fromHtml(this, Html.FROM_HTML_MODE_COMPACT)
        else {
            Html.fromHtml(this)
        }

    }

}
//SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日HH时mm分ss秒");
//// 例如：
//cc_time=1291778220 ;
//long lcc_time = Long.valueOf(cc_time);
//re_StrTime = sdf.format(new Date(lcc_time * 1000L));