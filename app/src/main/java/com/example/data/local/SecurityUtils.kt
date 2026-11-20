package com.example.data.local

import java.security.MessageDigest
import java.security.SecureRandom
import kotlin.math.max

object SecurityUtils {
    private val random = SecureRandom()

    fun generateSalt(): String {
        val bytes = ByteArray(16)
        random.nextBytes(bytes)
        return bytes.joinToString("") { "%02x".format(it) }
    }

    fun hashPassword(password: String, salt: String): String {
        val md = MessageDigest.getInstance("SHA-256")
        val input = "$salt:$password".toByteArray(Charsets.UTF_8)
        return md.digest(input).joinToString("") { "%02x".format(it) }
    }

    fun verifyPassword(password: String, storedValue: String): Boolean {
        val parts = storedValue.split(":")
        if (parts.size != 2) return false
        val salt = parts[0]
        val expectedHash = parts[1]
        return hashPassword(password, salt) == expectedHash
    }

    fun createStoredPassword(password: String): String {
        val salt = generateSalt()
        return "$salt:${hashPassword(password, salt)}"
    }
}

object LevelSystem {
    fun levelForXp(xp: Int): Int = max(1, (xp / 250) + 1)

    fun xpIntoCurrentLevel(xp: Int): Int = xp % 250

    fun xpNeededPerLevel(): Int = 250

    fun progressFraction(xp: Int): Float = (xp % 250) / 250f
}
