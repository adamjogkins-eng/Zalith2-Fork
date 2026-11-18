package com.movtery.zalithlauncher.performance

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.fpsTunerDataStore by preferencesDataStore(name = "zl2plus_fps_tuner")

data class FpsTunerState(
    val presetId: String = "performance",
    val resolutionScale: Float = 0.75f,
    val maxRefreshRate: Int = 90,
    val sustainedPerformance: Boolean = true,
    val forceBigCore: Boolean = true
)

class FpsTunerStore(private val context: Context) {

    private val presetKey = stringPreferencesKey("preset_id")
    private val scaleKey = floatPreferencesKey("resolution_scale")
    private val refreshKey = intPreferencesKey("max_refresh_rate")
    private val sustainedKey = stringPreferencesKey("sustained_performance")
    private val bigCoreKey = stringPreferencesKey("force_big_core")

    val state: Flow<FpsTunerState> = context.fpsTunerDataStore.data.map { prefs ->
        FpsTunerState(
            presetId = prefs[presetKey] ?: "performance",
            resolutionScale = prefs[scaleKey] ?: 0.75f,
            maxRefreshRate = prefs[refreshKey] ?: 90,
            sustainedPerformance = prefs[sustainedKey] != "false",
            forceBigCore = prefs[bigCoreKey] != "false"
        )
    }

    suspend fun applyPreset(preset: FpsPreset) {
        context.fpsTunerDataStore.edit {
            it[presetKey] = preset.id
            it[scaleKey] = preset.resolutionScale
            it[refreshKey] = preset.maxRefreshRate ?: 0
            it[sustainedKey] = preset.sustainedPerformance.toString()
            it[bigCoreKey] = preset.forceBigCore.toString()
        }
    }

    suspend fun setResolutionScale(value: Float) {
        context.fpsTunerDataStore.edit { it[scaleKey] = value }
    }

    suspend fun setMaxRefreshRate(value: Int) {
        context.fpsTunerDataStore.edit { it[refreshKey] = value }
    }

    suspend fun setSustainedPerformance(enabled: Boolean) {
        context.fpsTunerDataStore.edit { it[sustainedKey] = enabled.toString() }
    }

    suspend fun setForceBigCore(enabled: Boolean) {
        context.fpsTunerDataStore.edit { it[bigCoreKey] = enabled.toString() }
    }
}
