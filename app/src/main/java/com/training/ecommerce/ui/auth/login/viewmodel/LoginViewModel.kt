package com.training.ecommerce.ui.auth.login.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.training.ecommerce.data.repository.auth.FirebaseAuthRepository
import com.training.ecommerce.data.repository.user.UserPreferencesRepository
import com.training.ecommerce.ui.home.viewmodel.UserViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class LoginViewModel(
   private val userPref: UserPreferencesRepository,
   private val authRepository: FirebaseAuthRepository
): ViewModel() {
    val email = MutableStateFlow("")
    val password = MutableStateFlow("")

    fun login(){
        viewModelScope.launch {
            val email = email.value
            val password = password.value
            if (email.isNullOrBlank()&&password.isNullOrBlank())
            authRepository.loginWithEmailAndPassword(email, password)
        }
    }
companion object{
    private const val TAG = "LoginViewModel"
}
}
class LoginViewModelFactory(
    private val userPref: UserPreferencesRepository,
    private val authRepository: FirebaseAuthRepository):
    ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        if (modelClass.isAssignableFrom(UserViewModel::class.java)){
            @Suppress("UNCHECKED_CAST") return LoginViewModel(userPref, authRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}