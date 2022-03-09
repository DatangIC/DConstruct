package com.datangic.smartlock.utils


// MSG0E
const val MSG0E_RandomNumberVerificationFailed = 0x01
const val MSG0E_UserNotExist = 0x02
const val MSG0E_NoPermission = 0x03
const val MSG0E_DeviceBusy = 0x04
const val MSG0E_AuthenticationCodeError = 0x05
const val MSG0E_UserHasBeenSuspended = 0x06
const val MSG0E_IllegalApplicationConnection = 0x07
const val MSG0E_UserHasExpired = 0x08
const val MSG0E_TheSecurityInformationSetSuccessfully = 0x09
const val MSG0E_FactoryReset = 0x0A

// MSG11
const val MSG11_NewAdmin = 0x01.toByte()
const val MSG11_NewUser = 0x02.toByte()
const val MSG11_DeleteUser = 0x04.toByte()
const val MSG11_SuspendUser = 0x05.toByte()
const val MSG11_EnableUser = 0x06.toByte()
const val MSG11_NewUserWithoutScan = 0x07.toByte()
const val MSG11_StopScan = 0x08.toByte()
const val MSG11_QueryAuthCode = 0x09.toByte()

// MSG13
const val MSG13_DeleteAllAdmin = 0x00
const val MSG13_DeleteAllUser = 0x01
const val MSG13_DeleteAllAdminAndUser = 0x03

// MSG15
const val MSG15_CmdNew = 0x00.toByte()
const val MSG15_CmdDelete = 0x01.toByte()
const val MSG15_CmdEdit = 0x02.toByte()
const val MSG15_CmdCancel = 0x03.toByte()

const val MSG15_TypePassword = 0x00.toByte()
const val MSG15_TypeFingerprint = 0x01.toByte()
const val MSG15_TypeNFC = 0x02.toByte()
const val MSG15_TypeSeizedFingerprint = 0x0C.toByte()
const val MSG15_TypeFace = 0x0A.toByte()

// MSG17
const val MSG17_Password = 0x00
const val MSG17_Fingerprint = 0x01
const val MSG17_AllAndUser = 0x02
const val MSG17_AllOtherUserPwd = 0x03
const val MSG17_AllOtherUserFingerprint = 0x04
const val MSG17_AllOtherUserNFC = 0x06
const val MSG17_AllOtherUserFace = 0x0B


// MSG19
const val MSG19_DeviceCheck = 0x00.toByte()
const val MSG19_CombinationLockEnable = 0x01.toByte()
const val MSG19_CombinationLockUnable = 0x02.toByte()

const val MSG19_LockKeepOpenEnable = 0x03.toByte()
const val MSG19_LockKeepOpenUnable = 0x04.toByte()

const val MSG19_VoicePromptsEnable = 0x05.toByte()
const val MSG19_VoicePromptsUnable = 0x06.toByte()

const val MSG19_VersionInformationQuery = 0x07.toByte()
const val MSG19_RestoreFactorySettings = 0x08.toByte()
const val MSG19_SystemOTA = 0x09.toByte()
const val MSG19_BatteryQuery = 0x0A.toByte()
const val MSG19_SystemReset = 0x0B.toByte()

const val MSG19_LockCylinderEnable = 0x0C.toByte()
const val MSG19_LockCylinderUnable = 0x0D.toByte()
const val MSG19_AntiPrizingAlarmEnable = 0x0E.toByte()
const val MSG19_AntiPrizingAlarmUnable = 0x0F.toByte()
const val MSG19_SecurityCardEnable = 0x10.toByte()
const val MSG19_SecurityCardUnable = 0x11.toByte()
const val MSG19_SystemLogEnable = 0x12.toByte()
const val MSG19_SystemLogUnable = 0x13.toByte()

const val MSG19_SystemOTAStop = 0x14.toByte()

const val MSG19_BluetoothKeepOnEnable = 0x15.toByte()
const val MSG19_BluetoothKeepOnUnable = 0x16.toByte()

const val MSG19_FingerprintOTA = 0x17.toByte()
const val MSG19_FingerprintOTAStop = 0x18.toByte()
const val MSG19_FingerprintVersionQuery = 0x19.toByte()
const val MSG19_SystemRepair = 0x1A.toByte()
const val MSG19_SystemFastOTA = 0x1B.toByte()
const val MSG19_FingerprintFastOTA = 0x1C.toByte()
const val MSG19_DisconnectBluetooth = 0x1D.toByte()

const val MSG19_InfraredEnable = 0x1E.toByte()
const val MSG19_InfraredUnable = 0x1F.toByte()
const val MSG19_AutomaticClosingEnable = 0x20.toByte()
const val MSG19_AutomaticClosingUnable = 0x21.toByte()

