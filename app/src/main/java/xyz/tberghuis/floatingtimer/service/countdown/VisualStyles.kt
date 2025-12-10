package xyz.tberghuis.floatingtimer.service.countdown

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import xyz.tberghuis.floatingtimer.service.BubbleProperties
import androidx.compose.ui.graphics.drawscope.rotate

@Composable
fun RotatingClockNeedle(
    durationSeconds: Int,
    bubbleProperties: BubbleProperties
) {
    val durationMillis = durationSeconds * 1000L
    if (durationMillis <= 0) return

    val infiniteTransition = rememberInfiniteTransition(label = "needle_transition")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = durationMillis.toInt(), 
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Restart
        ),
        label = "needle_rotation"
    )

    Canvas(modifier = Modifier.fillMaxSize()) {
        val center = Offset(size.width / 2, size.height / 2)
        
        // Face - Use Secondary Color for background/face
        drawCircle(
            color = bubbleProperties.secondaryColor.copy(alpha=0.3f), // Secondary Color
            radius = size.minDimension / 2,
            style = Stroke(width = bubbleProperties.arcWidth.toPx())
        )

        // Needle - Use Halo Color (Primary)
        rotate(rotation, pivot = center) {
            drawLine(
                color = bubbleProperties.haloColor,
                start = center,
                end = Offset(center.x, center.y - (size.minDimension / 2) * 0.8f),
                strokeWidth = bubbleProperties.arcWidth.toPx(),
                cap = StrokeCap.Round
            )
        }
    }
}

@Composable
fun CountdownArc(
    timeLeftFraction: Float,
    bubbleProperties: BubbleProperties
) {
    val animatedProgress by animateFloatAsState(
        targetValue = timeLeftFraction,
        animationSpec = tween(durationMillis = 300),
        label = "arc_progress"
    )
    
    Canvas(modifier = Modifier.fillMaxSize()) {
        val sweepAngle = 360f * animatedProgress
        
        // Background arc - Use Secondary Color
        drawArc(
            color = bubbleProperties.secondaryColor.copy(alpha = 0.5f), // Secondary
            startAngle = -90f,
            sweepAngle = 360f,
            useCenter = false,
            style = Stroke(width = bubbleProperties.arcWidth.toPx(), cap = StrokeCap.Round)
        )
        
        // Progress arc - Use Halo Color (Primary) with some gradient
        drawArc(
            brush = Brush.sweepGradient(
                colors = listOf(
                    bubbleProperties.haloColor.copy(alpha=0.5f),
                    bubbleProperties.haloColor,
                    bubbleProperties.haloColor.copy(alpha=0.8f)
                )
            ),
            startAngle = -90f,
            sweepAngle = sweepAngle,
            useCenter = false,
            style = Stroke(width = bubbleProperties.arcWidth.toPx(), cap = StrokeCap.Round)
        )
    }
}

@Composable
fun IronManTechRings(
    bubbleProperties: BubbleProperties
) {
    val infiniteTransition = rememberInfiniteTransition(label = "rings")
    
    val rotation1 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "outer_ring"
    )
    
    val rotation2 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -360f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "inner_ring"
    )
    
    Canvas(modifier = Modifier.fillMaxSize()) {
        val center = Offset(size.width / 2, size.height / 2)
        
        // Outer ring - Halo Color (Primary)
        rotate(rotation1, pivot = center) {
            drawArc(
                brush = Brush.sweepGradient(
                    colors = listOf(
                        bubbleProperties.haloColor,
                        Color.Transparent,
                        bubbleProperties.haloColor.copy(alpha=0.5f),
                        Color.Transparent
                    )
                ),
                startAngle = 0f,
                sweepAngle = 180f,
                useCenter = false,
                style = Stroke(width = bubbleProperties.arcWidth.toPx(), cap = StrokeCap.Round)
            )
        }
        
        // Inner ring - Secondary Color
        rotate(rotation2, pivot = center) {
            drawArc(
                brush = Brush.sweepGradient(
                    colors = listOf(
                        bubbleProperties.secondaryColor.copy(alpha=0.8f), // Secondary
                        Color.Transparent,
                        bubbleProperties.secondaryColor.copy(alpha=0.4f),
                        Color.Transparent
                    )
                ),
                startAngle = 0f,
                sweepAngle = 240f,
                useCenter = false,
                size = Size(size.width * 0.7f, size.height * 0.7f),
                topLeft = Offset(size.width * 0.15f, size.height * 0.15f),
                style = Stroke(width = (bubbleProperties.arcWidth * 0.7f).toPx(), cap = StrokeCap.Round)
            )
        }
    }
}

