package com.training.ecommerce.data.repository.user

import android.content.Context
import androidx.datastore.preferences.core.edit
import com.training.ecommerce.data.datasourse.datastore.DataStoreKeys.IS_USER_LOGGED_IN
import com.training.ecommerce.data.datasourse.datastore.UserPreferencesDataSourse
import com.training.ecommerce.data.datasourse.datastore.dataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserPreferencesRepositoryImpl(private val userPreferencesDataSourse: UserPreferencesDataSourse):
    UserPreferencesRepository {

    //write to data store
    override suspend fun saveLoginState(isLoggedIn:Boolean){
        userPreferencesDataSourse.saveLoginState(isLoggedIn)
    }

    override suspend fun saveUserID(userId: String) {
        userPreferencesDataSourse.saveUserID(userId)
    }

    override fun getUserID(): Flow<String> {
        return userPreferencesDataSourse.getUserID()
    }

    //read from data store
    override suspend fun isUserLoggedIn(): Flow<Boolean> {
        return userPreferencesDataSourse.isUserLoggedIn()
    }

}