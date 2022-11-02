package com.datangic.smartlock.ui.system

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.datangic.common.utils.Logger
import com.datangic.localLock.LOCKER_RESULT
import com.datangic.localLock.LocalLockResult
import com.datangic.smartlock.R
import com.datangic.smartlock.databinding.ActivitySystemBinding
import com.datangic.smartlock.dialog.MaterialDialog
import com.datangic.smartlock.parcelable.ExtendedBluetoothDevice
import com.datangic.data.DatabaseRepository
import com.datangic.smartlock.respositorys.LocalPasswordRepository
import com.datangic.smartlock.respositorys.ToolbarRepository
import com.datangic.smartlock.ui.scanning.ScanActivity
import com.datangic.smartlock.utils.*
import org.koin.android.ext.android.inject

class SystemActivity : AppCompatActivity() {

    private val TAG = SystemActivity::class.simpleName
    lateinit var mBinding: ActivitySystemBinding
    private val mToolbarRepository: ToolbarRepository by inject()
    private val localPasswordRepository: LocalPasswordRepository by inject()
    private val mDatabase: DatabaseRepository by inject()
    private lateinit var mNavController: NavController
    private var mMenu: Int = 0
    var doRetrieve: ((ExtendedBluetoothDevice?, String?) -> Unit)? = null
    var doNewDevice: ((ExtendedBluetoothDevice) -> Unit)? = null

    var mRetrieveClick: (() -> Unit)? = null
    var count = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_system)
        mBinding.layoutToolbar.stateProgress.visibility = View.GONE
        mBinding.toolBarData = mToolbarRepository.mSystemActivityToolBar
        mToolbarRepository.initToolbarWithBack(this, mBinding.layoutToolbar.toolbar)
        mNavController = Navigation.findNavController(this, R.id.nav_system_fragment)
        mNavController.addOnDestinationChangedListener { _, destination, _ ->
            mToolbarRepository.mSystemActivityToolBar.title = destination.label ?: ""
        }
        when (intent.getIntExtra(FRAGMENT_ID, R.id.navigationDevice)) {
            R.id.navigationDevice -> {
                mMenu = R.menu.toolbar_device_menu
            }
            R.id.navigationSystem -> {
                mNavController.navigate(R.id.navigationSystem)
            }
        }
    }

    override fun onBackPressed() {
        if (mToolbarRepository.mOnBackPressed != null) {
            mToolbarRepository.mOnBackPressed?.let {
                if (it())
                    mToolbarRepository.mOnBackPressed = null
                super.onBackPressed()
            }
        } else {
            super.onBackPressed()
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.retrieve -> {
                mRetrieveClick?.let {
                    it()
                }
//                startActivityForResult(Intent(this, ScanActivity::class.java)
//                        .apply {
//                            putExtra(SCAN_FOR_RETRIEVE, true)
//                        }, REQUEST_RETRIEVE)
            }
        }
        return super.onOptionsItemSelected(item)
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        if (mMenu != 0) {
            menuInflater.inflate(mMenu, menu)
        }
        return super.onCreateOptionsMenu(menu)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_VERIFICATION_PASSWORD -> {
                data?.getParcelableExtra<LocalLockResult>(LOCKER_RESULT)?.let {
                    if (it.success) {
                        localPasswordRepository.saveLocalPassword(it)
                    }
                }
            }
            REQUEST_RETRIEVE -> {
                data?.getParcelableExtra<ExtendedBluetoothDevice>(SCAN_BLE_RESULT)?.let { bluetoothDevice ->
                    Log.e(TAG, bluetoothDevice.toString())
                    if (mDatabase.isDeviceRepeat(bluetoothDevice.device.address)) {
                        MaterialDialog.getAlertDialog(this, message = R.string.dialog_device_exists, isCancel = false).show()
                    } else {
                        doRetrieve?.let {
                            it(bluetoothDevice, null)
                        }
                    }
                }
                data?.getStringExtra(SCAN_QRCODE_RESULT)?.let { value ->
                    Logger.e(TAG, "value = $value")
                    handleScanResult(value, requestCode)
                }
            }
            REQUEST_NEW_DEVICE -> {
                data?.getParcelableExtra<ExtendedBluetoothDevice>(SCAN_BLE_RESULT)?.let { bluetoothDevice ->
                    Log.e(TAG, bluetoothDevice.toString())
                    if (mDatabase.isDeviceRepeat(bluetoothDevice.device.address)) {
                        MaterialDialog.getAlertDialog(this, message = R.string.dialog_device_exists, isCancel = false).show()
                    } else {
                        doNewDevice?.let {
                            it(bluetoothDevice)
                        }
                    }
                }
                data?.getStringExtra(SCAN_QRCODE_RESULT)?.let { value ->
                    handleScanResult(value, requestCode)
                }
            }

            REQUEST_SET_DEVICE_INFO -> {
                data?.getStringExtra(SCAN_QRCODE_RESULT)?.let { value ->
                    handleScanResult(value, requestCode)
                }
            }
        }
    }

    fun handleScanResult(value: String, requestCode: Int) {
        var error = R.string.dialog_error_message_wrong_qr_code
        when (UtilsMessage.isQrCodeValid(value).first) {
            0 -> {
                handleResult(value, requestCode)
                return
            }
            -2 -> error = R.string.dialog_error_qr_code_has_expired
            else -> {
            }
        }
        showErrorDialog(error)
    }

    fun handleResult(value: String, requestCode: Int) {
        when (requestCode) {
            REQUEST_RETRIEVE -> {
                doRetrieve?.let {
                    it(null, UtilsMessage.getMacAddressFromQrCode(value))
                }
            }
            REQUEST_NEW_DEVICE -> {
                startActivity(Intent(this, ScanActivity::class.java)
                    .apply {
                        putExtra(FRAGMENT_ID, R.layout.fragment_verifying)
                        putExtra(SCAN_QRCODE_RESULT, value)
                    })
            }
            REQUEST_SET_DEVICE_INFO -> {
                if (value.length == 47) {
                    startActivity(Intent(this, ScanActivity::class.java)
                        .apply {
                            putExtra(SCAN_FOR_ACTION, REQUEST_SET_DEVICE_INFO)
                            putExtra(SCAN_QRCODE_RESULT, value)
                        })
                } else {
                    showErrorDialog(R.string.dialog_error_message_wrong_qr_code)
                }
            }
        }
    }

    private fun showErrorDialog(error: Any) {
        MaterialDialog.getAlertDialog(
            this,
            title = R.string.error,
            message = error,
            isCancel = false,
            isError = true
        ).show()
    }
}