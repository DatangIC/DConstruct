package com.datangic.zxing.config

import android.graphics.Rect
import androidx.annotation.FloatRange
import com.google.zxing.DecodeHintType


/**
 * 解码配置：主要用于在扫码识别时，提供一些配置，便于扩展。通过配置可决定内置分析器的能力，从而间接的控制并简化扫码识别的流程
 *
 * >
 * 设置解码 [.setHints]内置的一些解码可参见如下：
 * @see {@link DecodeFormatManager.DEFAULT_HINTS}
 *
 * @see {@link DecodeFormatManager.ALL_HINTS}
 *
 * @see {@link DecodeFormatManager.CODE_128_HINTS}
 *
 * @see {@link DecodeFormatManager.QR_CODE_HINTS}
 *
 * @see {@link DecodeFormatManager.ONE_DIMENSIONAL_HINTS}
 *
 * @see {@link DecodeFormatManager.TWO_DIMENSIONAL_HINTS}
 *
 * @see {@link DecodeFormatManager.DEFAULT_HINTS}
 *
 */
class DecodeConfig {
    var hints: Map<DecodeHintType, Any> = DecodeFormatManager.QR_CODE_HINTS
    /**
     * 是否支持使用多解码
     * @return
     */
    /**
     * 是否支持使用多解码
     */
    var isMultiDecode = true
    /**
     * 是否支持识别反色码，黑白颜色反转
     * @return
     */
    /**
     * 是否支持识别反色码（条码黑白颜色反转的码）
     */
    var isSupportLuminanceInvert = true
    /**
     * 是否支持识别反色码（条码黑白颜色反转的码）使用多解码
     * @return
     */
    /**
     * 是否支持识别反色码（条码黑白颜色反转的码）使用多解码
     */
    var isSupportLuminanceInvertMultiDecode = true
    /**
     * 是否支持扫垂直的条码
     * @return
     */
    /**
     * 是否支持垂直的条码
     */
    var isSupportVerticalCode = false
    /**
     * 是否支持垂直的条码，使用多解码
     * @return
     */
    /**
     * 是否支持垂直的条码，使用多解码
     */
    var isSupportVerticalCodeMultiDecode = true
    /**
     * 需要分析识别区域
     * @return
     */
    /**
     * 需要分析识别区域
     */
    var analyzeAreaRect: Rect? = null
    /**
     * 是否支持全区域扫码识别
     * @return
     */
    /**
     * 是否支持全区域扫码识别
     */
    var isFullAreaScan = true
    /**
     * 识别区域比例，默认0.9，设置的比例最终会在预览区域裁剪基于此比例的一个矩形进行扫码识别
     * @return
     */
    /**
     * 识别区域比例，默认0.9
     */
    var areaRectRatio = 1f
    /**
     * 识别区域垂直方向偏移量
     * @return
     */
    /**
     * 识别区域垂直方向偏移量
     */
    var areaRectVerticalOffset = 0
    /**
     * 识别区域水平方向偏移量
     * @return
     */
    /**
     * 识别区域水平方向偏移量
     */
    var areaRectHorizontalOffset = 0

    /**
     * 设置解码
     * @param hints [DecodeFormatManager]
     *
     * 内置的一些解码可参见如下：
     * @see {@link DecodeFormatManager.DEFAULT_HINTS}
     *
     * @see {@link DecodeFormatManager.ALL_HINTS}
     *
     * @see {@link DecodeFormatManager.CODE_128_HINTS}
     *
     * @see {@link DecodeFormatManager.QR_CODE_HINTS}
     *
     * @see {@link DecodeFormatManager.ONE_DIMENSIONAL_HINTS}
     *
     * @see {@link DecodeFormatManager.TWO_DIMENSIONAL_HINTS}
     *
     * @see {@link DecodeFormatManager.DEFAULT_HINTS}
     *
     * 如果不满足您也可以通过{@link DecodeFormatManager.createDecodeHints
     * @return
     */
    fun setHints(hints: Map<DecodeHintType, Any>): DecodeConfig {
        this.hints = hints
        return this
    }

