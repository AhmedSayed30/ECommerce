package com.training.ecommerce.ui.auth.register.viewmodel

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
import com.training.ecommerce.ui.auth.login.viewmodel.LoginViewModel
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

class RegisterViewModel(
    private val appPreferencesRepository: AppPreferenceRepository,
    private val authRepository: FirebaseAuthRepository,
    private val userPreferencesRepository: UserPreferencesRepository
):ViewModel() {
    val name = MutableStateFlow("")
    val email = MutableStateFlow("")
    val password = MutableStateFlow("")
    val confirmPassword = MutableStateFlow("")

    private val _registerState = MutableSharedFlow<Resource<UserDetailsModel>>()
    val registerState: SharedFlow<Resource<UserDetailsModel>> = _registerState.asSharedFlow()

    private val isValidate: Flow<Boolean> = combine(name,email,password,confirmPassword){ name ,email,password,confirmPassword ->
        name.isNotEmpty() && email.isValidEmail() && password.length >= 6 && confirmPassword.isNotEmpty() && confirmPassword == password
    }

    fun signUp() = viewModelScope.launch(IO){

        val name = name.value
        val email = email.value
        val password = password.value

        if (isValidate.first()){
            authRepository.createUser(name,email,password).collect{ resource ->
                when(resource){
                    is Resource.Success->{
                        savePreferenceData(resource.data!!)
                        _registerState.emit(Resource.Success(resource.data))
                    }
                    else -> _registerState.emit(resource)
                }
            }
        }
    }
    private suspend fun savePreferenceData(userDetailsModel: UserDetailsModel) {
        appPreferencesRepository.saveLogInStatus(true)
        userPreferencesRepository.updateUserDetails(userDetailsModel.toUserDetailsPreferences())
    }
}
class RegisterViewModelFactory(private val contextValue: Context):
    ViewModelProvider.Factory{
    private val userPreferencesRepository= UserPreferencesRepositoryImpl(contextValue)
    private val appPreferencesRepository= AppPreferenceRepositoryImpl(AppPreferencesDataSource(contextValue))
    private val authRepository= FirebaseAuthRepositoryImpl()
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RegisterViewModel::class.java)){
            @Suppress("UNCHECKED_CAST") return RegisterViewModel(appPreferencesRepository, authRepository, userPreferencesRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}