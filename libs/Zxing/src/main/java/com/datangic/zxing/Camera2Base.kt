package com.datangic.zxing

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.*
import android.hardware.SensorManager
import android.hardware.camera2.*
import android.hardware.camera2.CameraCharacteristics.FLASH_INFO_AVAILABLE
import android.hardware.camera2.params.StreamConfigurationMap
import android.media.ImageReader
import android.os.Handler
import android.os.HandlerThread
import android.util.Log
import android.util.Size
import android.view.OrientationEventListener
import android.view.Surface
import android.view.TextureView
import androidx.core.app.ActivityCompat
import com.datangic.zxing.analyzer.ImageAnalyzer
import com.datangic.zxing.config.CameraUtils
import com.datangic.zxing.config.CameraUtils.Companion.ASPECT_RATIO_TOLERANCE
import com.datangic.zxing.config.CameraUtils.Companion.CAMERA_PERMISSIONS
import com.datangic.zxing.config.CameraUtils.Companion.MAX_PREVIEW
import com.datangic.zxing.config.CameraUtils.Companion.ORIENTATIONS
import com.datangic.zxing.view.AutoFitTextureView
import com.google.zxing.Result
import java.util.*
import java.util.concurrent.Semaphore
import java.util.concurrent.TimeUnit
import kotlin.math.abs


class Camera2Base(val mActivity: Activity, val mTextureView: AutoFitTextureView, val mCallBack: (Result) -> Unit) {

    private val TAG = Camera2Base::class.simpleName

    private var mBackgroundThread: HandlerThread? = null

    private var mBackgroundHandler: Handler? = null

    private val mCameraStateLock = Any()

    private var mPreviewSize: Size? = null

    private var mCameraId: String? = null

    private var mCameraDevice: CameraDevice? = null

    private var mYUVImageReader: ImageReader? = null

    private var mCaptureSession: CameraCaptureSession? = null

    private var mPreviewRequestBuilder: CaptureRequest.Builder? = null

    private var mState: Int = CameraUtils.CameraState.STATE_CLOSED

    private var mCharacteristics: CameraCharacteristics? = null

    private var mNoAFRun = false

    private var mAnalyzerTimer: Long = System.currentTimeMillis()

    private val mCameraOpenCloseLock = Semaphore(1)

    private var mOrientationListener: OrientationEventListener = object : OrientationEventListener(mActivity,
            SensorManager.SENSOR_DELAY_NORMAL) {
        override fun onOrientationChanged(orientation: Int) {
            if (mTextureView.isAvailable) {
                configureTransform(mTextureView.width, mTextureView.height)
            }
        }
    }

    fun onResume() {
        startBackgroundThread()
        openCamera()
        if (mTextureView.isAvailable) {
            configureTransform(mTextureView.width, mTextureView.height)
        } else {
            mTextureView.surfaceTextureListener = mSurfaceTextureListener
        }
        if (mOrientationListener.canDetectOrientation()) {
            mOrientationListener.enable()
        }
    }

    fun onPause() {
        mOrientationListener.disable()
        closeCamera()
        stopBackgroundThread()
    }

    private val mSurfaceTextureListener: TextureView.SurfaceTextureListener = object : TextureView.SurfaceTextureListener {
        override fun onSurfaceTextureAvailable(texture: SurfaceTexture, width: Int, height: Int) {
            configureTransform(width, height)
        }

        override fun onSurfaceTextureSizeChanged(texture: SurfaceTexture, width: Int, height: Int) {
            configureTransform(width, height)
        }

        override fun onSurfaceTextureDestroyed(texture: SurfaceTexture): Boolean {
            synchronized(mCameraStateLock) { mPreviewSize = null }
            return true
        }

        override fun onSurfaceTextureUpdated(texture: SurfaceTexture) {}
    }

    /**
     * Starts a background thread and its [Handler].
     */
    private fun startBackgroundThread() {
        mBackgroundThread = HandlerThread("CameraBackground")
        mBackgroundThread?.start()
        synchronized(mCameraStateLock) {
            mBackgroundHandler = mBackgroundThread?.looper?.let { Handler(it) }
        }
    }

