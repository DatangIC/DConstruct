package com.datangic.data.datastore

import android.content.Context
import androidx.datastore.core.CorruptionException
import androidx.datastore.core.DataStore
import androidx.datastore.core.Serializer
import com.datangic.data.SystemSettings
import com.datangic.data.ThemeType
import com.google.protobuf.InvalidProtocolBufferException
import java.io.InputStream
import java.io.OutputStream

class SystemSettingsDataStore(context: Context) {

    object SystemSettingData : Serializer<SystemSettings> {
        override val defaultValue: SystemSettings
            get() = SystemSettings.newBuilder()
                .setDebugOta(false)
                .setDebug(false)
                .setTheme(ThemeType.SYSTEM)
                .build()

        override suspend fun readFrom(input: InputStream): SystemSettings {
            try {
                return SystemSettings.parseFrom(input)
            } catch (exception: InvalidProtocolBufferException) {
                throw CorruptionException("Cannot read proto.", exception)
            }
        }

        override suspend fun writeTo(t: SystemSettings, output: OutputStream) {
            t.writeTo(output)
        }

    }

    val mSystemSettingsDataStore: DataStore<SystemSettings> = context.createDataStore(
        fileName = "SystemSettings.pb",
        serializer = SystemSettingData
    )


    suspend fun updateTheme(key: ThemeType) {
        mSystemSettingsDataStore.updateData {
            it.toBuilder().setTheme(key).build()
        }
    }

    suspend fun updateOTADebug(key: Boolean) {
        mSystemSettingsDataStore.updateData {
            it.toBuilder().setDebugOta(key).build()
        }
    }

    suspend fun updateDebug(key: Boolean) {
        mSystemSettingsDataStore.updateData {
            it.toBuilder().setDebug(key).build()
        }
    }
}