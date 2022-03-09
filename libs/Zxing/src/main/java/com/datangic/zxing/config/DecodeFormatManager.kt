package com.datangic.zxing.config

import com.google.zxing.BarcodeFormat
import com.google.zxing.DecodeHintType
import java.util.*
import kotlin.collections.ArrayList


object DecodeFormatManager {
    /**
     * 所有的
     */
    val ALL_HINTS: MutableMap<DecodeHintType, Any> = EnumMap(DecodeHintType::class.java)

    /**
     * CODE_128 (最常用的一维码)
     */
    val CODE_128_HINTS = createDecodeHint(BarcodeFormat.CODE_128)

    /**
     * QR_CODE (最常用的二维码)
     */
    val QR_CODE_HINTS = createDecodeHint(BarcodeFormat.QR_CODE)

    /**
     * 一维码
     */
    val ONE_DIMENSIONAL_HINTS: MutableMap<DecodeHintType, Any> = EnumMap(DecodeHintType::class.java)

    /**
     * 二维码
     */
    val TWO_DIMENSIONAL_HINTS: MutableMap<DecodeHintType, Any> = EnumMap(DecodeHintType::class.java)

    /**
     * 默认
     */
    val DEFAULT_HINTS: MutableMap<DecodeHintType, Any> = EnumMap(DecodeHintType::class.java)

    /**
     * 所有支持的[BarcodeFormat]
     * @return
     */
    private val allFormats: List<BarcodeFormat>
        get() {
            val list: MutableList<BarcodeFormat> = ArrayList()
            list.add(BarcodeFormat.AZTEC)
            list.add(BarcodeFormat.CODABAR)
            list.add(BarcodeFormat.CODE_39)
            list.add(BarcodeFormat.CODE_93)
            list.add(BarcodeFormat.CODE_128)
            list.add(BarcodeFormat.DATA_MATRIX)
            list.add(BarcodeFormat.EAN_8)
            list.add(BarcodeFormat.EAN_13)
            list.add(BarcodeFormat.ITF)
            list.add(BarcodeFormat.MAXICODE)
            list.add(BarcodeFormat.PDF_417)
            list.add(BarcodeFormat.QR_CODE)
            list.add(BarcodeFormat.RSS_14)
            list.add(BarcodeFormat.RSS_EXPANDED)
            list.add(BarcodeFormat.UPC_A)
            list.add(BarcodeFormat.UPC_E)
            list.add(BarcodeFormat.UPC_EAN_EXTENSION)
            return list
        }

    /**
     * 二维码
     * 包括如下几种格式：
     * [BarcodeFormat.CODABAR]
     * [BarcodeFormat.CODE_39]
     * [BarcodeFormat.CODE_93]
     * [BarcodeFormat.CODE_128]
     * [BarcodeFormat.EAN_8]
     * [BarcodeFormat.EAN_13]
     * [BarcodeFormat.ITF]
     * [BarcodeFormat.RSS_14]
     * [BarcodeFormat.RSS_EXPANDED]
     * [BarcodeFormat.UPC_A]
     * [BarcodeFormat.UPC_E]
     * [BarcodeFormat.UPC_EAN_EXTENSION]
     * @return
     */
    private val oneDimensionalFormats: List<BarcodeFormat>
        get() {
            val list: MutableList<BarcodeFormat> = ArrayList()
            list.add(BarcodeFormat.CODABAR)
            list.add(BarcodeFormat.CODE_39)
            list.add(BarcodeFormat.CODE_93)
            list.add(BarcodeFormat.CODE_128)
            list.add(BarcodeFormat.EAN_8)
            list.add(BarcodeFormat.EAN_13)
            list.add(BarcodeFormat.ITF)
            list.add(BarcodeFormat.RSS_14)
            list.add(BarcodeFormat.RSS_EXPANDED)
            list.add(BarcodeFormat.UPC_A)
            list.add(BarcodeFormat.UPC_E)
            list.add(BarcodeFormat.UPC_EAN_EXTENSION)
            return list
        }

    /**
     * 二维码
     * 包括如下几种格式：
     * [BarcodeFormat.AZTEC]
     * [BarcodeFormat.DATA_MATRIX]
     * [BarcodeFormat.MAXICODE]
     * [BarcodeFormat.PDF_417]
     * [BarcodeFormat.QR_CODE]
     * @return
     */
    private val twoDimensionalFormats: List<BarcodeFormat>
        get() {
            val list: MutableList<BarcodeFormat> = ArrayList()
            list.add(BarcodeFormat.AZTEC)
            list.add(BarcodeFormat.DATA_MATRIX)
            list.add(BarcodeFormat.MAXICODE)
            list.add(BarcodeFormat.PDF_417)
            list.add(BarcodeFormat.QR_CODE)
            return list
        }

    /**
     * 默认支持的格式
     * 包括如下几种格式：
     * [BarcodeFormat.QR_CODE]
     * [BarcodeFormat.UPC_A]
     * [BarcodeFormat.EAN_13]
     * [BarcodeFormat.CODE_128]
     * @return
     */
    private val defaultFormats: List<BarcodeFormat>
        get() {
            val list: MutableList<BarcodeFormat> = ArrayList()
            list.add(BarcodeFormat.QR_CODE)
            list.add(BarcodeFormat.UPC_A)
            list.add(BarcodeFormat.EAN_13)
            list.add(BarcodeFormat.CODE_128)
            return list
        }

    private fun <T> singletonList(o: T): List<T> {
        return listOf(o)
    }

    /**
     * 支持解码的格式
     * @param barcodeFormats [BarcodeFormat]
     * @return
     */
    fun createDecodeHints(vararg barcodeFormats: BarcodeFormat): Map<DecodeHintType, Any> {
        val hints: MutableMap<DecodeHintType, Any> = EnumMap(DecodeHintType::class.java)
        addDecodeHintTypes(hints, Arrays.asList(*barcodeFormats))
        return hints
    }

    /**
     * 支持解码的格式
     * @param barcodeFormat [BarcodeFormat]
     * @return
     */
    fun createDecodeHint(barcodeFormat: BarcodeFormat): Map<DecodeHintType, Any> {
        val hints: MutableMap<DecodeHintType, Any> = EnumMap(DecodeHintType::class.java)
        addDecodeHintTypes(hints, singletonList(barcodeFormat))
        return hints
    }

    /**
     *
     * @param hints
     * @param formats
     */
    private fun addDecodeHintTypes(hints: MutableMap<DecodeHintType, Any>, formats: List<BarcodeFormat>) {
        // Image is known to be of one of a few possible formats.
        hints[DecodeHintType.POSSIBLE_FORMATS] = formats
        // Spend more time to try to find a barcode; optimize for accuracy, not speed.
        hints[DecodeHintType.TRY_HARDER] = true
        // Specifies what character encoding to use when decoding, where applicable (type String)
        hints[DecodeHintType.CHARACTER_SET] = "UTF-8"
    }

    init {
        //all hints
        addDecodeHintTypes(ALL_HINTS, allFormats)
        //one dimension
        addDecodeHintTypes(ONE_DIMENSIONAL_HINTS, oneDimensionalFormats)
        //Two dimension
        addDecodeHintTypes(TWO_DIMENSIONAL_HINTS, twoDimensionalFormats)
        //default hints
        addDecodeHintTypes(DEFAULT_HINTS, defaultFormats)
    }
}