const val MSG19_OpenAlbum = 0x22.toByte()

const val MSG19_MagicNumberEnable = 0x23.toByte()
const val MSG19_MagicNumberUnable = 0x24.toByte()
const val MSG19_TempPwdEnable = 0x25.toByte()
const val MSG19_TempPwdUnable = 0x26.toByte()

const val MSG19_BackPanelCheck = 0x27.toByte()
const val MSG19_BackPanelRepair = 0x28.toByte()
const val MSG19_BackPanelVersionQuery = 0x29.toByte()
const val MSG19_AutoLockPeriodQuery = 0x2A.toByte()
const val MSG19_UnbindLock = 0x2B.toByte()

// MSG1A
const val MSG1A_WorkCorrectly = 0x00
const val MSG1A_KeyError = 0x01
const val MSG1A_NFCError = 0x02
const val MSG1A_FingerprintError = 0x04
const val MSG1A_FaceError = 0x1F8
const val MSG1A_FaceNIRError = 0x08
const val MSG1A_FaceRGBError = 0x10
const val MSG1A_FaceFlashError = 0x20
const val MSG1A_FaceTouchError = 0x40
const val MSG1A_FaceLcmError = 0x80
const val MSG1A_FaceExpansionChipError = 0x100
const val MSG1A_WifiSerialPortError = 0x200


// MSG 1D
const val MSG1D_UnlockPeriod5 = 5.toByte()
const val MSG1D_UnlockPeriod8 = 8.toByte()
const val MSG1D_UnlockPeriod10 = 10.toByte()
const val MSG1D_UnlockPeriod15 = 15.toByte()
const val MSG1D_UnlockPeriod30 = 30.toByte()

// MSG1E

const val MSG1E_SuspendedUserDone = 0x00
const val MSG1E_SuspendedUserFailed = 0x01
const val MSG1E_AddUserDone = 0x02
const val MSG1E_AddUserFailed = 0x03
const val MSG1E_DeleteUserDone = 0x04
const val MSG1E_DeleteUserFailed = 0x05
const val MSG1E_EnableUserDone = 0x06
const val MSG1E_EnableUserFailed = 0x07

const val MSG1E_AddOrModifyFingerprintFailed = 0x08
const val MSG1E_ModifyFingerprintDone = 0x09
const val MSG1E_DeleteFingerprintDone = 0x0A

const val MSG1E_AddOrModifyPasswordFailed = 0x0B
const val MSG1E_AddOrModifyPasswordDone = 0x0C
const val MSG1E_DeletePasswordDone = 0x0D

const val MSG1E_AddOrModifyNFCFailed = 0x0E
const val MSG1E_ModifyNFCDone = 0x0F
const val MSG1E_DeleteNFCDone = 0x10

const val MSG1E_DeleteAllUserDone = 0x11
const val MSG1E_DeleteAllUserFailed = 0x12

const val MSG1E_DeleteAllKeysDone = 0x13
const val MSG1E_DeleteAllKeysFailed = 0x14

const val MSG1E_CombinationLockDone = 0x15
const val MSG1E_CombinationLockFailed = 0x16

const val MSG1E_LockKeepOpenDone = 0x17
const val MSG1E_LockKeepOpenFailed = 0x18

const val MSG1E_VoicePromptsDone = 0x19
const val MSG1E_VoicePromptsFailed = 0x1A

const val MSG1E_LockCylinderDone = 0x1B
const val MSG1E_LockCylinderFailed = 0x1C

const val MSG1E_AntiPrizingAlarmDone = 0x1D
const val MSG1E_AntiPrizingAlarmFailed = 0x1E

const val MSG1E_UserValidPeriodDone = 0x1F
const val MSG1E_UnlockPeriodDone = 0x20
const val MSG1E_NoPermission = 0x21

const val MSG1E_RestoreFactorySettingsDone = 0x22

const val MSG1E_DeleteFingerprintFailed = 0x23

const val MSG1E_FingerprintIsFull = 0x24

const val MSG1E_DeviceBusy = 0x25

const val MSG1E_SecurityNFCDone = 0x26
const val MSG1E_SecurityNFCFailed = 0x27

const val MSG1E_LogDone = 0x28
const val MSG1E_LogFailed = 0x29

const val MSG1E_FingerprintHasExist = 0x2A
const val MSG1E_UserIsFull = 0x2B

const val MSG1E_NoDataForLongTime = 0x2C
const val MSG1E_AllowOTA = 0x2D
const val MSG1E_StopOTA = 0x2E

const val MSG1E_BluetoothKeepOnDone = 0x2F

const val MSG1E_FingerprintOTADone = 0x30
const val MSG1E_FingerprintOTAFailed = 0x31


