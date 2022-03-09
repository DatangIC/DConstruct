package com.datangic.data.database.table

object DeviceEnum {

    enum class LockType {
        NORMAL, FACE
    }

    enum class LockLanguage {
        CHINESE_ONLY, CHINESE, ENGLISH
    }

    enum class WifiStatus {
        NOT_SETTING, ROUTER_NOT_CONNECTED, SERVER_NOT_CONNECTED, SERVER_CONNECTED, ERROR
    }

    enum class LogState {
        UNLOCK, LOCK, OTHER, ADD_KEY, DELETE_KEY, LOCKED_IN, WARING, BATTERY, RESET, LOW_BATTERY, DOORBELL, ADD_USER, DELETE_USER, ERROR, UNKNOWN
    }

    enum class UnlockType {
        PASSWORD, FINGERPRINT, NFC, REMOTE, TEMPORARY_PASSWORD, COMBINATION, FACE, SEIZED_FINGERPRINT, SEIZED_PASSWORD, UNKNOWN
    }


    enum class NfcType {
        NORMAL, ENCRYPTION
    }

    enum class KeyType {
        PASSWORD, TEMPORARY_PASSWORD, FINGERPRINT, NFC, FACE, UNKNOWN, SEIZED_FINGERPRINT
    }

    enum class DeviceUserStatus {
        PAUSE, ACTIVATED, INACTIVATED, UNKNOWN
    }

    enum class FaceVersion {
        MAIN, NCPU, SCPU, MODEL, UI
    }

    fun getLockType(tag: Int): LockType {
        return when (tag) {
            0 -> LockType.NORMAL
            1 -> LockType.FACE
            else -> LockType.NORMAL
        }
    }

    fun setLockType(tag: LockType): Int {
        return when (tag) {
            LockType.NORMAL -> 0
            LockType.FACE -> 1
        }
    }

    fun getLockLanguage(tag: Int): LockLanguage {
        return when (tag) {
            0 -> LockLanguage.CHINESE_ONLY
            1 -> LockLanguage.CHINESE
            else -> LockLanguage.ENGLISH
        }
    }

    fun setLockLanguage(tag: LockLanguage): Int {
        return when (tag) {
            LockLanguage.CHINESE_ONLY -> 0
            LockLanguage.CHINESE -> 1
            LockLanguage.ENGLISH -> 2
        }
    }

    fun getWifiStatus(tag: Int): WifiStatus {
        return when (tag) {
            0 -> WifiStatus.NOT_SETTING
            1 -> WifiStatus.ROUTER_NOT_CONNECTED
            2 -> WifiStatus.SERVER_CONNECTED
            3 -> WifiStatus.SERVER_NOT_CONNECTED
            else -> WifiStatus.ERROR
        }
    }

    fun setWifiStatus(tag: WifiStatus): Int {
        return when (tag) {
            WifiStatus.NOT_SETTING -> 0
            WifiStatus.ROUTER_NOT_CONNECTED -> 1
            WifiStatus.SERVER_CONNECTED -> 2
            WifiStatus.SERVER_NOT_CONNECTED -> 3
            WifiStatus.ERROR -> 255
        }
    }

    fun getLogState(tag: Int): LogState {
        return when (tag) {
            1 -> LogState.UNLOCK
            2 -> LogState.LOCK
            3 -> LogState.OTHER
            4 -> LogState.ADD_KEY
            5 -> LogState.DELETE_KEY
            6 -> LogState.LOCKED_IN
            7 -> LogState.WARING
            8 -> LogState.BATTERY
            9 -> LogState.RESET
            10 -> LogState.LOW_BATTERY
            11 -> LogState.DOORBELL
            12 -> LogState.ADD_USER
            13 -> LogState.DELETE_USER
            14 -> LogState.ERROR
            else -> LogState.UNKNOWN
        }
    }

    fun setLogState(tag: LogState): Int {
        return when (tag) {
            LogState.UNLOCK -> 1
            LogState.LOCK -> 2
            LogState.OTHER -> 3
            LogState.ADD_KEY -> 4
            LogState.DELETE_KEY -> 5
            LogState.LOCKED_IN -> 6
            LogState.WARING -> 7
            LogState.BATTERY -> 8
            LogState.RESET -> 9
            LogState.LOW_BATTERY -> 10
            LogState.DOORBELL -> 11
            LogState.ADD_USER -> 12
            LogState.DELETE_USER -> 13
            LogState.ERROR -> 14
            LogState.UNKNOWN -> 255
        }
    }

