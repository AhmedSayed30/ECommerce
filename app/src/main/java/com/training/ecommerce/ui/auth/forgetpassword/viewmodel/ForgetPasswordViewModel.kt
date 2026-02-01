package com.training.ecommerce.ui.auth.forgetpassword.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.training.ecommerce.data.models.Resource
import com.training.ecommerce.data.repository.auth.FirebaseAuthRepository
import com.training.ecommerce.data.repository.auth.FirebaseAuthRepositoryImpl
import com.training.ecommerce.utils.isValidEmail
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class ForgetPasswordViewModel(private val authRepository: FirebaseAuthRepository): ViewModel() {
    val email = MutableStateFlow("")
    private val _forgetPasswordState = MutableSharedFlow<Resource<String>>()
    val forgetPasswordStatus: SharedFlow<Resource<String>> = _forgetPasswordState.asSharedFlow()

    fun sendUpdatePasswordEmail() = viewModelScope.launch(IO) {
        if (email.value.isValidEmail()){
            authRepository.sendUpdatePasswordEmail(email.value).collect{
                _forgetPasswordState.emit(it)
            }
        } else {
            _forgetPasswordState.emit(Resource.Error(Exception("Invalid email")))
        }
    }


}
class ForgetPasswordViewModelFactory(
    private val authRepository: FirebaseAuthRepository = FirebaseAuthRepositoryImpl()):
    ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ForgetPasswordViewModel::class.java)){
            @Suppress("UNCHECKED_CAST") return ForgetPasswordViewModel(authRepository) as T
        } else {
            throw Exception("Unknown view model")
        }
    }
}