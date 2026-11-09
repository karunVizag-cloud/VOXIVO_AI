package com.voxivo.ai.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.voxivo.ai.ui.theme.DarkGlassBorder
import com.voxivo.ai.ui.theme.DarkGlassSurface
import com.voxivo.ai.ui.theme.ElectricBlue
import com.voxivo.ai.ui.theme.NeonCyan
import com.voxivo.ai.ui.theme.SquircleShape

@Composable
fun CyberGlassCard(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(20.dp),
    borderColor: Color = DarkGlassBorder,
    backgroundColor: Color = DarkGlassSurface,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .shadow(elevation = 12.dp, shape = shape, spotColor = NeonCyan)
            .clip(shape)
            .background(backgroundColor)
            .border(width = 1.dp, color = borderColor, shape = shape)
            .padding(16.dp)
    ) {
        content()
    }
}

@Composable
fun AiOrb(
    modifier: Modifier = Modifier,
    isPulsing: Boolean = true,
    size: Dp = 120.dp
) {
    val infiniteTransition = rememberInfiniteTransition(label = "OrbPulse")

    val scale by infiniteTransition.animateFloat(
        initialValue = 0.92f,
        targetValue = if (isPulsing) 1.08f else 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "OrbScale"
    )

    val ringAlpha by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = if (isPulsing) 0.8f else 0.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "RingAlpha"
    )

    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.toPx() / 2f, size.toPx() / 2f)
            val baseRadius = (size.toPx() / 2f) * 0.75f * scale

            // Outer Glow Wave Ring
            drawCircle(
                color = NeonCyan.copy(alpha = ringAlpha * 0.3f),
                radius = baseRadius * 1.25f,
                center = center
            )

            // Mid Energy Ring
            drawCircle(
                color = ElectricBlue.copy(alpha = ringAlpha * 0.6f),
                radius = baseRadius * 1.08f,
                center = center
            )

            // Inner Core with Radial Gradient
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color.White, NeonCyan, ElectricBlue, Color(0xFF071426)),
                    center = center,
                    radius = baseRadius
                ),
                radius = baseRadius,
                center = center
            )
        }
    }
}

@Composable
fun SoundWaveVisualizer(
    modifier: Modifier = Modifier,
    isListening: Boolean = true
) {
    val infiniteTransition = rememberInfiniteTransition(label = "Waves")

    val h1 by infiniteTransition.animateFloat(
        initialValue = 8f, targetValue = if (isListening) 28f else 8f,
        animationSpec = infiniteRepeatable(tween(400, easing = LinearEasing), RepeatMode.Reverse), label = "h1"
    )
    val h2 by infiniteTransition.animateFloat(
        initialValue = 14f, targetValue = if (isListening) 40f else 12f,
        animationSpec = infiniteRepeatable(tween(600, easing = LinearEasing), RepeatMode.Reverse), label = "h2"
    )
    val h3 by infiniteTransition.animateFloat(
        initialValue = 20f, targetValue = if (isListening) 48f else 16f,
        animationSpec = infiniteRepeatable(tween(450, easing = LinearEasing), RepeatMode.Reverse), label = "h3"
    )
    val h4 by infiniteTransition.animateFloat(
        initialValue = 12f, targetValue = if (isListening) 34f else 10f,
        animationSpec = infiniteRepeatable(tween(550, easing = LinearEasing), RepeatMode.Reverse), label = "h4"
    )
    val h5 by infiniteTransition.animateFloat(
        initialValue = 6f, targetValue = if (isListening) 24f else 6f,
        animationSpec = infiniteRepeatable(tween(350, easing = LinearEasing), RepeatMode.Reverse), label = "h5"
    )

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        listOf(h1, h2, h3, h4, h5).forEach { barHeight ->
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(barHeight.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.verticalGradient(
                            listOf(NeonCyan, ElectricBlue)
                        )
                    )
            )
            Spacer(modifier = Modifier.width(4.dp))
        }
    }
}

@Composable
fun SquircleAvatar(
    letter: String = "K",
    size: Dp = 48.dp,
    onClick: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .size(size)
            .clip(SquircleShape)
            .background(
                Brush.linearGradient(
                    listOf(ElectricBlue, NeonCyan)
                )
            )
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(size - 4.dp)
                .clip(SquircleShape)
                .background(Color(0xFF071426)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = letter,
                color = NeonCyan,
                fontWeight = FontWeight.Bold,
                fontSize = (size.value * 0.45f).sp
            )
        }
    }
}
