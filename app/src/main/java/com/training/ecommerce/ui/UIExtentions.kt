package com.training.ecommerce.ui

import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.training.ecommerce.R

fun View.showSnakeBarError(msg: String) {
   val snackbar= Snackbar.make(this, msg, Snackbar.LENGTH_LONG)
        .setBackgroundTint(ContextCompat.getColor(this.context,R.color.white))
       .setBackgroundTintMode(android.graphics.PorterDuff.Mode.SRC_IN)
        .setAction(this.context.resources.getString(R.string.ok)) {}
            .setActionTextColor(
            ContextCompat.getColor(this.context, R.color.black)
        )

    // Change the message text color
    val snackbarText = snackbar.view.findViewById<TextView>(com.google.android.material.R.id.snackbar_text)
    snackbarText.setTextColor(ContextCompat.getColor(this.context, R.color.black))

    snackbar.show()
}

fun View.showRetrySnakeBarError(msg: String, retry: () -> Unit) {
    Snackbar.make(this, msg, Snackbar.LENGTH_LONG)
        .setAction(this.context.resources.getString(R.string.retry)) {retry.invoke()}.setActionTextColor(
            ContextCompat.getColor(this.context, R.color.white)
        ).show()
}