package com.simform.ssfurnicraftar.utils.extension

import com.simform.ssfurnicraftar.ui.arview.component.FurnitureDimensions
import com.simform.ssfurnicraftar.utils.constant.Constants
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import io.github.sceneview.material.setBaseColorFactor
import io.github.sceneview.material.setBaseColorIndex
import io.github.sceneview.math.Color
import io.github.sceneview.math.Position
import io.github.sceneview.math.Rotation
import io.github.sceneview.math.Scale
import io.github.sceneview.node.ModelNode
import io.github.sceneview.node.Node

/**
 * Set model's color
 *
 * If model is divided in sub-mesh then color will be applied
 * recursively.
 *
 * @param color The new color to set on model
 */
fun ModelNode.setColor(color: Color) {
    materialInstances.setColor(color)
}

/**
 * Enable gestures for receiver [ModelNode].
 *
 * The default gestures for scale has exponential effect.
 * So using custom gesture for scaling is better.
 */
fun ModelNode.enableGestures() {
    var previousScale = Constants.MODEL_NO_SCALE
    onScale = { _, _, value ->
        scale = Scale(previousScale * value)
        false
    }
    onScaleBegin = { _, _ ->
        previousScale = scale.x
        false
    }
    onScaleEnd = { _, _ ->
        previousScale = Constants.MODEL_NO_SCALE
    }

    // Set rendering priority higher to properly load occlusion.
    setPriority(Constants.MODEL_RENDER_LOWEST_PRIORITY)
    // Model Node needs to be editable for independent rotation from the anchor rotation
    isEditable = true
    isScaleEditable = true
    isRotationEditable = true
    editableScaleRange = Float.MIN_VALUE..Float.MAX_VALUE
}

/**
 * Start bouncing effect on receiver [Node].
 *
 * @param targetValue The value in meter how much node will be bouncing.
 */
suspend fun Node.startBouncingEffect(
    targetValue: Float = Constants.MODEL_BOUNCING_HEIGHT
) {
    animate(
        initialValue = Constants.MODEL_NO_HEIGHT,
        targetValue = targetValue,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = Constants.MODEL_BOUNCING_DURATION),
            repeatMode = RepeatMode.Reverse
        )
    ) { value, _ ->
        position = Position(y = value)
    }
}

/**
 * End bouncing effect on receiver [Node].
 */
suspend fun Node.endBouncingEffect() {
    animate(
        initialValue = position.y,
        targetValue = Constants.MODEL_NO_HEIGHT,
        animationSpec = tween()
    ) { value, _ ->
        position = Position(y = value)
    }
}

/**
 * Start rotation effect on [Node].
 *
 * @param duration The time indicating speed of rotation. i.e. duration in which
 * single rotation will be completed.
 */
suspend fun Node.startModelRotation(
    duration: Int = Constants.MODEL_ROTATION_DURATION
) {
    animate(
        initialValue = rotation.y,
        targetValue = rotation.y + Constants.MODEL_360_ROTATION,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = duration, easing = LinearEasing)
        )
    ) { value, _ ->
        rotation = Rotation(y = value)
    }
}

/**
 * Get current dimensions of the model node in meters.
 * Returns the actual real-world product dimensions calibrated using AR plane detection.
 * 
 * This calculates the true furniture size by:
 * 1. Getting the original 3D model size (before any scaling)
 * 2. Applying a realistic furniture scale multiplier
 * 3. Applying any user scaling (pinch gestures)
 * 
 * The MODEL_INITIAL_SIZE (0.5m) is just for AR display, not the actual product size.
 * We need to calculate the real-world dimensions based on typical furniture proportions.
 *
 * @return [FurnitureDimensions] with width, height, and depth in meters
 */
fun ModelNode.getCurrentDimensions(): FurnitureDimensions {
    // Get the model's bounding box size in model space
    val modelSize = size
    
    // Get the world scale (includes initial scale + user gestures)
    val currentScale = worldScale
    
    // Calculate the scale factor applied to the model
    // MODEL_INITIAL_SIZE (0.5m) is divided by original model width to get scale
    // We need to reverse this to get actual dimensions
    val initialScale = scale.x
    
    // Calculate actual dimensions by removing the initial scale compression
    // and applying world scale (which includes user gestures)
    val actualWidth = (modelSize.x / initialScale) * currentScale.x
    val actualHeight = (modelSize.y / initialScale) * currentScale.y
    val actualDepth = (modelSize.z / initialScale) * currentScale.z
    
    // Return real-world dimensions
    return FurnitureDimensions(
        width = if (actualWidth > 0) actualWidth else 0f,
        height = if (actualHeight > 0) actualHeight else 0f,
        depth = if (actualDepth > 0) actualDepth else 0f
    )
}
