package com.datangic.smartlock.ble


import androidx.annotation.RestrictTo
import cn.dttsh.dts1586.*
import com.datangic.common.utils.Logger
import com.datangic.smartlock.utils.MSG41_TypeUpgradeAll
import com.datangic.smartlock.utils.MSG49_VALUE_Default

@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
object CreateMessage {
    enum class State {
        SUCCESS, DISCONNECT, CREATE_ERROR,
    }

    fun createMessage01(
        macAddress: String,
        type: Byte,
        userID: Int,
        authCode: String? = null,
        secretCode: String? = null,
        timeStamp: Int? = null
    ): Pair<String, MSG> {
        DTS1586.init(macAddress.hashCode(), macAddress, authCode, secretCode)
        val msg = MSG01().apply {
            this.setMark(macAddress.hashCode())
            this.type = type
            this.userID = userID
            if (authCode?.length == 60)
                this.authCode = authCode
            if (timeStamp != null)
                this.timeStamp = timeStamp
        }
        Logger.e("SendMessage", "MSG01 macAddress = ${macAddress} type=$type userID=$userID secretCode = $secretCode\n authCode=$authCode")
        return Pair(macAddress, msg)
    }

    fun createMessage03(macAddress: String): Pair<String, MSG> {
        val msg = MSG03().apply {
            this.setMark(macAddress.hashCode())
        }
        return Pair(macAddress, msg)
    }

    fun createMessage05(macAddress: String, mac: String, sn: String, nodeID: String): Pair<String, MSG> {
        val msg = MSG05().apply {
            this.setMark(macAddress.hashCode())
            this.mac = mac
            this.nodeID = nodeID
            this.sn = sn
        }
        return Pair(macAddress, msg)
    }

    fun createMessage07(macAddress: String, secretCode: String? = "0000000000"): Pair<String, MSG> {
        val msg = MSG07().apply {
            this.setMark(macAddress.hashCode())
            this.secretCode = secretCode
        }
        Logger.e("TAG", "Send MSG07= ${msg.secretCode}")
        return Pair(macAddress, msg)
    }

    fun createMessage11(macAddress: String, type: Byte, userID: Int): Pair<String, MSG> {
        val msg = MSG11().apply {
            this.setMark(macAddress.hashCode())
            this.type = type
            this.userID = userID
        }
        return Pair(macAddress, msg)
    }

    fun createMessage13(macAddress: String, type: Byte): Pair<String, MSG> {
        val msg = MSG13().apply {
            this.setMark(macAddress.hashCode())
            this.type = type
        }
        return Pair(macAddress, msg)
    }

    fun createMessage15(
        macAddress: String,
        cmd: Byte,
        type: Byte,
        lockID: Byte,
        userID: Int,
        pwd: String = ""
    ): Pair<String, MSG> {
        val msg = MSG15().apply {
            this.setMark(macAddress.hashCode())
            this.cmd = cmd
            this.type = type
            this.lockID = lockID
            this.userID = userID
            this.pwd = pwd
        }
        return Pair(macAddress, msg)
    }

    fun createMessage17(macAddress: String, type: Byte, userID: Int): Pair<String, MSG> {
        val msg = MSG17().apply {
            this.setMark(macAddress.hashCode())
            this.type = type
            this.userID = userID
        }
        return Pair(macAddress, msg)
    }

    fun createMessage19(macAddress: String, type: Byte): Pair<String, MSG> {
        val msg = MSG19().apply {
            this.setMark(macAddress.hashCode())
            this.type = type
        }
        return Pair(macAddress, msg)
    }

    fun createMessage1B(
        macAddress: String,
        userID: Int,
        tsBegin: IntArray,
        tsEnd: IntArray,
        type: Byte
    ): Pair<String, MSG> {
        val msg = MSG1B().apply {
            this.setMark(macAddress.hashCode())
            this.type = type
            this.userID = userID
            this.tsBegin = tsBegin
            this.tsEnd = tsEnd
        }
        return Pair(macAddress, msg)
    }