    /**
     * 设置是否支持识别反色码，黑白颜色反转
     * @param supportLuminanceInvert 默认为`false`，想要增强支持扫码识别反色码时可使用，相应的也会增加性能消耗。
     * @return
     */
    fun setSupportLuminanceInvert(supportLuminanceInvert: Boolean): DecodeConfig {
        isSupportLuminanceInvert = supportLuminanceInvert
        return this
    }

    /**
     * 设置是否支持扫垂直的条码
     * @param supportVerticalCode 默认为`false`，想要增强支持扫码识别垂直的条码时可使用，相应的也会增加性能消耗。
     * @return
     */
    fun setSupportVerticalCode(supportVerticalCode: Boolean): DecodeConfig {
        isSupportVerticalCode = supportVerticalCode
        return this
    }

    /**
     * 是否支持使用多解码
     * @see {@link HybridBinarizer} , {@link GlobalHistogramBinarizer}
     *
     * @param multiDecode 默认为`true`
     * @return
     */
    fun setMultiDecode(multiDecode: Boolean): DecodeConfig {
        isMultiDecode = multiDecode
        return this
    }

    /**
     * 设置是否支持识别反色码（条码黑白颜色反转的码）使用多解码
     * @see {@link HybridBinarizer} , {@link GlobalHistogramBinarizer}
     *
     * @param supportLuminanceInvertMultiDecode  默认为`false`，想要增强支持扫码识别反色码时可使用，相应的也会增加性能消耗。
     * @return
     */
    fun setSupportLuminanceInvertMultiDecode(supportLuminanceInvertMultiDecode: Boolean): DecodeConfig {
        isSupportLuminanceInvertMultiDecode = supportLuminanceInvertMultiDecode
        return this
    }

    /**
     * 设置是否支持垂直的条码，使用多解码
     * @see {@link HybridBinarizer} , {@link GlobalHistogramBinarizer}
     *
     * @param supportVerticalCodeMultiDecode 默认为`false`，想要增强支持扫码识别垂直的条码时可使用，相应的也会增加性能消耗。
     * @return
     */
    fun setSupportVerticalCodeMultiDecode(supportVerticalCodeMultiDecode: Boolean): DecodeConfig {
        isSupportVerticalCodeMultiDecode = supportVerticalCodeMultiDecode
        return this
    }

    /**
     * 设置需要分析识别区域，优先级比识别区域比例高，当设置了指定的分析区域时，识别区域比例和识别区域偏移量相关参数都将无效
     * @param analyzeAreaRect
     *
     * 识别区域可设置的方式有如下几种：
     * [.setFullAreaScan] 设置是否支持全区域扫码识别，优先级比识别区域高
     * [.setAnalyzeAreaRect] 设置需要分析识别区域，优先级比识别区域比例高，当设置了指定的分析区域时，识别区域比例和识别区域偏移量相关参数都将无效
     * [.setAreaRectRatio] 设置识别区域比例，默认0.9，设置的比例最终会在预览区域裁剪基于此比例的一个矩形进行扫码识别，优先级最低
     *
     * 因为[androidx.camera.view.PreviewView]的预览区域是经过裁剪的，所以这里的区域并不是用户所能预览到的区域，而是指Camera预览的真实区域，
     * 您还可以通过[CameraScan.setCameraConfig]去自定义配置[CameraConfig]的配置信息控制预览相关配置信息
     *
     * 即判定区域分析的优先级顺序为:[.setFullAreaScan] -> [.setAnalyzeAreaRect] -> [.setAreaRectRatio]
     *
     * @return
     */
    fun setAnalyzeAreaRect(analyzeAreaRect: Rect?): DecodeConfig {
        this.analyzeAreaRect = analyzeAreaRect
        return this
    }

