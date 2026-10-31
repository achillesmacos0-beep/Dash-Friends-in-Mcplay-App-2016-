package com.example.ui.screens.games

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.util.SoundManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.*

data class CollectibleStar(
    val id: Int,
    val x: Float,
    val y: Float,
    var collected: Boolean = false
)

@Composable
fun BoomerangCatchGame(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()

    var score by remember { mutableIntStateOf(0) }
    var throwCount by remember { mutableIntStateOf(0) }
    var isThrowing by remember { mutableStateOf(false) }

    // Flight animation progress (0f to 1f)
    var flightProgress by remember { mutableFloatStateOf(0f) }
    var spinAngle by remember { mutableFloatStateOf(0f) }

    // Stars scattered in the sky
    var stars by remember {
        mutableStateOf(
            listOf(
                CollectibleStar(1, 0.35f, 0.3f),
                CollectibleStar(2, 0.5f, 0.2f),
                CollectibleStar(3, 0.7f, 0.35f),
                CollectibleStar(4, 0.6f, 0.5f),
                CollectibleStar(5, 0.4f, 0.45f)
            )
        )
    }

    fun launchBoomerang() {
        if (isThrowing) return
        isThrowing = true
        throwCount++
        SoundManager.playBoomerangWhoosh()

        coroutineScope.launch {
            val steps = 90
            for (step in 0..steps) {
                flightProgress = step / steps.toFloat()
                spinAngle = (step * 25f) % 360f

                // Evaluate collision along the elliptical arc
                val angleRad = flightProgress * 2.0 * Math.PI
                val currentX = 0.5f - 0.32f * sin(angleRad).toFloat()
                val currentY = 0.65f - 0.45f * sin(angleRad / 2.0).toFloat()

                stars.forEach { star ->
                    if (!star.collected) {
                        val dist = hypot(currentX - star.x, currentY - star.y)
                        if (dist < 0.08f) {
                            star.collected = true
                            score += 50
                            SoundManager.playClick()
                        }
                    }
                }
                delay(22)
            }
            isThrowing = false
            flightProgress = 0f
            if (stars.all { it.collected }) {
                SoundManager.playUnlockFanfare()
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF87CEEB)) // Clear blue sky
    ) {
        // Top Bar
        Surface(
            color = McPlayYellow,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("boomerang_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = McPlayTextDark
                        )
                    }
                    Text(
                        text = "Dash Boomerang Loop",
                        color = McPlayTextDark,
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = McPlayRed
                ) {
                    Text(
                        text = "⭐ Score: $score",
                        color = Color.White,
                        fontWeight = FontWeight.Black,
                        fontSize = 13.sp,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }
        }

        // Sky Canvas
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .clickable { launchBoomerang() }
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val canvasW = size.width
                val canvasH = size.height

                // Fluffy clouds
                drawCircle(Color.White.copy(alpha = 0.7f), radius = 50f, center = Offset(canvasW * 0.2f, canvasH * 0.15f))
                drawCircle(Color.White.copy(alpha = 0.7f), radius = 65f, center = Offset(canvasW * 0.28f, canvasH * 0.16f))
                drawCircle(Color.White.copy(alpha = 0.7f), radius = 55f, center = Offset(canvasW * 0.8f, canvasH * 0.25f))

                // Ground grass
                drawRect(
                    color = Color(0xFF4CA826),
                    topLeft = Offset(0f, canvasH * 0.82f),
                    size = androidx.compose.ui.geometry.Size(canvasW, canvasH * 0.18f)
                )

                // Flight path dotted trajectory
                val path = Path()
                for (i in 0..60) {
                    val p = i / 60f
                    val a = p * 2.0 * Math.PI
                    val px = canvasW * (0.5f - 0.32f * sin(a).toFloat())
                    val py = canvasH * (0.65f - 0.45f * sin(a / 2.0).toFloat())
                    if (i == 0) path.moveTo(px, py) else path.lineTo(px, py)
                }
                drawPath(
                    path = path,
                    color = Color.White.copy(alpha = 0.4f),
                    style = androidx.compose.ui.graphics.drawscope.Stroke(
                        width = 4f,
                        pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(floatArrayOf(12f, 12f))
                    )
                )

                // Collectible Stars
                stars.forEach { star ->
                    if (!star.collected) {
                        val sx = canvasW * star.x
                        val sy = canvasH * star.y
                        drawCircle(color = McPlayYellow, radius = 22f, center = Offset(sx, sy))
                        drawCircle(color = Color(0xFFFFA000), radius = 16f, center = Offset(sx, sy))
                    }
                }

                // Boomerang position
                val angleRad = flightProgress * 2.0 * Math.PI
                val boomX = if (isThrowing) canvasW * (0.5f - 0.32f * sin(angleRad).toFloat()) else canvasW * 0.5f
                val boomY = if (isThrowing) canvasH * (0.65f - 0.45f * sin(angleRad / 2.0).toFloat()) else canvasH * 0.65f

                // Draw rotating Dash Boomerang
                rotate(spinAngle, pivot = Offset(boomX, boomY)) {
                    // Curved V-shape boomerang wings
                    val boomPath = Path().apply {
                        moveTo(boomX - 35f, boomY + 25f)
                        quadraticBezierTo(boomX, boomY - 10f, boomX + 35f, boomY + 25f)
                        lineTo(boomX + 30f, boomY + 35f)
                        quadraticBezierTo(boomX, boomY + 5f, boomX - 30f, boomY + 35f)
                        close()
                    }
                    drawPath(path = boomPath, color = McPlayRed)

                    // Center Dash head badge (green circle with smiley face)
                    drawCircle(color = McPlayGreen, radius = 14f, center = Offset(boomX, boomY))
                    drawCircle(color = Color.White, radius = 3.5f, center = Offset(boomX - 5f, boomY - 3f))
                    drawCircle(color = Color.White, radius = 3.5f, center = Offset(boomX + 5f, boomY - 3f))
                }
            }

            // Tap hint
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = Color.Black.copy(alpha = 0.7f),
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 12.dp)
            ) {
                Text(
                    text = if (isThrowing) "Flying... collecting stars!" else "Tap anywhere to fling Dash's Boomerang!",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                )
            }
        }

        // Action Bar
        Surface(
            color = Color.White,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = {
                        stars = listOf(
                            CollectibleStar(1, 0.35f, 0.3f),
                            CollectibleStar(2, 0.5f, 0.2f),
                            CollectibleStar(3, 0.7f, 0.35f),
                            CollectibleStar(4, 0.6f, 0.5f),
                            CollectibleStar(5, 0.4f, 0.45f)
                        )
                        score = 0
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = McPlayYellow)
                ) {
                    Icon(imageVector = Icons.Default.Refresh, contentDescription = "Reset", tint = McPlayTextDark)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Reset Stars", color = McPlayTextDark, fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = { launchBoomerang() },
                    colors = ButtonDefaults.buttonColors(containerColor = McPlayGreen),
                    modifier = Modifier.testTag("fling_boomerang_button")
                ) {
                    Text("FLING BOOMERANG! 🪃", color = Color.White, fontWeight = FontWeight.Black)
                }
            }
        }
    }
}
