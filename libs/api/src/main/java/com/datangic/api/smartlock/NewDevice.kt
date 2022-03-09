package com.datangic.api.smartlock

data class NewDevice(
    val name: String,
    val sn: String,
    val mac: String,
    val imei: String,
    val secretCode: String,
    val createTime: Long = 0,
    val updateTime: Long = 0,
    val devUsers: Array<NewDevUser>
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as NewDevice

        if (name != other.name) return false
        if (sn != other.sn) return false
        if (mac != other.mac) return false
        if (imei != other.imei) return false
        if (secretCode != other.secretCode) return false
        if (createTime != other.createTime) return false
        if (updateTime != other.updateTime) return false
        if (!devUsers.contentEquals(other.devUsers)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + sn.hashCode()
        result = 31 * result + mac.hashCode()
        result = 31 * result + imei.hashCode()
        result = 31 * result + secretCode.hashCode()
        result = 31 * result + createTime.hashCode()
        result = 31 * result + updateTime.hashCode()
        result = 31 * result + devUsers.contentHashCode()
        return result
    }
}
