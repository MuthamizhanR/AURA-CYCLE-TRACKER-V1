package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import kotlin.math.sin

@Composable
fun CanvasBunny(
    phase: String,
    mood: String,
    modifier: Modifier = Modifier,
    celebrating: Boolean = false,
    accentColor: Color
) {
    val effectiveMood = if (celebrating) "celebrating" else mood

    val earAngleDeg = when (effectiveMood) {
        "energetic" -> 10f
        "glowing" -> 5f
        "celebrating" -> 12f
        "cozy" -> 40f
        "resting" -> 60f
        else -> 20f
    }

    val eyesOpen = effectiveMood != "resting" && effectiveMood != "cozy"
    val halfClosed = effectiveMood == "cozy"

    val infiniteTransition = rememberInfiniteTransition()
    val breatheAnimation by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.035f,
        animationSpec = infiniteRepeatable(
            animation = tween(1600, easing = LinearOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    val yOffset = if (celebrating) -12f else 0f

    val plumText = Color(0xFF4B2440)
    val bunnyWhite = Color(0xFFFFFDFB)

    Canvas(modifier = modifier) {
        val sX = size.width / 200f
        val sY = size.height / 200f

        withTransform({
            scale(scaleX = sX * breatheAnimation, scaleY = sY * breatheAnimation, pivot = Offset(100f * sX, 100f * sY))
            translate(top = yOffset * sY)
        }) {
            // Glow for ovulation/celebrating
            if (effectiveMood == "glowing" || celebrating) {
                drawCircle(color = accentColor, radius = 70f, center = Offset(100f, 105f), alpha = 0.22f)
            }

            // Left Ear
            withTransform({
                rotate(degrees = -earAngleDeg, pivot = Offset(78f, 60f))
            }) {
                drawOval(color = bunnyWhite, topLeft = Offset(78f - 13f, 28f - 32f), size = Size(26f, 64f))
                drawOval(color = accentColor, topLeft = Offset(78f - 6f, 32f - 20f), size = Size(12f, 40f), alpha = 0.55f)
            }

            // Right Ear
            withTransform({
                rotate(degrees = earAngleDeg, pivot = Offset(122f, 60f))
            }) {
                drawOval(color = bunnyWhite, topLeft = Offset(122f - 13f, 28f - 32f), size = Size(26f, 64f))
                drawOval(color = accentColor, topLeft = Offset(122f - 6f, 32f - 20f), size = Size(12f, 40f), alpha = 0.55f)
            }

            // Body
            drawOval(color = bunnyWhite, topLeft = Offset(100f - 56f, 132f - 48f), size = Size(112f, 96f))
            // Head
            drawOval(color = bunnyWhite, topLeft = Offset(100f - 47f, 85f - 43f), size = Size(94f, 86f))

            // Blush
            drawCircle(color = Color(0xFFFF9EB5), radius = 6f, center = Offset(76f, 96f), alpha = 0.55f)
            drawCircle(color = Color(0xFFFF9EB5), radius = 6f, center = Offset(124f, 96f), alpha = 0.55f)

            // Eyes
            if (eyesOpen) {
                drawCircle(color = plumText, radius = 5f, center = Offset(84f, 84f))
                drawCircle(color = plumText, radius = 5f, center = Offset(116f, 84f))
                drawCircle(color = Color.White, radius = 1.6f, center = Offset(86f, 82f))
                drawCircle(color = Color.White, radius = 1.6f, center = Offset(118f, 82f))
            } else if (halfClosed) {
                val pathL = Path().apply { moveTo(79f, 84f); quadraticBezierTo(84f, 89f, 89f, 84f) }
                val pathR = Path().apply { moveTo(111f, 84f); quadraticBezierTo(116f, 89f, 121f, 84f) }
                drawPath(pathL, color = plumText, style = Stroke(width = 2.5f))
                drawPath(pathR, color = plumText, style = Stroke(width = 2.5f))
            } else {
                val pathL = Path().apply { moveTo(79f, 86f); quadraticBezierTo(84f, 82f, 89f, 86f) }
                val pathR = Path().apply { moveTo(111f, 86f); quadraticBezierTo(116f, 82f, 121f, 86f) }
                drawPath(pathL, color = plumText, style = Stroke(width = 2.5f))
                drawPath(pathR, color = plumText, style = Stroke(width = 2.5f))
            }

            // Mouth
            if (celebrating) {
                val p = Path().apply { moveTo(88f, 98f); quadraticBezierTo(100f, 110f, 112f, 98f) }
                drawPath(p, color = plumText, style = Stroke(width = 2.5f))
            } else if (effectiveMood == "resting") {
                val p = Path().apply { moveTo(94f, 99f); lineTo(106f, 99f) }
                drawPath(p, color = plumText, style = Stroke(width = 2.2f))
            } else {
                val p = Path().apply { moveTo(92f, 97f); quadraticBezierTo(100f, 104f, 108f, 97f) }
                drawPath(p, color = plumText, style = Stroke(width = 2.2f))
            }

            // Paws
            drawOval(color = bunnyWhite, topLeft = Offset(80f - 11f, 166f - 8f), size = Size(22f, 16f))
            drawOval(color = bunnyWhite, topLeft = Offset(120f - 11f, 166f - 8f), size = Size(22f, 16f))

            // Accessories per phase
            when {
                phase == "follicular" && !celebrating -> {
                    withTransform({ translate(130f, 50f) }) {
                        drawCircle(color = Color(0xFFFFC857), radius = 3.5f)
                        drawCircle(color = Color(0xFFFFD78A), radius = 3f, center = Offset(6f, -3f))
                        drawCircle(color = Color(0xFFFFD78A), radius = 3f, center = Offset(-6f, -3f))
                        drawCircle(color = Color(0xFFFFD78A), radius = 3f, center = Offset(6f, 3f))
                        drawCircle(color = Color(0xFFFFD78A), radius = 3f, center = Offset(-6f, 3f))
                    }
                }
                phase == "luteal" && !celebrating -> {
                    withTransform({ translate(82f, 140f) }) {
                        drawRoundRect(color = Color(0xFFC9AED6), topLeft = Offset(0f, 0f), size = Size(36f, 26f), cornerRadius = CornerRadius(10f))
                        drawRoundRect(color = Color(0xFFB79AC7), topLeft = Offset(12f, -6f), size = Size(12f, 8f), cornerRadius = CornerRadius(3f))
                    }
                }
                phase == "menstrual" && !celebrating -> {
                    val ap = Path().apply {
                        moveTo(58f, 150f)
                        quadraticBezierTo(100f, 190f, 142f, 150f)
                        lineTo(142f, 170f)
                        quadraticBezierTo(100f, 200f, 58f, 170f)
                        close()
                    }
                    drawPath(ap, color = Color(0xFFFF8FAE), alpha = 0.85f)
                }
                celebrating -> {
                    val ap1 = Path().apply { moveTo(40f, 60f); lineTo(44f, 50f); lineTo(48f, 60f); lineTo(44f, 57f); close() }
                    drawPath(ap1, color = Color(0xFFFFC857))
                    val ap2 = Path().apply { moveTo(165f, 70f); lineTo(169f, 60f); lineTo(173f, 70f); lineTo(169f, 67f); close() }
                    drawPath(ap2, color = Color(0xFF7FD9B9))
                    val ap3 = Path().apply { moveTo(30f, 110f); lineTo(34f, 100f); lineTo(38f, 110f); lineTo(34f, 107f); close() }
                    drawPath(ap3, color = Color(0xFFFF8FAE))
                }
            }
        }
    }
}
