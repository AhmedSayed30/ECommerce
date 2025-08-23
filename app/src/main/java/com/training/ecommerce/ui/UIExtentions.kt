package com.training.ecommerce.ui

import android.view.View
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.training.ecommerce.R

fun View.showSnakeBarError(msg: String) {
    Snackbar.make(this, msg, Snackbar.LENGTH_LONG)
        .setBackgroundTint(ContextCompat.getColor(this.context,R.color.blue))
        .setAction(this.context.resources.getString(R.string.ok)) {}.setActionTextColor(
            ContextCompat.getColor(this.context, R.color.black)
        ).show()
}

fun View.showRetrySnakeBarError(msg: String, retry: () -> Unit) {
    Snackbar.make(this, msg, Snackbar.LENGTH_LONG)
        .setAction(this.context.resources.getString(R.string.retry)) {retry.invoke()}.setActionTextColor(
            ContextCompat.getColor(this.context, R.color.white)
        ).show()
}