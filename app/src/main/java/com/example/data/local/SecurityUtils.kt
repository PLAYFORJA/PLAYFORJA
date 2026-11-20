package com.example.data.local

import java.security.MessageDigest
import java.security.SecureRandom
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec
import kotlin.math.max

object SecurityUtils {
    private const val PBKDF2_ALGORITHM = "PBKDF2WithHmacSHA256"
    private const val PBKDF2_ITERATIONS = 210_000
    private const val KEY_LENGTH_BITS = 256
    private const val LEGACY_SEPARATOR = ":"
    private const val VERSION_PREFIX = "pbkdf2_sha256"

    private val random = SecureRandom()

    fun generateSalt(): String {
        val bytes = ByteArray(16)
        random.nextBytes(bytes)
        return bytes.toHex()
    }

    /**
     * Legacy SHA-256 helper retained only to verify accounts created by older app versions.
     * New passwords must be stored with [createStoredPassword].
     */
    fun hashPassword(password: String, salt: String): String {
        val md = MessageDigest.getInstance("SHA-256")
        val input = "$salt:$password".toByteArray(Charsets.UTF_8)
        return md.digest(input).toHex()
    }

    fun verifyPassword(password: String, storedValue: String): Boolean {
        val parts = storedValue.split(LEGACY_SEPARATOR)
        return when {
            parts.size == 4 && parts[0] == VERSION_PREFIX -> {
                val iterations = parts[1].toIntOrNull() ?: return false
                if (iterations < 1_000 || iterations > 2_000_000) return false
                val salt = parts[2].hexToBytesOrNull() ?: return false
                val expected = parts[3].hexToBytesOrNull() ?: return false
                val actual = deriveKey(password, salt, iterations) ?: return false
                MessageDigest.isEqual(expected, actual)
            }
            parts.size == 2 -> {
                val expected = parts[1].hexToBytesOrNull() ?: return false
                val actual = hashPassword(password, parts[0]).hexToBytesOrNull() ?: return false
                MessageDigest.isEqual(expected, actual)
            }
            else -> false
        }
    }

    fun createStoredPassword(password: String): String {
        val salt = ByteArray(16).also(random::nextBytes)
        val hash = deriveKey(password, salt, PBKDF2_ITERATIONS)
            ?: throw IllegalStateException("No se pudo generar el hash de contraseña.")
        return "$VERSION_PREFIX:$PBKDF2_ITERATIONS:${salt.toHex()}:${hash.toHex()}"
    }

    fun needsRehash(storedValue: String): Boolean =
        !storedValue.startsWith("$VERSION_PREFIX:")

    private fun deriveKey(password: String, salt: ByteArray, iterations: Int): ByteArray? {
        val spec = PBEKeySpec(password.toCharArray(), salt, iterations, KEY_LENGTH_BITS)
        return try {
            SecretKeyFactory.getInstance(PBKDF2_ALGORITHM)
                .generateSecret(spec)
                .encoded
        } catch (_: Exception) {
            null
        } finally {
            spec.clearPassword()
        }
    }

    private fun ByteArray.toHex(): String =
        joinToString("") { byte -> "%02x".format(byte) }

    private fun String.hexToBytesOrNull(): ByteArray? {
        if (isEmpty() || length % 2 != 0 || any { it.digitToIntOrNull(16) == null }) return null
        return chunked(2).map { it.toInt(16).toByte() }.toByteArray()
    }
}

object LevelSystem {
    fun levelForXp(xp: Int): Int = max(1, (xp / 250) + 1)

    fun xpIntoCurrentLevel(xp: Int): Int = xp % 250

    fun xpNeededPerLevel(): Int = 250

    fun progressFraction(xp: Int): Float = (xp % 250) / 250f
}
