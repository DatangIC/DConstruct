package com.datangic.data.datastore

import android.content.Context
import androidx.datastore.core.CorruptionException
import androidx.datastore.core.DataStore
import androidx.datastore.core.Serializer
import com.datangic.data.LocalPassword
import com.google.protobuf.InvalidProtocolBufferException
import java.io.InputStream
import java.io.OutputStream

class LocalPasswordDataStore(context: Context) {
    object LocalPasswordData : Serializer<LocalPassword> {
        override val defaultValue: LocalPassword
            get() = LocalPassword.newBuilder()
                    .setBiometric(false)
                    .setPassword("")
                    .setType(0)
                    .build()

        override suspend fun readFrom(input: InputStream): LocalPassword {
            try {
                return LocalPassword.parseFrom(input)
            } catch (exception: InvalidProtocolBufferException) {
                throw CorruptionException("Cannot read proto.", exception)
            }
        }

        override suspend fun writeTo(t: LocalPassword, output: OutputStream) = t.writeTo(output)

    }

    private val mLocalPasswordDataStore: DataStore<LocalPassword> = context.createDataStore(
            fileName = "LocalPassword.pb",
            serializer = LocalPasswordData
    )

    suspend fun setPassword(type: Int, value: String) {
        mLocalPasswordDataStore.updateData {
            it.toBuilder().setType(type).setPassword(value).build()
        }
    }

    suspend fun setBiometric(value: Boolean) {
        mLocalPasswordDataStore.updateData {
            it.toBuilder().setBiometric(value).build()
        }
    }
}