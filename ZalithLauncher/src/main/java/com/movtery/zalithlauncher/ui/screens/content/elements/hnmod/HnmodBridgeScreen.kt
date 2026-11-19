/*
 * Zalith Launcher 2
 * Copyright (C) 2025 MovTery <movtery228@qq.com> and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/gpl-3.0.txt>.
 */

package com.movtery.zalithlauncher.ui.screens.content.elements.hnmod

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import com.movtery.zalithlauncher.ui.components.BackgroundCard
import com.movtery.zalithlauncher.ui.components.CardTitleLayout
import java.io.File
import java.net.URL
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private const val HNMOD_EXTENSION = ".hnmod"

@Composable
fun HnmodBridgeScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var directUrl by remember { mutableStateOf("") }
    var status by remember { mutableStateOf("No native mod selected.") }
    var selectedFile by remember { mutableStateOf<File?>(null) }
    var isDownloading by remember { mutableStateOf(false) }

    val filePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            scope.launch {
                val imported = withContext(Dispatchers.IO) {
                    runCatching {
                        val downloads = File(
                            context.getExternalFilesDir(null),
                            "horizon/native-mods"
                        ).apply { mkdirs() }

                        val fileName = (uri.lastPathSegment ?: "native-mod.hnmod")
                            .substringAfterLast('/')
                            .ifBlank { "native-mod.hnmod" }

                        val target = File(downloads, fileName)
                        context.contentResolver.openInputStream(uri)?.use { input ->
                            target.outputStream().use(output -> input.copyTo(output))
                        }
                        target
                    }.getOrNull()
                }

                if (imported != null) {
                    selectedFile = imported
                    status = "Imported: ${imported.name}"
                } else {
                    status = "Could not import the selected file."
                }
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        CardTitleLayout(
            influencedByBackground = true
        ) {
            Text(
                text = "Native Horizon Mods",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(16.dp)
            )
        }

        BackgroundCard(
            influencedByBackground = true
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Download or import a Horizon native mod (.hnmod), then open it in Starling.",
                    style = MaterialTheme.typography.bodyMedium
                )

                Text(
                    text = "Native mods run with your account permissions and are not sandboxed. Only import mods from sources you trust.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error
                )

                OutlinedTextField(
                    value = directUrl,
                    onValueChange = { directUrl = it },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    label = { Text("Direct .hnmod HTTPS link") }
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = {
                            if (!directUrl.startsWith("https://")) {
                                status = "Enter a direct HTTPS link to a .hnmod file."
                                return@Button
                            }

                            isDownloading = true
                            status = "Downloading native mod…"

                            scope.launch {
                                val downloaded = withContext(Dispatchers.IO) {
                                    runCatching {
                                        val downloads = File(
                                            context.getExternalFilesDir(null),
                                            "horizon/native-mods"
                                        ).apply { mkdirs() }

                                        val fileName = directUrl
                                            .substringAfterLast('/')
                                            .ifBlank { "native-mod.hnmod" }
                                            .let { if (it.endsWith(HNMOD_EXTENSION)) it else "$it$HNMOD_EXTENSION" }

                                        val target = File(downloads, fileName)
                                        URL(directUrl).openStream().use { input ->
                                            target.outputStream().use(output -> input.copyTo(output))
                                        }
                                        target
                                    }.getOrNull()
                                }

                                isDownloading = false
                                if (downloaded != null) {
                                    selectedFile = downloaded
                                    status = "Downloaded: ${downloaded.name}"
                                } else {
                                    status = "Download failed. Check the link and internet connection."
                                }
                            }
                        },
                        enabled = !isDownloading
                    ) {
                        Text(if (isDownloading) "Downloading…" else "Download")
                    }

                    OutlinedButton(
                        onClick = { filePicker.launch("*/*") }
                    ) {
                        Text("Import file")
                    }
                }
            }
        }

        BackgroundCard(
            influencedByBackground = true
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = status,
                    style = MaterialTheme.typography.bodyMedium
                )

                Button(
                    onClick = {
                        val file = selectedFile
                        if (file == null) {
                            status = "Import or download a .hnmod first."
                            return@Button
                        }

                        val uri = FileProvider.getUriForFile(
                            context,
                            "${context.packageName}.provider",
                            file
                        )

                        val intent = Intent(Intent.ACTION_VIEW).apply {
                            setDataAndType(uri, "application/octet-stream")
                            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        }

                        val handler = context.packageManager.queryIntentActivities(intent, 0)
                        if (handler.isEmpty()) {
                            status = "No app can open this .hnmod. Install Starling first."
                        } else {
                            runCatching { context.startActivity(intent) }
                                .onFailure { status = "Could not open Starling: ${it.message}" }
                        }
                    },
                    enabled = selectedFile != null
                ) {
                    Text("Open in Starling")
                }

                Text(
                    text = "ZL2++ downloads and prepares the file. Starling validates, compiles, and installs native Horizon mods.",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}