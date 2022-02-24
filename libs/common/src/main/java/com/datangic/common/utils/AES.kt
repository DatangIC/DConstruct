package com.datangic.common.utils

import java.nio.charset.Charset
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

/**
 * Android 加密，详情见：https://developer.android.google.cn/reference/kotlin/javax/crypto/Cipher?hl=en
 * Algorithms 见：https://docs.oracle.com/javase/8/docs/technotes/guides/security/StandardNames.html#Cipher
 */
object AES {

    // CBC(Cipher Block Chaining, 加密快链)模式，PKCS5Padding补码方式
    // AES是加密方式 CBC是工作模式 PKCS5Padding是填充模式
    private const val AesKey = "MeiTang¥STK_AL0"

    //    val AesKey = "9166756720826509"
    private const val AesIv = "DaTang_Construct"


    enum class Algorithm(val algorithm: String, val mode: String, val padding: String) {
        AES_CBC_NO("AES", "CBC", "NoPadding"),
        AES_CBC_PKCS5("AES", "CBC", "PKCS5Padding"),

        AES_ECB_NO("AES", "ECB", "NoPadding"),
        AES_ECB_PKCS5("AES", "ECB", "PKCS5Padding"),
    }


    // 密钥偏移量
    //private static final String mstrIvParameter = "1234567890123456";
    /* key必须为16位，可更改为自己的key */ //String mstrTestKey = "1234567890123456";
    // 加密
    /**
     * AES 加密
     *
     * @param strKey            加密密钥
     * @param strText      待加密内容
     * @param mstrIvParameter   密钥偏移量
     * @return 返回Base64转码后的加密数据
     */
    fun encrypt(
        strText: String,
        strKey: String = AesKey,
        mstrIvParameter: String = AesIv,
        charset: Charset = Charsets.UTF_8,
        mode: Algorithm = Algorithm.AES_CBC_PKCS5
    ): String? {
        try {
            // 创建AES密钥
            val skeySpec = SecretKeySpec(strKey.toByteArray(charset), mode.algorithm)
            // 创建密码器
            val cipher = Cipher.getInstance("${mode.algorithm}/${mode.mode}/${mode.padding}")
            // 创建偏移量
            val iv = IvParameterSpec(mstrIvParameter.toByteArray(charset))
            // 初始化加密器
            if (mode == Algorithm.AES_CBC_PKCS5)
                cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv)
            else
                cipher.init(Cipher.ENCRYPT_MODE, skeySpec)
            // 执行加密操作
            val cipherText = cipher.doFinal(strText.toByteArray(charset))
            return cipherText.toHexString()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }
    // 解密
    /**
     * AES 解密
     *
     * @param strText      待解密内容
     * @param strKey            解密密钥
     * @param mstrIvParameter   偏移量
     * @return 返回Base64转码后的加密数据
     */
    @Throws(Exception::class)
    fun decrypt(
        strText: String,
        strKey: String = AesKey,
        mstrIvParameter: String = AesIv,
        charset: Charset = Charsets.UTF_8,
        mode: Algorithm = Algorithm.AES_CBC_PKCS5
    ): String? {
        try {
            // 创建AES秘钥
            val skeySpec = SecretKeySpec(strKey.toByteArray(charset), mode.algorithm)
            // 创建密码器
            val cipher = Cipher.getInstance("${mode.algorithm}/${mode.mode}/${mode.padding}")
            // 创建偏移量
            val iv = IvParameterSpec(mstrIvParameter.toByteArray(charset))
            // 初始化解密器
            if (mode == Algorithm.AES_CBC_PKCS5)
                cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv)
            else
                cipher.init(Cipher.DECRYPT_MODE, skeySpec)
            val clearText = cipher.doFinal(strText.toHexByteArray())
            return clearText.toString(charset)
        } catch (e: Exception) {
            e.printStackTrace()
            return e.message
        }

    }
}
