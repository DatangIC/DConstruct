package com.datangic.data.datastore

import android.content.Context
import androidx.datastore.core.CorruptionException
import androidx.datastore.core.DataStore
import androidx.datastore.core.Serializer
import com.datangic.data.DefaultData.SECRET_CODE
import com.datangic.data.SecretCodeMap
import com.google.protobuf.InvalidProtocolBufferException
import java.io.InputStream
import java.io.OutputStream

const val DefaultSting = "Default"

class SecretCodeMapDataStore(context: Context) {


    object SecretCodeMapData : Serializer<SecretCodeMap> {
        override suspend fun readFrom(input: InputStream): SecretCodeMap {
            try {
                return SecretCodeMap.parseFrom(input)
            } catch (exception: InvalidProtocolBufferException) {
                throw CorruptionException("Cannot read proto.", exception)
            }
        }

        override suspend fun writeTo(t: SecretCodeMap, output: OutputStream) = t.writeTo(output)
        override val defaultValue: SecretCodeMap
            get() = SecretCodeMap.newBuilder()
                    .setDefault(DefaultSting)
                    .putAllSecretCode(SECRET_CODE)
                    .build()
    }

    val mSecretCodeDataStore: DataStore<SecretCodeMap> = context.createDataStore(
            fileName = "SecretCode.pb",
            serializer = SecretCodeMapData
    )

    suspend fun addSecretCode(key: String, value: String) {
        mSecretCodeDataStore.updateData {
            it.toBuilder().putSecretCode(key, value).setDefault(key).build()
        }
    }

    suspend fun setDefault(key: String) {
        mSecretCodeDataStore.updateData {
            it.toBuilder().setDefault(key).build()
        }
    }

    suspend fun setDelete(key: String) {
        if (key != DefaultSting) {
            mSecretCodeDataStore.updateData {
                it.toBuilder().removeSecretCode(key).build()
            }
            setDefault(DefaultSting)
        }
    }

}