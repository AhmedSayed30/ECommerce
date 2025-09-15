package com.training.ecommerce.ui.auth.login.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.training.ecommerce.data.models.Resource
import com.training.ecommerce.data.repository.auth.FirebaseAuthRepository
import com.training.ecommerce.data.repository.user.UserPreferencesRepository
import com.training.ecommerce.utils.isValidEmail
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class LoginViewModel(
   private val userPref: UserPreferencesRepository,
   private val authRepository: FirebaseAuthRepository
): ViewModel() {
    val email = MutableStateFlow("")
    val password = MutableStateFlow("")
    private val _loginState = MutableSharedFlow<Resource<String>>()
    val loginState : SharedFlow<Resource<String>> = _loginState.asSharedFlow()
    private val isValidate : Flow<Boolean> = combine(email,password){ email, password ->
        email.isValidEmail() && password.length >= 6
    }
    fun login(){
        viewModelScope.launch {
            val email = email.value
            val password = password.value
            if (isValidate.first()){
                viewModelScope.launch {
                    authRepository.loginWithEmailAndPassword(email, password).onEach { resource ->
                        when(resource){
                            is Resource.Success -> {
                                val useId =resource.data ?: "Empty User Id"
                                saveUserSession(useId)
                                _loginState.emit(Resource.Success(useId))
                            }

                            else -> _loginState.emit(resource)
                        }
                    }.launchIn(viewModelScope)
                }
            } else {
                _loginState.emit(Resource.Error(Exception("Invalid Email or Password")))
            }

        }

    }

    fun loginWithGoogle(idToken: String) =
        viewModelScope.launch {
            authRepository.loginWithGoogle(idToken).onEach { resource ->
                when(resource){
                    is Resource.Success -> {
                        val useId =resource.data ?: "Empty User Id"
                        saveUserSession(useId)
                        _loginState.emit(Resource.Success(useId))
                    }

                    else -> _loginState.emit(resource)

                }
            }.launchIn(viewModelScope)
        }

    fun loginWithFacebook(idToken: String) =
        viewModelScope.launch {
            authRepository.loginWithFacebook(idToken).onEach { resource ->
                when(resource){
                    is Resource.Success -> {
                        val useId =resource.data ?: "Empty User Id"
                        saveUserSession(useId)
                        _loginState.emit(Resource.Success(useId))
                    }
                    else -> _loginState.emit(resource)
                }
            }.launchIn(viewModelScope)
        }

    private suspend fun saveUserSession(userId: String) {
        userPref.saveLoginState(true)
        userPref.saveUserID(userId)
    }


companion object{
    private const val TAG = "LoginViewModel"
}
}
class LoginViewModelFactory(
    private val userPref: UserPreferencesRepository,
    private val authRepository: FirebaseAuthRepository):
    ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)){
            @Suppress("UNCHECKED_CAST") return LoginViewModel(userPref, authRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}