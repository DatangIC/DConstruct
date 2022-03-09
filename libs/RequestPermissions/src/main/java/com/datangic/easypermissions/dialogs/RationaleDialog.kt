/*
 * Copyright 2019 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.datangic.easypermissions.dialogs

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.datangic.easypermissions.EasyPermissions
import com.datangic.easypermissions.callbacks.PermissionCallbacks
import com.datangic.easypermissions.callbacks.RationaleCallbacks
import com.datangic.easypermissions.helpers.base.PermissionsHelper
import com.datangic.easypermissions.models.PermissionRequest
import com.google.android.material.dialog.MaterialAlertDialogBuilder

/**
 * Dialog to prompt the user to go to the app's settings screen and enable permissions. If the user
 * clicks 'OK' on the dialog, they are sent to the settings screen. The result is returned to the
 * Activity via [Activity.onActivityResult].
 */
class RationaleDialog(
        private val context: Context,
        private val model: PermissionRequest
) : DialogInterface.OnClickListener {

    private val permissionCallbacks: PermissionCallbacks?
        get() = EasyPermissions.mPermissionCallbacks
    private val rationaleCallbacks: RationaleCallbacks?
        get() = EasyPermissions.mRationaleCallbacks

    /**
     * Display the dialog.
     */
    fun showCompatDialog() {

        val dialogBuilder = MaterialAlertDialogBuilder(context, model.theme)
                .setCancelable(false)
                .setPositiveButton(model.positiveButtonText, this)
                .setNegativeButton(model.negativeButtonText, this)
        model.icon?.let {
            when (it) {
                is Drawable -> dialogBuilder.setIcon(it)
                is Int -> dialogBuilder.setIcon(it)
                else -> return@let
            }
        }
        model.title?.let {
            when (it) {
                is String -> dialogBuilder.setTitle(it)
                is Int -> dialogBuilder.setTitle(it)
                else -> return@let
            }
        }
        model.rationale?.let {
            when (it) {
                is String -> dialogBuilder.setMessage(it)
                is Int -> dialogBuilder.setMessage(it)
                else -> return@let
            }
        }
        dialogBuilder.show()
    }

    fun showDialog() {
        val dialogBuilder = MaterialAlertDialogBuilder(context, model.theme)
                .setCancelable(false)
                .setPositiveButton(model.positiveButtonText, this)
                .setNegativeButton(model.negativeButtonText, this)
        model.icon?.let {
            when (it) {
                is Drawable -> dialogBuilder.setIcon(it)
                is Int -> dialogBuilder.setIcon(it)
                else -> return@let
            }
        }
        model.title?.let {
            when (it) {
                is String -> dialogBuilder.setTitle(it)
                is Int -> dialogBuilder.setTitle(it)
                else -> return@let
            }
        }
        model.rationale?.let {
            when (it) {
                is String -> dialogBuilder.setMessage(it)
                is Int -> dialogBuilder.setMessage(it)
                else -> return@let
            }
        }
        dialogBuilder.show()
    }

    override fun onClick(dialogInterface: DialogInterface?, buttonType: Int) {
        when (buttonType) {
            Dialog.BUTTON_POSITIVE -> {
                rationaleCallbacks?.onRationaleAccepted(model.code)
                when (context) {
                    is Fragment ->
                        PermissionsHelper
                                .newInstance(context)
                                .directRequestPermissions(model.code, model.perms)
                    is Activity ->
                        PermissionsHelper
                                .newInstance(context)
                                .directRequestPermissions(model.code, model.perms)
                    is AppCompatActivity ->
                        PermissionsHelper
                                .newInstance(context)
                                .directRequestPermissions(model.code, model.perms)
                }
            }
            Dialog.BUTTON_NEGATIVE -> {
                rationaleCallbacks?.onRationaleDenied(model.code)
                permissionCallbacks?.onPermissionsDenied(model.code, model.perms.toList())
            }
        }
    }
}
