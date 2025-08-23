package com.training.ecommerce.ui.common.views

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import com.training.ecommerce.R

class ProgressDialog {
    companion object{
        fun createProgressDialog(context: Context):Dialog{
            val dialog = Dialog(context)
            val inflate =
                LayoutInflater.from(context).inflate(R.layout.progress_dialog_layout,null)
            dialog.setContentView(inflate)
            dialog.setCancelable(false)
            dialog.window!!.setBackgroundDrawableResource(
                android.R.color.transparent)
            return dialog
        }
    }
}