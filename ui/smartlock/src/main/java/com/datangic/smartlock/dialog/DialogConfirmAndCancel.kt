package com.datangic.smartlock.dialog

import android.app.Dialog
import android.os.Bundle
import com.datangic.smartlock.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.io.Serializable


class DialogConfirmAndCancel(private val isCancel: Boolean = true, private val isConfirm: Boolean = false, private val isCanceledOnTouchOutside: Boolean = false) : DialogFragmentMessage() {
    private val TAG = DialogConfirmAndCancel::class.simpleName
    var mOnOperateListener: OnOperateListener? = null

    constructor(isCancel: Boolean = true, isConfirm: Boolean = true, isCanceledOnTouchOutside: Boolean = false, onOperateListener: OnOperateListener?) : this(isCancel, isConfirm) {
        mOnOperateListener = onOperateListener
    }

    interface OnOperateListener {
        fun onCancel()
        fun onConfirm()
    }

    companion object {
        fun newInstance(title: Any?, message: Any?, icon: Int? = R.drawable.ic_tips, isCancel: Boolean = true, isConfirm: Boolean = true, isError: Boolean = false, isCanceledOnTouchOutside: Boolean = false, onOperateListener: OnOperateListener? = null): DialogConfirmAndCancel {
            val args = Bundle()
            val fragment: DialogConfirmAndCancel = if (onOperateListener != null) {
                DialogConfirmAndCancel(isCancel, isConfirm, isCanceledOnTouchOutside, onOperateListener)
            } else {
                DialogConfirmAndCancel(isCancel, isConfirm, isCanceledOnTouchOutside)
            }
            args.putSerializable(TITLE, title as Serializable?)
            icon?.let { args.putInt(ICON_RES_ID, it) }
            args.putSerializable(MESSAGE, message as Serializable?)
            args.putBoolean(ERROR, isError)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        dialogBuilder = MaterialAlertDialogBuilder(requireActivity(), if (isError == true) R.style.AppTheme_DialogError else R.style.AppTheme_MaterialDialog)
        icon?.let {
            dialogBuilder?.setIcon(it)
        }
        title?.let {
            when (it) {
                is Int ->
                    dialogBuilder?.setTitle(it)
                is String ->
                    dialogBuilder?.setTitle(it)
                else -> return@let
            }
        }
        message?.let {
            when (it) {
                is Int ->
                    dialogBuilder?.setMessage(it)
                is String ->
                    dialogBuilder?.setMessage(it)
                else -> return@let
            }
        }
        if (isCancel)
            dialogBuilder?.setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
                run {
                    mOnOperateListener?.onCancel()
                    dialog.cancel()
                }
            }
        if (isConfirm)
            dialogBuilder?.setPositiveButton(getString(R.string.confirm)) { dialog, _ ->
                run {
                    mOnOperateListener?.onConfirm()
                    dialog.cancel()
                }
            }
        return super.onCreateDialog(savedInstanceState).apply {
            setCanceledOnTouchOutside(isCanceledOnTouchOutside)
        }
    }

}