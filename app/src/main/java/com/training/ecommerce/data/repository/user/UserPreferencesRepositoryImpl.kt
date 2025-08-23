package com.training.ecommerce.data.repository.user

import com.training.ecommerce.data.datasourse.datastore.UserPreferencesDataSource
import kotlinx.coroutines.flow.Flow

class UserPreferencesRepositoryImpl(private val userPreferencesDataSource: UserPreferencesDataSource):
    UserPreferencesRepository {

    //write to data store
    override suspend fun saveLoginState(isLoggedIn:Boolean){
        userPreferencesDataSource.saveLoginState(isLoggedIn)
    }

    override suspend fun saveUserID(userId: String) {
        userPreferencesDataSource.saveUserID(userId)
    }

    override fun getUserID(): Flow<String> {
        return userPreferencesDataSource.getUserID()
    }

    //read from data store
    override suspend fun isUserLoggedIn(): Flow<Boolean> {
        return userPreferencesDataSource.isUserLoggedIn()
    }

}