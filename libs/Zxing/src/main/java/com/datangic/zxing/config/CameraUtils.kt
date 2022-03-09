package com.datangic.zxing.config

import android.Manifest
import android.util.Size
import android.util.SparseIntArray
import android.view.Surface
import java.util.*

class CameraUtils {

    companion object {
        /**
         * Permissions required to take a picture.
         */
        val CAMERA_PERMISSIONS = arrayOf(
                Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)

        /**
         * Max preview width && height that is guaranteed by Camera2 API
         */
        val MAX_PREVIEW = Size(1920, 1080)

        /**
         * Tolerance when comparing aspect ratios.
         */
        val ASPECT_RATIO_TOLERANCE = 0.005

        /**
         * Conversion from screen rotation to JPEG orientation.
         */
        val ORIENTATIONS = SparseIntArray().apply {
            this.append(Surface.ROTATION_0, 0)
            this.append(Surface.ROTATION_90, 90)
            this.append(Surface.ROTATION_180, 180)
            this.append(Surface.ROTATION_270, 270)
        }

        /**
         * Timeout for the pre-capture sequence.
         */
        val PRECAPTURE_TIMEOUT_MS: Long = 1000


    }

    /**
     * A wrapper for an [AutoCloseable] object that implements reference counting to allow
     * for resource management.
     */
    class RefCountedAutoCloseable<T : AutoCloseable>(obj: T) : AutoCloseable {
        private var mObject: T?
        private var mRefCount: Long = 0

        /**
         * Increment the reference count and return the wrapped object.
         *
         * @return the wrapped object, or null if the object has been released.
         */
        @get:Synchronized
        val andRetain: T?
            get() {
                if (mRefCount < 0) {
                    return null
                }
                mRefCount++
                return mObject
            }

        /**
         * Return the wrapped object.
         *
         * @return the wrapped object, or null if the object has been released.
         */
        @Synchronized
        fun get(): T? {
            return mObject
        }

        /**
         * Decrement the reference count and release the wrapped object if there are no other
         * users retaining this object.
         */
        @Synchronized
        override fun close() {
            if (mRefCount >= 0) {
                mRefCount--
                if (mRefCount < 0) {
                    try {
                        mObject!!.close()
                    } catch (e: Exception) {
                        throw RuntimeException(e)
                    } finally {
                        mObject = null
                    }
                }
            }
        }

        /**
         * Wrap the given object.
         *
         * @param object an object to wrap.
         */
        init {
            mObject = obj
        }
    }

    // Utility classes and methods:
    // *********************************************************************************************
    /**
     * Comparator based on area of the given [Size] objects.
     */
    class CompareSizesByArea : Comparator<Size> {
        override fun compare(lhs: Size, rhs: Size): Int {
            // We cast here to ensure the multiplications won't overflow
            return java.lang.Long.signum(lhs.width.toLong() * lhs.height - rhs.width.toLong() * rhs.height)
        }
    }

    object CameraState {

        /**
         * Camera state: Device is closed.
         */
        val STATE_CLOSED: Int = 0

        /**
         * Camera state: Device is opened, but is not capturing.
         */
        val STATE_OPENED = 1

        /**
         * Camera state: Showing camera preview.
         */
        val STATE_PREVIEW = 2

        /**
         * Camera state: Waiting for 3A convergence before capturing a photo.
         */
        val STATE_WAITING_FOR_3A_CONVERGENCE = 3


    }
}