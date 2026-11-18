
package com.example.luveabeauty.data

import java.security.MessageDigest
import java.security.SecureRandom
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

object PasswordHasher {

    fun generateSalt(): String {
        val salt = ByteArray(16)
        SecureRandom().nextBytes(salt)

        return salt.joinToString("") {
            "%02x".format(it)
        }
    }

    fun hashPassword(password: String, salt: String): String {
        val spec = PBEKeySpec(
            password.toCharArray(),
            salt.chunked(2).map { it.toInt(16).toByte() }.toByteArray(),
            120000,
            256
        )

        val hash = SecretKeyFactory
            .getInstance("PBKDF2WithHmacSHA256")
            .generateSecret(spec)
            .encoded

        spec.clearPassword()

        return hash.joinToString("") {
            "%02x".format(it)
        }
    }

    fun verifyPassword(
        password: String,
        salt: String,
        savedHash: String
    ): Boolean {
        val newHash = hashPassword(password, salt)

        return MessageDigest.isEqual(
            newHash.toByteArray(),
            savedHash.toByteArray()
        )
    }
}
