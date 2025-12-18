package com.training.ecommerce.data.repository.user

import android.content.Context
import com.training.ecommerce.data.datasourse.datastore.appDataStore
import com.training.ecommerce.data.datasourse.datastore.userDetailsDataStore
import com.training.ecommerce.data.models.user.UserDetailsPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserPreferencesRepositoryImpl(private val context: Context):UserPreferencesRepository {
    override fun getUserDetails(): Flow<UserDetailsPreferences> =
        context.userDetailsDataStore.data


    override suspend fun getUserId(): Flow<String> =
        context.userDetailsDataStore.data.map {it.id}

    override suspend fun updateUserDetails(userDetails: UserDetailsPreferences) {
        context.userDetailsDataStore.updateData { userDetails }
    }

    override suspend fun updateUserId(userId: String) {
        context.userDetailsDataStore.updateData {preferences ->
            preferences.toBuilder().setId(userId).build()
        }    }

    override suspend fun clearUserPreferences() {
        context.userDetailsDataStore.updateData {preferences->
            preferences.toBuilder().clear().build()
        }
    }
}