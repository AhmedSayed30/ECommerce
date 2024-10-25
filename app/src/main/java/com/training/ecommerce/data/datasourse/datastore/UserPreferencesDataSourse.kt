package com.training.ecommerce.data.datasourse.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserPreferencesDataSourse(private val context: Context) {


    //write to data store
    suspend fun saveLoginState(isLoggedIn:Boolean){
        context.dataStore.edit {preferences->
            preferences[DataStoreKeys.IS_USER_LOGGED_IN] = isLoggedIn
        }
    }

    suspend fun saveUserID(userId: String) {
        context.dataStore.edit { preferences ->
            preferences[DataStoreKeys.USER_ID] = userId
        }
    }

    fun getUserID(): Flow<String> = context.dataStore.data
        .map { preferences ->
            preferences[DataStoreKeys.USER_ID] ?: ""
        }

    //read from data store
    fun isUserLoggedIn(): Flow<Boolean> = context.dataStore.data
            .map {preferences->
                preferences[DataStoreKeys.IS_USER_LOGGED_IN] ?: false
            }

}