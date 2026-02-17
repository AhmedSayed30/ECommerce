package com.training.ecommerce.ui.auth.login.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.training.ecommerce.data.datasourse.datastore.AppPreferencesDataSource
import com.training.ecommerce.data.models.Resource
import com.training.ecommerce.data.models.user.UserDetailsModel
import com.training.ecommerce.data.repository.auth.FirebaseAuthRepository
import com.training.ecommerce.data.repository.auth.FirebaseAuthRepositoryImpl
import com.training.ecommerce.data.repository.common.AppPreferenceRepository
import com.training.ecommerce.data.repository.common.AppPreferenceRepositoryImpl
import com.training.ecommerce.data.repository.user.UserPreferencesRepository
import com.training.ecommerce.data.repository.user.UserPreferencesRepositoryImpl
import com.training.ecommerce.domain.models.toUserDetailsPreferences
import com.training.ecommerce.utils.isValidEmail
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class LoginViewModel(
    private val userPreferencesRepository: UserPreferencesRepository,
    private val appPreferencesRepository: AppPreferenceRepository,
    private val authRepository: FirebaseAuthRepository
): ViewModel() {
    val email = MutableStateFlow("")
    val password = MutableStateFlow("")
    private val _loginState = MutableSharedFlow<Resource<UserDetailsModel>>()
    val loginState: SharedFlow<Resource<UserDetailsModel>> = _loginState.asSharedFlow()

    private val isValidate: Flow<Boolean> = combine(email, password) { email, password ->
        email.isValidEmail() && password.length >= 6
    }

    fun loginWithEmailAndPassword() = viewModelScope.launch(IO) {
        val email = email.value
        val password = password.value
        if (isValidate.first()) {
            handleLoginFlow {
                authRepository.loginWithEmailAndPassword(email, password)
            }
        }
    }

    fun loginWithGoogle(idToken: String) =
        viewModelScope.launch(IO) {
            handleLoginFlow { authRepository.loginWithGoogle(idToken) }
        }

    fun loginWithFacebook(idToken: String) =
        viewModelScope.launch (IO){
            handleLoginFlow { authRepository.loginWithGoogle(idToken) }

        }

    private fun handleLoginFlow(loginFlow: suspend () -> Flow<Resource<UserDetailsModel>>) =
        viewModelScope.launch(IO) {
            loginFlow().collect { resource ->
                when (resource) {
                    is Resource.Success -> {
                        savePreferenceData(resource.data!!)
                        _loginState.emit(Resource.Success(resource.data))
                    }

                    else -> _loginState.emit(resource)
                }
            }
        }

    private suspend fun savePreferenceData(userDetailsModel: UserDetailsModel) {
        appPreferencesRepository.saveLogInStatus(true)
        userPreferencesRepository.updateUserDetails(userDetailsModel.toUserDetailsPreferences())
    }


    companion object{
        private const val TAG = "LoginViewModel"
    }
}
class LoginViewModelFactory(private val contextValue: Context):
    ViewModelProvider.Factory{
    private val userPreferencesRepository= UserPreferencesRepositoryImpl(contextValue)
    private val appPreferencesRepository= AppPreferenceRepositoryImpl(AppPreferencesDataSource(contextValue))
    private val authRepository= FirebaseAuthRepositoryImpl()
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)){
            @Suppress("UNCHECKED_CAST") return LoginViewModel(userPreferencesRepository, appPreferencesRepository, authRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}