package com.datangic.smartlock.respositorys

import com.datangic.smartlock.R
import com.datangic.smartlock.ble.ReceivedMessageHandle
import com.datangic.smartlock.dialog.MaterialDialog

class SubjectRepository(val mBleManagerApi: BleManagerApiRepository) {


//    private val mConnectErrorDialog
//        get() = MaterialDialog.getAlertDialog(
//            mActivity,
//            message = R.string.ble_link_loss,
//            isError = true,
//            isCancel = true,
//            action = object : MaterialDialog.OnMaterialAlterDialogListener {
//                override fun onCancel() {
//                }
//
//                override fun onConfirm() {
//                    mBleManagerApi.connectWithRegister(
//                        mBleManagerApi.mDefaultDeviceInfo?.second!!,
//                        mActivity,
//                        ReceivedMessageHandle.RegisterType.NORMAL_REGISTER
//                    )
//                }
//            })

}