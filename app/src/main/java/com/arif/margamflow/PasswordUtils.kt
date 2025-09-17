package com.arif.margamflow

import java.security.SecureRandom
import java.security.spec.KeySpec
import java.util.Base64
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

object PasswordUtils {

    // Generate salt (16 bytes)
    fun generateSalt(): String {
        val random = SecureRandom()
        val salt = ByteArray(16)
        random.nextBytes(salt)
        return Base64.getEncoder().encodeToString(salt)
    }

    // Hash password using PBKDF2WithHmacSHA256
    fun hashPassword(password: String, salt: String): String {
        val iterations = 65536
        val keyLength = 256
        val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")

        val spec: KeySpec = PBEKeySpec(password.toCharArray(), Base64.getDecoder().decode(salt), iterations, keyLength)
        val hash = factory.generateSecret(spec).encoded

        return Base64.getEncoder().encodeToString(hash)
    }

    // Verify input password
    fun verifyPassword(inputPassword: String, storedHash: String, storedSalt: String): Boolean {
        val newHash = hashPassword(inputPassword, storedSalt)
        return newHash == storedHash
    }
}
