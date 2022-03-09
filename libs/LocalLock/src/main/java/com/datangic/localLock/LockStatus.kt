package com.datangic.localLock

import androidx.annotation.IntDef
import com.datangic.localLock.LockStatus.Companion.CONFIRM_PASSWORD
import com.datangic.localLock.LockStatus.Companion.DONE
import com.datangic.localLock.LockStatus.Companion.INPUT_PASSWORD
import com.datangic.localLock.LockStatus.Companion.VERIFICATION_PASSWORD

@IntDef(value = [VERIFICATION_PASSWORD, INPUT_PASSWORD, CONFIRM_PASSWORD, DONE])
@kotlin.annotation.Retention(AnnotationRetention.SOURCE)
annotation class LockStatus {
    companion object {
        const val VERIFICATION_PASSWORD = 105
        const val INPUT_PASSWORD = 106
        const val CONFIRM_PASSWORD = 107
        const val DONE = 100
    }
}