package com.datangic.zxing.analyzer

import android.annotation.SuppressLint
import android.graphics.ImageFormat
import android.media.Image
import android.util.Log
import androidx.camera.core.ImageProxy
import com.datangic.zxing.config.DecodeConfig
import com.datangic.zxing.config.DecodeFormatManager
import com.google.zxing.*
import com.google.zxing.common.GlobalHistogramBinarizer
import com.google.zxing.common.HybridBinarizer
import java.util.*

object ImageAnalyzer {
    private val TAG = ImageAnalyzer::class.simpleName
    fun analyze(image: Image): Result? {
        if (image.format == ImageFormat.YUV_420_888) {
            @SuppressLint("UnsafeExperimentalUsageError") val buffer = image.planes[0].buffer
            val data = ByteArray(buffer.remaining())
            buffer[data]
            return analyze(data, image.width, image.height)
        }
        return null

    }

    fun analyze(image: ImageProxy): Result? {
        if (image.format == ImageFormat.YUV_420_888) {
            @SuppressLint("UnsafeExperimentalUsageError") val buffer = image.planes[0].buffer
            val data = ByteArray(buffer.remaining())
            buffer[data]
            return analyze(data, image.width, image.height)
        }
        return null

    }

    /**
     * 分析图像数据
     * @param data
     * @param width
     * @param height
     */
    fun analyze(data: ByteArray, width: Int, height: Int, decodeConfig: DecodeConfig = DecodeConfig()): Result? {
        if (decodeConfig.isFullAreaScan) {
            return analyze(data, width, height, 0, 0, width, height)
        } else {
            decodeConfig.analyzeAreaRect?.let { rect ->
                return analyze(data, width, height, rect.left, rect.top, rect.width(), rect.height())
            } ?: let {
                //如果分析区域为空，则通过识别区域比例和相关的偏移量计算出最终的区域进行扫码识别
                val size = (width.coerceAtMost(height) * decodeConfig.areaRectRatio).toInt()
                val left: Int = (width - size) / 2 + decodeConfig.areaRectHorizontalOffset
                val top: Int = (height - size) / 2 + decodeConfig.areaRectHorizontalOffset
                return analyze(data, width, height, left, top, size, size, decodeConfig)
            }
        }

    }

    fun analyze(data: ByteArray?, dataWidth: Int, dataHeight: Int, left: Int, top: Int, width: Int, height: Int, decodeConfig: DecodeConfig = DecodeConfig()): Result? {
        var rawResult: Result? = null
        val mReader = MultiFormatReader()
        try {
            mReader.setHints(decodeConfig.hints)
            val source = PlanarYUVLuminanceSource(data, dataWidth, dataHeight, left, top, width, height, false)
            Log.i(TAG, "PlanarYUVLuminanceSource width=$width height=$height")
            decodeInternal(source, decodeConfig.isMultiDecode, mReader)?.let { result ->
                rawResult = result
            } ?: let {
                if (decodeConfig.isSupportVerticalCode) {
                    val rotatedData = ByteArray(data?.size ?: 0)
                    for (y in 0 until dataHeight) {
                        for (x in 0 until dataWidth)
                            rotatedData[x * dataHeight + dataHeight - y - 1] = data?.get(x + y * dataWidth)
                                    ?: 0
                    }
                    rawResult = decodeInternal(PlanarYUVLuminanceSource(rotatedData, dataHeight, dataWidth, top, left, height, width, false), decodeConfig.isSupportLuminanceInvertMultiDecode, mReader)
                }
                if (decodeConfig.isSupportLuminanceInvert) {
                    rawResult = decodeInternal(source.invert(), decodeConfig.isSupportLuminanceInvertMultiDecode, mReader)
                }
            }
        } catch (e: java.lang.Exception) {
        } finally {
            mReader.reset()
        }
        return rawResult

    }

    private fun decodeInternal(source: LuminanceSource, isMultiDecode: Boolean, reader: MultiFormatReader): Result? {
        var result: Result? = null
        try {
            //采用HybridBinarizer解析
            try {
                result = reader.decodeWithState(BinaryBitmap(HybridBinarizer(source)))
            } catch (e: Exception) {
                Log.e(TAG, "ERROR1=$e")
            }
            if (isMultiDecode && result == null) {
                //如果没有解析成功，再采用GlobalHistogramBinarizer解析一次
                result = reader.decodeWithState(BinaryBitmap(GlobalHistogramBinarizer(source)))
            }
        } catch (e: Exception) {
            Log.e(TAG, "ERROR2=$e")
        }
        return result
    }

}