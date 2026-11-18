package com.movtery.zalithlauncher.performance

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class BenchmarkResult(
    val averageFps: Float,
    val onePercentLowFps: Float,
    val averageFrameTimeMs: Float,
    val maxFrameTimeMs: Float,
    val samples: Int
)

data class BenchmarkState(
    val running: Boolean = false,
    val elapsedSeconds: Int = 0,
    val currentFps: Float = 0f,
    val result: BenchmarkResult? = null
)

class FpsBenchmark {

    private val frameTimes = mutableListOf<Long>()
    private val _state = MutableStateFlow(BenchmarkState())
    val state: StateFlow<BenchmarkState> = _state.asStateFlow()

    fun start() {
        synchronized(frameTimes) {
            frameTimes.clear()
        }
        _state.value = BenchmarkState(running = true)
    }

    fun stop(): BenchmarkResult? {
        val frames = synchronized(frameTimes) {
            frameTimes.toList()
        }

        if (frames.size < 2) {
            _state.value = _state.value.copy(running = false)
            return null
        }

        val sorted = frames.sorted()
        val averageFrameTime = frames.average().toFloat()
        val averageFps = if (averageFrameTime > 0f) 1000f / averageFrameTime else 0f

        val onePercentIndex = ((sorted.size * 0.01).toInt()).coerceAtLeast(0)
        val onePercentFrameTime = sorted[onePercentIndex].toFloat()
        val onePercentLowFps = if (onePercentFrameTime > 0f) {
            1000f / onePercentFrameTime
        } else 0f

        val result = BenchmarkResult(
            averageFps = averageFps,
            onePercentLowFps = onePercentLowFps,
            averageFrameTimeMs = averageFrameTime,
            maxFrameTimeMs = sorted.last().toFloat(),
            samples = frames.size
        )

        _state.value = BenchmarkState(
            running = false,
            result = result
        )

        return result
    }

    fun onFrameRendered(frameTimeNanos: Long) {
        if (!_state.value.running) return

        val frameTimeMs = frameTimeNanos / 1_000_000.0

        synchronized(frameTimes) {
            frameTimes.add(frameTimeMs.toLong())
        }

        val recent = synchronized(frameTimes) {
            frameTimes.takeLast(60)
        }

        val average = if (recent.isNotEmpty()) recent.average() else 0.0
        val fps = if (average > 0.0) 1000.0 / average else 0.0

        _state.value = _state.value.copy(
            currentFps = fps.toFloat(),
            elapsedSeconds = frameTimes.size / 60
        )
    }
}
