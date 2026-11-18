package com.movtery.zalithlauncher.performance

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun FpsTunerScreen(
    store: FpsTunerStore,
    benchmark: FpsBenchmark,
    onApplyPreset: (FpsPreset) -> Unit
) {
    val state by store.state.collectAsState(initial = FpsTunerState())
    val benchmarkState by benchmark.state.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "FPS Tuner",
                style = MaterialTheme.typography.headlineSmall
            )
        }

        items(FpsPresets.ALL) { preset ->
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = preset.name,
                        style = MaterialTheme.typography.titleMedium
                    )

                    Text(text = preset.description)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Button(
                            onClick = {
                                onApplyPreset(preset)
                            }
                        ) {
                            Text("Apply")
                        }
                    }
                }
            }
        }

        item {
            Card {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text("Resolution scale: ${(state.resolutionScale * 100).toInt()}%")

                    Slider(
                        value = state.resolutionScale,
                        onValueChange = { store.setResolutionScale(it) },
                        valueRange = 0.5f..1.0f
                    )

                    Text("Max refresh rate: ${state.maxRefreshRate} FPS")

                    Slider(
                        value = state.maxRefreshRate.toFloat(),
                        onValueChange = { store.setMaxRefreshRate(it.toInt()) },
                        valueRange = 30f..120f
                    )

                    SettingToggle(
                        title = "Sustained performance",
                        checked = state.sustainedPerformance,
                        onCheckedChange = { store.setSustainedPerformance(it) }
                    )

                    SettingToggle(
                        title = "Force big cores",
                        checked = state.forceBigCore,
                        onCheckedChange = { store.setForceBigCore(it) }
                    )
                }
            }
        }

        item {
            Card {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text("Benchmark")

                    if (benchmarkState.running) {
                        Text("Running: ${benchmarkState.elapsedSeconds}s")
                        Text("Current FPS: ${benchmarkState.currentFps.toInt()}")
                    } else {
                        benchmarkState.result?.let { result ->
                            Text("Average FPS: ${result.averageFps.toInt()}")
                            Text("1% low FPS: ${result.onePercentLowFps.toInt()}")
                            Text("Average frame time: ${result.averageFrameTimeMs.toInt()} ms")
                            Text("Samples: ${result.samples}")
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Button(
                            onClick = {
                                if (benchmarkState.running) {
                                    benchmark.stop()
                                } else {
                                    benchmark.start()
                                }
                            }
                        ) {
                            Text(
                                if (benchmarkState.running) "Stop" else "Start"
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SettingToggle(
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            modifier = Modifier.weight(1f)
        )

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}