    /**
     * Stops the background thread and its [Handler].
     */
    private fun stopBackgroundThread() {
        mBackgroundThread?.quitSafely()
        try {
            mBackgroundThread?.join()
            mBackgroundThread = null
            synchronized(mCameraStateLock) { mBackgroundHandler = null }
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }

    private fun configureTransform(viewWidth: Int, viewHeight: Int) {
        synchronized(mCameraStateLock) {
            val map: StreamConfigurationMap? = mCharacteristics?.get(
                    CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)

            // For still image captures, we always use the largest available size.
            val largestJpeg = Collections.max(map?.let { listOf(*it.getOutputSizes(ImageFormat.JPEG)) },
                    CameraUtils.CompareSizesByArea())
            val supportedSize = map?.getOutputSizes(SurfaceTexture::class.java)

            // Find the rotation of the device relative to the native device orientation.
            val deviceRotation = mActivity.windowManager.defaultDisplay.rotation
            val displaySize = Point()
            mActivity.windowManager.defaultDisplay.getSize(displaySize)

            // Find the rotation of the device relative to the camera sensor's orientation.
            val totalRotation: Int = sensorToDeviceRotation(mCharacteristics, deviceRotation)

            // Swap the view dimensions for calculation as needed if they are rotated relative to
            // the sensor.
            val swappedDimensions = totalRotation == 90 || totalRotation == 270
            var rotatedViewWidth = viewWidth
            var rotatedViewHeight = viewHeight
            var maxPreviewWidth = displaySize.x
            var maxPreviewHeight = displaySize.y
            if (swappedDimensions) {
                rotatedViewWidth = viewHeight
                rotatedViewHeight = viewWidth
                maxPreviewWidth = displaySize.y
                maxPreviewHeight = displaySize.x
            }

            // Preview should not be larger than display size and 1080p.
            if (maxPreviewWidth > MAX_PREVIEW.width) {
                maxPreviewWidth = MAX_PREVIEW.width
            }
            if (maxPreviewHeight > MAX_PREVIEW.height) {
                maxPreviewHeight = MAX_PREVIEW.height
            }

            // Find the best preview size for these view dimensions and configured JPEG size.
            val previewSize: Size = chooseOptimalSize(map!!.getOutputSizes(SurfaceTexture::class.java),
                    rotatedViewWidth, rotatedViewHeight, maxPreviewWidth, maxPreviewHeight,
                    largestJpeg)
            if (swappedDimensions) {
                mTextureView.setAspectRatio(
                        previewSize.height, previewSize.width)
            } else {
                mTextureView.setAspectRatio(
                        previewSize.width, previewSize.height)
            }

            // Find rotation of device in degrees (reverse device orientation for front-facing
            // cameras).
            val rotation: Int = if (mCharacteristics?.get(CameraCharacteristics.LENS_FACING) ==
                    CameraCharacteristics.LENS_FACING_FRONT) (360 + ORIENTATIONS.get(deviceRotation)) % 360 else (360 - ORIENTATIONS.get(deviceRotation)) % 360
            val matrix = Matrix()
            val viewRect = RectF(0F, 0F, viewWidth.toFloat(), viewHeight.toFloat())
            val bufferRect = RectF(0F, 0F, previewSize.height.toFloat(), previewSize.width.toFloat())
            val centerX = viewRect.centerX()
            val centerY = viewRect.centerY()

            if (Surface.ROTATION_90 == deviceRotation || Surface.ROTATION_270 == deviceRotation) {
                bufferRect.offset(centerX - bufferRect.centerX(), centerY - bufferRect.centerY())
                matrix.setRectToRect(viewRect, bufferRect, Matrix.ScaleToFit.FILL)
                val scale = (viewHeight.toFloat() / previewSize.height).coerceAtLeast(viewWidth.toFloat() / previewSize.width)
                matrix.postScale(scale, scale, centerX, centerY)
            }
            matrix.postRotate(rotation.toFloat(), centerX, centerY)
            mTextureView.setTransform(matrix)
            if (mPreviewSize == null || !checkAspectsEqual(previewSize, mPreviewSize!!)) {
                mPreviewSize = previewSize
                if (mState != CameraUtils.CameraState.STATE_CLOSED) {
                    createCameraPreviewSessionLocked()
                }
            }
        }
    }

    /**
     * Return true if the two given [Size]s have the same aspect ratio.
     *
     * @param a first [Size] to compare.
     * @param b second [Size] to compare.
     * @return true if the sizes have the same aspect ratio, otherwise false.
     */
    private fun checkAspectsEqual(a: Size, b: Size): Boolean {
        val aAspect = a.width / a.height.toDouble()
        val bAspect = b.width / b.height.toDouble()
        return abs(aAspect - bAspect) <= ASPECT_RATIO_TOLERANCE
    }

    private fun createCameraPreviewSessionLocked() {
        try {
            // We set up a CaptureRequest.Builder with the output Surface.
            mPreviewRequestBuilder = mCameraDevice?.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
            mTextureView.surfaceTexture?.let { texture ->
                mPreviewSize?.let { size ->
                    texture.setDefaultBufferSize(size.width, size.height)
                }


                // This is the output Surface we need to start preview.
                val surface = Surface(texture)

                mPreviewRequestBuilder?.addTarget(surface)
                mYUVImageReader?.surface?.let { mPreviewRequestBuilder?.addTarget(it) }

                // Here, we create a CameraCaptureSession for camera preview.
                mCameraDevice?.createCaptureSession(listOf(surface,
                        mYUVImageReader?.surface), object : CameraCaptureSession.StateCallback() {
                    override fun onConfigured(cameraCaptureSession: CameraCaptureSession) {
                        synchronized(mCameraStateLock) {
                            try {
                                mPreviewRequestBuilder?.let {
//                                    setup3AControlsLocked(it)
                                    cameraCaptureSession.setRepeatingRequest(
                                            it.build(),
                                            mPreCaptureCallback, mBackgroundHandler)
                                    mState = CameraUtils.CameraState.STATE_PREVIEW
                                }
                                // Finally, we start displaying the camera preview.

                            } catch (e: CameraAccessException) {
                                e.printStackTrace()
                                return
                            } catch (e: IllegalStateException) {
                                e.printStackTrace()
                                return
                            }
                            // When the session is ready, we start displaying the preview.
                            mCaptureSession = cameraCaptureSession
                        }
                    }

                    override fun onConfigureFailed(cameraCaptureSession: CameraCaptureSession) {
                        Log.e(TAG, "Failed to configure camera.")
                    }
                }, mBackgroundHandler)
            }
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }
    }

    private val mPreCaptureCallback: CameraCaptureSession.CaptureCallback = object : CameraCaptureSession.CaptureCallback() {
        private fun process(result: CaptureResult) {
            synchronized(mCameraStateLock) {
                when (mState) {
                    CameraUtils.CameraState.STATE_PREVIEW -> {
                    }
                    CameraUtils.CameraState.STATE_WAITING_FOR_3A_CONVERGENCE -> {
                        // wait timeout, too bad! Begin capture anyway.
                        // After this, the camera will go back to the normal state of preview.
                        mState = CameraUtils.CameraState.STATE_PREVIEW

                    }
                }
            }
        }

        override fun onCaptureProgressed(session: CameraCaptureSession,
                                         request: CaptureRequest,
                                         partialResult: CaptureResult) {
            process(partialResult)
        }

        override fun onCaptureCompleted(session: CameraCaptureSession,
                                        srequest: CaptureRequest,
                                        result: TotalCaptureResult) {
            process(result)
        }
    }

    private fun setup3AControlsLocked(builder: CaptureRequest.Builder) {
        // Enable auto-magical 3A run by camera device
        builder.set(CaptureRequest.CONTROL_MODE,
                CaptureRequest.CONTROL_MODE_AUTO)
        val minFocusDist = mCharacteristics?.get(CameraCharacteristics.LENS_INFO_MINIMUM_FOCUS_DISTANCE)

        // If MINIMUM_FOCUS_DISTANCE is 0, lens is fixed-focus and we need to skip the AF run.
        mNoAFRun = minFocusDist == null || minFocusDist == 0f
        if (!mNoAFRun) {
            // If there is a "continuous picture" mode available, use it, otherwise default to AUTO.
            if (mCharacteristics?.get(
                            CameraCharacteristics.CONTROL_AF_AVAILABLE_MODES)?.contains(
                            CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE) == true) {
                builder.set(CaptureRequest.CONTROL_AF_MODE,
                        CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE)
            } else {
                builder.set(CaptureRequest.CONTROL_AF_MODE,
                        CaptureRequest.CONTROL_AF_MODE_AUTO)
            }
        }

        // If there is an auto-magical flash control mode available, use it, otherwise default to
        // the "on" mode, which is guaranteed to always be available.
        if (mCharacteristics?.get(
                        CameraCharacteristics.CONTROL_AE_AVAILABLE_MODES)?.contains(
                        CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH) == true) {
            builder.set(CaptureRequest.CONTROL_AE_MODE,
                    CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH)
        } else {
            builder.set(CaptureRequest.CONTROL_AE_MODE,
                    CaptureRequest.CONTROL_AE_MODE_ON)
        }

        // If there is an auto-magical white balance control mode available, use it.
        if (mCharacteristics?.get(CameraCharacteristics.CONTROL_AWB_AVAILABLE_MODES)?.contains(
                        CaptureRequest.CONTROL_AWB_MODE_AUTO) == true) {
            // Allow AWB to run auto-magically if this device supports this
            builder.set(CaptureRequest.CONTROL_AWB_MODE,
                    CaptureRequest.CONTROL_AWB_MODE_AUTO)
        }
    }

    private fun chooseOptimalSize(choices: Array<Size>, textureViewWidth: Int,
                                  textureViewHeight: Int, maxWidth: Int, maxHeight: Int, aspectRatio: Size): Size {
        // Collect the supported resolutions that are at least as big as the preview Surface
        val bigEnough: MutableList<Size> = ArrayList()
        // Collect the supported resolutions that are smaller than the preview Surface
        val notBigEnough: MutableList<Size> = ArrayList()
        val w = aspectRatio.width
        val h = aspectRatio.height
        choices.forEach { option ->
            if (option.width <= maxWidth && option.height <= maxHeight && option.height == option.width * h / w) {
                if (option.width >= textureViewWidth &&
                        option.height >= textureViewHeight) {
                    bigEnough.add(option)
                } else {
                    notBigEnough.add(option)
                }
            }
        }

        // Pick the smallest of those big enough. If there is no one big enough, pick the
        // largest of those not big enough.
        return when {
            bigEnough.size > 0 -> {
                Collections.min(bigEnough, CameraUtils.CompareSizesByArea())
            }
            notBigEnough.size > 0 -> {
                Collections.max(notBigEnough, CameraUtils.CompareSizesByArea())
            }
            else -> {
                Log.e(TAG, "Couldn't find any suitable preview size")
                choices[0]
            }
        }
    }

    private fun sensorToDeviceRotation(c: CameraCharacteristics?, deviceOrientation0: Int): Int {
        var deviceOrientation = deviceOrientation0
        val sensorOrientation = c?.get(CameraCharacteristics.SENSOR_ORIENTATION)

        // Get device orientation in degrees
        deviceOrientation = ORIENTATIONS.get(deviceOrientation)

        // Reverse device orientation for front-facing cameras
        if (c?.get(CameraCharacteristics.LENS_FACING) == CameraCharacteristics.LENS_FACING_FRONT) {
            deviceOrientation = -deviceOrientation
        }

        // Calculate desired JPEG orientation relative to camera orientation to make
        // the image upright relative to the device orientation
        return (sensorOrientation ?: 0 - deviceOrientation + 360) % 360
    }

    private fun openCamera() {
        if (!setUpCameraOutputs()) {
            return
        }
        if (!hasAllPermissionsGranted()) {
//            requestCameraPermissions()
            return
        }
        val manager = mActivity.getSystemService(Context.CAMERA_SERVICE) as CameraManager
        try {
            // Wait for any previously running session to finish.
            if (!mCameraOpenCloseLock.tryAcquire(2500, TimeUnit.MILLISECONDS)) {
                throw RuntimeException("Time out waiting to lock camera opening.")
            }
            var cameraId: String?
            var backgroundHandler: Handler?
            synchronized(mCameraStateLock) {
                cameraId = mCameraId
                backgroundHandler = mBackgroundHandler
            }

            // Attempt to open the camera. mStateCallback will be called on the background handler's
            // thread when this succeeds or fails.
            if (ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                return
            }
            manager.openCamera(cameraId!!, mStateCallback, backgroundHandler)
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        } catch (e: InterruptedException) {
            throw RuntimeException("Interrupted while trying to lock camera opening.", e)
        }
    }

    private fun setUpCameraOutputs(): Boolean {
        val manager = mActivity.getSystemService(Context.CAMERA_SERVICE) as CameraManager
        try {
            // Find a CameraDevice that supports RAW captures, and configure state.
            for (cameraId in manager.cameraIdList) {
                val characteristics = manager.getCameraCharacteristics(cameraId)

                // We only use a camera that supports RAW in this sample.
                if (characteristics.get(CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES)?.contains(CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_BACKWARD_COMPATIBLE) == false) {
                    continue
                }
                val map = characteristics.get(
                        CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)
                val largestYUV = Collections.max(
                        listOf(*map!!.getOutputSizes(ImageFormat.YUV_420_888)),
                        CameraUtils.CompareSizesByArea())
                synchronized(mCameraStateLock) {

                    if (mYUVImageReader == null) {
                        mYUVImageReader = ImageReader.newInstance(minOf(largestYUV.width, MAX_PREVIEW.width),
                                minOf(largestYUV.height, MAX_PREVIEW.height), ImageFormat.YUV_420_888,  /*maxImages*/2)
                    }

                    mYUVImageReader?.setOnImageAvailableListener(
                            mOnYUVImageAvailableListener,
                            mBackgroundHandler
                    )
                    mCharacteristics = characteristics
                    mCameraId = cameraId
                }
                return true
            }
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }


        // If we found no suitable cameras for capturing RAW, warn the user.
//        Camera2RawFragment.ErrorDialog.buildErrorDialog("This device doesn't support capturing RAW photos").show(getFragmentManager(), "dialog")
        Log.e(TAG, "This device doesn't support capturing RAW photos")
        return false
    }

    private val mOnYUVImageAvailableListener = ImageReader.OnImageAvailableListener { reader: ImageReader ->


        reader.acquireLatestImage()?.let { image ->
            if (System.currentTimeMillis() - mAnalyzerTimer > 2000L) {
                mAnalyzerTimer = System.currentTimeMillis()
                ImageAnalyzer.analyze(image)?.let { result ->
                    Log.i(TAG, "result=$result")
                    mCallBack(result)
                }
            }
            image.close()
        }

    }

    private fun hasAllPermissionsGranted(): Boolean {
        for (permission in CAMERA_PERMISSIONS) {
            if (ActivityCompat.checkSelfPermission(mActivity, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                return false
            }
        }
        return true
    }

    private val mStateCallback: CameraDevice.StateCallback = object : CameraDevice.StateCallback() {
        override fun onOpened(cameraDevice: CameraDevice) {
            // This method is called when the camera is opened.  We start camera preview here if
            // the TextureView displaying this has been set up.
            synchronized(mCameraStateLock) {
                mState = CameraUtils.CameraState.STATE_OPENED
                mCameraOpenCloseLock.release()
                mCameraDevice = cameraDevice

                // Start the preview session if the TextureView has been set up already.
                if (mPreviewSize != null && mTextureView.isAvailable) {
                    createCameraPreviewSessionLocked()
                }
            }
        }

        override fun onDisconnected(cameraDevice: CameraDevice) {
            synchronized(mCameraStateLock) {
                mState = CameraUtils.CameraState.STATE_CLOSED
                mCameraOpenCloseLock.release()
                cameraDevice.close()
                mCameraDevice = null
            }
        }

        override fun onError(cameraDevice: CameraDevice, error: Int) {
            Log.e(TAG, "Received camera device error: $error")
            synchronized(mCameraStateLock) {
                mState = CameraUtils.CameraState.STATE_CLOSED
                mCameraOpenCloseLock.release()
                cameraDevice.close()
                mCameraDevice = null
            }
            mActivity.finish()
        }
    }

    /**
     * Closes the current [CameraDevice].
     */
    private fun closeCamera() {
        try {
            mCameraOpenCloseLock.acquire()
            synchronized(mCameraStateLock) {
                // Reset state and clean up resources used by the camera.
                // Note: After calling this, the ImageReaders will be closed after any background
                // tasks saving Images from these readers have been completed.
                mState = CameraUtils.CameraState.STATE_CLOSED
                mCaptureSession?.close().also {
                    mCaptureSession = null
                }
                mCameraDevice?.close().also {
                    mCameraDevice = null
                }
                mYUVImageReader?.close().also {
                    mYUVImageReader = null
                }
            }
        } catch (e: InterruptedException) {
            throw java.lang.RuntimeException("Interrupted while trying to lock camera closing.", e)
        } finally {
            mCameraOpenCloseLock.release()
        }
    }

    /**
     * Flashlight
     */
    private var flashState = false
    fun setFlash(): Boolean {
        if (!hasFlash()) return false
        if (!flashState) {
            openFlash()
        } else {
            closeFlash()
        }
        return flashState
    }

    private fun openFlash() {
        try {
            mPreviewRequestBuilder?.let {
                it.set(CaptureRequest.FLASH_MODE, CaptureRequest.FLASH_MODE_TORCH)
                mCaptureSession?.setRepeatingRequest(it.build(), mPreCaptureCallback, mBackgroundHandler)
                flashState = true
            }

        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }
    }

    private fun closeFlash() {
        try {
            mPreviewRequestBuilder?.let {
                it.set(CaptureRequest.FLASH_MODE, CaptureRequest.FLASH_MODE_OFF)

                mCaptureSession?.setRepeatingRequest(it.build(), mPreCaptureCallback, mBackgroundHandler)
                flashState = false
            }

        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }
    }


    private fun hasFlash(): Boolean {
        return mCharacteristics?.get(FLASH_INFO_AVAILABLE) ?: false
    }
}