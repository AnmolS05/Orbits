package com.orbits.app.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput

/**
 * Applies a 3D tactile push-down effect when the modifier is pressed.
 * Simulates a physical button press interaction.
 */
fun Modifier.premiumClickable(onDoubleTap: (() -> Unit)? = null, onClick: () -> Unit) = composed {
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(if (isPressed) 0.96f else 1.0f, label = "scale")
    val translationY by animateFloatAsState(if (isPressed) 4f else 0f, label = "translation")

    this
        .graphicsLayer {
            scaleX = scale
            scaleY = scale
            this.translationY = translationY
        }
        .pointerInput(Unit) {
            detectTapGestures(
                onPress = {
                    isPressed = true
                    tryAwaitRelease()
                    isPressed = false
                },
                onDoubleTap = onDoubleTap?.let { { it() } },
                onTap = { onClick() }
            )
        }
}