@Composable
fun ClockFaceWithTicks(
    timeLeftFraction: Float,
    bubbleProperties: BubbleProperties
) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val center = Offset(size.width / 2, size.height / 2)
        val radius = size.minDimension / 2
        
        // Draw 12 tick marks - Secondary Color
        for (i in 0..11) {
            val angle = (i * 30f - 90f) * (PI / 180f).toFloat()
            val startRadius = radius * 0.8f
            val endRadius = radius * 0.95f
            
            drawLine(
                color = bubbleProperties.secondaryColor.copy(alpha=0.6f), // Secondary
                start = Offset(
                    center.x + cos(angle) * startRadius,
                    center.y + sin(angle) * startRadius
                ),
                end = Offset(
                    center.x + cos(angle) * endRadius,
                    center.y + sin(angle) * endRadius
                ),
                strokeWidth = 3.dp.toPx()
            )
        }
        
        // Rotating progress indicator - Halo Color (Primary)
        val progressAngle = (timeLeftFraction * 360f - 90f) * (PI / 180f).toFloat()
        
        drawLine(
            color = bubbleProperties.haloColor,
            start = center,
            end = Offset(
                center.x + cos(progressAngle) * radius * 0.7f,
                center.y + sin(progressAngle) * radius * 0.7f
            ),
            strokeWidth = 6.dp.toPx(),
            cap = StrokeCap.Round
        )
    }
}

@Composable
fun AssembleClockHand(
    durationSeconds: Int,
    bubbleProperties: BubbleProperties
) {
    val durationMillis = durationSeconds * 1000L
    if (durationMillis <= 0) return

    val infiniteTransition = rememberInfiniteTransition(label = "assemble_transition")
    
    // Main rotation
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis.toInt(), easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )
    
    // Pulse/Assemble animation (0..1 every 1 second)
    val assembleValue by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "assemble"
    )

    Canvas(modifier = Modifier.fillMaxSize()) {
        val center = Offset(size.width / 2, size.height / 2)
        val handLength = (size.minDimension / 2) * 0.9f
        
        // Draw faint track - Secondary Color
        drawCircle(
            color = bubbleProperties.secondaryColor.copy(alpha=0.1f),
            radius = size.minDimension / 2,
            style = Stroke(2.dp.toPx())
        )
        
        rotate(rotation, pivot = center) {
            // Main hand - Halo Color
            drawLine(
                color = bubbleProperties.haloColor,
                start = center,
                end = Offset(center.x, center.y - handLength),
                strokeWidth = bubbleProperties.arcWidth.toPx(),
                cap = StrokeCap.Round
            )
            
            // "Assemble" particle/segment moving along the hand - Secondary Color
            // Moves from center to tip
            val particleY = center.y - (handLength * assembleValue)
            val particleSize = 4.dp.toPx()
            
            drawCircle(
                color = bubbleProperties.secondaryColor,
                radius = particleSize,
                center = Offset(center.x, particleY)
            )
        }
    }
}
@Composable
fun ParticleClock(
    timeLeftFraction: Float,
    bubbleProperties: BubbleProperties
) {
    // Particle system state
    // We want particles to orbit or form the progress ring
    // Simple implementation: Particles emitted from the progress head
    
    val infiniteTransition = rememberInfiniteTransition(label = "particle_clock")
    val time by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "time"
    )
    
    // Random state for particle positions (pseudo-random based on time)
    val particles = remember { List(50) { it } } // 50 particles
    
    Canvas(modifier = Modifier.fillMaxSize()) {
        val center = Offset(size.width / 2, size.height / 2)
        val radius = size.minDimension / 2 * 0.9f
        
        // Background track (faint)
        drawCircle(
            color = bubbleProperties.secondaryColor.copy(alpha=0.2f),
            radius = radius,
            style = Stroke(width = 2.dp.toPx())
        )
        
        // Calculate progress angle
        val sweepAngle = 360f * timeLeftFraction
        val startAngle = -90f
        val currentAngleRad = (startAngle + sweepAngle) * (Math.PI / 180f)
        
        // Draw progress arc
        drawArc(
            color = bubbleProperties.haloColor.copy(alpha=0.3f),
            startAngle = startAngle,
            sweepAngle = sweepAngle,
            useCenter = false,
            style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round)
        )
        
        // Particles at the head
        val headX = center.x + radius * cos(currentAngleRad).toFloat()
        val headY = center.y + radius * sin(currentAngleRad).toFloat()
        
        drawCircle(
            color = bubbleProperties.haloColor,
            radius = 6.dp.toPx(),
            center = Offset(headX, headY)
        )
        
        // Floating particles around the ring
        particles.forEach { index ->
            // Deterministic random motion based on index and time
            val offset = (index * 7.13f + time * 10f) % 100f // 0..100
            val angle = (index * (360f / particles.size) + offset * 3.6f) * (Math.PI / 180f)
            
            // Particles fade in/out
            val alpha = (sin(offset * 0.1f) + 1f) / 2f * 0.8f
            
            val px = center.x + radius * cos(angle).toFloat()
            val py = center.y + radius * sin(angle).toFloat()
            
            drawCircle(
                color = bubbleProperties.secondaryColor.copy(alpha = alpha),
                radius = (2 + (index % 3)).dp.toPx(),
                center = Offset(px, py)
            )
        }
    }
}
