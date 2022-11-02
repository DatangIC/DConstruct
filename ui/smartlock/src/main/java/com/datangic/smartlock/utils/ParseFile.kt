package com.datangic.smartlock.utils

import androidx.lifecycle.MutableLiveData
import com.datangic.common.utils.Logger
import com.datangic.smartlock.utils.UtilsFormat.hexStringToBytes
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File
import java.io.IOException
import java.io.RandomAccessFile
import java.util.*
import kotlin.math.floor


class ParseFile {

    val TAG = ParseFile::class.simpleName

    private val TOTAL_CHUNK = "fileTotalChunk"
    private val INDEX_CHUNK = "fileIndexChunk"
    private val DATA_BYTES = "dataBytes"
    private val DATA_BUF = 495 * 1024
    private var PACKET_SIZE = 128
    val mProgressLiveData: MutableLiveData<Pair<String, Float>> = MutableLiveData(Pair("", 0F))

    var file: File? = null

    // 需要获取的文件大小
    var GetTotalSize: Long = 0
    var Offset: Int = 0

    // 总共分的区域大小
    var fileTotalChunk: Int = 0

    // 当前区域
    var fileIndexChunk: Int = 0

    // 缓存数据
    var dataBuf: ByteArray = ByteArray(0)
    var bufTotalChunk: Int = 0
    var bufIndexChunk: Int = 0

    fun clear() {
        mProgressLiveData.value = Pair("", 0F)
        file = null
        // 需要获取的文件大小
        GetTotalSize = 0
        Offset = 0
        // 总共分的区域大小
        fileTotalChunk = 0
        // 当前区域
        fileIndexChunk = 0
        // 缓存数据
        dataBuf = ByteArray(0)
        bufTotalChunk = 0
        bufIndexChunk = 0
    }


    /**
     * 传输大文件
     *
     * @param file 文件路径
     */
    fun setFile(file: File, sha1: String? = null, fileSize: Long = file.length(), offset: Int = 0, packetSize: Int = 128) {
        clear()
        Offset = offset
        this.file = file
        PACKET_SIZE = packetSize
        val fileInfo: Map<String, Any> = loadFile(file, 0, fileSize, offset)
        if (sha1 == null) {
            this.dataBuf = fileInfo[DATA_BYTES] as ByteArray
        } else {
            val data = fileInfo[DATA_BYTES] as ByteArray?
            val mSha1 = sha1.hexStringToBytes()
            if (data != null && mSha1 != null) {
                dataBuf = ByteArray(data.size + mSha1.size)
                System.arraycopy(mSha1, 0, dataBuf, 0, mSha1.size)
                System.arraycopy(data, 0, dataBuf, mSha1.size, data.size)
            }
            Logger.e(TAG, "sha1=${Arrays.toString(mSha1)}}")
        }
        this.fileTotalChunk = fileInfo[TOTAL_CHUNK] as Int
        this.fileIndexChunk = fileInfo[INDEX_CHUNK] as Int
        this.GetTotalSize = fileSize
        val length: Int = dataBuf.size
        bufTotalChunk = if (length % PACKET_SIZE == 0) {
            length / PACKET_SIZE
        } else {
            floor((length / PACKET_SIZE + 1).toDouble()).toInt()
        }
    }

    private fun loadFile(file: File, chunk: Int = 0, size: Long = file.length(), offset: Int = 0): Map<String, Any> {
        val result: MutableMap<String, Any> = HashMap()
        var gCmdBytes: ByteArray? = ByteArray(DATA_BUF)
        var totalChunk = 0
        if (file.isFile) {
            totalChunk = if (size % DATA_BUF == 0L) {
                (size / DATA_BUF).toInt()
            } else {
                floor((size / DATA_BUF + 1).toDouble()).toInt()
            }
            if (chunk < totalChunk) {
                gCmdBytes = getBlock((offset + chunk * DATA_BUF).toLong(), file, DATA_BUF)
            }
        }
        result[TOTAL_CHUNK] = totalChunk
        if (gCmdBytes != null) result[DATA_BYTES] = gCmdBytes else result[DATA_BYTES] = ByteArray(1)
        result[INDEX_CHUNK] = chunk
        return result
    }

    /**
     * getByteArray
     * @param offset
     * @param file
     * @param blockSize
     * @return byteArray
     */
    private fun getBlock(offset: Long, file: File, blockSize: Int): ByteArray? {
        val result = ByteArray(blockSize)
        var accessFile: RandomAccessFile? = null
        try {
            accessFile = RandomAccessFile(file, "r")
            if (offset > 0)
                accessFile.seek(offset)
            return when (val readSize = accessFile.read(result)) {
                -1 -> {
                    null
                }
                blockSize -> {
                    result
                }
                else -> {
                    val tmpByte = ByteArray(readSize)
                    System.arraycopy(result, 0, tmpByte, 0, readSize)
                    tmpByte
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            if (accessFile != null) {
                try {
                    accessFile.close()
                } catch (e1: IOException) {
                }
            }
        }
        return null
    }

    fun getNextPacket(): ByteArray {
        return this.getPacket(getNextIndex())
    }

    fun hasNextPacket(): Boolean {
        return if (this.fileIndexChunk + 1 < this.fileTotalChunk) {
            this.bufTotalChunk > 0 && this.bufIndexChunk <= this.bufTotalChunk
        } else {
            this.bufTotalChunk > 0 && this.bufIndexChunk < this.bufTotalChunk
        }
    }

    private fun getNextIndex(): Int {
        return this.bufIndexChunk++
    }

    private fun getPacket(index: Int): ByteArray {
        val length: Int = dataBuf.size
        val packetSize: Int = if (length > PACKET_SIZE) {
            if (index + 1 >= this.bufTotalChunk) {
                length - index * PACKET_SIZE
            } else {
                PACKET_SIZE
            }
        } else {
            length
        }

        val packet = ByteArray(packetSize)
        System.arraycopy(this.dataBuf, index * PACKET_SIZE, packet, 0, packetSize)
        GlobalScope.launch {
            if (index + 1 >= bufTotalChunk) {
                getNextFileChunk()
            }
            updateProgress()
        }
        return packet
    }

    private fun getNextFileChunk() {
        if (fileIndexChunk + 1 < fileTotalChunk) {
            file?.let {
                Logger.e(TAG, "GetNextFileChunk")
                val fileInfo: Map<String, Any> = loadFile(it, ++fileIndexChunk, GetTotalSize, Offset)
                this.dataBuf = fileInfo[DATA_BYTES] as ByteArray
                val length: Int = dataBuf.size
                this.bufIndexChunk = 0
                bufTotalChunk = if (length % PACKET_SIZE == 0) {
                    length / PACKET_SIZE
                } else {
                    floor((length / PACKET_SIZE + 1).toDouble()).toInt()
                }
            } ?: let {
                Logger.e(TAG, "GetNextFileChunk Error")
            }
        }
    }

    private fun updateProgress() {
        Logger.v(TAG, "dataIndexChunk=$bufIndexChunk")
        Logger.v(TAG, "dataTotalChunk=$bufTotalChunk")
        Logger.v(TAG, "indexChunk=$fileIndexChunk")
        Logger.v(TAG, "totalChunk=$fileTotalChunk")
        Logger.v(TAG, "TotalSize=$GetTotalSize")
        val a: Float = (bufIndexChunk * PACKET_SIZE + DATA_BUF * fileIndexChunk).toFloat()
        val progress = (a / GetTotalSize * 100)
        mProgressLiveData.postValue(Pair(file?.name ?: "", progress))
    }
}