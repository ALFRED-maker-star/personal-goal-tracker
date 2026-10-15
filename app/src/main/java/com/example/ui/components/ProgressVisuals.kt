package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.GradientEnd
import com.example.ui.theme.GradientMid
import com.example.ui.theme.GradientStart
import com.example.ui.theme.SuccessGold
import com.example.ui.theme.SuccessGoldLight
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

@Composable
fun GradientLinearProgressBar(
    progress: Float,
    modifier: Modifier = Modifier,
    height: Dp = 10.dp,
    trackColor: Color = MaterialTheme.colorScheme.surfaceVariant,
    isCompleted: Boolean = false
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress.coerceIn(0f, 1f),
        animationSpec = tween(durationMillis = 600, easing = LinearEasing),
        label = "linear_progress"
    )

    val gradientBrush = if (isCompleted) {
        Brush.horizontalGradient(listOf(SuccessGold, Color(0xFFFBBF24), SuccessGold))
    } else {
        Brush.horizontalGradient(listOf(GradientStart, GradientMid, GradientEnd))
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .clip(RoundedCornerShape(percent = 50))
            .background(trackColor)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(animatedProgress)
                .height(height)
                .clip(RoundedCornerShape(percent = 50))
                .background(gradientBrush)
        )
    }
}

@Composable
fun GradientCircularProgressRing(
    progress: Float,
    modifier: Modifier = Modifier,
    size: Dp = 120.dp,
    strokeWidth: Dp = 12.dp,
    isCompleted: Boolean = false,
    content: @Composable (() -> Unit)? = null
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress.coerceIn(0f, 1f),
        animationSpec = tween(durationMillis = 750, easing = LinearEasing),
        label = "circular_progress"
    )

    val trackColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
    val sweepColors = if (isCompleted) {
        listOf(SuccessGold, Color(0xFFFDE68A), SuccessGold)
    } else {
        listOf(GradientStart, GradientMid, GradientEnd, GradientStart)
    }

    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize().padding(strokeWidth / 2)) {
            val canvasSize = this.size
            val strokePx = strokeWidth.toPx()
            val radius = (canvasSize.minDimension - strokePx) / 2f
            val center = Offset(canvasSize.width / 2f, canvasSize.height / 2f)

            // Background track
            drawCircle(
                color = trackColor,
                radius = radius,
                center = center,
                style = Stroke(width = strokePx)
            )

            if (animatedProgress > 0.01f) {
                val brush = Brush.sweepGradient(
                    colors = sweepColors,
                    center = center
                )

                drawArc(
                    brush = brush,
                    startAngle = -90f,
                    sweepAngle = animatedProgress * 360f,
                    useCenter = false,
                    topLeft = Offset(center.x - radius, center.y - radius),
                    size = Size(radius * 2, radius * 2),
                    style = Stroke(width = strokePx, cap = StrokeCap.Round)
                )
            }
        }

        if (content != null) {
            content()
        } else {
            val percentText = (animatedProgress * 100).toInt()
            if (isCompleted || percentText >= 100) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Completed",
                    tint = SuccessGold,
                    modifier = Modifier.size(size * 0.4f)
                )
            } else {
                Text(
                    text = "$percentText%",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = (size.value * 0.22f).sp
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@Composable
fun DeadlineCountdownBadge(
    daysRemaining: Long,
    isCompleted: Boolean,
    modifier: Modifier = Modifier
) {
    if (isCompleted) {
        Surface(
            modifier = modifier,
            shape = RoundedCornerShape(8.dp),
            color = SuccessGoldLight
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.EmojiEvents,
                    contentDescription = null,
                    tint = SuccessGold,
                    modifier = Modifier.size(13.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Goal Conquered",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = SuccessGold
                )
            }
        }
        return
    }

    val (bg, textColor, text) = when {
        daysRemaining < 0 -> Triple(
            Color(0xFFFEE2E2),
            Color(0xFFDC2626),
            "Overdue by ${-daysRemaining}d"
        )
        daysRemaining == 0L -> Triple(
            Color(0xFFFEF3C7),
            Color(0xFFD97706),
            "Due Today!"
        )
        daysRemaining <= 7 -> Triple(
            Color(0xFFFEF3C7),
            Color(0xFFB45309),
            "$daysRemaining days left"
        )
        else -> Triple(
            Color(0xFFF3E8FF),
            GradientStart,
            "$daysRemaining days left"
        )
    }

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        color = bg
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Schedule,
                contentDescription = null,
                tint = textColor,
                modifier = Modifier.size(13.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                color = textColor
            )
        }
    }
}

@Composable
fun CelebratoryBadge(
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = SuccessGoldLight,
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(SuccessGold),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.EmojiEvents,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(15.dp)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "CONQUERED",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 1.sp
                ),
                color = SuccessGold
            )
        }
    }
}

private data class ConfettiParticle(
    val x: Float,
    val y: Float,
    val speedY: Float,
    val size: Float,
    val color: Color,
    val rotation: Float,
    val rotationSpeed: Float
)

@Composable
fun ConfettiVisual(
    modifier: Modifier = Modifier
) {
    val confettiColors = listOf(
        SuccessGold,
        Color(0xFFFBBF24),
        GradientStart,
        GradientEnd,
        Color(0xFF38BDF8),
        Color(0xFF34D399)
    )

    val particles = remember {
        List(35) {
            ConfettiParticle(
                x = Random.nextFloat(),
                y = Random.nextFloat() * -0.5f,
                speedY = 0.003f + Random.nextFloat() * 0.006f,
                size = 6f + Random.nextFloat() * 8f,
                color = confettiColors.random(),
                rotation = Random.nextFloat() * 360f,
                rotationSpeed = (Random.nextFloat() - 0.5f) * 10f
            )
        }
    }

    val anim = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        anim.animateTo(
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 2400, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            )
        )
    }

    Canvas(modifier = modifier.fillMaxSize()) {
        val currentProgress = anim.value
        val width = size.width
        val height = size.height

        particles.forEach { p ->
            val py = ((p.y + currentProgress * p.speedY * 150) % 1.2f) * height
            val px = (p.x * width) + sin((py / 30f).toDouble()).toFloat() * 12f

            if (py in 0f..height) {
                drawCircle(
                    color = p.color.copy(alpha = 0.85f),
                    radius = p.size,
                    center = Offset(px, py)
                )
            }
        }
    }
}
