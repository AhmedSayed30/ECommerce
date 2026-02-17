package com.training.ecommerce.ui.common.views

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.WindowManager
import androidx.core.graphics.drawable.toDrawable
import com.training.ecommerce.R

class LoadingDialog(context: Context){

    private val dialog: Dialog = Dialog(context, R.style.TransparentDialog)

    init {
        val view = LayoutInflater.from(context).inflate(R.layout.progress_dialog_layout, null)
        dialog.setContentView(view)
        dialog.setCancelable(false)
        dialog.window?.setBackgroundDrawable(Color.TRANSPARENT.toDrawable())
        dialog.window?.setLayout(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
    }

    fun show() {
        if (!dialog.isShowing) {
            dialog.show()
        }
    }

    fun dismiss() {
        if (dialog.isShowing) {
            dialog.dismiss()
        }
    }
}