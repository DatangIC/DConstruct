package com.datangic.localLock.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import com.datangic.localLock.*
import com.datangic.localLock.biometricprompt.FingerprintCallback
import com.datangic.localLock.biometricprompt.FingerprintVerifyManager
import com.datangic.localLock.utils.SystemUtils

class FingerprintLockFragment : Fragment() {
    private val TAG = "FingerprintLockFragment"

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fingerprint_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        view.findViewById<ImageButton>(R.id.fingerprint_view).apply {
            setOnClickListener {
                showFingerprint()
            }
        }
        showFingerprint()
    }

    fun showFingerprint() {
        this.activity?.let {
            FingerprintVerifyManager.Builder(this,
                    fingerprintCallback
            ).build()
        }
    }

    private val fingerprintCallback = object : FingerprintCallback {
        override fun onHwUnavailable() {
            (requireActivity() as LockerActivity).changeVerified()
        }

        override fun onNoneEnrolled() {
            Log.e(TAG, "onNoneEnrolled")
            (requireActivity() as LockerActivity).changeVerified()
        }

        override fun onSucceeded() {
            (requireActivity() as LockerActivity).setResult(LocalLockResult(
                    action = LockAction.VERIFICATION,
                    type = LockType.FINGERPRINT,
                    password = (requireActivity() as LockerActivity).extra?.password ?: "",
                    success = true
            ))
        }

        override fun onFailed() {
            Log.e(TAG, "onFailed")
            SystemUtils.starVibrate(requireContext())
            (requireActivity() as LockerActivity).authError()
        }

        override fun onUsepwd() {
            Log.e(TAG, "onUsepwd")
            (requireActivity() as LockerActivity).changeVerified()
        }

        override fun onCancel() {
            Log.e(TAG, "onCancel")
        }

    }
}