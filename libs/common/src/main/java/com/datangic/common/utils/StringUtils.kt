package com.datangic.common.utils


/**
 * String to HexByteArray
 */

fun String.toHexByteArray(): ByteArray {
    if (this.matches("^[0123456789abcdefABCDEF]+$".toRegex()) && this.length % 2 == 0) {
        val result = ByteArray(this.length / 2)
        for (i in this.indices.step(2)) {
            val l = this.codePointAt(i + 1).charToInt() + (this.codePointAt(i)).charToInt() * 16
            result[i / 2] = (if (l > 128) l - 256 else l).toByte()
        }
        return result
    } else {
        throw RuntimeException("$this is not HexString!")
    }
}

private fun Int.charToInt(): Int {
    return when (this) {
        in 48 until 58 -> this - 48
        in 97 until 103 -> this - 87
        in 65 until 71 -> this - 55
        else -> 0
    }
}

/**
 * ByteString to HexString
 */
fun ByteArray.toHexString(): String {
    val bytes = "0123456789ABCDEF"
    var result = ""
    for (i in this) {
        val j: Int = if (i.toInt() >= 0) i.toInt() else (i.toInt() + 256)
        result += bytes[(j.and(0xF0).shr(4)) % 16]
        result += bytes[(j.and(0x0F)) % 16]
    }
    return result
}


fun String.isPhoneNumber(zone: Int = 86): Boolean {

    return when (zone) {
        86 -> {
            this.matches("^[1]\\d{10}$".toRegex())
        }
        else -> {
            false
        }
    }
}
