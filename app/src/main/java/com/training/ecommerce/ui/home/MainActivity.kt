package com.training.ecommerce.ui.home

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.app.ActivityOptions
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.AnticipateInterpolator
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.animation.doOnEnd
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import com.training.ecommerce.R
import com.training.ecommerce.databinding.ActivityMainBinding
import com.training.ecommerce.ui.common.viewmodel.UserViewModel
import com.training.ecommerce.ui.common.viewmodel.UserViewModelFactory
import com.training.ecommerce.ui.auth.AuthActivity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking


class MainActivity : AppCompatActivity() {
    private val userVModer : UserViewModel by viewModels {
        UserViewModelFactory(this)
    }
    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        initSplashScreen()
        Log.d(TAG, "onCreate: ${runBlocking { userVModer.isUserLoggedIn().first()}}")

        val isLoggedIn = runBlocking { userVModer.isUserLoggedIn().first() }
        Log.d(TAG, "onCreate: $isLoggedIn")

        if (!isLoggedIn) {
            goToAuthActivity()
            return
        }
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.tvOut.setOnClickListener {
            logOut()
        }

        initViewModel()

    }

    private fun initViewModel() {
        lifecycleScope.launch {
            val userDetails = runBlocking { userVModer.getUserDetails().first() }

            userVModer.userDetailsState.collect {  }
        }
    }

    private fun logOut(){
        lifecycleScope.launch {
            userVModer.logOut()
            Log.d(TAG, "onCreate: ${runBlocking { userVModer.isUserLoggedIn().first()}}")

            goToAuthActivity()
        }
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

    private fun goToAuthActivity() {
        val intent = Intent(this@MainActivity, AuthActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val options = ActivityOptions.makeCustomAnimation(
            this@MainActivity,
            android.R.anim.fade_in,
            android.R.anim.fade_out)
        startActivity(intent, options.toBundle())
        finish()
    }
    companion object {
        private const val TAG = "MainActivity"
    }
}