package com.datangic.localLock.biometricprompt

import android.content.Context
import androidx.fragment.app.Fragment

interface IFingerprint {
    /**
     * 检测指纹硬件是否可用，及是否添加指纹
     * @param context
     * @param callback
     * @return
     */
    fun canAuthenticate(context: Context): Boolean

    /**
     * 初始化并调起指纹验证
     *
     * @param context
     * @param verificationDialogStyleBean
     * @param callback
     */
    fun authenticate(context: Fragment, verificationDialogStyleBean: VerificationDialogStyleBean, callback: FingerprintCallback)
}