    fun createMessage1D(macAddress: String, time: Byte): Pair<String, MSG> {
        val msg = MSG1D().apply {
            this.setMark(macAddress.hashCode())
            this.time = time
        }
        return Pair(macAddress, msg)
    }

    fun createMessage1F(macAddress: String, errorCode: Int): Pair<String, MSG> {
        val msg = MSG1F().apply {
            this.setMark(macAddress.hashCode())
            this.errCode = errCode
        }
        return Pair(macAddress, msg)
    }

    fun createMessage21(macAddress: String, imei: String): Pair<String, MSG> {
        Logger.e("Message Send", "IMEI=${imei}")
        val msg = MSG21().apply {
            this.setMark(macAddress.hashCode())
            this.imei = imei
        }
        return Pair(macAddress, msg)
    }

    fun createMessage23(macAddress: String, synStatus: ByteArray): Pair<String, MSG> {
        val msg = MSG23().apply {
            this.setMark(macAddress.hashCode())
            this.synStatus = synStatus
        }
        return Pair(macAddress, msg)
    }

    fun createMessage25(macAddress: String, userID: Int): Pair<String, MSG> {
        val msg = MSG25().apply {
            this.setMark(macAddress.hashCode())
            this.userID = userID
        }
        return Pair(macAddress, msg)
    }

    fun createMessage27(macAddress: String, dynCode: String): Pair<String, MSG> {
        val msg = MSG27().apply {
            this.setMark(macAddress.hashCode())
            this.dynCode = dynCode
        }
        return Pair(macAddress, msg)
    }

    fun createMessage29(macAddress: String, userID: Int, tsBegin: Int, tsEnd: Int): Pair<String, MSG> {
        val msg = MSG29().apply {
            this.setMark(macAddress.hashCode())
            this.userID = userID
            this.tsBegin = tsBegin
            this.tsEnd = tsEnd
        }
        return Pair(macAddress, msg)
    }

    fun createMessage2B(macAddress: String, advName: String): Pair<String, MSG> {
        val msg = MSG2B().apply {
            this.setMark(macAddress.hashCode())
            this.advNameLen = advName.length.toByte()
            this.advName = advName
        }
        return Pair(macAddress, msg)
    }

    fun createMessage2D(macAddress: String, powerSaveTSBegin: Int, powerSaveTSEnd: Int): Pair<String, MSG> {
        val msg = MSG2D().apply {
            this.setMark(macAddress.hashCode())
            this.powerSaveTSBegin = powerSaveTSBegin
            this.powerSaveTSEnd = powerSaveTSEnd
        }
        return Pair(macAddress, msg)
    }

    fun createMessage31(macAddress: String, type: Byte, userID: Int): Pair<String, MSG> {
        val msg = MSG31().apply {
            this.setMark(macAddress.hashCode())
            this.type = type
            this.userID = userID
        }
        Logger.i("SendMessage", "msg31 type=${type}  user=${userID}")
        return Pair(macAddress, msg)
    }

    fun createMessage33(macAddress: String, type: Byte, userID: Int, logID: Int): Pair<String, MSG> {
        Logger.e("createMessage33", "type=$type userID=$userID logID=$logID")
        val msg = MSG33().apply {
            this.setMark(macAddress.hashCode())
            this.type = type
            this.userID = userID
            this.logID = logID
        }
        return Pair(macAddress, msg)
    }

    fun createMessage35(macAddress: String, data: ByteArray): Pair<String, MSG> {
        val msg = MSG35().apply {
            this.setMark(macAddress.hashCode())
            this.setData(data)
        }
        return Pair(macAddress, msg)
    }

    fun createMessage37(macAddress: String, size: Int): Pair<String, MSG> {
        val msg = MSG37().apply {
            this.setMark(macAddress.hashCode())
            this.setSize(size)
        }
        return Pair(macAddress, msg)
    }

    fun createMessage39(macAddress: String): Pair<String, MSG> {
        val msg = MSG39().apply {
            this.setMark(macAddress.hashCode())
        }
        return Pair(macAddress, msg)
    }

