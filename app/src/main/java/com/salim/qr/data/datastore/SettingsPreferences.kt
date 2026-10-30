package com.salim.qr.data.datastore

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "salim_settings")

data class SalimSettings(
    val onboardingCompleted: Boolean = false,
    val themeMode: String = "system", // "system", "light", "dark"
    val hapticsEnabled: Boolean = true,
    val beepEnabled: Boolean = true,
    val speechEnabled: Boolean = false,
    val autoOpenLinks: Boolean = false,
    val autoCopy: Boolean = false,
    val continuousScan: Boolean = false,
    val saveScansToHistory: Boolean = true,
    val saveCreatedToHistory: Boolean = true,
    val appLockEnabled: Boolean = false,
    val viewfinderStyle: String = "frame", // "frame", "large", "fullscreen"
    val retentionPeriod: String = "forever" // "forever", "30", "90"
)

class SettingsPreferences(private val context: Context) {
    companion object {
        private val KEY_ONBOARDING_COMPLETED = booleanPreferencesKey("onboarding_completed")
        private val KEY_THEME_MODE = stringPreferencesKey("theme_mode")
        private val KEY_HAPTICS = booleanPreferencesKey("haptics_enabled")
        private val KEY_BEEP = booleanPreferencesKey("beep_enabled")
        private val KEY_SPEECH = booleanPreferencesKey("speech_enabled")
        private val KEY_AUTO_OPEN = booleanPreferencesKey("auto_open_links")
        private val KEY_AUTO_COPY = booleanPreferencesKey("auto_copy")
        private val KEY_CONTINUOUS_SCAN = booleanPreferencesKey("continuous_scan")
        private val KEY_SAVE_SCANS = booleanPreferencesKey("save_scans")
        private val KEY_SAVE_CREATED = booleanPreferencesKey("save_created")
        private val KEY_APP_LOCK = booleanPreferencesKey("app_lock")
        private val KEY_VIEWFINDER = stringPreferencesKey("viewfinder_style")
        private val KEY_RETENTION = stringPreferencesKey("retention_period")
    }

    val settingsFlow: Flow<SalimSettings> = context.dataStore.data.map { prefs ->
        SalimSettings(
            onboardingCompleted = prefs[KEY_ONBOARDING_COMPLETED] ?: false,
            themeMode = prefs[KEY_THEME_MODE] ?: "system",
            hapticsEnabled = prefs[KEY_HAPTICS] ?: true,
            beepEnabled = prefs[KEY_BEEP] ?: true,
            speechEnabled = prefs[KEY_SPEECH] ?: false,
            autoOpenLinks = prefs[KEY_AUTO_OPEN] ?: false,
            autoCopy = prefs[KEY_AUTO_COPY] ?: false,
            continuousScan = prefs[KEY_CONTINUOUS_SCAN] ?: false,
            saveScansToHistory = prefs[KEY_SAVE_SCANS] ?: true,
            saveCreatedToHistory = prefs[KEY_SAVE_CREATED] ?: true,
            appLockEnabled = prefs[KEY_APP_LOCK] ?: false,
            viewfinderStyle = prefs[KEY_VIEWFINDER] ?: "frame",
            retentionPeriod = prefs[KEY_RETENTION] ?: "forever"
        )
    }

    suspend fun setOnboardingCompleted(completed: Boolean) {
        context.dataStore.edit { it[KEY_ONBOARDING_COMPLETED] = completed }
    }

    suspend fun setThemeMode(mode: String) {
        context.dataStore.edit { it[KEY_THEME_MODE] = mode }
    }

    suspend fun setHapticsEnabled(enabled: Boolean) {
        context.dataStore.edit { it[KEY_HAPTICS] = enabled }
    }

    suspend fun setBeepEnabled(enabled: Boolean) {
        context.dataStore.edit { it[KEY_BEEP] = enabled }
    }

    suspend fun setSpeechEnabled(enabled: Boolean) {
        context.dataStore.edit { it[KEY_SPEECH] = enabled }
    }

    suspend fun setAutoOpenLinks(enabled: Boolean) {
        context.dataStore.edit { it[KEY_AUTO_OPEN] = enabled }
    }

    suspend fun setAutoCopy(enabled: Boolean) {
        context.dataStore.edit { it[KEY_AUTO_COPY] = enabled }
    }

    suspend fun setContinuousScan(enabled: Boolean) {
        context.dataStore.edit { it[KEY_CONTINUOUS_SCAN] = enabled }
    }

    suspend fun setSaveScansToHistory(enabled: Boolean) {
        context.dataStore.edit { it[KEY_SAVE_SCANS] = enabled }
    }

    suspend fun setSaveCreatedToHistory(enabled: Boolean) {
        context.dataStore.edit { it[KEY_SAVE_CREATED] = enabled }
    }

    suspend fun setAppLockEnabled(enabled: Boolean) {
        context.dataStore.edit { it[KEY_APP_LOCK] = enabled }
    }

    suspend fun setViewfinderStyle(style: String) {
        context.dataStore.edit { it[KEY_VIEWFINDER] = style }
    }

    suspend fun setRetentionPeriod(period: String) {
        context.dataStore.edit { it[KEY_RETENTION] = period }
    }
}
