package com.datangic.api.smartlock

import org.json.JSONObject


const val NULL_STRING = "--"

object UpgradeRequest {
    data class UpdateRequestData(
        val deviceSn: String,
        val devCurVer: String,
        val extension: String = "bin",
        val isTest: Boolean = false,
        var fingerprint: Fingerprint? = null,
        var face: Face? = null,
        var backPanel: BackPanel? = null

    )

    data class Fingerprint(
        val fpType: String,
        val fpCurZone: String,
        val fpCurVer: String
    )

    data class Face(
        val faceType: String,
        val mainCurVer: String,
        val nCpuCurVer: String?,
        val sCpuCurVer: String?,
        val modelCurVer: String?,
        val uiCurVer: String?
    )

    data class BackPanel(
        val bpType: String,
        val bpCurVer: String
    )

    data class Response(
        var device: ResponseDevice? = null,
        var fingerprint: ResponseFingerprint? = null,
        var backPanel: ResponseBackPanel? = null,
        var face: ResponseFace? = null
    )

    data class ResponseDevice(
        val filename: String,
        val version: String,
        val md5: String,
        val updateDate: Int,
        val msg: String,
        val path: String
    )

    data class ResponseFingerprint(
        val filename: String,
        val version: String,
        val updateDate: Int,
        val msg: String,
        val sha1: String,
        val zone: String,
        val path: String
    )

    data class ResponseBackPanel(
        val filename: String,
        val version: String,
        val msg: String,
        val updateDate: Int,
        val path: String
    )

    data class ResponseFace(
        val version: String,
        val msg: String? = null,
        val updateDate: Int,
        var sCpu: FaceDetail? = null,
        var nCpu: FaceDetail? = null,
        var model: FaceModelDetail? = null,
        var ui: FaceDetail? = null
    )

    data class FaceDetail(
        val filename: String,
        val version: String,
        val updateDate: Int,
        val path: String,
    )

    data class FaceModelDetail(
        val filename: String,
        val version: String,
        val updateDate: Int,
        val path: String,
        val fwPath: String,
        val fwFilename: String,
    )

    fun responseToData(r: JSONObject): Response {
        val device = try {
            r.getJSONObject("device")
        } catch (e: Exception) {
            null
        }
        val fingerprint = try {
            r.getJSONObject("fingerprint")
        } catch (e: Exception) {
            null
        }
        val backPanel = try {
            r.getJSONObject("backPanel")
        } catch (e: Exception) {
            null
        }
        val face = try {
            r.getJSONObject("face")
        } catch (e: Exception) {
            null
        }
        val res = Response()
        device?.let {
            res.device = ResponseDevice(
                filename = device.getString("filename"),
                version = device.getString("version"),
                md5 = device.getString("md5"),
                msg = if (it.getString("msg") == "null") {
                    NULL_STRING
                } else {
                    it.getString("msg") ?: NULL_STRING
                },
                path = device.getString("path"),
                updateDate = device.getInt("updateDate"),
            )
        }
        fingerprint?.let {
            res.fingerprint = ResponseFingerprint(
                filename = it.getString("filename"),
                version = it.getString("version"),
                msg = if (it.getString("msg") == "null") {
                    NULL_STRING
                } else {
                    it.getString("msg") ?: NULL_STRING
                },
                path = it.getString("path"),
                zone = it.getString("zone"),
                sha1 = it.getString("sha1"),
                updateDate = it.getInt("updateDate"),
            )
        }
        backPanel?.let {
            res.backPanel = ResponseBackPanel(
                filename = it.getString("filename"),
                version = it.getString("version"),
                msg = if (it.getString("msg") == "null") {
                    NULL_STRING
                } else {
                    it.getString("msg") ?: NULL_STRING
                },
                path = it.getString("path"),
                updateDate = it.getInt("updateDate"),
            )
        }
        face?.let {
            val sCpu = try {
                it.getJSONObject("sCPU")
            } catch (e: Exception) {
                null
            }
            val nCpu = try {
                it.getJSONObject("nCPU")
            } catch (e: Exception) {
                null
            }
            val model = try {
                it.getJSONObject("model")
            } catch (e: Exception) {
                null
            }
            val ui = try {
                it.getJSONObject("ui")
            } catch (e: Exception) {
                null
            }
            res.face = ResponseFace(
                version = it.getString("version"),
                msg = if (it.getString("msg") == "null") {
                    NULL_STRING
                } else {
                    it.getString("msg") ?: NULL_STRING
                },
                updateDate = it.getInt("updateDate"),
            )
            sCpu?.let { it1 ->
                res.face?.sCpu = FaceDetail(
                    filename = it1.getString("fileName"),
                    version = it1.getString("version"),
                    updateDate = it1.getInt("updateDate"),
                    path = it1.getString("path")
                )
            }
            nCpu?.let { it1 ->
                res.face?.nCpu = FaceDetail(
                    filename = it1.getString("fileName"),
                    version = it1.getString("version"),
                    updateDate = it1.getInt("updateDate"),
                    path = it1.getString("path")
                )
            }
            model?.let { it1 ->
                res.face?.model = FaceModelDetail(
                    filename = it1.getString("fileName"),
                    version = it1.getString("version"),
                    updateDate = it1.getInt("updateDate"),
                    path = it1.getString("path"),
                    fwPath = it1.getString("pathFW"),
                    fwFilename = it1.getString("fileNameFW")
                )
            }
            ui?.let {
                res.face?.ui = FaceDetail(
                    filename = ui.getString("fileName"),
                    version = ui.getString("version"),
                    updateDate = ui.getInt("updateDate"),
                    path = ui.getString("path")
                )
            }
        }
        return res
    }
}