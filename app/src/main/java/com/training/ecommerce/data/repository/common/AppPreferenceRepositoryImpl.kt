package com.training.ecommerce.data.repository.common

import com.training.ecommerce.data.datasourse.datastore.AppPreferencesDataSource
import kotlinx.coroutines.flow.Flow

class AppPreferenceRepositoryImpl(private val appPreferencesDataSource: AppPreferencesDataSource):AppPreferenceRepository {
    override suspend fun saveLogInStatus(isLoggedIn: Boolean) {
        appPreferencesDataSource.saveLoginState(isLoggedIn)
    }

    override suspend fun isLogIn(): Flow<Boolean>  = appPreferencesDataSource.isLoggedIn

}