package com.datangic.api.smartlock

data class DeleteDevUser(
    val homeId:Int,
    val devSn:String,
    val users:Array<Int>
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as DeleteDevUser

        if (homeId != other.homeId) return false
        if (devSn != other.devSn) return false
        if (!users.contentEquals(other.users)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = homeId
        result = 31 * result + devSn.hashCode()
        result = 31 * result + users.contentHashCode()
        return result
    }
}
