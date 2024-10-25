package com.training.ecommerce.ui.home.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.training.ecommerce.data.repository.user.UserPreferencesRepository
import com.training.ecommerce.data.repository.user.UserPreferencesRepositoryImpl
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class UserViewModel(
    private val userPref: UserPreferencesRepository
) : ViewModel() {
    suspend fun isUserLoggedIn() = userPref.isUserLoggedIn()
    fun setIsLoggedIn(b: Boolean) {
        viewModelScope.launch(IO) {
            userPref.saveLoginState(b)
        }
    }
}

class UserViewModelFactory(
    private val userPref: UserPreferencesRepository):
        ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        if (modelClass.isAssignableFrom(UserViewModel::class.java)){
            @Suppress("UNCHECKED_CAST") return UserViewModel(userPref) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

        }