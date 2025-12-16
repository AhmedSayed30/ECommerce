package com.training.ecommerce.ui.common.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.training.ecommerce.data.datasourse.datastore.AppPreferencesDataSource
import com.training.ecommerce.data.models.Resource
import com.training.ecommerce.data.repository.auth.FirebaseAuthRepository
import com.training.ecommerce.data.repository.auth.FirebaseAuthRepositoryImpl
import com.training.ecommerce.data.repository.common.AppPreferenceRepository
import com.training.ecommerce.data.repository.common.AppPreferenceRepositoryImpl
import com.training.ecommerce.data.repository.user.UserFireStoreRepository
import com.training.ecommerce.data.repository.user.UserFireStoreRepositoryImpl
import com.training.ecommerce.data.repository.user.UserPreferencesRepository
import com.training.ecommerce.data.repository.user.UserPreferencesRepositoryImpl
import com.training.ecommerce.domain.models.toUserDetailsModel
import com.training.ecommerce.domain.models.toUserDetailsPreferences
import com.training.ecommerce.utils.CrashlyticsUtils
import com.training.ecommerce.utils.CrashlyticsUtils.LISTEN_TO_USER_DETAILS
import com.training.ecommerce.utils.UserDetailsException
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class UserViewModel(
    private val appPreferenceRepository: AppPreferenceRepository,
    private val userPreferencesRepository: UserPreferencesRepository,
    private val userFireStoreRepository: UserFireStoreRepository,
    private val firebaseAuthRepository: FirebaseAuthRepository
) : ViewModel() {
    suspend fun isUserLoggedIn() = appPreferenceRepository.isLogIn()

    private val logoutState = MutableSharedFlow<Resource<Unit>>()

    val userDetailsState = getUserDetails().stateIn(
        scope = viewModelScope, SharingStarted.Eagerly, initialValue = null
    )

    init {
        listenToUserDetails()
    }


    // load user data flow
    // we can use this to get user data in the view in main thread so we do not want to wait the data from state
    // note that this flow block the main thread while you get the data every time you call it
    @OptIn(ExperimentalCoroutinesApi::class)
    fun getUserDetails() = userPreferencesRepository.getUserDetails().mapLatest { it.toUserDetailsModel() }

    private fun listenToUserDetails() = viewModelScope.launch {
        val userId = userPreferencesRepository.getUserId().first()
        if (userId.isEmpty()) return@launch
        userFireStoreRepository.getUserDetails(userId).catch {e->
            val msg = e.message ?: "Error listening to user details"
            CrashlyticsUtils.sendCustomLogToCrashlytics<UserDetailsException>(
                msg, LISTEN_TO_USER_DETAILS to msg
            )
            if(e is UserDetailsException) logOut()
        }.collectLatest {resource->
            when (resource){
                is Resource.Success ->{
                    resource.data?.let{
                        userPreferencesRepository.updateUserDetails(it.toUserDetailsPreferences())
                    }
                }
                else->{
                    // Do nothing
                }
            }
        }
    }

    suspend fun logOut() = viewModelScope.launch {
        logoutState.emit(Resource.Loading())
        firebaseAuthRepository.logout()
        userPreferencesRepository.clearUserPreferences()
        appPreferenceRepository.saveLogInStatus(false)
        logoutState.emit(Resource.Success(Unit))
    }
}

class UserViewModelFactory(
    private val context:Context):
        ViewModelProvider.Factory{
            private val appPreferenceRepository = AppPreferenceRepositoryImpl(
                AppPreferencesDataSource(context)
            )
    private val userPreferencesRepository = UserPreferencesRepositoryImpl(context)
    private val userFireStoreRepository = UserFireStoreRepositoryImpl()
    private val firebaseAuthRepository = FirebaseAuthRepositoryImpl()
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        if (modelClass.isAssignableFrom(UserViewModel::class.java)){
            @Suppress("UNCHECKED_CAST") return UserViewModel(appPreferenceRepository,userPreferencesRepository,userFireStoreRepository,firebaseAuthRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

        }