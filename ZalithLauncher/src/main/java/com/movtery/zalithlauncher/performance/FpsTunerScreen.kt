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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch

@Suppress("UNUSED_PARAMETER")
@Composable
fun FpsTunerScreen(
    store: FpsTunerStore,
    benchmark: FpsBenchmark,
    onApplyPreset: (FpsPreset) -> Unit
) {
    val state by store.state.collectAsState(
        initial = FpsTunerState()
    )

    val coroutineScope = rememberCoroutineScope()
    var saving by remember { mutableStateOf(false) }
    var statusMessage by remember {
        mutableStateOf<String?>(null)
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(
            16.dp
        )
    ) {
        item {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "FPS Tuner",
                    style = MaterialTheme.typography.headlineSmall
                )

                Text(
                    text = "Preset storage preview",
                    style = MaterialTheme.typography.titleMedium
                )

                Text(
                    text = "These presets are not connected to the " +
                        "game settings yet. Saving one does not " +
                        "change Minecraft performance.",
                    style = MaterialTheme.typography.bodyMedium
                )

                Text(
                    text = "Use the quick resolution buttons in " +
                        "Renderer settings for the integrated " +
                        "resolution control.",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val presetName =
                        FpsPresets.byId(state.presetId)?.name
                            ?: state.presetId

                    Text(
                        text = "Saved preset: $presetName",
                        style = MaterialTheme.typography.titleMedium
                    )

                    Text(
                        text = "Stored resolution: " +
                            "${(state.resolutionScale * 100).toInt()}%"
                    )

                    val refreshRateText =
                        if (state.maxRefreshRate > 0) {
                            "${state.maxRefreshRate} Hz"
                        } else {
                            "Not specified"
                        }

                    Text(
                        text = "Stored refresh preference: " +
                            refreshRateText
                    )

                    Text(
                        text = "Stored sustained performance: " +
                            if (state.sustainedPerformance) {
                                "On"
                            } else {
                                "Off"
                            }
                    )

                    Text(
                        text = "Stored big-core preference: " +
                            if (state.forceBigCore) {
                                "On"
                            } else {
                                "Off"
                            }
                    )

                    Text(
                        text = "Stored preferences only; " +
                            "not active game settings.",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }

        items(
            items = FpsPresets.ALL,
            key = { preset -> preset.id }
        ) { preset ->
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = preset.name,
                        style = MaterialTheme.typography.titleMedium
                    )

                    Text(
                        text = "Resolution preference: " +
                            "${(preset.resolutionScale * 100).toInt()}%"
                    )

                    Text(
                        text = "This saves a preset for future " +
                            "integration. It does not apply RAM, " +
                            "JVM, renderer, or driver changes.",
                        style = MaterialTheme.typography.bodySmall
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Button(
                            enabled = !saving,
                            onClick = {
                                saving = true
                                statusMessage = null

                                coroutineScope.launch {
                                    try {
                                        store.applyPreset(preset)
                                        statusMessage =
                                            "${preset.name} saved. " +
                                            "Game settings unchanged."
                                    } catch (
                                        cancelled: CancellationException
                                    ) {
                                        throw cancelled
                                    } catch (error: Exception) {
                                        statusMessage =
                                            "Could not save preset: " +
                                            (
                                                error.localizedMessage
                                                    ?: "Unknown error"
                                            )
                                    } finally {
                                        saving = false
                                    }
                                }
                            }
                        ) {
                            Text(
                                text = if (saving) {
                                    "Saving…"
                                } else {
                                    "Save preset"
                                }
                            )
                        }
                    }
                }
            }
        }

        statusMessage?.let { message ->
            item {
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
        }

        item {
            Text(
                text = "Benchmark controls are temporarily disabled " +
                    "until accurate game-frame measurements " +
                    "are connected.",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}
