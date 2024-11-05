package com.training.ecommerce.ui.home

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.app.ActivityOptions
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.animation.AnticipateInterpolator
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.animation.doOnEnd
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import com.training.ecommerce.R
import com.training.ecommerce.data.datasourse.datastore.DataStoreKeys
import com.training.ecommerce.data.datasourse.datastore.UserPreferencesDataSourse
import com.training.ecommerce.data.datasourse.datastore.dataStore
import com.training.ecommerce.data.repository.user.UserPreferencesRepositoryImpl
import com.training.ecommerce.ui.home.viewmodel.UserViewModel
import com.training.ecommerce.ui.home.viewmodel.UserViewModelFactory
import com.training.ecommerce.ui.auth.login.AuthActivity
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {
    private val viewModel : UserViewModel by viewModels {
        UserViewModelFactory(UserPreferencesRepositoryImpl(UserPreferencesDataSourse(this)))
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        initSplashScreen()
        super.onCreate(savedInstanceState)

        lifecycleScope.launch(Main){
            if (viewModel.isUserLoggedIn().first()){
                setContentView(R.layout.activity_main)
                viewModel.setIsLoggedIn(false)
            } else{
                viewModel.setIsLoggedIn(true)
                goToAuthActivity()
            }
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
    }
}