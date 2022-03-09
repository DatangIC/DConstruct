package com.datangic.localLock.biometricprompt

import android.os.Build
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyPermanentlyInvalidatedException
import android.security.keystore.KeyProperties
import java.io.IOException
import java.security.*
import java.security.cert.CertificateException
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey

object CipherHelper {


    // This can be key name you want. Should be unique for the app.
    private const val KEY_NAME = "com.datangic.fingerprint.CipherHelper"

    // We always use this keystore on Android.
    private const val KEYSTORE_NAME = "AndroidKeyStore"

    // Should be no need to change these values.
    private const val KEY_ALGORITHM = KeyProperties.KEY_ALGORITHM_AES
    private const val BLOCK_MODE = KeyProperties.BLOCK_MODE_CBC
    private const val ENCRYPTION_PADDING = KeyProperties.ENCRYPTION_PADDING_PKCS7

    private var mKeystore: KeyStore = KeyStore.getInstance(KEYSTORE_NAME)

    /**
     * 获得Cipher
     *
     * @return
     */
    fun createCipher(): Cipher {
        return createCipher(true)
    }

    /**
     * 创建一个Cipher，用于 FingerprintManager.CryptoObject 的初始化
     * https://developer.android.google.cn/reference/javax/crypto/Cipher.html
     *
     * @param retry
     * @return
     * @throws Exception
     */
    private fun createCipher(retry: Boolean): Cipher {

        val TRANSFORMATION = "$KEY_ALGORITHM/$BLOCK_MODE/$ENCRYPTION_PADDING"
        val cipher = Cipher.getInstance(TRANSFORMATION) // Cipher c = Cipher.getInstance("DES/CBC/PKCS5Padding");
        try {
            createKey()
            cipher.init(Cipher.ENCRYPT_MODE or Cipher.DECRYPT_MODE, mKeystore.getKey(KEY_NAME, null))
        } catch (e: KeyPermanentlyInvalidatedException) {
            mKeystore.deleteEntry(KEY_NAME)
            if (retry) {
                createCipher(false)
            }
            throw Exception("Could not create the cipher for fingerprint authentication.", e)
        }
        return cipher
    }

    private fun getKey(): Key {
        try {
            mKeystore = KeyStore.getInstance(KEYSTORE_NAME).apply { load(null) }
        } catch (e: KeyStoreException) {
            throw RuntimeException("Failed to get an instance of KeyStore", e)
        }
        if (!mKeystore.isKeyEntry(KEY_NAME)) {
            createKey()
        }
        return mKeystore.getKey(KEY_NAME, null)
    }

    private fun createKey() {
        try {
            mKeystore.load(null)
            val keyGen = KeyGenerator.getInstance(KEY_ALGORITHM, KEYSTORE_NAME)
            val keyGenSpec = KeyGenParameterSpec.Builder(KEY_NAME, KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
                    .setUserAuthenticationRequired(true).apply {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            this.setInvalidatedByBiometricEnrollment(true)
                        }
                    }
                    .build()
            keyGen.run {
                keyGen.init(keyGenSpec)
                keyGen.generateKey()
            }
        } catch (e: Exception) {
            when (e) {
                is NoSuchAlgorithmException,
                is InvalidAlgorithmParameterException,
                is CertificateException,
                is IOException -> throw RuntimeException(e)
                else -> throw e
            }
        }
    }
}