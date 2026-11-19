package com.example.smartexpensemanager.data.local.auth

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

/*
Lần đầu mã hóa, app tạo khóa AES trong Android Keystore.
Những lần sau, app lấy lại khóa theo KEY_ALIAS.
Mỗi lần mã hóa, Cipher tạo một IV mới. DataStore giữ IV và bản mã.
Android Keystore giữ khóa.
Base64 chuyển các byte thành chuỗi để ghi vào Preferences DataStore
 */
internal object SessionCipher {
    private const val KEYSTORE = "AndroidKeyStore"
    private const val KEY_ALIAS = "smart_expense_auth_session"
    private const val TRANSFORMATION = "AES/GCM/NoPadding"

    private fun existingKey(): SecretKey? = KeyStore.getInstance(KEYSTORE)
        .apply {
            load(null)
        }
        .getKey(KEY_ALIAS, null) as? SecretKey

    @Synchronized
    private fun getOrCreateKey(): SecretKey {
        existingKey()?.let { return it }
        val generator = KeyGenerator.getInstance(
            KeyProperties.KEY_ALGORITHM_AES, KEYSTORE)
        generator.init(
            KeyGenParameterSpec.Builder(
                KEY_ALIAS, KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT)
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                .setKeySize(256)
                .build())
        return generator.generateKey()
    }

    fun encrypt(plaintText: String): String {
        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.ENCRYPT_MODE, getOrCreateKey())
        val iv = Base64.encodeToString(cipher.iv, Base64.NO_WRAP)
        val encrypted = cipher.doFinal(plaintText.toByteArray(Charsets.UTF_8))
        val cipherText = Base64.encodeToString(encrypted, Base64.NO_WRAP)
        return "$iv:$cipherText"
    }

    fun decrypt(value: String): String? {
        val parts = value.split(':', limit = 2)
        if (parts.size != 2) return null
        val key = existingKey() ?: return null
        val iv = Base64.decode(parts[0], Base64.NO_WRAP)
        val cipherText = Base64.decode(parts[1], Base64.NO_WRAP)
        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.DECRYPT_MODE, key, GCMParameterSpec(128, iv))
        return cipher.doFinal(cipherText)
            .toString(Charsets.UTF_8)
    }
}