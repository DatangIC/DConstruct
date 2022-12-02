package com.datangic.data.datastore

import android.content.Context
import androidx.datastore.core.CorruptionException
import androidx.datastore.core.DataStore
import androidx.datastore.core.Serializer
import com.datangic.data.UserPrivateInfo
import com.google.protobuf.InvalidProtocolBufferException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

class UserPrivateInfoDataStore(val context: Context) {
    object UserPrivateInfoData : Serializer<UserPrivateInfo> {
        override val defaultValue: UserPrivateInfo
            get() = UserPrivateInfo.newBuilder()
                .setUserId(0)
                .setPassword("")
                .setAuthentication("")
                .build()

        override suspend fun readFrom(input: InputStream): UserPrivateInfo {
            try {
                return UserPrivateInfo.parseFrom(input)
            } catch (exception: InvalidProtocolBufferException) {
                throw CorruptionException("Cannot read proto.", exception)
            }
        }

        override suspend fun writeTo(t: UserPrivateInfo, output: OutputStream) {
            t.writeTo(output)
        }
    }

    val mUserPrivateInfoDataStore: DataStore<UserPrivateInfo> = context.createDataStore(
        fileName = "UserPrivateInfo.pd",
        serializer = UserPrivateInfoData
    )

    suspend fun updateUserInfo(userID:Int, auth: String?, password: String?) {
        mUserPrivateInfoDataStore.updateData {
            it.toBuilder()
                .setUserId(userID)
                .setAuthentication(auth)
                .setPassword(password)
                .build()
        }
    }

    suspend fun getUserPrivateInfo(): Flow<UserPrivateInfo> = mUserPrivateInfoDataStore.data.catch { exception ->
        if (exception is IOException) {
            emit(UserPrivateInfo.getDefaultInstance())
        } else {
            throw exception
        }

    }
}