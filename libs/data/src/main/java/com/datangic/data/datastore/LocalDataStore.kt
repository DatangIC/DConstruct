package com.datangic.data.datastore

import android.annotation.SuppressLint
import android.content.Context
import androidx.datastore.core.DataStore
import com.datangic.data.SecretCodeMap
import com.datangic.data.SystemSettings
import com.datangic.data.ThemeType
import com.datangic.data.UserPrivateInfo
import kotlinx.coroutines.flow.catch
import java.io.IOException

class LocalDataStore(val context: Context) {

    private val TAG = DataStore::class.simpleName

    companion object {
        @SuppressLint("StaticFieldLeak")
        @Volatile
        private var INSTANCE: LocalDataStore? = null

        fun getInstance(context: Context): LocalDataStore {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: LocalDataStore(context).also { INSTANCE = it }
            }
        }
    }

    private val mSecretCodeMapDataStore by lazy { SecretCodeMapDataStore(context) }
    private val mSystemSettingsDataStore by lazy { SystemSettingsDataStore(context) }
    private val mUserPrivateInfoDataStore by lazy { UserPrivateInfoDataStore(context) }


    val mSecretCodeFlow = mSecretCodeMapDataStore.mSecretCodeDataStore.data.catch { exception ->
        if (exception is IOException) {
            emit(SecretCodeMap.getDefaultInstance())
        } else {
            throw exception
        }
    }
    val mUserPrivateInfoFlow = mUserPrivateInfoDataStore.mUserPrivateInfoDataStore.data.catch { exception ->
        if (exception is IOException) {
            emit(UserPrivateInfo.getDefaultInstance())
        } else {
            throw exception
        }
    }


    suspend fun addSecretCode(key: String, value: String) {
        mSecretCodeMapDataStore.addSecretCode(key, value)
    }

    suspend fun setDefaultSecretCode(key: String) {
        mSecretCodeMapDataStore.setDefault(key)
    }

    suspend fun setDeleteSecretCode(key: String) {
        mSecretCodeMapDataStore.setDelete(key)
    }

    suspend fun setUserPrivateInfo(userID: Long, auth: String?, password: String?) {
        mUserPrivateInfoDataStore.updateUserInfo(userID, auth, password)
    }


    val mSystemSettingsFlow by lazy {
        mSystemSettingsDataStore.mSystemSettingsDataStore.data.catch { exception ->
            if (exception is IOException) {
                emit(SystemSettings.getDefaultInstance())
            } else {
                throw exception
            }
        }
    }

    suspend fun updateTheme(key: ThemeType) {
        mSystemSettingsDataStore.updateTheme(key)
    }


    suspend fun updateOTADebug(key: Boolean) {
        mSystemSettingsDataStore.updateOTADebug(key)
    }

    suspend fun updateDebug(key: Boolean) {
        mSystemSettingsDataStore.updateDebug(key)
    }

}