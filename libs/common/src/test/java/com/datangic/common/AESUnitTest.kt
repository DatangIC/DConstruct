package com.datangic.common

import com.datangic.common.utils.AES
import org.junit.Assert.assertEquals
import org.junit.Test

class AESUnitTest {


    @Test
    fun AESCBC() {
        AES.encrypt("Passwordewc")?.let {
            val aol = AES.decrypt(it)
            assertEquals("Passwordewc", aol)
        } ?: let {
            assertEquals("Password2", "aol")
        }

        AES.encrypt("Passwordewc", mode = AES.Algorithm.AES_ECB_PKCS5)?.let {
            val aol = AES.decrypt(it, mode = AES.Algorithm.AES_ECB_PKCS5)
            assertEquals("Passwordewc", aol)
        } ?: let {
            assertEquals("Password2", "aol")
        }
        AES.encrypt("Passwordewc", mode = AES.Algorithm.AES_ECB_PKCS5)?.let {
            val aol = AES.decrypt(it, mode = AES.Algorithm.AES_ECB_PKCS5)
            assertEquals("Passwordewc", aol)
        } ?: let {
            assertEquals("Password2", "aol")
        }
    }

}