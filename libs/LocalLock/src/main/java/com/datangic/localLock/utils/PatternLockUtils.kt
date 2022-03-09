package com.datangic.localLock.utils

import com.datangic.localLock.widgets.PatternLockView
import java.io.UnsupportedEncodingException
import java.math.BigInteger
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.*

object PatternLockUtils {

    private const val UTF8 = "UTF-8"
    private const val SHA1 = "SHA-1"
    private const val MD5 = "MD5"

    private fun PatternLockUtils() {
        throw AssertionError("You can not instantiate this class. Use its static utility " +
                "methods instead")
    }

    /**
     * Serializes a given pattern to its equivalent string representation. You can store this string
     * in any persistence storage or send it to the server for verification
     *
     * @param pattern The actual pattern
     * @return The pattern in its string form
     */
    fun patternToString(patternLockView: PatternLockView,
                        pattern: List<PatternLockView.Dot>?): String {
        if (pattern == null) {
            return ""
        }
        val patternSize = pattern.size
        val stringBuilder = StringBuilder()
        for (i in 0 until patternSize) {
            val dot: PatternLockView.Dot = pattern[i]
            stringBuilder.append(dot.mRow * patternLockView.mDotCount + dot.mColumn)
        }
        return stringBuilder.toString()
    }

    /**
     * De-serializes a given string to its equivalent pattern representation
     *
     * @param string The pattern serialized with [.patternToString]
     * @return The actual pattern
     */
    fun stringToPattern(patternLockView: PatternLockView,
                        string: String): List<PatternLockView.Dot> {
        val result: MutableList<PatternLockView.Dot> = ArrayList<PatternLockView.Dot>()
        for (element in string) {
            val number = Character.getNumericValue(element)
            result.add(PatternLockView.Dot.of(number / patternLockView.mDotCount,
                    number % patternLockView.mDotCount))
        }
        return result
    }

    /**
     * Serializes a given pattern to its equivalent SHA-1 representation. You can store this string
     * in any persistence storage or send it to the server for verification
     *
     * @param pattern The actual pattern
     * @return The SHA-1 string of the pattern
     */
    fun patternToSha1(patternLockView: PatternLockView,
                      pattern: List<PatternLockView.Dot>?): String? {
        return try {
            val messageDigest = MessageDigest.getInstance(SHA1)
            messageDigest.update(patternToString(patternLockView, pattern).toByteArray(charset(UTF8)))
            val digest = messageDigest.digest()
            val bigInteger = BigInteger(1, digest)
            String.format(null as Locale?,
                            "%0" + digest.size * 2 + "x", bigInteger).lowercase(Locale.getDefault())
        } catch (e: NoSuchAlgorithmException) {
            null
        } catch (e: UnsupportedEncodingException) {
            null
        }
    }

    /**
     * Serializes a given pattern to its equivalent MD5 representation. You can store this string
     * in any persistence storage or send it to the server for verification
     *
     * @param pattern The actual pattern
     * @return The MD5 string of the pattern
     */
    fun patternToMD5(patternLockView: PatternLockView,
                     pattern: List<PatternLockView.Dot>?): String? {
        return try {
            val messageDigest = MessageDigest.getInstance(MD5)
            messageDigest.update(patternToString(patternLockView, pattern).toByteArray(charset(UTF8)))
            val digest = messageDigest.digest()
            val bigInteger = BigInteger(1, digest)
            String.format(null as Locale?,
                            "%0" + digest.size * 2 + "x", bigInteger).lowercase(Locale.ROOT)
        } catch (e: NoSuchAlgorithmException) {
            null
        } catch (e: UnsupportedEncodingException) {
            null
        }
    }

