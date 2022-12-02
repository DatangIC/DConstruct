package com.datangic.api.smartlock

data class NetLock<T>(
    val devId: Int,
    val devName:String,
    val devNo:String,
    val productResource:T
)
//
//{
//    "devId": 1257,
//    "devIndex": "b0cadb99-1c15-4691-8d83-9bed0d19419f",
//    "devName": "tuya1",
//    "devNo": "112233445566",
//    "homeId": 201,
//    "productName": "LockWiFiTuYaMT1586",
//    "productResource": {
//    "createTime": 1628736224298,
//    "imei": "359355041886388",
//    "secretCode": "asdq323rqfw242424tg43w4ttgwg",
//    "sn": "0000112233445566",
//    "battery": 0,
//    "users": [
//    {
//        "authCode": "24tg5d6ue5h7yeurg56yedg456yed4f56yws4e56tffw4e5twse5t4e4d5ted4r5yseg45tyws4",
//        "createTime": 1628736224333,
//        "userName": "张三",
//        "userId": 1
//    }
//    ]
//}
//}
