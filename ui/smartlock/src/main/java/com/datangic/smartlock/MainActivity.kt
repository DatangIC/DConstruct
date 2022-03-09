package com.datangic.smartlock

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.setupWithNavController
import com.alibaba.android.arouter.facade.annotation.Route
import com.datangic.libs.base.Router
import com.datangic.localLock.*
import com.datangic.data.database.table.Device
import com.datangic.smartlock.databinding.ActivityMainBinding
import com.datangic.smartlock.dialog.MaterialDialog
import com.datangic.smartlock.preference.LanguageHelper
import com.datangic.smartlock.respositorys.DatabaseRepository
import com.datangic.smartlock.respositorys.LocalPasswordRepository
import com.datangic.smartlock.ui.scanning.ScanActivity
import com.datangic.smartlock.utils.*
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

@Route(path = Router.MAIN_ACTIVITY)
class MainActivity : AppCompatActivity() {

    private val TAG = MainActivity::class.java.simpleName

    lateinit var mBinding: ActivityMainBinding

    interface FabListener {
        fun hasDevice(): Boolean
        fun onClick()
    }

    var mFabListener: FabListener? = null

    val localPasswordRepository: LocalPasswordRepository by inject()
    val mDatabase: DatabaseRepository by inject()

    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        navController = Navigation.findNavController(this, R.id.nav_host_fragment)
    }

    override fun onStart() {
        super.onStart()
        mBinding.navView.setupWithNavController(navController)
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.navigation_home -> {
                    mBinding.bottomBar.visibility = View.VISIBLE
                    if (mFabListener?.hasDevice() == true) {
                        mBinding.homeFab.show()
                    } else {
                        mBinding.homeFab.hide()
                    }
                }
                R.id.navigation_me -> {
                    mBinding.bottomBar.visibility = View.VISIBLE
                    mBinding.homeFab.hide()
                }
                else -> {
                    mBinding.homeFab.visibility = View.GONE
                    mBinding.bottomBar.visibility = View.GONE
                }
            }

        }
        mBinding.homeFab.setOnClickListener {
            mFabListener?.onClick()
        }
    }

    fun showFab() {
        if (navController.currentDestination?.id == R.id.navigation_home) {
            mBinding.homeFab.show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        lifecycleScope.launch {
            if (requestCode == REQUEST_SCAN_QRCODE_CODE) {
                data?.let { intent ->
                    val value = intent.getStringExtra(SCAN_QRCODE_RESULT)
                    var error = R.string.dialog_error_message_wrong_qr_code
                    if (value != null) {
                        val res = UtilsMessage.isQrCodeValid(value)
                        Logger.i(TAG, "Scan Value =${value} res=${res}")
                        when (res.first) {
                            0 -> {
                                res.second?.let { macAddress ->
                                    if (!hasDevice(this@MainActivity, macAddress)) {
                                        startActivity(Intent(this@MainActivity, ScanActivity::class.java)
                                            .apply {
                                                putExtra(FRAGMENT_ID, R.layout.fragment_verifying)
                                                putExtra(SCAN_QRCODE_RESULT, value)
                                            })
                                    }
                                    return@launch
                                }
                            }
                            -2 -> error = R.string.dialog_error_qr_code_has_expired
                            else -> {
                            }
                        }
                    }
                    MaterialDialog.getAlertDialog(
                        this@MainActivity,
                        title = R.string.error,
                        message = error,
                        isCancel = false,
                        isError = true
                    ).show()
                }
            } else if (requestCode == REQUEST_VERIFICATION_PASSWORD) {
                data?.getParcelableExtra<LocalLockResult>(LOCKER_RESULT)?.let {
                    if (it.success) {
                        localPasswordRepository.saveLocalPassword(it)
                    }
                }
            }
        }
    }

    private fun hasDevice(context: Context, macAddress: String): Boolean {
        val device: Device? = mDatabase.getDeviceByMac(macAddress)
        device?.let {
            MaterialDialog.getAlertDialog(context, message = R.string.dialog_device_exists, isCancel = false).show()
            return true
        } ?: let {
            return false
        }
    }

    override fun onResume() {
        super.onResume()
        LanguageHelper.reSetLanguage(this)
    }
}