    fun getUnlockLogType(tag: Int): UnlockType {
        return when (tag) {
            0 -> UnlockType.PASSWORD
            1 -> UnlockType.FINGERPRINT
            2 -> UnlockType.NFC
            7 -> UnlockType.REMOTE
            8 -> UnlockType.TEMPORARY_PASSWORD
            9 -> UnlockType.COMBINATION
            10 -> UnlockType.FACE
            12 -> UnlockType.SEIZED_FINGERPRINT
            13 -> UnlockType.SEIZED_PASSWORD
            else -> UnlockType.UNKNOWN
        }
    }

    fun setUnlockLogType(tag: UnlockType): Int {
        return when (tag) {
            UnlockType.PASSWORD -> 0
            UnlockType.FINGERPRINT -> 1
            UnlockType.NFC -> 2
            UnlockType.REMOTE -> 7
            UnlockType.TEMPORARY_PASSWORD -> 8
            UnlockType.COMBINATION -> 9
            UnlockType.FACE -> 10
            UnlockType.SEIZED_FINGERPRINT -> 12
            UnlockType.SEIZED_PASSWORD -> 13
            UnlockType.UNKNOWN -> 255
        }
    }

    fun setDeviceUserStatus(tag: DeviceUserStatus): Int {
        return when (tag) {
            DeviceUserStatus.INACTIVATED -> 0
            DeviceUserStatus.ACTIVATED -> 1
            DeviceUserStatus.PAUSE -> 2
            DeviceUserStatus.UNKNOWN -> 255
        }
    }

    fun getDeviceUserStatus(tag: Int): DeviceUserStatus {
        return when (tag) {
            0 -> DeviceUserStatus.INACTIVATED
            1 -> DeviceUserStatus.ACTIVATED
            2 -> DeviceUserStatus.PAUSE
            else -> DeviceUserStatus.UNKNOWN
        }
    }

    fun setNfcType(tag: NfcType): Int {
        return when (tag) {
            NfcType.NORMAL -> 0
            NfcType.ENCRYPTION -> 1
        }
    }

    fun getNfcType(tag: Int): NfcType {
        return when (tag) {
            0 -> NfcType.NORMAL
            1 -> NfcType.ENCRYPTION
            else -> NfcType.NORMAL
        }
    }

    fun getNfcType(tag: Boolean): NfcType {
        return when (tag) {
            false -> NfcType.NORMAL
            true -> NfcType.ENCRYPTION
        }
    }

    fun setKeyType(tag: KeyType): Int {
        return when (tag) {
            KeyType.UNKNOWN -> 0
            KeyType.PASSWORD -> 1
            KeyType.FINGERPRINT -> 2
            KeyType.NFC -> 3
            KeyType.FACE -> 4
            KeyType.TEMPORARY_PASSWORD -> 5
            KeyType.SEIZED_FINGERPRINT -> 6
        }
    }

    fun getKeyType(tag: Int): KeyType {
        return when (tag) {
            0 -> KeyType.UNKNOWN
            1 -> KeyType.PASSWORD
            2 -> KeyType.FINGERPRINT
            3 -> KeyType.NFC
            4 -> KeyType.FACE
            5 -> KeyType.TEMPORARY_PASSWORD
            6 -> KeyType.SEIZED_FINGERPRINT
            else -> KeyType.UNKNOWN
        }
    }

    fun getFaceVersion(tag: Int): FaceVersion {
        return when (tag) {
            1 -> FaceVersion.MODEL
            2 -> FaceVersion.SCPU
            3 -> FaceVersion.NCPU
            4 -> FaceVersion.UI
            else -> FaceVersion.MAIN
        }
    }

    fun setFaceVersion(tag: FaceVersion): Int {
        return when (tag) {
            FaceVersion.MAIN -> 5
            FaceVersion.UI -> 4
            FaceVersion.NCPU -> 3
            FaceVersion.SCPU -> 2
            FaceVersion.MODEL -> 1
        }
    }

}