package com.example.ui.screens.games

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
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

data class TargetBlock(
    val id: Int,
    var x: Float,
    var y: Float,
    val width: Float = 55f,
    val height: Float = 35f,
    var isHit: Boolean = false
)

@Composable
fun LauncherKnockdownGame(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()

    var score by remember { mutableIntStateOf(0) }
    var shotsLeft by remember { mutableIntStateOf(5) }
    var level by remember { mutableIntStateOf(1) }

    // Launcher drag vector
    var dragOffset by remember { mutableStateOf(Offset.Zero) }
    var isDragging by remember { mutableStateOf(false) }

    // Projectile state
    var projectilePos by remember { mutableStateOf<Offset?>(null) }
    var projectileVel by remember { mutableStateOf(Offset.Zero) }
    var isFlying by remember { mutableStateOf(false) }

    // Target blocks (stacked orange blocks matching the toy)
    var blocks by remember {
        mutableStateOf(
            listOf(
                TargetBlock(1, 0.75f, 0.65f),
                TargetBlock(2, 0.75f, 0.55f),
                TargetBlock(3, 0.75f, 0.45f),
                TargetBlock(4, 0.85f, 0.65f),
                TargetBlock(5, 0.85f, 0.55f)
            )
        )
    }

    fun resetBlocks() {
        blocks = listOf(
            TargetBlock(1, 0.75f, 0.65f),
            TargetBlock(2, 0.75f, 0.55f),
            TargetBlock(3, 0.75f, 0.45f),
            TargetBlock(4, 0.85f, 0.65f),
            TargetBlock(5, 0.85f, 0.55f)
        )
        shotsLeft = 5
        score = 0
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFEFE9DB))
    ) {
        // Game Top Bar
        Surface(
            color = McPlayPurple,
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
                        modifier = Modifier.testTag("launcher_game_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                    Text(
                        text = "Dash Launcher Blast",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = McPlayYellow
                    ) {
                        Text(
                            text = "Score: $score",
                            color = McPlayTextDark,
                            fontWeight = FontWeight.Black,
                            fontSize = 13.sp,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = McPlayOrange
                    ) {
                        Text(
                            text = "Blocks: $shotsLeft",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }

        // Gameplay Canvas Stage
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .pointerInput(shotsLeft, isFlying) {
                    if (shotsLeft <= 0 || isFlying) return@pointerInput
                    detectDragGestures(
                        onDragStart = {
                            isDragging = true
                            dragOffset = Offset.Zero
                        },
                        onDragEnd = {
                            if (!isDragging) return@detectDragGestures
                            isDragging = false
                            if (shotsLeft > 0) {
                                shotsLeft--
                                SoundManager.playLaunch()
                                isFlying = true

                                // Launch calculation
                                val startX = size.width * 0.2f
                                val startY = size.height * 0.7f
                                projectilePos = Offset(startX, startY)
                                projectileVel = Offset(-dragOffset.x * 0.22f, -dragOffset.y * 0.22f)

                                coroutineScope.launch {
                                    var currentPos = projectilePos ?: Offset.Zero
                                    var vel = projectileVel
                                    val gravity = 0.98f

                                    for (step in 0..65) {
                                        vel = vel.copy(y = vel.y + gravity)
                                        currentPos = Offset(currentPos.x + vel.x, currentPos.y + vel.y)
                                        projectilePos = currentPos

                                        // Check collision with target blocks
                                        blocks.forEach { block ->
                                            if (!block.isHit) {
                                                val bx = size.width * block.x
                                                val by = size.height * block.y
                                                if (hypot(currentPos.x - bx, currentPos.y - by) < 45f) {
                                                    block.isHit = true
                                                    score += 100
                                                    SoundManager.playSmash()
                                                }
                                            }
                                        }

                                        if (currentPos.y > size.height * 0.85f || currentPos.x > size.width) {
                                            break
                                        }
                                        delay(16)
                                    }
                                    isFlying = false
                                    projectilePos = null
                                    dragOffset = Offset.Zero

                                    if (blocks.all { it.isHit }) {
                                        SoundManager.playUnlockFanfare()
                                    }
                                }
                            }
                        },
                        onDrag = { change, dragAmount ->
                            change.consume()
                            dragOffset = Offset(
                                (dragOffset.x + dragAmount.x).coerceIn(-120f, 60f),
                                (dragOffset.y + dragAmount.y).coerceIn(-60f, 120f)
                            )
                        }
                    )
                }
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val canvasW = size.width
                val canvasH = size.height

                // Ground & Stand
                drawRect(
                    color = Color(0xFFC7B299),
                    topLeft = Offset(0f, canvasH * 0.8f),
                    size = Size(canvasW, canvasH * 0.2f)
                )

                // Target Platform Table
                drawRect(
                    color = Color(0xFF8D6E63),
                    topLeft = Offset(canvasW * 0.7f, canvasH * 0.75f),
                    size = Size(canvasW * 0.26f, canvasH * 0.05f)
                )

                // Purple Dash Launcher Base
                val launcherBaseX = canvasW * 0.2f
                val launcherBaseY = canvasH * 0.75f
                drawRoundRect(
                    color = Color(0xFF6A2C91),
                    topLeft = Offset(launcherBaseX - 35f, launcherBaseY - 25f),
                    size = Size(70f, 50f),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(10f, 10f)
                )

                // Slingshot / Launch Line if dragging
                if (isDragging) {
                    val dragX = launcherBaseX + dragOffset.x
                    val dragY = launcherBaseY + dragOffset.y

                    // Trajectory guide line
                    drawLine(
                        color = Color(0xFFDA291C),
                        start = Offset(launcherBaseX, launcherBaseY),
                        end = Offset(dragX, dragY),
                        strokeWidth = 6f
                    )

                    // Dragged purple projectile block
                    drawRoundRect(
                        color = Color(0xFF9C27B0),
                        topLeft = Offset(dragX - 20f, dragY - 20f),
                        size = Size(40f, 40f),
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(8f, 8f)
                    )
                }

                // In-flight projectile
                projectilePos?.let { pos ->
                    drawRoundRect(
                        color = Color(0xFF9C27B0),
                        topLeft = Offset(pos.x - 20f, pos.y - 20f),
                        size = Size(40f, 40f),
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(8f, 8f)
                    )
                }

                // Orange Target Blocks
                blocks.forEach { block ->
                    val bx = canvasW * block.x
                    val by = canvasH * block.y

                    if (!block.isHit) {
                        drawRoundRect(
                            color = Color(0xFFFF8200),
                            topLeft = Offset(bx, by),
                            size = Size(block.width, block.height),
                            cornerRadius = androidx.compose.ui.geometry.CornerRadius(6f, 6f)
                        )
                        // Block inner line detail
                        drawLine(
                            color = Color(0xFFD66000),
                            start = Offset(bx, by + block.height / 2),
                            end = Offset(bx + block.width, by + block.height / 2),
                            strokeWidth = 2f
                        )
                    } else {
                        // Fallen tumbled block
                        drawRoundRect(
                            color = Color(0xFFFFB74D),
                            topLeft = Offset(bx + 15f, canvasH * 0.78f),
                            size = Size(block.width, block.height * 0.6f),
                            cornerRadius = androidx.compose.ui.geometry.CornerRadius(4f, 4f)
                        )
                    }
                }
            }

            // Bottom Instructions Pill
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = Color.Black.copy(alpha = 0.75f),
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 12.dp)
            ) {
                Text(
                    text = if (blocks.all { it.isHit }) "🎉 ALL BLOCKS TOPPLED! STAGE CLEAR!" else "Touch the purple block & drag left to aim & launch!",
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
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
                    onClick = { resetBlocks() },
                    colors = ButtonDefaults.buttonColors(containerColor = McPlayYellow),
                    modifier = Modifier.testTag("reset_launcher_game_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Restart",
                        tint = McPlayTextDark
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("New Round", color = McPlayTextDark, fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = onBack,
                    colors = ButtonDefaults.buttonColors(containerColor = McPlayGreen)
                ) {
                    Text("Done Playing", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
