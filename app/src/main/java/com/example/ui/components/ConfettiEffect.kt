package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import kotlin.random.Random

private data class ConfettiParticle(
    val xRatio: Float,
    val initialSpeedY: Float,
    val speedX: Float,
    val color: Color,
    val size: Float,
    val rotationSpeed: Float,
    val isCircle: Boolean
)

@Composable
fun ConfettiEffect(
    trigger: Long,
    modifier: Modifier = Modifier
) {
    if (trigger <= 0L) return

    val progress = remember(trigger) { Animatable(0f) }

    val particles = remember(trigger) {
        val colors = listOf(
            Color(0xFFF59E0B), // Gold
            Color(0xFF10B981), // Emerald
            Color(0xFF3B82F6), // Blue
            Color(0xFF8B5CF6), // Purple
            Color(0xFFEC4899), // Pink
            Color(0xFFF97316)  // Orange
        )
        List(60) {
            ConfettiParticle(
                xRatio = Random.nextFloat(),
                initialSpeedY = Random.nextFloat() * 800f + 500f,
                speedX = (Random.nextFloat() - 0.5f) * 350f,
                color = colors.random(),
                size = Random.nextFloat() * 14f + 8f,
                rotationSpeed = (Random.nextFloat() - 0.5f) * 720f,
                isCircle = Random.nextBoolean()
            )
        }
    }

    LaunchedEffect(trigger) {
        progress.snapTo(0f)
        progress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 2200, easing = LinearEasing)
        )
    }

    if (progress.value < 1f) {
        Canvas(modifier = modifier.fillMaxSize()) {
            val canvasW = size.width
            val canvasH = size.height
            val t = progress.value

            particles.forEach { p ->
                val currentX = (p.xRatio * canvasW) + (p.speedX * t)
                // Gravity acceleration effect: y = v0*t + 0.5*g*t^2
                val currentY = (p.initialSpeedY * t) + (600f * t * t) - 50f
                val rotation = p.rotationSpeed * t
                val alpha = (1f - t * 0.9f).coerceIn(0f, 1f)

                if (currentY in 0f..canvasH && currentX in 0f..canvasW) {
                    val drawColor = p.color.copy(alpha = alpha)
                    rotate(degrees = rotation, pivot = Offset(currentX, currentY)) {
                        if (p.isCircle) {
                            drawCircle(
                                color = drawColor,
                                radius = p.size / 2,
                                center = Offset(currentX, currentY)
                            )
                        } else {
                            drawRect(
                                color = drawColor,
                                topLeft = Offset(currentX - p.size / 2, currentY - p.size / 2),
                                size = Size(p.size, p.size * 0.6f)
                            )
                        }
                    }
                }
            }
        }
    }
}
