package com.datangic.localLock

import androidx.annotation.IntDef
import com.datangic.localLock.LockType.Companion.FINGERPRINT
import com.datangic.localLock.LockType.Companion.PASSWORD
import com.datangic.localLock.LockType.Companion.PATTERN

@IntDef(value = [PATTERN, PASSWORD, FINGERPRINT])
@kotlin.annotation.Retention(AnnotationRetention.SOURCE)
annotation class LockType {
    companion object {
        const val PATTERN = 1001
        const val PASSWORD = 1002
        const val FINGERPRINT = 1003
    }
}