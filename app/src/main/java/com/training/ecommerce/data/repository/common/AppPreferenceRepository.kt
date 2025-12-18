package com.training.ecommerce.data.repository.common

import kotlinx.coroutines.flow.Flow

interface AppPreferenceRepository {
    suspend fun saveLogInStatus(isLoggedIn: Boolean)
    suspend fun isLogIn(): Flow<Boolean>

}