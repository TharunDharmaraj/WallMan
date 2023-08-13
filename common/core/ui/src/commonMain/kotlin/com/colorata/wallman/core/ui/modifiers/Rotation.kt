package com.colorata.wallman.core.ui.modifiers

import android.os.Build
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.math.pow
import kotlin.math.sin

private const val maxAngle = 50f
private const val pi = Math.PI.toFloat()
private const val maxTranslation = 140f
private val SpringEasing = Easing { x ->
    val c4 = (2 * pi) / 3f

    return@Easing when (x) {
        0f -> 0f
        1f -> 1f
        else -> 2f.pow(-10 * x) * sin((x * 10f - 0.75f) * c4) + 1f
    }
}

interface RotationState {
    fun startRotation()

    fun update(rotation: Offset)

    fun reset()

    val rotation: Offset

    val isRotationInProgress: Boolean
}

private class RotationStateImpl(private val scope: CoroutineScope) : RotationState {
    override val rotation: Offset
        get() = Offset(_rotationX.value, _rotationY.value)

    private val _rotationX = Animatable(0f)
    private val _rotationY = Animatable(0f)

    override var isRotationInProgress: Boolean by mutableStateOf(false)

    override fun startRotation() {
        isRotationInProgress = true
    }

    override fun update(rotation: Offset) {
        scope.launch {
            _rotationX.animateTo(rotation.x)
        }
        scope.launch {
            _rotationY.animateTo(rotation.y)
        }
    }

    override fun reset() {
        val animationSpec = tween<Float>(1000, easing = SpringEasing)
        scope.launch {
            _rotationX.animateTo(0f, animationSpec)
        }
        scope.launch {
            _rotationY.animateTo(0f, animationSpec)
        }
        isRotationInProgress = false
    }
}

@Composable
fun rememberRotationState(): RotationState {
    val scope = rememberCoroutineScope()
    return remember { RotationStateImpl(scope) }
}

fun Modifier.detectRotation(state: RotationState) = composed {

    var angle by remember { mutableStateOf(Offset.Zero) }
    var viewSize by remember { mutableStateOf(Size.Zero) }
    onGloballyPositioned { coordinates ->
        viewSize = Size(
            width = coordinates.size.width.toFloat(),
            height = coordinates.size.height.toFloat()
        )
    }
        .pointerInput(Unit) {
            detectDragGestures(
                onDragCancel = {
                    state.reset()
                },
                onDragEnd = {
                    state.reset()
                },
                onDragStart = {
                    angle = Offset.Zero
                    state.startRotation()
                }
            ) { change, dragAmount ->
                change.consume()
                if (viewSize != Size.Zero) {
                    val newAngle = getRotationAngles(dragAmount, viewSize)

                    val update = Offset(
                        (angle.x + newAngle.x).coerceIn(-maxAngle, maxAngle),
                        (angle.y + newAngle.y).coerceIn(-maxAngle, maxAngle)
                    )

                    angle = update
                    state.update(Offset(-angle.y, angle.x))
                }
            }
        }
}

fun Modifier.displayRotation(
    state: RotationState,
    layer: Float = 0f
) = this then runWhen(Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
    graphicsLayer {
        rotationY = state.rotation.y
        rotationX = state.rotation.x
        translationX = state.rotation.y * maxTranslation / 90f * layer
        translationY = -state.rotation.x * maxTranslation / 90f * layer
    }
}

private fun getRotationAngles(
    distance: Offset,
    size: Size
): Offset {
    val acceleration = 3
    val rotationX = (distance.x / size.width) * maxAngle * acceleration
    val rotationY = (distance.y / size.height) * maxAngle * acceleration
    return Offset(rotationX, rotationY)
}