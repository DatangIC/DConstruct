package com.datangic.localLock

import androidx.annotation.IntDef
import com.datangic.localLock.LockAction.Companion.CHANGE_PASSWORD
import com.datangic.localLock.LockAction.Companion.NEW_PASSWORD
import com.datangic.localLock.LockAction.Companion.VERIFICATION

@IntDef(value = [NEW_PASSWORD, VERIFICATION, CHANGE_PASSWORD])
@kotlin.annotation.Retention(AnnotationRetention.SOURCE)
annotation class LockAction {
    companion object {
        const val NEW_PASSWORD = 101
        const val VERIFICATION = 102
        const val CHANGE_PASSWORD = 103
    }
}