package com.training.ecommerce.data.datasourse.datastore

import android.content.Context
import androidx.datastore.core.CorruptionException
import androidx.datastore.core.DataStore
import androidx.datastore.core.Serializer
import androidx.datastore.dataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.datastore.preferences.protobuf.InvalidProtocolBufferException
import com.training.ecommerce.data.datasourse.datastore.DataStoreKeys.E_COMMERCE_PREFERENCES
import com.training.ecommerce.data.datasourse.datastore.DataStoreKeys.USER_DETAILS_PREFERENCES
import com.training.ecommerce.data.models.user.UserDetailsPreferences
import java.io.InputStream
import java.io.OutputStream

object DataStoreKeys {
    val USER_ID = stringPreferencesKey("user_id")
    const val E_COMMERCE_PREFERENCES = "e_commerce_preferences"
    const val USER_DETAILS_PREFERENCES = "user_details.pb"
    val IS_USER_LOGGED_IN = booleanPreferencesKey("is_user_logged_in")
}

val Context.appDataStore: DataStore<Preferences> by preferencesDataStore(name = E_COMMERCE_PREFERENCES)

val Context.userDetailsDataStore by dataStore(fileName = USER_DETAILS_PREFERENCES, serializer = UserDetailsPreferenceSerializer)

object UserDetailsPreferenceSerializer : Serializer<UserDetailsPreferences> {

    override val defaultValue: UserDetailsPreferences = UserDetailsPreferences.getDefaultInstance()

    override suspend fun readFrom(input: InputStream): UserDetailsPreferences = try {
        UserDetailsPreferences.parseFrom(input)
    } catch (exception: InvalidProtocolBufferException) {
        throw CorruptionException("Cannot read proto.", exception)
    }

    override suspend fun writeTo(t: UserDetailsPreferences, output: OutputStream) {
        t.writeTo(output)
    }

}