package com.datangic.common.file

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment
import android.util.Log
import androidx.core.content.FileProvider
import com.datangic.common.Config
import com.datangic.common.Config.PACKAGE
import com.datangic.common.utils.Logger
import kotlinx.coroutines.runBlocking
import java.io.*
import java.math.BigInteger
import java.security.MessageDigest
import java.text.SimpleDateFormat
import java.util.*

@SuppressLint("StaticFieldLeak")
object LockFile {
    private val TAG = LockFile::class.simpleName

    @SuppressLint("SimpleDateFormat")
    private val mLogFileSDF: SimpleDateFormat = SimpleDateFormat("yyyy-MM-dd") // 文件名
    private var mContext: Context? = null
    fun init(context: Context) {
        context.also { mContext = it }
        runBlocking {
            deleteOldFile(context)
        }
    }

    private fun getFilesAllName(path: String): Array<File>? {
        val file = File(path)
        return file.listFiles()
    }

    private fun deleteOldFile(context: Context) {
        val pathLog = getFilePath(context, Config.FOLDER_LOG)
        getFilesAllName(pathLog)?.let {
            if (it.size > 5)
                for (i in it) {
                    if (System.currentTimeMillis() - i.lastModified() > 604800 * 1000) {
                        if (i.exists()) {
                            i.delete()
                        }
                    }
                }
        }
        val pathSoftware = getFilePath(context, Config.FOLDER_FIRMWARE)
        getFilesAllName(pathSoftware)?.let {
            for (i in it) {
                if (i.exists()) {
                    i.delete()
                }
            }
        }
    }

    /**
     * 获取文件地址
     */
    private fun getFilePath(context: Context, folder: String): String {
        // 判断是否有SD卡或者外部存储器
        val file: File =
                if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED) {
                    // 有SD卡则使用SD - PS:没SD卡但是有外部存储器，会使用外部存储器
                    // SD\Android\data\包名\files\Log\logs.txt
                    File(context.getExternalFilesDir(folder)?.path.toString() + "/")
                } else {
                    // 没有SD卡或者外部存储器，使用内部存储器
                    // \data\data\包名\files\Log\logs.txt
                    File(context.filesDir.path.toString() + "/$folder/")
                }
        // 若目录不存在则创建目录
        if (!file.exists()) {
            if (file.mkdir()) {
                Log.e(TAG, "file is error")
            }
        }
        return file.path
    }

    /**
     * 获取APP日志文件
     *
     * @return APP日志文件
     */
    fun getLogFile(): File? {
        mContext?.let {
            val logFile = File("${getFilePath(it, Config.FOLDER_LOG)}/${mLogFileSDF.format(Date())}.txt")
            if (!logFile.exists()) {
                try {
                    logFile.createNewFile()
                } catch (e: java.lang.Exception) {
                    Log.e(TAG, "Create log file failure !!! $e")
                }
            }
            return logFile
        }
        return null
    }

    /**
     * @return 固件地址
     */
    fun getSoftwareFile(filename: String): File? {
        mContext?.let {
            val logFile = File("${getFilePath(it, Config.FOLDER_FIRMWARE)}/$filename")
            if (!logFile.exists()) {
                try {
                    logFile.createNewFile()
                } catch (e: java.lang.Exception) {
                    Logger.e(TAG, "Create log file failure !!! $e")
                }
            }
            return logFile
        }
        return null
    }

    /**
     * save bitmap
     */
    fun saveBitMap(context: Context, bitmap: Bitmap, filename: String): Uri? {
        try {
            val file = File("${getFilePath(context, Config.FOLDER_SHARE_BITMAP)}/${filename}.jpg")
            if (!file.exists()) {
                try {
                    file.createNewFile()
                } catch (e: java.lang.Exception) {
                    Logger.e(TAG, "Create log file failure !!! $e")
                }
            }
            val out = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
            out.flush()
            out.close()
            return FileProvider.getUriForFile(context, PACKAGE, file)
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return null
    }


    /**
     * 获取单个文件的MD5值！
     *
     * @param file
     * @return
     */
    fun getFileMD5(file: File): String? {
        if (!file.isFile) {
            return null
        }
        val digest: MessageDigest?
        val inString: FileInputStream?
        val buffer = ByteArray(1024)
        var len: Int
        try {
            digest = MessageDigest.getInstance("MD5")
            inString = FileInputStream(file)
            while (inString.read(buffer, 0, 1024).also { len = it } != -1) {
                digest.update(buffer, 0, len)
            }
            inString.close()
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
        val bigInt = BigInteger(1, digest.digest())
        return bigInt.toString(16)
    }
}