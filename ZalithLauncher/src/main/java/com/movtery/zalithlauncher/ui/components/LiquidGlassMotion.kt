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

package com.movtery.zalithlauncher.ui.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.graphicsLayer

/**
 * Shared motion constants for the liquid-glass UI.
 */
object LiquidGlassMotion {
    const val PAGE_DURATION_MS = 280
    const val LIST_DURATION_MS = 220
    const val LIST_STAGGER_MS = 32

    val pressedScale = 0.97f

    fun pageSpec() = tween<Float>(
        durationMillis = PAGE_DURATION_MS
    )

    fun pressSpring() = spring<Float>(
        dampingRatio = Spring.DampingRatioMediumBouncy,
        stiffness = Spring.StiffnessMediumLow
    )
}

/**
 * Adds a subtle iOS-like press response to a glass card or button.
 */
fun Modifier.liquidGlassPress(
    interactionSource: MutableInteractionSource,
    pressedScale: Float = LiquidGlassMotion.pressedScale
): Modifier = composed {
    val pressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (pressed) pressedScale else 1f,
        animationSpec = LiquidGlassMotion.pressSpring(),
        label = "liquidGlassPressScale"
    )

    graphicsLayer {
        scaleX = scale
        scaleY = scale
    }
}

/**
 * Creates a shared interaction source for pressable liquid-glass surfaces.
 */
@Composable
fun rememberLiquidGlassInteractionSource(): MutableInteractionSource {
    return remember { MutableInteractionSource() }
}