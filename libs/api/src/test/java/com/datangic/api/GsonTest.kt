package com.datangic.api

import com.datangic.api.login.LoginApi
import com.datangic.api.login.LoginDataResult
import com.datangic.api.login.VerifyCodeResult
import com.google.gson.Gson
import io.reactivex.rxjava3.core.Observer
import io.reactivex.rxjava3.disposables.Disposable
import org.junit.Test

class GsonTest {
    @Test
    fun deserialize() {
        val str = "{\n" +
                "    \"respCode\": \"successful\",\n" +
                "    \"device\": {\n" +
                "        \"filename\": \"TT8258-LJ_1.0.4_20211129.bin\",\n" +
                "        \"version\": \"1.0.4\",\n" +
                "        \"versionCode\": 8,\n" +
                "        \"updateDate\": 1638755727,\n" +
                "        \"md5\": \"dc4f1ed701ea2aeb798c452569f32a3a\",\n" +
                "        \"msg\": \"T8258-LJ_1.0.4_20211129\",\n" +
                "        \"forceUpdate\": true,\n" +
                "        \"path\": \"/api/v1.0/firmware/61ad6d8f0cb8f4138467845b&621cbc11cwa7sz\"\n" +
                "    },\n" +
                "    \"fingerprint\": {\n" +
                "        \"filename\": \"TDMTPB4S_Cry_COS_1_V3.1.46_20210525.bin\",\n" +
                "        \"version\": \"3.1.46\",\n" +
                "        \"msg\": \"V46\\n算法优化。请注意，升级后指纹需要重新录。\",\n" +
                "        \"versionCode\": 41,\n" +
                "        \"updateDate\": 1625734330,\n" +
                "        \"sha1\": \"9BB1FB8B30C92AB7ABBA5E1CE0AF218274FE60407E1BB83CA599D33A5FBF4FA90001C3D4\",\n" +
                "        \"zone\": \"1\",\n" +
                "        \"path\": \"/api/v1.0/firmware_fp/60e6bcba0cb8f45cc881326c&1&621cbc11cwa7sz\"\n" +
                "    },\n" +
                "    \"face\": {\n" +
                "        \"version\": \"3.92.8\",\n" +
                "        \"msg\": null,\n" +
                "        \"versionCode\": 5,\n" +
                "        \"updateDate\": 1634281453,\n" +
                "        \"sCPU\": {\n" +
                "            \"fileName\": \"TKL520-AIC-NP-V1_SCPU_2.0.3_20211015.bin\",\n" +
                "            \"version\": \"2.0.3\",\n" +
                "            \"sha1\": \"undefined\",\n" +
                "            \"updateDate\": 1634281453,\n" +
                "            \"path\": \"/api/v1.0/firmware_face/616927ed0cb8f44c6c26e18e&621cbc11cwa7sz\"\n" +
                "        },\n" +
                "        \"nCPU\": {\n" +
                "            \"fileName\": \"TKL520-AIC-NP-V1_fw_ncpu_0.5.5_20210622.bin\",\n" +
                "            \"version\": \"0.5.5\",\n" +
                "            \"sha1\": \"undefined\",\n" +
                "            \"updateDate\": 1632713598,\n" +
                "            \"path\": \"/api/v1.0/firmware_face/61513b7e0cb8f42bc0ef8272&621cbc11cwa7sz\"\n" +
                "        }\n" +
                "    }\n" +
                "}"
        val verifyCodeResult = "{\n" +
                "\"userPhone\": \"1232323\",\n" +
                "\"expirationTime\": 16787820039\n" +
                "}"

        val loginResult = "{\n" +
                "\"userId\": 12,\n" +
                "\"userPhone\": \"13212341234\",\n" +
                "\"roleId\": 12,\n" +
                "\"roleName\": \"Admin\",\n" +
                "\"authorization\": \"234hkdljslds\",\n" +
                "\"userPlatform\": \"Lock\",\n" +
                "\"homeIds\": [\n" +
                "1,\n" +
                "2,\n" +
                "3,\n" +
                "4,\n" +
                "5\n" +
                "],\n" +
                "\"thirdPartyPlatformUid\": \"sd\",\n" +
                "\"nickname\": \"小丑\",\n" +
                "\"avatar\": \"sdfsdfdfsfs\"\n" +
                "}"
        val gson = GsonUtils.getGson()
//        val mResult = gson.fromJson(str, UpgradeRequest.Response::class.java)
//        println(mResult)
//        val json = gson.toJson(mResult, UpgradeRequest.Response::class.java)
//        println(json)
        val _verifyCodeResult = gson.fromJson(verifyCodeResult, VerifyCodeResult::class.java)
        println(_verifyCodeResult)
        val _loginResult = gson.fromJson(loginResult, LoginDataResult::class.java)
        println(_loginResult)
    }
}