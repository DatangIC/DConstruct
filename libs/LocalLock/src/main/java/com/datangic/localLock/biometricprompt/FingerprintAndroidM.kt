package com.datangic.localLock.biometricprompt

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.biometric.BiometricManager
import androidx.core.hardware.fingerprint.FingerprintManagerCompat
import androidx.core.os.CancellationSignal
import androidx.fragment.app.Fragment
import com.datangic.localLock.R
import com.datangic.localLock.dialog.MaterialDialog
import com.datangic.localLock.dialog.MaterialDialog.setMessage

@RequiresApi(Build.VERSION_CODES.M)
class FingerprintAndroidM : IFingerprint {
    private var fingerprintAndroidM: FingerprintAndroidM? = null

    //指向调用者的指纹回调
    var fingerprintCallback: FingerprintCallback? = null

    private var fingerprintDialog: AlertDialog? = null


    private var fingerprintManagerCompat: FingerprintManagerCompat? = null

    fun newInstance(): FingerprintAndroidM {
        fingerprintAndroidM?.let {
            return it
        } ?: let { _ ->
            return FingerprintAndroidM().also { new ->
                fingerprintAndroidM = new
            }
        }
    }

    @SuppressLint("ResourceAsColor")
    private val authenticationCallback: FingerprintManagerCompat.AuthenticationCallback = object : FingerprintManagerCompat.AuthenticationCallback() {

        override fun onAuthenticationError(errorCode: Int, errString: CharSequence?) {

            super.onAuthenticationError(errorCode, errString)
            if (fingerprintCallback != null) {
                if (errorCode == 5) { //用户取消指纹验证，不必向用户抛提示信息
                    fingerprintCallback?.onCancel()
                    fingerprintDialog?.setMessage(errString, R.color.red)
                    return
                }
            }
        }


        override fun onAuthenticationHelp(helpCode: Int, helpString: CharSequence?) {
            super.onAuthenticationHelp(helpCode, helpString)
//            fingerprintDialog?.setMessage(helpString, android.R.color.holo_red_dark)
            Log.e("TAG", "Help")
        }

        override fun onAuthenticationSucceeded(result: FingerprintManagerCompat.AuthenticationResult?) {
            super.onAuthenticationSucceeded(result)
            fingerprintDialog?.setMessage(R.string.verified_successfully)
            fingerprintDialog?.dismiss()
            fingerprintCallback?.onSucceeded()
        }


        override fun onAuthenticationFailed() {
            super.onAuthenticationFailed()
            fingerprintDialog?.setMessage(R.string.verification_failed, R.color.red)
            fingerprintCallback?.onFailed()
        }

    }

    @SuppressLint("WrongConstant")
    override fun canAuthenticate(context: Context): Boolean {

        val biometricManager = BiometricManager.from(context)
        when (biometricManager.canAuthenticate()) {
            BiometricManager.BIOMETRIC_SUCCESS -> {

                return true
            }
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> {
                fingerprintCallback?.onHwUnavailable()
                return false
            }

            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> {
                fingerprintCallback?.onHwUnavailable()
                return false
            }
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                fingerprintCallback?.onNoneEnrolled()
                // Prompts the user to create credentials that your app accepts.
            }
            else -> {
                fingerprintCallback?.onHwUnavailable()
            }
        }
        return true
    }

    override fun authenticate(context: Fragment, verificationDialogStyleBean: VerificationDialogStyleBean, callback: FingerprintCallback) {
        val cryptoObject = FingerprintManagerCompat.CryptoObject(CipherHelper.createCipher())
        fingerprintCallback = callback
        fingerprintDialog = MaterialDialog.getFingerprintDialog(context.requireContext())

        fingerprintManagerCompat = FingerprintManagerCompat.from(context.requireContext())
        fingerprintManagerCompat?.let { compat ->
            compat.authenticate(cryptoObject,
                    0,
                    CancellationSignal().apply {
                        fingerprintDialog?.dismiss()
                    },
                    authenticationCallback, null)
        }
        fingerprintDialog?.show()


    }
}