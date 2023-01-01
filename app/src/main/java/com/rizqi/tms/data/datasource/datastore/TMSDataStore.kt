package com.rizqi.tms.data.datasource.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

private const val TMS_PREFERENCES_NAME = "toko_management_system_preferences"

private val Context.datastore : DataStore<Preferences> by preferencesDataStore(
    name = TMS_PREFERENCES_NAME
)

class AppDataStore(private val context : Context) {
    private val IS_LOGIN = booleanPreferencesKey("is_login")
    private val IS_ANONYMOUS = booleanPreferencesKey("is_anonymous")
    private val USER_ID = intPreferencesKey("user_id")
    private val FIREBASE_USER_ID = stringPreferencesKey("firebase_user_id")
    private val LAST_BACKUP_DATE = longPreferencesKey("last_backup_date")
    private val IS_BACKUP_WITH_IMAGE = booleanPreferencesKey("is_backup_with_image")
    private val BACKUP_SCHEDULE = intPreferencesKey("backup_schedule")
    private val NEXT_BACKUP_DATE = longPreferencesKey("next_backup_date")

    suspend fun saveLoginToPreferencesStore(isLogin : Boolean) {
        context.datastore.edit { preferences ->
            preferences[IS_LOGIN] = isLogin
        }
    }

    val isLoginPreference : Flow<Boolean> = context.datastore.data
        .catch {
            if (it is IOException){
                emit(emptyPreferences())
            } else {
                throw it
            }
        }
        .map { preferences ->
            preferences[IS_LOGIN] ?: false
        }

    suspend fun saveIsAnonymousToPreferencesStore(isAnonymous : Boolean) {
        context.datastore.edit { preferences ->
            preferences[IS_ANONYMOUS] = isAnonymous
        }
    }

    val isAnonymousPreference : Flow<Boolean> = context.datastore.data
        .catch {
            if (it is IOException){
                emit(emptyPreferences())
            } else {
                throw it
            }
        }
        .map { preferences ->
            preferences[IS_ANONYMOUS] ?: true
        }

    suspend fun saveUserIdToPreferencesStore(userId : Int) {
        context.datastore.edit { preferences ->
            preferences[USER_ID] = userId
        }
    }

    val userIdPreference : Flow<Int> = context.datastore.data
        .catch {
            if (it is IOException){
                emit(emptyPreferences())
            } else {
                throw it
            }
        }
        .map { preferences ->
            preferences[USER_ID] ?: -1
        }
}