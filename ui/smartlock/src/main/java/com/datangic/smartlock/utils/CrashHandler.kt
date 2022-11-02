package com.datangic.smartlock.utils

import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Build
import com.datangic.common.utils.Logger
import java.io.PrintWriter
import java.io.StringWriter
import java.io.Writer
import java.lang.reflect.Field


class CrashHandler : Thread.UncaughtExceptionHandler {
    private val TAG = "CrashHandler"

    /*系统默认的UncaughtException处理类*/
    private val defaultHandler: Thread.UncaughtExceptionHandler by lazy {
        Thread.getDefaultUncaughtExceptionHandler()
    }

    private var context: Context? = null

    /*用来存储设备信息和异常信息*/
    private val info: MutableMap<String, String> = HashMap()

    companion object {
        private val instance: CrashHandler = CrashHandler()
        fun getInstance(): CrashHandler {
            return instance
        }
    }

    fun init(context: Context) {
        this.context = context
        //获取系统默认的UncaughtException处理器
        //设置该CrashHandler为程序的默认处理器
        Thread.setDefaultUncaughtExceptionHandler(this)
    }

    /**
     * 【说明】：当UncaughtException发生时会转入该方法来处理
     *
     */
    override fun uncaughtException(thread: Thread?, ex: Throwable?) {
        if (!handleException(ex)) {
            //如果用户没有处理则让系统默认的异常处理器处理
            if (thread != null && ex != null) {
                defaultHandler.uncaughtException(thread, ex)
            }
        }
    }

    /**
     * 【说明】：自定义错误处理（包括收集错误信息，生成错误日志文件）
     */
    private fun handleException(ex: Throwable?): Boolean {
        if (ex == null) {
            return false
        }
        context?.let { collectDeviceInfo(it) }
        saveCrashInfo2File(ex)
        return true
    }

    /**
     * 【说明】：收集应用参数信息
     */

    private fun collectDeviceInfo(ctx: Context) {
        try {
            val pm: PackageManager = ctx.packageManager  //获取应用包管理者对象
            val pi: PackageInfo = pm.getPackageInfo(ctx.packageName, PackageManager.GET_ACTIVITIES)
            val versionName = if (pi.versionName == null) "null" else pi.versionName
            info["VERSION_NAME"] = versionName
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                info["VERSION_CODE"] = pi.longVersionCode.toString()
            } else {
                info["VERSION_CODE"] = pi.versionCode.toString()
            }
            info["PACKAGE_NAME"] = pi.packageName
        } catch (e: PackageManager.NameNotFoundException) {
            Logger.e(TAG, "an error occurred when collect package info...")
        }
        val fields: Array<Field> = Build::class.java.declaredFields
        for (field in fields) {
            try {
                field.isAccessible = true
                info[field.name] = field.get(null).toString()
            } catch (e: IllegalAccessException) {
                Logger.e(TAG, "an error occurred when collect crash info...")
            }
        }
    }

    /**
     * 【说明】：保存错误信息到指定文件中
     */
    private fun saveCrashInfo2File(ex: Throwable) {
        val sbf = StringBuffer()
        for ((key, value) in info) {
            sbf.append("$key=$value\n")
        }
        val writer: Writer = StringWriter()
        val printWriter = PrintWriter(writer)
        ex.printStackTrace(printWriter)
        var cause = ex.cause
        while (cause != null) {
            cause.printStackTrace(printWriter)
            cause = cause.cause
        }
        printWriter.close()
        val result: String = writer.toString()
        sbf.append(result)
        Logger.e(sbf.toString())
    }
}