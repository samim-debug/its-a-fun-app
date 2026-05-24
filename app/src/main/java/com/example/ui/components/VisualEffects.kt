package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import kotlin.random.Random

// 1. Premium Glassmorphic Brush Generator
val GlassmorphicGradient = Brush.verticalGradient(
    colors = listOf(
        Color(0x14FFFFFF), // ~8% opacity
        Color(0x05FFFFFF)  // ~2% opacity
    )
)

val GlassmorphicBorderGradient = Brush.verticalGradient(
    colors = listOf(
        Color(0x1AFFFFFF), // 10% opacity white
        Color(0x0DFFFFFF)  // 5% opacity white
    )
)

// 2. Interactive Flipping Virtual Card Container
@Composable
fun InteractiveVirtualCard(
    isFlipped: Boolean,
    onFlip: () -> Unit,
    modifier: Modifier = Modifier,
    frontContent: @Composable BoxScope.() -> Unit,
    backContent: @Composable BoxScope.() -> Unit
) {
    // Rotation Y Animation
    val rotation by animateFloatAsState(
        targetValue = if (isFlipped) 180f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "CardFlipAnimation"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(210.dp)
            .graphicsLayer {
                rotationY = rotation
                cameraDistance = 12 * density
            }
            .clip(RoundedCornerShape(32.dp))
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color(0xFF1E1E1E),
                        Color(0xFF121212),
                        Color(0xFF1A1A1A)
                    )
                )
            )
            .border(
                width = 1.dp,
                brush = GlassmorphicBorderGradient,
                shape = RoundedCornerShape(32.dp)
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null, // No distracting full-rect grey ripple
                onClick = onFlip
            )
    ) {
        if (rotation <= 90f) {
            Box(Modifier.fillMaxSize()) {
                frontContent()
            }
        } else {
            Box(
                Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        rotationY = 180f // Correct mirror flip for back contents
                    }
            ) {
                backContent()
            }
        }
    }
}

// 3. Falling Particle Confetti Overlay for Successful payments success
data class ConfettiParticle(
    var x: Float,
    var y: Float,
    var size: Float,
    var color: Color,
    var speedY: Float,
    var speedX: Float,
    var rotation: Float,
    var rotationSpeed: Float
)

@Composable
fun ConfettiOverlay(modifier: Modifier = Modifier) {
    val particles = remember { mutableStateListOf<ConfettiParticle>() }
    val colors = listOf(
        NeonLime, // Vibrant Theme Orange
        CyberPurple, // Cyber Purple
        CyberCyan, // Cyber Cyan
        NeonCoral, // Neon Coral
        Color(0xFFFFC107), // Gold
        Color(0xFF4CAF50)  // Green
    )

    LaunchedEffect(Unit) {
        // Spawn 65 dynamic random particles
        for (i in 0..65) {
            particles.add(
                ConfettiParticle(
                    x = Random.nextFloat() * 1000f,
                    y = -Random.nextFloat() * 200f - 20f,
                    size = Random.nextFloat() * 25f + 12f,
                    color = colors.random(),
                    speedY = Random.nextFloat() * 12f + 6f,
                    speedX = (Random.nextFloat() - 0.5f) * 6f,
                    rotation = Random.nextFloat() * 360f,
                    rotationSpeed = (Random.nextFloat() - 0.5f) * 10f
                )
            )
        }

        // Particle physics render loop
        while (true) {
            delay(16) // ~60fps step
            for (i in particles.indices) {
                val p = particles[i]
                p.y += p.speedY
                p.x += p.speedX
                p.rotation += p.rotationSpeed
                if (p.y > 2200f) {
                    p.y = -50f
                    p.x = Random.nextFloat() * 1080f
                }
            }
            // Trigger recomposition manually using a state hook
        }
    }

    // Canvas render
    val triggerSwap = remember { mutableStateOf(0) }
    LaunchedEffect(particles) {
        while (true) {
            delay(20)
            triggerSwap.value += 1
        }
    }

    Canvas(modifier = modifier.fillMaxSize()) {
        triggerSwap.value // reference
        for (p in particles) {
            drawContext.canvas.save()
            drawContext.canvas.translate(p.x, p.y)
            drawContext.canvas.rotate(p.rotation)
            drawRect(
                color = p.color,
                size = androidx.compose.ui.geometry.Size(p.size, p.size / 2)
            )
            drawContext.canvas.restore()
        }
    }
}

// 4. Custom Pulsing Tactile Buttons with visual microfeedback
@Composable
fun TapPulsateButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit
) {
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.94f else 1.0f,
        animationSpec = spring(stiffness = Spring.StiffnessHigh),
        label = "ButtonPulsate"
    )

    Row(
        modifier = modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clickable {
                onClick()
            },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        content()
    }
}

// 5. Solid Indian Flag Rupees Logo
@Composable
fun IndianFlagInrLogo(
    modifier: Modifier = Modifier,
    size: androidx.compose.ui.unit.Dp = 48.dp,
    symbolSize: androidx.compose.ui.unit.TextUnit = 32.sp
) {
    Box(
        modifier = modifier
            .size(size)
            .clip(RoundedCornerShape(14.dp))
            .background(Color.White)
            .border(1.5.dp, Color.White.copy(alpha = 0.9f), RoundedCornerShape(14.dp)),
        contentAlignment = Alignment.Center
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Saffron
            Box(modifier = Modifier.fillMaxWidth().weight(1f).background(Color(0xFFFF9933)))
            // White
            Box(modifier = Modifier.fillMaxWidth().weight(1f).background(Color(0xFFFFFFFF)), contentAlignment = Alignment.Center) {
                // Ashoka Chakra Navy Blue Circle
                Box(
                    modifier = Modifier
                        .size((size.value * 0.25f).dp)
                        .border(1.dp, Color(0xFF000080), CircleShape)
                )
            }
            // Green
            Box(modifier = Modifier.fillMaxWidth().weight(1f).background(Color(0xFF128807)))
        }
        // Bold Navy Blue Rupees Symbol Overlay
        Text(
            text = "₹",
            fontSize = symbolSize,
            fontWeight = FontWeight.ExtraBold,
            color = Color(0xFF002244), // Contrast deep blue
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

