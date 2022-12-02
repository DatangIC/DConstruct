package com.datangic.smartlock.dialog

import android.app.Activity.RESULT_OK
import android.app.Dialog
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.datangic.smartlock.R
import com.datangic.smartlock.utils.REQUEST_ENABLE_BLUETOOTH
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class DialogEnableBluetooth : DialogFragment() {
    private var mDialogBuilder: MaterialAlertDialogBuilder? = null
    private var mDialog: AlertDialog? = null


    companion object {
        private var mFragment: DialogEnableBluetooth? = null
        fun newInstance(): DialogEnableBluetooth {
            if (mFragment == null) {
                mFragment = DialogEnableBluetooth()
            }
            return mFragment!!
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        mDialogBuilder = MaterialAlertDialogBuilder(requireContext(), R.style.AppTheme_MaterialDialog)
        mDialogBuilder?.setIcon(R.drawable.ic_bluetooth_disabled)
        mDialogBuilder?.setTitle(R.string.bluetooth_disabled_title)
        mDialogBuilder?.setMessage(R.string.bluetooth_disabled_info)

        mDialogBuilder?.setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
            run {
                dialog.cancel()
            }
        }

        mDialogBuilder?.setPositiveButton(getString(R.string.bluetooth_disabled_action)) { _, _ ->
            run {
                val enableIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                startActivityForResult(enableIntent, REQUEST_ENABLE_BLUETOOTH)
            }
        }
        mDialog = mDialogBuilder!!.show()
        mDialog?.setCanceledOnTouchOutside(false)
        return mDialog!!
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_ENABLE_BLUETOOTH) {
            if (resultCode == RESULT_OK) {
                mDialog?.dismiss()
            }
        }
    }
}