package com.datangic.data.datastore

import android.content.Context
import androidx.datastore.core.CorruptionException
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.core.Serializer
import com.datangic.data.DefaultDevice
import com.google.protobuf.InvalidProtocolBufferException
import java.io.File
import java.io.InputStream
import java.io.OutputStream

class DefaultDeviceDataStore(context: Context) {

    object DefaultDeviceSerializer : Serializer<DefaultDevice> {
        override suspend fun readFrom(input: InputStream): DefaultDevice {
            try {
                return DefaultDevice.parseFrom(input)
            } catch (exception: InvalidProtocolBufferException) {
                throw CorruptionException("Cannot read proto.", exception)
            }
        }

        override suspend fun writeTo(t: DefaultDevice, output: OutputStream) = t.writeTo(output)
        override val defaultValue: DefaultDevice
            get() = DefaultDevice.newBuilder()
                .setSerialNumber("")
                .setMacAddress("").build()
    }

    val mDefaultDeviceDataStore: DataStore<DefaultDevice> = DataStoreFactory.create(
        serializer = DefaultDeviceSerializer
    ) {
        File(context.filesDir, "datastore/DefaultDevice.pb")
    }


    suspend fun saveDefaultDevice(serialNumber: String, macAddress: String) {
        mDefaultDeviceDataStore.updateData { preferences ->
            preferences.toBuilder().setSerialNumber(serialNumber).setMacAddress(macAddress).build()
        }
    }
}