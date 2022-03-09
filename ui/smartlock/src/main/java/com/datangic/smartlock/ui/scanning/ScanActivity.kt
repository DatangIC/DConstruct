package com.datangic.smartlock.ui.scanning

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.datangic.smartlock.R
import com.datangic.smartlock.databinding.ActivityScanBinding
import com.datangic.smartlock.respositorys.ToolbarRepository
import com.datangic.smartlock.utils.FRAGMENT_ID
import com.datangic.smartlock.utils.SCAN_QRCODE_RESULT
import org.koin.android.ext.android.inject


class ScanActivity : AppCompatActivity() {

    private val TAG = ScanActivity::class.simpleName

    lateinit var binding: ActivityScanBinding
    val mToolbarRepository: ToolbarRepository by inject()
    private lateinit var mNavController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_scan)

        mToolbarRepository.initToolbarWithBack(this, binding.scanToolbar.toolbar)
        binding.scanToolBarData = mToolbarRepository.mScannerActivityToolBar
        mNavController = Navigation.findNavController(this, R.id.nav_scan_fragment)
        mNavController.addOnDestinationChangedListener { _, destination, _ ->
            mToolbarRepository.mScannerActivityToolBar.title = destination.label!!
        }
        when (intent.getIntExtra(FRAGMENT_ID, R.layout.fragment_scanning)) {
            R.layout.fragment_verifying -> {
                mNavController.navigate(R.id.navigation_verifying, VerifyingFragmentArgs(intent.getStringExtra(SCAN_QRCODE_RESULT)).toBundle())
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
}
