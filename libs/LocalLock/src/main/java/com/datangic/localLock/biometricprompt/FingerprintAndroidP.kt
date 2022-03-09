package com.datangic.localLock.biometricprompt

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.os.CancellationSignal
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.datangic.localLock.R

@RequiresApi(Build.VERSION_CODES.P)
class FingerprintAndroidP : IFingerprint {
    private var fingerprintAndroidP: FingerprintAndroidP? = null

    //指向调用者的指纹回调
    var fingerprintCallback: FingerprintCallback? = null

    private var cancellationSignal: CancellationSignal? = null


    fun newInstance(): FingerprintAndroidP {
        fingerprintAndroidP?.let {
            return it
        } ?: let {
            return FingerprintAndroidP().also { new ->
                fingerprintAndroidP = new
            }
        }
    }

    private val authenticationCallback: BiometricPrompt.AuthenticationCallback = object : BiometricPrompt.AuthenticationCallback() {
        override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {

            super.onAuthenticationError(errorCode, errString)
            if (fingerprintCallback != null) {
                if (errorCode == 5) { //用户取消指纹验证，不必向用户抛提示信息
                    fingerprintCallback?.onCancel()
                    return
                }
            }
        }

        override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
            super.onAuthenticationSucceeded(result)
            fingerprintCallback?.onSucceeded()
        }

        override fun onAuthenticationFailed() {
            super.onAuthenticationFailed()
            fingerprintCallback?.onFailed()
        }

    }

    @SuppressLint("WrongConstant")
    override fun canAuthenticate(context: Context): Boolean {
        when (BiometricManager.from(context).canAuthenticate()) {
            BiometricManager.BIOMETRIC_SUCCESS -> {
                Log.e("TAG", "SUCCESS")
                return true
            }
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> {
                Log.e("TAG", "BIOMETRIC_ERROR_NO_HARDWARE")
                fingerprintCallback?.onHwUnavailable()
                return false
            }

            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> {
                Log.e("TAG", "BIOMETRIC_ERROR_HW_UNAVAILABLE")
                fingerprintCallback?.onHwUnavailable()
                return false
            }
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                Log.e("TAG", "BIOMETRIC_ERROR_NONE_ENROLLED")
                fingerprintCallback?.onNoneEnrolled()
                // Prompts the user to create credentials that your app accepts.
                return false
            }
        }
        return false
    }

    override fun authenticate(context: Fragment, verificationDialogStyleBean: VerificationDialogStyleBean, callback: FingerprintCallback) {
        val cryptoObject: BiometricPrompt.CryptoObject = BiometricPrompt.CryptoObject(CipherHelper.createCipher())
        fingerprintCallback = callback
        /*
         * 初始化 BiometricPrompt.Builder
         */
        val title = if (verificationDialogStyleBean.title.isNullOrEmpty()) context.getString(R.string.fingerprint_verification) else verificationDialogStyleBean.title
                ?: ""
        val cancelText = if (verificationDialogStyleBean.cancelBtnText.isNullOrEmpty())
            context.getString(R.string.cancel) else verificationDialogStyleBean.cancelBtnText ?: ""
        val builder = BiometricPrompt.PromptInfo.Builder()
                .setTitle(title)
                .setNegativeButtonText(cancelText)

        verificationDialogStyleBean.subTitle?.let {
            builder.setSubtitle(it)
        }
        verificationDialogStyleBean.description?.let {
            builder.setDescription(it)
        }

        //构建 BiometricPrompt

        //取消扫描，每次取消后需要重新创建新示例
        cancellationSignal = CancellationSignal()
        cancellationSignal?.setOnCancelListener {}
        //构建 BiometricPrompt
        BiometricPrompt(
                context, ContextCompat.getMainExecutor(context.requireContext()), authenticationCallback
        ).authenticate(builder.build(), cryptoObject)


    }
}