const val MSG1E_StopFingerprintOTA = 0x32
const val MSG1E_AllowFingerprintOTA = 0x33

const val MSG1E_KeysHasExist = 0x34
const val MSG1E_UserLifecycleFailed = 0x35

const val MSG1E_SystemRepairDone = 0x36
const val MSG1E_OpenTheSlide = 0x37


const val MSG1E_DeleteFaceFailed = 0x38
const val MSG1E_FaceHasExist = 0x39

const val MSG1E_RestoreFactorySettingsFailed = 0x3A

const val MSG1E_AddORModifyFaceFailed = 0x3B

const val MSG1E_ModifyFaceDone = 0x3C
const val MSG1E_DeleteFaceDone = 0x3D

const val MSG1E_EnableInfraredDone = 0x3E
const val MSG1E_UnableInfraredDone = 0x3F

const val MSG1E_EnableAutomaticClosingDone = 0x40
const val MSG1E_EnableAutomaticClosingFailed = 0x41
const val MSG1E_UnableAutomaticClosingDone = 0x42
const val MSG1E_UnableAutomaticClosingFailed = 0x43

const val MSG1E_FaceIsFull = 0x44

const val MSG1E_EnableAlbumDone = 0x45
const val MSG1E_EnableAlbumFailed = 0x46

const val MSG1E_EnableMagicNumberDone = 0x47
const val MSG1E_UnableMagicNumberDone = 0x48

const val MSG1E_EnableTempPwdDone = 0x49
const val MSG1E_UnableTempPwdDone = 0x4A

const val MSG1E_FingerprintSamplingOnceDone = 0x4B
const val MSG1E_UnbindDone = 0x4C

// MSG2E


const val MSG2E_RemoteOpenDone = 0x00
const val MSG2E_DynamicCodeUpdateDone = 0x01
const val MSG2E_NoPermission = 0x02
const val MSG2E_UserLifecycleDone = 0x03

const val MSG2E_BluetoothNameModifyDone = 0x04
const val MSG2E_BluetoothNameModifyFailed = 0x05

const val MSG2E_PowerSavingDone = 0x06
const val MSG2E_PowerSavingFailed = 0x07

const val MSG2E_UserLifecycleFailed = 0x08
const val MSG2E_RemoteOpenFailed = 0x09

// MSG31

const val MSG31_QueryUserRecords = 0x00.toByte()
const val MSG31_QueryAllUserRecords = 0x01.toByte()
const val MSG31_QueryUserEvents = 0x02.toByte()
const val MSG31_QueryAllUserEvents = 0x03.toByte()

// MSG33

const val MSG33_TYPE_DeleteAllLOCKLog = 0x00.toByte()
const val MSG33_TYPE_DeleteAllUserLOCKLog = 0x01.toByte()
const val MSG33_TYPE_DeleteOneLOCKLog = 0x02.toByte()
const val MSG33_TYPE_DeleteAllUSERLog = 0x03.toByte()
const val MSG33_TYPE_DeleteAllUserUSERLog = 0x04.toByte()
const val MSG33_TYPE_DeleteOneUSERLog = 0x05.toByte()

// MSG 41
const val MSG41_CMDStartOta = 0x01.toByte()
const val MSG41_CMDStopOta = 0x02.toByte()
const val MSG41_CMDQueryVersion = 0x03.toByte()
const val MSG41_CMDFastStartOta = 0x04.toByte()
const val MSG41_CMDOtaSize = 0x05.toByte()
const val MSG41_CMDUpgradeDone = 0x06.toByte()

const val MSG41_TypeUpgradeAll = 0x00.toByte()
const val MSG41_TypeUpgradeSCPU = 0x01.toByte()
const val MSG41_TypeUpgradeNCPU = 0x02.toByte()
const val MSG41_TypeUpgradeModel = 0x03.toByte()
const val MSG41_TypeUpgradeUI = 0x04.toByte()
const val MSG41_TypeUpgradeFW = 0x05.toByte()

// MSG 42
const val MSG42_RspOTADone = 0x01
const val MSG42_RspOTAFailed = 0x02
const val MSG42_RspOTARefused = 0x03
const val MSG42_RspOTAAllow = 0x04
const val MSG42_RspOTABusy = 0x05
const val MSG42_RspOTAGetSizeDone = 0x06
const val MSG42_RspOTAGetFileDone = 0x07


// MSG 49

