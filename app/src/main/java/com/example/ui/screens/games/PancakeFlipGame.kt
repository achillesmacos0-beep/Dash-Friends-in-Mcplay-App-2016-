package com.example.ui.screens.games

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.util.SoundManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun PancakeFlipGame(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()

    var stackCount by remember { mutableIntStateOf(0) }
    var streak by remember { mutableIntStateOf(0) }
    var flipFeedback by remember { mutableStateOf<String?>(null) }

    // Sizzle meter (0f to 100f)
    var sizzleMeter by remember { mutableFloatStateOf(0f) }
    var isFlipping by remember { mutableStateOf(false) }
    var pancakeHeightOffset by remember { mutableFloatStateOf(0f) }

    // Auto sizzling loop
    LaunchedEffect(isFlipping) {
        if (!isFlipping) {
            sizzleMeter = 0f
            while (true) {
                delay(35)
                sizzleMeter = (sizzleMeter + 2.5f)
                if (sizzleMeter > 100f) {
                    // Burnt pancake!
                    flipFeedback = "Too hot! Sizzle reset!"
                    streak = 0
                    sizzleMeter = 0f
                }
            }
        }
    }

    fun executeFlip() {
        if (isFlipping) return
        isFlipping = true
        SoundManager.playFlip()

        val success = sizzleMeter in 45f..80f

        coroutineScope.launch {
            // Pancake airborne arc
            for (step in 1..25) {
                pancakeHeightOffset = if (step <= 12) step * 14f else (25 - step) * 14f
                delay(20)
            }
            pancakeHeightOffset = 0f

            if (success) {
                stackCount++
                streak++
                flipFeedback = "Perfect Golden Flip! ⭐🥞"
                SoundManager.playUnlockFanfare()
            } else if (sizzleMeter < 45f) {
                flipFeedback = "Underdone! Needs more sizzle."
                streak = 0
            } else {
                flipFeedback = "Overdone!"
                streak = 0
            }
            delay(500)
            isFlipping = false
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFFFF3E0))
    ) {
        // Top Header
        Surface(
            color = McPlayOrange,
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
                        modifier = Modifier.testTag("pancake_game_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                    Text(
                        text = "Del Pan Sizzle Flip",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = McPlayYellow
                ) {
                    Text(
                        text = "🥞 Stack: $stackCount",
                        color = McPlayTextDark,
                        fontWeight = FontWeight.Black,
                        fontSize = 13.sp,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }

        // Kitchen Griddle Stage
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val canvasW = size.width
                val canvasH = size.height

                // Tabletop
                drawRect(
                    color = Color(0xFFD7CCC8),
                    topLeft = Offset(0f, canvasH * 0.7f),
                    size = Size(canvasW, canvasH * 0.3f)
                )

                // Pan handle
                drawRoundRect(
                    color = Color(0xFF37474F),
                    topLeft = Offset(canvasW * 0.12f, canvasH * 0.62f),
                    size = Size(canvasW * 0.25f, 26f),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(10f, 10f)
                )

                // Pan Body (Blue skillet pan matching Del Pan toy)
                drawOval(
                    color = Color(0xFF1E88E5),
                    topLeft = Offset(canvasW * 0.32f, canvasH * 0.55f),
                    size = Size(canvasW * 0.55f, canvasH * 0.22f)
                )
                drawOval(
                    color = Color(0xFF0D47A1),
                    topLeft = Offset(canvasW * 0.35f, canvasH * 0.57f),
                    size = Size(canvasW * 0.49f, canvasH * 0.18f)
                )

                // Stacked pancakes on plate
                for (i in 0 until minOf(stackCount, 8)) {
                    val py = (canvasH * 0.58f) - (i * 12f)
                    drawOval(
                        color = Color(0xFFD78822),
                        topLeft = Offset(canvasW * 0.42f, py),
                        size = Size(canvasW * 0.35f, 28f)
                    )
                }

                // Current flipping pancake
                val currentPancakeY = (canvasH * 0.61f) - pancakeHeightOffset
                val pancakeColor = when {
                    sizzleMeter < 45f -> Color(0xFFFFF176) // Raw batter
                    sizzleMeter <= 80f -> Color(0xFFFFA000) // Golden brown delicious
                    else -> Color(0xFF5D4037) // Burned
                }
                drawOval(
                    color = pancakeColor,
                    topLeft = Offset(canvasW * 0.44f, currentPancakeY),
                    size = Size(canvasW * 0.32f, 28f)
                )
            }

            // Sizzle Timing Bar Overlay
            Column(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "SIZZLE TIMER",
                    color = McPlayTextDark,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )

                Box(
                    modifier = Modifier
                        .width(220.dp)
                        .height(20.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color.LightGray)
                ) {
                    // Sweet spot green zone
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .width(77.dp)
                            .offset(x = 99.dp)
                            .background(McPlayGreen.copy(alpha = 0.5f))
                    )

                    // Current progress bar
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .width((2.2f * sizzleMeter).dp)
                            .background(
                                if (sizzleMeter in 45f..80f) McPlayGreen else McPlayOrange
                            )
                    )
                }

                Text(
                    text = if (sizzleMeter in 45f..80f) "🔥 GOLDEN SIZZLE! FLIP NOW!" else "Heating up in the skillet...",
                    fontWeight = FontWeight.Black,
                    color = if (sizzleMeter in 45f..80f) McPlayGreen else McPlayGray,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(top = 4.dp)
                )

                flipFeedback?.let {
                    Text(
                        text = it,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = McPlayDeepBlue,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        }

        // Flip Action Controls
        Surface(
            color = Color.White,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        stackCount = 0
                        streak = 0
                        flipFeedback = null
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Restart")
                }

                Button(
                    onClick = { executeFlip() },
                    colors = ButtonDefaults.buttonColors(containerColor = McPlayOrange),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .weight(2f)
                        .height(50.dp)
                        .testTag("flip_pancake_button")
                ) {
                    Text("FLIP PANCAKE! 🥞", fontWeight = FontWeight.Black, fontSize = 16.sp, color = Color.White)
                }
            }
        }
    }
}
