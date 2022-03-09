package com.datangic.zxing

import android.content.Intent
import android.os.Bundle
import android.os.Vibrator
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.datangic.zxing.view.AutoFitTextureView

const val SCAN_QRCODE_RESULT = "SCAN_QRCODE_RESULT"


open class ScanActivity : AppCompatActivity() {
    private val TAG = ScanActivity::class.simpleName
    private val REQUEST_CODE_PHOTO = 0x89

    private lateinit var mTextureView: AutoFitTextureView
    private lateinit var mIvFile: View
    private lateinit var mIvBack: View
    private lateinit var mIvFlashlight: View

    private lateinit var mCamera2Base: Camera2Base


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.camer_capture)
        mTextureView = findViewById(R.id.textureView)
        mIvFile = findViewById(R.id.ivPhoto)
        mIvBack = findViewById(R.id.ivBack)
        mIvFlashlight = findViewById(R.id.ivFlashlight)

        mCamera2Base = Camera2Base(this, mTextureView) { result ->
            if (result.text != null) {
                setForResult(result.text)
            }
        }
        setListener()
    }

    override fun onResume() {
        mCamera2Base.onResume()
        super.onResume()
    }

    override fun onPause() {
        mCamera2Base.onPause()
        super.onPause()
    }


    private fun setListener() {
        mIvBack.setOnClickListener {
            onBackPressed()
        }
        mIvFlashlight.setOnClickListener {
            mIvFlashlight.isSelected = mCamera2Base.setFlash()
        }
        mIvFile.setOnClickListener {
            startReadPhoto()
        }
    }

    open fun startReadPhoto() {
//        if (PermissionUtils.checkPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
        startPhotoCode()
//        } else {
//            PermissionUtils.requestPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE)
//        }
    }

    open fun startPhotoCode() {
        val pickIntent = Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        pickIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*")
        startActivityForResult(pickIntent, REQUEST_CODE_PHOTO)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK && data != null) {
            when (requestCode) {
                REQUEST_CODE_PHOTO -> {
                    parsePhoto(data)

                }
            }
        }
    }

    private fun parsePhoto(data: Intent) {
        val path: String? = UriUtils.getImagePath(this, data)

        if (TextUtils.isEmpty(path)) {
            return
        }
        //异步解析
        asyncThread {
            path?.let { p ->
                try {
                    CodeUtils.parseCode(p)?.let { str ->
                        runOnUiThread {
                            setForResult(str)
                        }
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Error=$e")
                }
            }
        }
    }

    private fun setForResult(string: String) {
        SystemUtils.starVibrate(this)
        val intent = Intent()
        intent.putExtra(SCAN_QRCODE_RESULT, string)
        this.setResult(RESULT_OK, intent)
        this.finish()
    }


    open fun asyncThread(runnable: Runnable) {
        Thread(runnable).start()
    }

}