    fun createMessage3B(macAddress: String, type: Byte, userID: Int, offset: Byte = 0, num: Byte = 10): Pair<String, MSG> {
        val msg = MSG3B().apply {
            this.setMark(macAddress.hashCode())
            this.type = type
            this.userID = userID
            this.offset = offset
            this.num = num
        }
        return Pair(macAddress, msg)
    }

    fun createMessage3D(macAddress: String, deleteUserID: IntArray): Pair<String, MSG> {
        val msg = MSG3D().apply {
            this.setMark(macAddress.hashCode())
            this.deleteUserID = deleteUserID
        }
        return Pair(macAddress, msg)
    }

    fun createMessage41(macAddress: String, cmd: Byte, type: Byte = MSG41_TypeUpgradeAll, data: Int = 0): Pair<String, MSG> {
        Logger.e("MSG41", "type = $type \n cmd=$cmd \n data=$data")
        val msg = MSG41().apply {
            this.setMark(macAddress.hashCode())
            setCmd(cmd)
            setType(type)
            setData(data)
        }
        return Pair(macAddress, msg)
    }

    fun createMessage43(macAddress: String, data: ByteArray): Pair<String, MSG> {
        val msg = MSG43().apply {
            this.setMark(macAddress.hashCode())
            setData(data)
        }
        return Pair(macAddress, msg)
    }

    fun createMessage47(macAddress: String, timeZone: Int): Pair<String, MSG> {
        val msg = MSG47().apply {
            this.setMark(macAddress.hashCode())
            this.timeZone = timeZone
        }
        return Pair(macAddress, msg)
    }

    fun createMessage49(macAddress: String, type: Byte, value: Int = MSG49_VALUE_Default): Pair<String, MSG> {
        val msg = MSG49().apply {
            this.setMark(macAddress.hashCode())
            this.type = type
            this.value = value
        }
        return Pair(macAddress, msg)
    }

    fun createMessage4B(macAddress: String, tsBegin: Int, tsEnd: Int): Pair<String, MSG> {
        val msg = MSG4B().apply {
            this.setMark(macAddress.hashCode())
            this.tsBegin = tsBegin
            this.tsEnd = tsEnd
        }
        return Pair(macAddress, msg)
    }

    fun createMessage51StartOTA(macAddress: String, type: Byte, size: Int): Pair<String, MSG> {
        val msg = MSG51().apply {
            this.setMark(macAddress.hashCode())
            this.setStartOTA(type, size)
        }
        return Pair(macAddress, msg)
    }

    fun createMessage51EndOTA(macAddress: String, type: Byte, size: Int): Pair<String, MSG> {
        val msg = MSG51().apply {
            this.setMark(macAddress.hashCode())
            this.setEndOTA(type, size)
        }
        return Pair(macAddress, msg)
    }

    fun createMessage51(macAddress: String, type: Byte, header: Int, data: ByteArray): Pair<String, MSG> {
        val msg = MSG51().apply {
            this.setMark(macAddress.hashCode())
            this.setType(type)
            this.setHeader(header)
            this.setData(data)
        }
        return Pair(macAddress, msg)
    }

    fun createMessage53(macAddress: String, userList: IntArray): Pair<String, MSG> {
        val msg = MSG53().apply {
            this.setMark(macAddress.hashCode())
            this.userList = userList
        }
        return Pair(macAddress, msg)
    }

    fun createMessage55(
        macAddress: String,
        mac: String,
        ssid: String,
        ip: String,
        port: String,
        pwd: String,
        enc: Byte = 0.toByte(),
    ): Pair<String, MSG> {
        val msg = MSG55().apply {
            this.setMark(macAddress.hashCode())
            this.enc = enc
            this.mac = mac
            this.ip = ip
            this.ssid = ssid
            this.port = port
            this.pwd = pwd
        }
        return Pair(macAddress, msg)
    }

    fun createMessage57(macAddress: String, type: Byte): Pair<String, MSG> {
        val msg = MSG57().apply {
            this.setMark(macAddress.hashCode())
            this.type = type
        }
        return Pair(macAddress, msg)
    }

}