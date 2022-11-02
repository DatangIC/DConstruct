package com.datangic.common.utils

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import com.datangic.common.Config
import com.datangic.common.file.LockFile
import kotlinx.coroutines.runBlocking
import java.io.BufferedWriter
import java.io.File
import java.io.FileInputStream
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.*


@SuppressLint("StaticFieldLeak")
object Logger {
    private val TAG = Logger::class.simpleName
    private var tag: String = ""

    @SuppressLint("SimpleDateFormat")
    private val mLogSDF: SimpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS") // 日志的输出格式

    private var mContext: Context? = null
    private var file: File? = null

    fun init(context: Context) {
        mContext = context
        file = LockFile.getLogFile()
        write("\n\t===============New Logs================\n")
    }

    @SuppressLint("StaticFieldLeak")
    fun v(tag: String?, msg: String = "") {
        runBlocking {
            mContext?.let {
                if (Config.isDebug(it)) {
                    write("Verbose $tag - $msg")
                }
                if (Config.isUsbDebug(it)) {
                    Log.v(tag, msg)
                }
            }
        }
    }

    fun i(tag: String?, msg: String = "") {
        runBlocking {
            mContext?.let {
                if (Config.isDebug(it)) {
                    write("Info $tag - $msg")
                }
                if (Config.isUsbDebug(it)) {
                    Log.i(tag, msg)
                }
            }
        }
    }

    fun e(tag: String?, msg: String = "") {
        runBlocking {
            mContext?.let {
                if (Config.isDebug(it)) {
                    write("Error $tag : $msg")
                }
                if (Config.isUsbDebug(it)) {
                    Log.e(tag, msg)
                }
            }
        }
    }


    private fun write(str: String) {
        val logStr = getFunctionInfo() + "--" + str
        runBlocking {
            try {
                file?.let {
                    val bw = BufferedWriter(FileWriter(it, true))
                    bw.write(logStr + "\r\t")
                    bw.flush()
                }
            } catch (e: java.lang.Exception) {
                Log.e(TAG, "Logger Error")
            }
        }

    }


    /**
     * 获取文件大小
     * @param file
     * @return 文件大小
     */
    private fun getFileSize(file: File): Int {
        var size = 0
        if (file.exists()) {
            try {
                size = FileInputStream(file).available()
            } catch (e: Exception) {
                Log.e(TAG, "getFileSize failure $e")
            }
        }
        return size
    }


    /**
     * 获取当前函数的信息
     *
     * @return 当前函数的信息
     */
    private fun getFunctionInfo(): String? {
        val sts = Thread.currentThread().stackTrace ?: return null
        for (st in sts) {
            if (st.isNativeMethod) {
                continue
            }
            if (st.className == Thread::class.java.name) {
                continue
            }
            if (st.className.contains("Logger")) {
                continue
            }
            tag = st.fileName
            return "[" + mLogSDF.format(Date()) + " " + st.className + " " + st.methodName + " Line:" + st.lineNumber + "]"
        }

        return null
    }

}