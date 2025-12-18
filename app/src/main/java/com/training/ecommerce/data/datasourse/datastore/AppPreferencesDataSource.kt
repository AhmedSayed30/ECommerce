package com.training.ecommerce.data.datasourse.datastore

import android.content.Context
import android.util.Log
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AppPreferencesDataSource(private val context: Context) {
    suspend fun saveLoginState(isLoggedIn: Boolean){
        Log.d("LOGIN_STATE2", "Saving isLoggedIn = $isLoggedIn")
        context.appDataStore.edit {preferences->
            preferences[DataStoreKeys.IS_USER_LOGGED_IN] = isLoggedIn
        }
    }
    val isLoggedIn : Flow<Boolean> = context.appDataStore.data.map {preferences->
        preferences[DataStoreKeys.IS_USER_LOGGED_IN] ?: false
    }
}