const val MSG49_TYPE_SetVolume = 0x00.toByte()
const val MSG49_TYPE_GetVolume = 0x01.toByte()
const val MSG49_TYPE_GetLockStatus = 0x02.toByte()
const val MSG49_TYPE_SetLanguage = 0x03.toByte()
const val MSG49_TYPE_GetLanguage = 0x04.toByte()
const val MSG49_TYPE_SetLockLifecycle = 0x05.toByte()
const val MSG49_TYPE_AdministratorSwitch = 0x06.toByte()
const val MSG49_TYPE_SetDoorbellFollow = 0x07.toByte()
const val MSG49_TYPE_GetDoorbellFollow = 0x08.toByte()
const val MSG49_TYPE_GetWifiRSSI = 0x09.toByte()
const val MSG49_TYPE_GetWifiStatus = 0x0A.toByte()
const val MSG49_TYPE_SetWifiPower = 0x0B.toByte()
const val MSG49_TYPE_GetWifiPower = 0x0C.toByte()

const val MSG49_VALUE_VolumeMute = 0x00
const val MSG49_VALUE_VolumeLow = 0x01
const val MSG49_VALUE_VolumeMiddle = 0x02
const val MSG49_VALUE_VolumeHigh = 0x03

const val MSG49_VALUE_Default = 0
const val MSG49_VALUE_LanguageOnlyChinese = 0x00
const val MSG49_VALUE_LanguageChinese = 0x01
const val MSG49_VALUE_LanguageEnglish = 0x02
const val MSG49_VALUE_DoorbellFollowEnable = 0x00
const val MSG49_VALUE_DoorbellFollowUnable = 0x01
const val MSG49_VALUE_WifiEnable = 0x00
const val MSG49_VALUE_WifiUnable = 0x01

// MSG4A

const val MSG4A_TYPE_Volume = 0x01
const val MSG4A_TYPE_LockStatus = 0x02
const val MSG4A_TYPE_Language = 0x04
const val MSG4A_TYPE_DoorbellFollow = 0x08
const val MSG4A_TYPE_WifiRSSI = 0x09
const val MSG4A_TYPE_WifiStatus = 0x0A
const val MSG4A_TYPE_WifiPower = 0x0C

const val MSG4A_VALUE_VolumeMute = 0x00
const val MSG4A_VALUE_VolumeLow = 0x01
const val MSG4A_VALUE_VolumeMiddle = 0x02
const val MSG4A_VALUE_VolumeHigh = 0x03

const val MSG4A_VALUE_LockIsOpen = 0x00
const val MSG4A_VALUE_LockIsClose = 0x01

const val MSG4A_VALUE_LanguageOnlyChinese = 0x00
const val MSG4A_VALUE_LanguageChinese = 0x01
const val MSG4A_VALUE_LanguageEnglish = 0x02

const val MSG4A_VALUE_DoorbellFollowUnable = 0x00
const val MSG4A_VALUE_DoorbellFollowEnable = 0x01

const val MSG4A_VALUE_WifiOff = 0x00
const val MSG4A_VALUE_WifiOn = 0x01

// MSG4E

const val MSG4E_SetTimeZoneDone = 0x00
const val MSG4E_SetVolumeDone = 0x01
const val MSG4E_SetUnlockPeriodDone = 0x02
const val MSG4E_SetLanguageDone = 0x03
const val MSG4E_SetLockLifecycleDone = 0x04
const val MSG4E_NotSupport = 0x05
const val MSG4E_PermissionChangedDone = 0x06
const val MSG4E_PermissionChangedFailed = 0x07
const val MSG4E_SetDoorbellFollowDone = 0x08
const val MSG4E_SetDoorbellFollowFailed = 0x09
const val MSG4E_SetWifiPowerDone = 0x0A
const val MSG4E_SetWifiPowerFailed = 0x0B
const val MSG4E_NoPermission = 0xFF

// MSG51

const val MSG51_LockFirmware = 0x01.toByte()
const val MSG51_FingerprintFirmware = 0x02.toByte()
const val MSG51_BackPanelFirmware = 0x03.toByte()

const val MSG57_WiFiInfo = 0x01.toByte()
const val MSG57_WifiHardWareInfo = 0x02.toByte()
const val MSG57_WifiVersionQuery = 0x03.toByte()

// MSG5E
const val MSG5E_Firmware = 0x00.toByte()
const val MSG5E_LockFirmware = 0x01.toByte()
const val MSG5E_FingerprintFirmware = 0x02.toByte()
const val MSG5E_BackPanelFirmware = 0x03.toByte()


const val MSG5E_OTASuccess = 0x00
const val MSG5E_OTAFailure = 0x01
const val MSG5E_OTAStartSendFile = 0x02
const val MSG5E_WifiSuccess = 0x03
const val MSG5E_WifiFailure = 0x04
const val MSG5E_WifiQueryFailure = 0x05


// MSG5F
const val MSG5F_VersionBackPanel = 0x00.toByte()
