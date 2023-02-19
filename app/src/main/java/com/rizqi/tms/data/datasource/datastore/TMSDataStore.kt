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
    private val FIREBASE_USER_ID = stringPreferencesKey("firebase_user_id")
    private val LAST_BACKUP_TIME_MILLIS = longPreferencesKey("last_backup_time_millis")
    private val IS_BACKUP_WITH_IMAGE = booleanPreferencesKey("is_backup_with_image")
    private val BACKUP_SCHEDULE = intPreferencesKey("backup_schedule")

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

    suspend fun saveLastBackupTimeMillisToPreferencesStore(lastBackupTimeMillis : Long) {
        context.datastore.edit { preferences ->
            preferences[LAST_BACKUP_TIME_MILLIS] = lastBackupTimeMillis
        }
    }

    val lastBackupTimeMillisPreference : Flow<Long?> = context.datastore.data
        .catch {
            if (it is IOException){
                emit(emptyPreferences())
            } else {
                throw it
            }
        }
        .map { preferences ->
            preferences[LAST_BACKUP_TIME_MILLIS]
        }

    suspend fun saveFirebaseUserIdToPreferencesStore(firebaseUserId : String) {
        context.datastore.edit { preferences ->
            preferences[FIREBASE_USER_ID] = firebaseUserId
        }
    }

    val firebaseUserIdPreference : Flow<String?> = context.datastore.data
        .catch {
            if (it is IOException){
                emit(emptyPreferences())
            } else {
                throw it
            }
        }
        .map { preferences ->
            preferences[FIREBASE_USER_ID]
        }

    suspend fun saveIsBackupWithImageToPreferencesStore(isBackupWithImage : Boolean) {
        context.datastore.edit { preferences ->
            preferences[IS_BACKUP_WITH_IMAGE] = isBackupWithImage
        }
    }

    val isBackupWithImagePreference : Flow<Boolean> = context.datastore.data
        .catch {
            if (it is IOException){
                emit(emptyPreferences())
            } else {
                throw it
            }
        }
        .map { preferences ->
            preferences[IS_BACKUP_WITH_IMAGE] ?: false
        }

    suspend fun saveBackupScheduleToPreferencesStore(backupSchedule: BackupSchedule) {
        context.datastore.edit { preferences ->
            preferences[BACKUP_SCHEDULE] = backupSchedule.ordinal
        }
    }

    val backupSchedulePreference : Flow<BackupSchedule> = context.datastore.data
        .catch {
            if (it is IOException){
                emit(emptyPreferences())
            } else {
                throw it
            }
        }
        .map { preferences ->
            val backupScheduleOrdinal = preferences[BACKUP_SCHEDULE] ?: BackupSchedule.EVERY_MONTH.ordinal
            when(backupScheduleOrdinal){
                BackupSchedule.EVERY_DAY.ordinal -> BackupSchedule.EVERY_DAY
                BackupSchedule.EVERY_WEEK.ordinal -> BackupSchedule.EVERY_WEEK
                BackupSchedule.EVERY_MONTH.ordinal -> BackupSchedule.EVERY_MONTH
                else -> BackupSchedule.NEVER
            }
        }

}