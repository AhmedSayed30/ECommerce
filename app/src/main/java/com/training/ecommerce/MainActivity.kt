package com.training.ecommerce

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import android.view.animation.AnticipateInterpolator
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.animation.doOnEnd
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        initSplashScreen()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    @SuppressLint("ResourceType")
    private fun initSplashScreen() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            installSplashScreen()
            splashScreen.setOnExitAnimationListener {splashScreenView ->

                val slideUp = ObjectAnimator.ofFloat(
                    splashScreenView, View.TRANSLATION_Y,0f,-splashScreenView.height.toFloat()
                )
                slideUp.interpolator= AnticipateInterpolator()
                slideUp.duration = 1000L

                slideUp.doOnEnd { splashScreenView.remove() }

                slideUp.start()
            }
        } else{
            setTheme(R.style.Theme_ECommerce)
        }
    }
}