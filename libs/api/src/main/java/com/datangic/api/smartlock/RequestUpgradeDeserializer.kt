package com.datangic.api.smartlock

import com.google.gson.*
import java.lang.reflect.Type

object RequestUpgradeDeserializer {

    class UpdateRequestDataDeserializer : JsonSerializer<UpgradeRequest.UpdateRequestData>, JsonDeserializer<UpgradeRequest.UpdateRequestData> {
        override fun serialize(src: UpgradeRequest.UpdateRequestData, typeOfSrc: Type?, context: JsonSerializationContext?): JsonElement {
            val json = JsonObject()
            json.addProperty("deviceSn", src.deviceSn)
            json.addProperty("devCurVer", src.devCurVer)
            json.addProperty("extension", src.extension)
            json.addProperty("isTest", src.isTest)
            json.addProperty("fingerprint", Gson().toJson(src.fingerprint, UpgradeRequest.Fingerprint::class.java))
            json.addProperty("face", Gson().toJson(src.face, UpgradeRequest.Face::class.java))
            json.addProperty("backPanel", Gson().toJson(src.backPanel, UpgradeRequest.BackPanel::class.java))
            return json
        }

        override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): UpgradeRequest.UpdateRequestData {
            val mJson = json.asJsonObject
            val fingerprint = try {
                mJson.getAsJsonObject("fingerprint")
            } catch (e: Exception) {
                null
            }
            val backPanel = try {
                mJson.getAsJsonObject("backPanel")
            } catch (e: Exception) {
                null
            }
            val face = try {
                mJson.getAsJsonObject("face")
            } catch (e: Exception) {
                null
            }
            val res = UpgradeRequest.UpdateRequestData(
                deviceSn = mJson.get("deviceSn").asString,
                devCurVer = mJson.get("devCurVer").asString,
                extension = mJson.get("extension").asString,
                isTest = mJson.get("isTest").asBoolean
            )
            fingerprint?.let {
                res.fingerprint = UpgradeRequest.Fingerprint(
                    fpType = it.get("fpType").asString,
                    fpCurVer = it.get("fpCurVer").asString,
                    fpCurZone = it.get("fpCurZone").asString
                )
            }
            face?.let {
                res.face = UpgradeRequest.Face(
                    faceType = it.get("faceType").asString,
                    mainCurVer = it.get("mainCurVer").asString,
                    nCpuCurVer = it.get("nCpuCurVer").asString,
                    sCpuCurVer = it.get("sCpuCurVer").asString,
                    modelCurVer = it.get("modelCurVer").asString,
                    uiCurVer = it.get("uiCurVer").asString
                )
            }
            backPanel?.let {
                res.backPanel = UpgradeRequest.BackPanel(
                    bpType = it.get("bpType").asString,
                    bpCurVer = it.get("bpCurVer").asString
                )
            }
            return res

        }

    }

    class UpgradeResponse : JsonSerializer<UpgradeRequest.Response>, JsonDeserializer<UpgradeRequest.Response> {
        override fun serialize(src: UpgradeRequest.Response, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
            val json = JsonObject()
            json.addProperty("device", Gson().toJson(src.device, UpgradeRequest.ResponseDevice::class.java))
            json.addProperty("fingerprint", Gson().toJson(src.fingerprint, UpgradeRequest.ResponseFingerprint::class.java))
            json.addProperty("backPanel", Gson().toJson(src.backPanel, UpgradeRequest.ResponseBackPanel::class.java))
            val scpu = json.addProperty("sCPU", Gson().toJson(src.face?.sCpu, UpgradeRequest.FaceDetail::class.java))
            val ncpu = json.addProperty("sCPU", Gson().toJson(src.face?.nCpu, UpgradeRequest.FaceDetail::class.java))
            val ui = json.addProperty("ui", Gson().toJson(src.face?.ui, UpgradeRequest.FaceDetail::class.java))
            val model = json.addProperty("model", Gson().toJson(src.face?.model, UpgradeRequest.FaceDetail::class.java))
            return json
        }

        override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): UpgradeRequest.Response {
            val res = UpgradeRequest.Response()
            val mJson = json.asJsonObject
            mJson.getAsJsonObject("device")?.let {
                res.device = UpgradeRequest.ResponseDevice(
                    filename = it.get("filename").asString,
                    version = it.get("version").asString,
                    md5 = it.get("md5").asString,
                    msg = if (it.get("msg") is JsonNull) {
                        NULL_STRING
                    } else {
                        it.get("msg")?.asString ?: NULL_STRING
                    },
                    path = it.get("path").asString,
                    updateDate = it.get("updateDate").asInt
                )
            }

            mJson.getAsJsonObject("fingerprint")?.let {
                res.fingerprint = UpgradeRequest.ResponseFingerprint(
                    filename = it.get("filename").asString,
                    version = it.get("version").asString,
                    msg = if (it.get("msg") is JsonNull) {
                        NULL_STRING
                    } else {
                        it.get("msg")?.asString ?: NULL_STRING
                    },
                    path = it.get("path").asString,
                    updateDate = it.get("updateDate").asInt,
                    zone = it.get("zone").asString,
                    sha1 = it.get("sha1").asString
                )
            }

            mJson.getAsJsonObject("backPanel")?.let {
                res.backPanel = UpgradeRequest.ResponseBackPanel(
                    filename = it.get("filename").asString,
                    version = it.get("version").asString,
                    msg = if (it.get("msg") is JsonNull) {
                        NULL_STRING
                    } else {
                        it.get("msg")?.asString ?: NULL_STRING
                    },
                    path = it.get("path").asString,
                    updateDate = it.get("updateDate").asInt,
                )
            }

            mJson.getAsJsonObject("face")?.let { face ->
                res.face = UpgradeRequest.ResponseFace(
                    version = face.get("version").asString,
                    msg = if (face.get("msg") is JsonNull) {
                        NULL_STRING
                    } else {
                        face.get("msg")?.asString ?: NULL_STRING
                    },
                    updateDate = face.get("updateDate").asInt,
                    sCpu = face.get("sCPU")?.let {

                        UpgradeRequest.FaceDetail(
                            filename = it.asJsonObject.get("fileName").asString,
                            version = it.asJsonObject.get("version").asString,
                            updateDate = it.asJsonObject.get("updateDate").asInt,
                            path = it.asJsonObject.get("path").asString,
                        )
                    },
                    nCpu = face.get("nCPU")?.let {
                        UpgradeRequest.FaceDetail(
                            filename = it.asJsonObject.get("fileName").asString,
                            version = it.asJsonObject.get("version").asString,
                            updateDate = it.asJsonObject.get("updateDate").asInt,
                            path = it.asJsonObject.get("path").asString,
                        )
                    },
                    model = face.get("model")?.let {
                        UpgradeRequest.FaceModelDetail(
                            filename = it.asJsonObject.get("fileName").asString,
                            version = it.asJsonObject.get("version").asString,
                            updateDate = it.asJsonObject.get("updateDate").asInt,
                            path = it.asJsonObject.get("path").asString,
                            fwPath = it.asJsonObject.get("pathFW").asString,
                            fwFilename = it.asJsonObject.get("fileNameFW").asString
                        )
                    },
                    ui = face.get("ui")?.let {
                        UpgradeRequest.FaceDetail(
                            filename = it.asJsonObject.get("fileName").asString,
                            version = it.asJsonObject.get("version").asString,
                            updateDate = it.asJsonObject.get("updateDate").asInt,
                            path = it.asJsonObject.get("path").asString,
                        )
                    })
            }
            return res
        }

    }
}