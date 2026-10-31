package com.example.ui.screens.games

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.util.SoundManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.hypot
import kotlin.random.Random

data class SplashTarget(
    val id: Int,
    var x: Float,
    var y: Float,
    val emoji: String,
    var isHit: Boolean = false
)

data class WaterSplashEffect(
    val x: Float,
    val y: Float,
    var radius: Float = 10f,
    var alpha: Float = 1f
)

@Composable
fun WaterSplashGame(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()

    var score by remember { mutableIntStateOf(0) }
    var waterAmmo by remember { mutableIntStateOf(10) }
    val splashes = remember { mutableStateListOf<WaterSplashEffect>() }

    var targets by remember {
        mutableStateOf(
            listOf(
                SplashTarget(1, 0.25f, 0.35f, "🦆"),
                SplashTarget(2, 0.65f, 0.25f, "⚽"),
                SplashTarget(3, 0.45f, 0.55f, "🎯"),
                SplashTarget(4, 0.8f, 0.45f, "🦆"),
                SplashTarget(5, 0.2f, 0.65f, "🎈")
            )
        )
    }

    fun squirtAt(tapX: Float, tapY: Float, canvasW: Float, canvasH: Float) {
        if (waterAmmo <= 0) return
        waterAmmo--
        SoundManager.playWaterSquirt()

        val splash = WaterSplashEffect(tapX, tapY)
        splashes.add(splash)

        coroutineScope.launch {
            for (step in 1..15) {
                splash.radius += 3.5f
                splash.alpha -= 0.06f
                delay(20)
            }
            splashes.remove(splash)
        }

        // Check target hit
        targets.forEach { target ->
            if (!target.isHit) {
                val tx = canvasW * target.x
                val ty = canvasH * target.y
                if (hypot(tapX - tx, tapY - ty) < 70f) {
                    target.isHit = true
                    score += 100
                    SoundManager.playClick()
                }
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFE0F7FA))
    ) {
        // Game Header
        Surface(
            color = McPlaySkyBlue,
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
                        modifier = Modifier.testTag("water_game_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                    Text(
                        text = "Dot Water Squirter",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = McPlayYellow
                    ) {
                        Text(
                            text = "Score: $score",
                            color = McPlayTextDark,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color.White
                    ) {
                        Text(
                            text = "💧 $waterAmmo",
                            color = McPlayDeepBlue,
                            fontWeight = FontWeight.Black,
                            fontSize = 13.sp,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }

        // Swimming Pool Water Area
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0xFF80DEEA), Color(0xFF26C6DA), Color(0xFF00ACC1))
                    )
                )
                .pointerInput(waterAmmo) {
                    detectTapGestures { offset ->
                        squirtAt(offset.x, offset.y, size.width.toFloat(), size.height.toFloat())
                    }
                }
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val canvasW = size.width
                val canvasH = size.height

                // Pool ripples
                drawCircle(
                    color = Color.White.copy(alpha = 0.2f),
                    radius = canvasW * 0.4f,
                    center = Offset(canvasW * 0.5f, canvasH * 0.5f),
                    style = androidx.compose.ui.graphics.drawscope.Stroke(width = 4f)
                )

                // Splash rings
                splashes.forEach { splash ->
                    if (splash.alpha > 0f) {
                        drawCircle(
                            color = Color.White.copy(alpha = splash.alpha),
                            radius = splash.radius,
                            center = Offset(splash.x, splash.y)
                        )
                        drawCircle(
                            color = Color(0xFF00E5FF).copy(alpha = splash.alpha * 0.8f),
                            radius = splash.radius * 0.7f,
                            center = Offset(splash.x, splash.y),
                            style = androidx.compose.ui.graphics.drawscope.Stroke(width = 3f)
                        )
                    }
                }
            }

            // Target icons
            targets.forEach { target ->
                if (!target.isHit) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = target.emoji,
                            fontSize = 38.sp,
                            modifier = Modifier
                                .align(Alignment.TopStart)
                                .offset(
                                    x = (target.x * 280).dp,
                                    y = (target.y * 360).dp
                                )
                        )
                    }
                }
            }

            // Instructions Overlay
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = Color.Black.copy(alpha = 0.65f),
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 12.dp)
            ) {
                Text(
                    text = if (waterAmmo <= 0) "Refill water to keep squirting!" else "Tap on pool targets to squirt water streams!",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                )
            }
        }

        // Bottom Controls
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
                        waterAmmo = 10
                        targets = listOf(
                            SplashTarget(1, 0.25f, 0.35f, "🦆"),
                            SplashTarget(2, 0.65f, 0.25f, "⚽"),
                            SplashTarget(3, 0.45f, 0.55f, "🎯"),
                            SplashTarget(4, 0.8f, 0.45f, "🦆"),
                            SplashTarget(5, 0.2f, 0.65f, "🎈")
                        )
                        SoundManager.playWaterSquirt()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = McPlaySkyBlue),
                    modifier = Modifier.testTag("refill_water_button")
                ) {
                    Icon(imageVector = Icons.Default.Refresh, contentDescription = "Refill", tint = Color.White)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Refill Squirter 💦", color = Color.White, fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = onBack,
                    colors = ButtonDefaults.buttonColors(containerColor = McPlayGreen)
                ) {
                    Text("Done", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
