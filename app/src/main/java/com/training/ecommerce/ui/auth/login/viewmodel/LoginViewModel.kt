package com.training.ecommerce.ui.auth.login.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.training.ecommerce.data.repository.user.UserPreferencesRepository
import com.training.ecommerce.ui.home.viewmodel.UserViewModel

class LoginViewModel(
    val userPref: UserPreferencesRepository
): ViewModel() {
companion object{
    private const val TAG = "LoginViewModel"
}
}
class LoginViewModelFactory(
    private val userPref: UserPreferencesRepository):
    ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        if (modelClass.isAssignableFrom(UserViewModel::class.java)){
            @Suppress("UNCHECKED_CAST") return LoginViewModel(userPref) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}