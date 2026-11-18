package com.movtery.zalithlauncher.performance

import kotlinx.serialization.Serializable

@Serializable
data class FpsPreset(
    val id: String,
    val name: String,
    val description: String,
    val resolutionScale: Float,
    val maxRefreshRate: Int?,
    val sustainedPerformance: Boolean,
    val forceBigCore: Boolean,
    val renderer: String?,
    val vulkanDriver: String?,
    val javaMemoryMb: Int?,
    val extraJvmArgs: List<String>
)

object FpsPresets {
    val POTATO = FpsPreset(
        id = "potato",
        name = "Potato",
        description = "Maximum FPS on weak devices. Low resolution, conservative CPU use.",
        resolutionScale = 0.50f,
        maxRefreshRate = 60,
        sustainedPerformance = false,
        forceBigCore = false,
        renderer = null,
        vulkanDriver = null,
        javaMemoryMb = 2048,
        extraJvmArgs = listOf(
            "-XX:+UseG1GC",
            "-XX:MaxGCPauseMillis=50",
            "-Dfml.readTimeout=180"
        )
    )

    val PERFORMANCE = FpsPreset(
        id = "performance",
        name = "Performance",
        description = "Best balance of FPS, visuals, and battery life.",
        resolutionScale = 0.75f,
        maxRefreshRate = 90,
        sustainedPerformance = true,
        forceBigCore = true,
        renderer = null,
        vulkanDriver = null,
        javaMemoryMb = 3072,
        extraJvmArgs = listOf(
            "-XX:+UseG1GC",
            "-XX:MaxGCPauseMillis=40"
        )
    )

    val BALANCED = FpsPreset(
        id = "balanced",
        name = "Balanced",
        description = "Good visuals with stable performance.",
        resolutionScale = 0.90f,
        maxRefreshRate = 120,
        sustainedPerformance = true,
        forceBigCore = false,
        renderer = null,
        vulkanDriver = null,
        javaMemoryMb = 4096,
        extraJvmArgs = listOf(
            "-XX:+UseG1GC"
        )
    )

    val MAX_QUALITY = FpsPreset(
        id = "max_quality",
        name = "Max Quality",
        description = "Native resolution and high refresh rate. Requires a strong device.",
        resolutionScale = 1.00f,
        maxRefreshRate = null,
        sustainedPerformance = true,
        forceBigCore = true,
        renderer = null,
        vulkanDriver = null,
        javaMemoryMb = 6144,
        extraJvmArgs = emptyList()
    )

    val ALL = listOf(POTATO, PERFORMANCE, BALANCED, MAX_QUALITY)

    fun byId(id: String): FpsPreset? = ALL.firstOrNull { it.id == id }
}
