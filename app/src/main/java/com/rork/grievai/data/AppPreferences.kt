package com.rork.grievai.data

import android.content.Context
import android.content.SharedPreferences
import java.security.MessageDigest

class AppPreferences(context: Context) {

    private val prefs: SharedPreferences =
        context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    var hasCompletedOnboarding: Boolean
        get() = prefs.getBoolean(KEY_ONBOARDING, false)
        set(value) = prefs.edit().putBoolean(KEY_ONBOARDING, value).apply()

    var lastRole: String
        get() = prefs.getString(KEY_LAST_ROLE, UserRole.STUDENT.name) ?: UserRole.STUDENT.name
        set(value) = prefs.edit().putString(KEY_LAST_ROLE, value).apply()

    fun hasPin(): Boolean = !prefs.getString(KEY_PIN_HASH, null).isNullOrBlank()

    fun setPin(pin: String) {
        require(pin.length == 4 && pin.all { it.isDigit() }) { "PIN must be 4 digits" }
        prefs.edit().putString(KEY_PIN_HASH, hash(pin)).apply()
    }

    fun verifyPin(pin: String): Boolean {
        val stored = prefs.getString(KEY_PIN_HASH, null) ?: return false
        return stored == hash(pin)
    }

    fun clearPin() {
        prefs.edit().remove(KEY_PIN_HASH).apply()
    }

    private fun hash(pin: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
            .digest(pin.toByteArray(Charsets.UTF_8))
        return digest.joinToString("") { "%02x".format(it) }
    }

    companion object {
        private const val PREFS_NAME = "grievai_prefs"
        private const val KEY_ONBOARDING = "has_completed_onboarding"
        private const val KEY_PIN_HASH = "app_pin_hash"
        private const val KEY_LAST_ROLE = "last_role"
    }
}