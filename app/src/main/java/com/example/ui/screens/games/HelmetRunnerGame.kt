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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
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

data class CyberTarget(
    val id: Int,
    var x: Float,
    var y: Float,
    var locked: Boolean = false
)

@Composable
fun HelmetRunnerGame(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    var score by remember { mutableIntStateOf(0) }
    var targetsLocked by remember { mutableIntStateOf(0) }

    var targets by remember {
        mutableStateOf(
            listOf(
                CyberTarget(1, 0.25f, 0.3f),
                CyberTarget(2, 0.7f, 0.4f),
                CyberTarget(3, 0.45f, 0.6f),
                CyberTarget(4, 0.8f, 0.7f)
            )
        )
    }

    val infiniteTransition = rememberInfiniteTransition(label = "hud_spin")
    val hudAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "hud_rotation"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0D1B2A))
    ) {
        // Cyber HUD Top Bar
        Surface(
            color = Color(0xFF1B263B),
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
                        modifier = Modifier.testTag("helmet_game_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = McPlaySkyBlue
                        )
                    }
                    Text(
                        text = "Dee's Cyber Helmet Visor",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = McPlaySkyBlue
                ) {
                    Text(
                        text = "LOCKED: $targetsLocked/4",
                        color = Color.White,
                        fontWeight = FontWeight.Black,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }

        // Helmet Visor HUD Screen
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .pointerInput(targets) {
                    detectTapGestures { offset ->
                        val cw = size.width
                        val ch = size.height

                        targets.forEach { target ->
                            if (!target.locked) {
                                val tx = cw * target.x
                                val ty = ch * target.y
                                if (hypot(offset.x - tx, offset.y - ty) < 80f) {
                                    target.locked = true
                                    score += 250
                                    targetsLocked++
                                    SoundManager.playScanBeep()
                                    if (targetsLocked == 4) {
                                        SoundManager.playUnlockFanfare()
                                    }
                                }
                            }
                        }
                    }
                }
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val canvasW = size.width
                val canvasH = size.height

                // Cyber Grid Lines
                for (i in 1..6) {
                    val y = canvasH * (i / 7f)
                    drawLine(
                        color = Color(0xFF00E5FF).copy(alpha = 0.15f),
                        start = Offset(0f, y),
                        end = Offset(canvasW, y),
                        strokeWidth = 1.5f
                    )
                }
                for (i in 1..4) {
                    val x = canvasW * (i / 5f)
                    drawLine(
                        color = Color(0xFF00E5FF).copy(alpha = 0.15f),
                        start = Offset(x, 0f),
                        end = Offset(x, canvasH),
                        strokeWidth = 1.5f
                    )
                }

                // Central Radar Sweep
                drawCircle(
                    color = Color(0xFF00E5FF).copy(alpha = 0.3f),
                    radius = canvasW * 0.38f,
                    center = Offset(canvasW * 0.5f, canvasH * 0.48f),
                    style = Stroke(width = 3f)
                )

                // Targets
                targets.forEach { target ->
                    val tx = canvasW * target.x
                    val ty = canvasH * target.y

                    if (!target.locked) {
                        drawCircle(color = Color(0xFFFF5252), radius = 24f, center = Offset(tx, ty), style = Stroke(width = 4f))
                        drawCircle(color = Color(0xFFFF1744), radius = 8f, center = Offset(tx, ty))
                    } else {
                        drawCircle(color = Color(0xFF00E676), radius = 24f, center = Offset(tx, ty), style = Stroke(width = 4f))
                        drawLine(
                            color = Color(0xFF00E676),
                            start = Offset(tx - 12f, ty),
                            end = Offset(tx - 3f, ty + 10f),
                            strokeWidth = 4f
                        )
                        drawLine(
                            color = Color(0xFF00E676),
                            start = Offset(tx - 3f, ty + 10f),
                            end = Offset(tx + 12f, ty - 8f),
                            strokeWidth = 4f
                        )
                    }
                }
            }

            // HUD Bottom message
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color.Black.copy(alpha = 0.8f),
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 16.dp)
            ) {
                Text(
                    text = if (targetsLocked == 4) "🎯 ALL TARGETS IDENTIFIED & LOCKED!" else "Tap red cyber beacons to lock with Dee's visor HUD!",
                    color = Color(0xFF00E5FF),
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
        }

        // Action Bar
        Surface(
            color = Color(0xFF1B263B),
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
                        targets = listOf(
                            CyberTarget(1, 0.25f, 0.3f),
                            CyberTarget(2, 0.7f, 0.4f),
                            CyberTarget(3, 0.45f, 0.6f),
                            CyberTarget(4, 0.8f, 0.7f)
                        )
                        targetsLocked = 0
                        score = 0
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF415A77))
                ) {
                    Icon(imageVector = Icons.Default.Refresh, contentDescription = "Reset", tint = Color.White)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Re-Scan Grid", color = Color.White)
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