    /**
     * Generates a random "CAPTCHA" pattern. The generated pattern is easy for the user to re-draw.
     *
     *
     * NOTE: This method is **not** optimized and **not** benchmarked yet for large mSize
     * of the pattern's matrix. Currently it works fine with a matrix of `3x3` cells.
     * Be careful when the mSize increases.
     */
    @Throws(IndexOutOfBoundsException::class)
    fun generateRandomPattern(patternLockView: PatternLockView?,
                              size: Int): ArrayList<PatternLockView.Dot>? {
        requireNotNull(patternLockView) { "PatternLockView can not be null." }
        if (size <= 0 || size > patternLockView.mDotCount) {
            throw IndexOutOfBoundsException("Size must be in range [1, " +
                    patternLockView.mDotCount.toString() + "]")
        }
        val usedIds: MutableList<Int> = ArrayList()
        var lastId: Int = RandomUtils.randInt(patternLockView.mDotCount)
        usedIds.add(lastId)
        while (usedIds.size < size) {
            // We start from an empty matrix, so there's always a break point to
            // exit this loop
            val lastRow: Int = lastId / patternLockView.mDotCount
            val lastCol: Int = lastId % patternLockView.mDotCount

            // This is the max available rows/ columns that we can reach from
            // the cell of `lastId` to the border of the matrix.
            val maxDistance = lastRow.coerceAtLeast(patternLockView.mDotCount - lastRow).coerceAtLeast(lastCol.coerceAtLeast(patternLockView.mDotCount - lastCol))
            lastId = -1

            // Starting from `distance` = 1, find the closest-available
            // neighbour value of the cell [lastRow, lastCol].
            for (distance in 1..maxDistance) {

                // Now we have a square surrounding the current cell. We call it
                // ABCD, in which A is top-left, and C is bottom-right.
                val rowA = lastRow - distance
                val colA = lastCol - distance
                val rowC = lastRow + distance
                val colC = lastCol + distance
                var randomValues: IntArray

                // Process randomly AB, BC, CD, and DA. Break the loop as soon
                // as we find one value.
                val lines: IntArray = RandomUtils.randIntArray(4)
                for (line in lines) {
                    when (line) {
                        0 -> {
                            if (rowA >= 0) {
                                randomValues = RandomUtils.randIntArray(Math.max(0, colA),
                                        Math.min(patternLockView.mDotCount,
                                                colC + 1))
                                for (c in randomValues) {
                                    lastId = (rowA * patternLockView.mDotCount
                                            + c)
                                    lastId = if (usedIds.contains(lastId)) -1 else break
                                }
                            }
                        }
                        1 -> {
                            if (colC < patternLockView.mDotCount) {
                                randomValues = RandomUtils.randIntArray(Math.max(0, rowA + 1),
                                        Math.min(patternLockView.mDotCount,
                                                rowC + 1))
                                for (r in randomValues) {
                                    lastId = (r * patternLockView.mDotCount
                                            + colC)
                                    lastId = if (usedIds.contains(lastId)) -1 else break
                                }
                            }
                        }
                        2 -> {
                            if (rowC < patternLockView.mDotCount) {
                                randomValues = RandomUtils.randIntArray(Math.max(0, colA),
                                        Math.min(patternLockView.mDotCount,
                                                colC))
                                for (c in randomValues) {
                                    lastId = (rowC * patternLockView.mDotCount
                                            + c)
                                    lastId = if (usedIds.contains(lastId)) -1 else break
                                }
                            }
                        }
                        3 -> {
                            if (colA >= 0) {
                                randomValues = RandomUtils.randIntArray(Math.max(0, rowA + 1),
                                        Math.min(patternLockView.mDotCount,
                                                rowC))
                                for (r in randomValues) {
                                    lastId = (r * patternLockView.mDotCount
                                            + colA)
                                    lastId = if (usedIds.contains(lastId)) -1 else break
                                }
                            }
                        }
                    }
                    if (lastId >= 0) break
                }
                if (lastId >= 0) break
            }
            usedIds.add(lastId)
        }
        val result: ArrayList<PatternLockView.Dot> = ArrayList<PatternLockView.Dot>()
        for (id in usedIds) {
            result.add(PatternLockView.Dot.of(id))
        }
        return result
    }
}