    /**
     * 设置是否支持全区域扫码识别，优先级比识别区域高
     * @param fullAreaScan 默认为`true`
     *
     * 识别区域可设置的方式有如下几种：
     * [.setFullAreaScan] 设置是否支持全区域扫码识别，优先级比识别区域高
     * [.setAnalyzeAreaRect] 设置需要分析识别区域，优先级比识别区域比例高，当设置了指定的分析区域时，识别区域比例和识别区域偏移量相关参数都将无效
     * [.setAreaRectRatio] 设置识别区域比例，默认0.9，设置的比例最终会在预览区域裁剪基于此比例的一个矩形进行扫码识别，优先级最低
     *
     * 因为[androidx.camera.view.PreviewView]的预览区域是经过裁剪的，所以这里的区域并不是用户所能预览到的区域，而是指Camera预览的真实区域，
     * 您还可以通过[CameraScan.setCameraConfig]去自定义配置[CameraConfig]的配置信息控制预览相关配置信息
     *
     * 即判定区域分析的优先级顺序为:[.setFullAreaScan] -> [.setAnalyzeAreaRect] -> [.setAreaRectRatio]
     * @return
     */
    fun setFullAreaScan(fullAreaScan: Boolean): DecodeConfig {
        isFullAreaScan = fullAreaScan
        return this
    }

    /**
     * 设置识别区域比例，默认0.9，设置的比例最终会在预览区域裁剪基于此比例的一个矩形进行扫码识别，优先级最低
     * @param areaRectRatio
     *
     * 识别区域可设置的方式有如下几种：
     * [.setFullAreaScan] 设置是否支持全区域扫码识别，优先级比识别区域高
     * [.setAnalyzeAreaRect] 设置需要分析识别区域，优先级比识别区域比例高，当设置了指定的分析区域时，识别区域比例和识别区域偏移量相关参数都将无效
     * [.setAreaRectRatio] 设置识别区域比例，默认0.9，设置的比例最终会在预览区域裁剪基于此比例的一个矩形进行扫码识别，优先级最低
     *
     * 因为[androidx.camera.view.PreviewView]的预览区域是经过裁剪的，所以这里的区域并不是用户所能预览到的区域，而是指Camera预览的真实区域，
     * 您还可以通过[CameraScan.setCameraConfig]去自定义配置[CameraConfig]的配置信息控制预览相关配置信息
     *
     * 即判定区域分析的优先级顺序为:[.setFullAreaScan] -> [.setAnalyzeAreaRect] -> [.setAreaRectRatio]
     *
     * @return
     */
    fun setAreaRectRatio(@FloatRange(from = 0.5, to = 1.0) areaRectRatio: Float): DecodeConfig {
        this.areaRectRatio = areaRectRatio
        return this
    }

    /**
     * 设置识别区域垂直方向偏移量
     * @param areaRectVerticalOffset
     * @return
     */
    fun setAreaRectVerticalOffset(areaRectVerticalOffset: Int): DecodeConfig {
        this.areaRectVerticalOffset = areaRectVerticalOffset
        return this
    }

    /**
     * 设置识别区域水平方向偏移量
     * @param areaRectHorizontalOffset
     * @return
     */
    fun setAreaRectHorizontalOffset(areaRectHorizontalOffset: Int): DecodeConfig {
        this.areaRectHorizontalOffset = areaRectHorizontalOffset
        return this
    }

    override fun toString(): String {
        return "DecodeConfig{" +
                "hints=" + hints +
                ", isMultiDecode=" + isMultiDecode +
                ", isSupportLuminanceInvert=" + isSupportLuminanceInvert +
                ", isSupportLuminanceInvertMultiDecode=" + isSupportLuminanceInvertMultiDecode +
                ", isSupportVerticalCode=" + isSupportVerticalCode +
                ", isSupportVerticalCodeMultiDecode=" + isSupportVerticalCodeMultiDecode +
                ", analyzeAreaRect=" + analyzeAreaRect +
                ", isFullAreaScan=" + isFullAreaScan +
                ", areaRectRatio=" + areaRectRatio +
                ", areaRectVerticalOffset=" + areaRectVerticalOffset +
                ", areaRectHorizontalOffset=" + areaRectHorizontalOffset +
                '}'
    }
}