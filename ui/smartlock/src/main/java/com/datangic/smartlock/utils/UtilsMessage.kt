package com.datangic.smartlock.utils

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Message
import android.os.Parcelable
import android.view.View
import android.widget.Toast
import cn.dttsh.dts1586.DTS1586
import cn.dttsh.dts1586.PARSER_SHARE
import com.datangic.smartlock.R
import com.datangic.smartlock.utils.UtilsBle.Companion.checkMac
import com.datangic.smartlock.utils.UtilsFormat.toHtml
import com.google.android.material.snackbar.Snackbar

object UtilsMessage {
    /**
     * 检测二维码是否正确
     * @return Int 0:二维码有效
     *            -1:分享二维码错误
     *            -2:分享二维码过期
     *            -3:二维码错误
     */
    fun isQrCodeValid(value: String): Pair<Int, String?> {
        return if (Regex("[0-9a-fA-F,:]+").matches(value)) {
            when (value.length) {
                12, 17, 47 -> Pair(0, getMacAddressFromQrCode(value))
                127, 128 -> isShareCodeValid(value)
                else -> Pair(-3, null)
            }
        } else
            Pair(-3, null)
    }


    private fun isShareCodeValid(value: String): Pair<Int, String?> {
        val parserShare = PARSER_SHARE().apply { setShareCode(value) }
        return if (DTS1586.additionCmd(parserShare) == 0) {
            Pair(0, parserShare.mac.checkMac())
        } else {
            Pair(-3, null)
        }
    }

    fun getMacAddressFromQrCode(value: String): String? {
        return when (value.length) {
            in listOf(12, 17) -> value.checkMac()
            47 -> {
                val deviceInfo = value.split(",")
                deviceInfo[1].checkMac()
            }
            else -> null
        }
    }

    fun getMacAddressFromShareCode(value: String): Triple<Int, String, String>? {
        return when (value.length) {
            in listOf(127, 128) -> {
                val parserShare = PARSER_SHARE().apply { setShareCode(value) }
                if (DTS1586.additionCmd(parserShare) == 0) {
                    parserShare.mac.checkMac()
                    Triple(parserShare.userID, parserShare.mac.checkMac(), parserShare.authCode)
                } else {
                    null
                }
            }
            else -> return null
        }
    }

    fun getMacAddressFromDeviceInfo(value: String): Triple<String, String, String>? {
        return when (value.length) {
            47 -> {
                //  "SN,MacAddress,IMEI"
                val deviceInfo = value.split(",")
                if (deviceInfo.size == 3)
                    Triple(deviceInfo[0], deviceInfo[1].checkMac(), deviceInfo[2])
                else null
            }
            else -> null
        }
    }

    fun displaySnackBar(view: View, text: Any, actionText: Int = R.string.undo, action: (() -> Unit)? = null) {
        var mSnackBar: Snackbar? = null
        if (text is String) {
            mSnackBar = Snackbar.make(view, text.toHtml(), Snackbar.LENGTH_LONG)
        } else if (text is Int) {
            mSnackBar = Snackbar.make(view, view.resources.getString(text).toHtml(), Snackbar.LENGTH_LONG)
        }
        action?.let { mSnackBar?.setAction(actionText) { it() } }
        mSnackBar?.show()
    }

    fun displayToast(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }

    @SuppressLint("QueryPermissionsNeeded")
    fun share(context: Context, shareContext: Any) {
//        val targetApp =
//                arrayOf(
//                        "com.tencent.mm.ui.tools.ShareImgUI",
//                        "com.tencent.mm.ui.tools.ShareToTimeLineUI",
//                        "com.tencent.mobileqq.activity.JumpActivity")

        val shareIntent = Intent(Intent.ACTION_SEND)

        if (shareContext is String) {
            shareIntent.type = "text/plain" // 设置分享内容的类型：图片
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareContext)
        } else if (shareContext is Uri) {
            shareIntent.type = "image/*" // 设置分享内容的类型：图片
            shareIntent.putExtra(Intent.EXTRA_STREAM, shareContext)
        }
        try {
            val resInfo = context.packageManager.queryIntentActivities(shareIntent, 0)
            if (resInfo.isNotEmpty()) {
                val targetedShareIntents = ArrayList<Intent>()
                for (info in resInfo) {
                    val targeted = Intent(Intent.ACTION_SEND)
                    val activityInfo = info.activityInfo
                    // 如果还需要分享至其它平台，可以打印出具体信息，然后找到对应的Activity名称，填入上面的数组中即可
                    //         println("package = ${activityInfo.packageName}, activity =
                    // ${activityInfo.name}")
                    // 进行过滤（只显示需要分享的平台）
//                    if (targetApp.any { it == activityInfo.name }) {
                    val comp = ComponentName(activityInfo.packageName, activityInfo.name)
                    targeted.component = comp
                    if (shareContext is String) {
                        targeted.type = "text/plain" // 设置分享内容的类型：图片
                        targeted.putExtra(Intent.EXTRA_TEXT, shareContext)
                    } else if (shareContext is Uri) {
                        targeted.type = "image/*" // 设置分享内容的类型：图片
                        targeted.putExtra(Intent.EXTRA_STREAM, shareContext)
                    }
                    targetedShareIntents.add(targeted)
//                    }
                }
                val chooserIntent =
                    Intent.createChooser(targetedShareIntents.removeAt(0), context.getString(R.string.dialog_share))
                if (chooserIntent != null) {
                    chooserIntent.putExtra(
                        Intent.EXTRA_INITIAL_INTENTS,
                        targetedShareIntents.toTypedArray()
                    )
                    context.startActivity(chooserIntent)
                }
            }
        } catch (e: Exception) {
            Logger.e("Share", "Unable to share image, logs : $e")
        }
    }

}