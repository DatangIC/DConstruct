package com.datangic.localLock.dialog

import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.os.Build
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import androidx.annotation.ColorInt
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AlertDialog
import com.datangic.localLock.R
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textview.MaterialTextView

object MaterialDialog {

    interface OnMaterialAlterDialogListener {
        fun onCancel()
        fun onConfirm()
    }


    private val TAG = MaterialDialog::class.simpleName

    private fun MaterialAlertDialogBuilder.setItems(items: Array<Int>, onClickListener: DialogInterface.OnClickListener) {
        val mItems = arrayOfNulls<String>(items.size)
        for (i in items.indices) {
            mItems[i] = this.context.getString(items[i])
        }
        this.setItems(mItems, onClickListener)
    }

    fun AlertDialog.setIcon(icon: Any?) {
        when (icon) {
            is Int ->
                this.setIcon(icon)
            else -> return
        }
    }

    fun AlertDialog.setTitle(title: Any?) {
        when (title) {
            is Int ->
                this.setTitle(title)
            is String ->
                this.setTitle(title)
            else -> return
        }
    }

    @SuppressLint("CutPasteId")
    fun AlertDialog.setMessage(message: Any?, @ColorInt color: Int = 0) {
        when (message) {
            is Int ->
                if (message != 0) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        Html.fromHtml(this.context.getText(message).toString(), Html.FROM_HTML_MODE_COMPACT)
                    } else {
                        Html.fromHtml(this.context.getText(message).toString())
                    }
                } else {
                    null
                }
            is String ->
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    Html.fromHtml(message, Html.FROM_HTML_MODE_COMPACT)
                } else {
                    Html.fromHtml(message)
                }
            else -> null
        }?.let { spanned ->
            this.findViewById<MaterialTextView>(R.id.dialog_tips)?.let { textView ->
                textView.text = spanned
                Log.e(TAG, "Find View spanned=$spanned message =$message")
                if (color != 0) {
                    textView.setTextColor(color)
                }
            } ?: let {
                this.setMessage(spanned)
            }
        }
    }


    private fun MaterialAlertDialogBuilder.setIcon(icon: Any?) {
        when (icon) {
            is Int ->
                this.setIcon(icon)
            else -> return
        }
    }

    fun MaterialAlertDialogBuilder.setTitle(title: Any?) {
        when (title) {
            is Int ->
                if (title != 0)
                    this.setTitle(title)
            is String ->
                this.setTitle(title)
            else -> return
        }
    }


    fun MaterialAlertDialogBuilder.setMessage(message: Any?) {
        when (message) {
            is Int ->
                if (message != 0) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        this.setMessage(Html.fromHtml(this.context.getText(message).toString(), Html.FROM_HTML_MODE_COMPACT))
                    } else {
                        this.setMessage(Html.fromHtml(this.context.getText(message).toString()))
                    }
                }
            is String ->
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    this.setMessage(Html.fromHtml(message, Html.FROM_HTML_MODE_COMPACT))
                } else {
                    this.setMessage(Html.fromHtml(message))
                }
            else -> return
        }
    }


    fun getAlertDialog(context: Context,
                       icon: Int? = R.drawable.ic_tips,
                       title: Any? = R.string.notice,
                       message: Any,
                       isError: Boolean = false,
                       isCancel: Boolean = true,
                       isConfirm: Boolean = true,
                       action: OnMaterialAlterDialogListener? = null
    ): AlertDialog {
        val mDialogBuilder = MaterialAlertDialogBuilder(context, if (isError) R.style.AppTheme_DialogError else R.style.AppTheme_MaterialDialog).apply {
            setTitle(title)
            setMessage(message)
            setIcon(icon)
            setCancelable(true)
        }
        if (isCancel)
            mDialogBuilder.setNegativeButton(context.getString(R.string.cancel)) { dialog, _ ->
                run {
                    action?.onCancel()
                    dialog.cancel()
                }
            }
        if (isConfirm)
            mDialogBuilder.setPositiveButton(context.getString(R.string.confirm)) { dialog, _ ->
                run {
                    action?.onConfirm()
                    dialog.cancel()
                }
            }
        return mDialogBuilder.create()
    }

    @SuppressLint("InflateParams")
    fun getFingerprintDialog(context: Context,
                             isCancel: Boolean = true,
                             action: OnMaterialAlterDialogListener? = null
    ): AlertDialog {
        val layout = LayoutInflater.from(context).inflate(R.layout.fingerprint_dialog, null)
        val mDialogBuilder = MaterialAlertDialogBuilder(context, R.style.AppTheme_MaterialDialog).apply {
            setView(layout)
            setCancelable(true)
        }
        if (isCancel)
            mDialogBuilder.setNegativeButton(context.getString(R.string.cancel)) { dialog, _ ->
                run {
                    action?.onCancel()
                    dialog.cancel()
                }
            }
        return mDialogBuilder.create().apply {
            setOnCancelListener {
                action?.onCancel()
            }
        }
    }

    fun getBottomSheetDialogWithLayout(context: Context, @LayoutRes layoutResId: Int, title: Any? = null, cancelAction: (() -> Unit)? = null): BottomSheetDialog {
        val bottomSheetDialog = BottomSheetDialog(context)
        bottomSheetDialog.setCancelable(true)
        bottomSheetDialog.setContentView(layoutResId)
        bottomSheetDialog.dismissWithAnimation = true
        bottomSheetDialog.behavior.apply {
            isFitToContents = true
            isDraggable = true
            isHideable = true
        }
        when (title) {
            is Int -> bottomSheetDialog.findViewById<MaterialTextView>(R.id.dialog_title)?.setText(title)
            is String ->
                bottomSheetDialog.findViewById<MaterialTextView>(R.id.dialog_title)?.text = title
            else -> {
            }
        }
        bottomSheetDialog.setCanceledOnTouchOutside(true)
        bottomSheetDialog.setOnCancelListener { cancelAction?.let { it() } }
        return bottomSheetDialog
    }

}