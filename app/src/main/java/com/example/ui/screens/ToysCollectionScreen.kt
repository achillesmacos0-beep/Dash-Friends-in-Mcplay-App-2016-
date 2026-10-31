package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.GameType
import com.example.data.Toy
import com.example.data.ToyRepository
import com.example.ui.Screen
import com.example.ui.theme.*
import com.example.util.SoundManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun ToysCollectionScreen(
    unlockedToyIds: Set<String>,
    onToyClick: (Toy) -> Unit,
    onNavigateScan: () -> Unit,
    onPlayGame: (GameType) -> Unit,
    onUnlockToy: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var activeInspectToy by remember { mutableStateOf<Toy?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(McPlayPeachBg)
    ) {
        // Collection Banner Header
        Surface(
            color = Color.White,
            shadowElevation = 2.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Dash & Friends Toys",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black,
                        color = McPlayRed
                    )
                    Text(
                        text = "${unlockedToyIds.size} of 8 Toys Collected",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = McPlayGray
                    )
                }

                // Scan Toy Button
                Button(
                    onClick = onNavigateScan,
                    colors = ButtonDefaults.buttonColors(containerColor = McPlayGreen),
                    shape = RoundedCornerShape(20.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
                    modifier = Modifier.testTag("collection_scan_toy_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.CameraAlt,
                        contentDescription = "Scan Toy",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Scan Toy",
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }

        // Toy Collection Grid (2 columns)
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            modifier = Modifier
                .fillMaxSize()
                .testTag("toys_collection_grid")
        ) {
            items(ToyRepository.allToys) { toy ->
                val isUnlocked = unlockedToyIds.contains(toy.id)
                ToyCard(
                    toy = toy,
                    isUnlocked = isUnlocked,
                    onClick = {
                        activeInspectToy = toy
                        SoundManager.playClick()
                    }
                )
            }
        }
    }

    // Interactive Toy Inspection & Action Dialog
    activeInspectToy?.let { toy ->
        val isUnlocked = unlockedToyIds.contains(toy.id)
        ToyDetailModal(
            toy = toy,
            isUnlocked = isUnlocked,
            onDismiss = { activeInspectToy = null },
            onUnlock = {
                onUnlockToy(toy.id)
            },
            onPlayGame = { gameType ->
                activeInspectToy = null
                onPlayGame(gameType)
            }
        )
    }
}

@Composable
fun ToyCard(
    toy: Toy,
    isUnlocked: Boolean,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("toy_card_${toy.id}")
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(10.dp)
        ) {
            // Header: Toy Number badge & status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = McPlayYellow
                ) {
                    Text(
                        text = "#${toy.number}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = McPlayTextDark,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }

                if (isUnlocked) {
                    Surface(
                        shape = CircleShape,
                        color = McPlayGreen.copy(alpha = 0.15f),
                        modifier = Modifier.size(24.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Unlocked",
                                tint = McPlayGreen,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                } else {
                    Surface(
                        shape = CircleShape,
                        color = Color.LightGray.copy(alpha = 0.3f),
                        modifier = Modifier.size(24.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = "Locked",
                                tint = Color.Gray,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Toy Photo / Artwork
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFF9F9F9)),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = toy.imageRes),
                    contentDescription = toy.name,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.size(110.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Toy Title
            Text(
                text = toy.name,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                color = McPlayTextDark,
                maxLines = 2,
                lineHeight = 16.sp
            )

            // Character Label
            Text(
                text = "with ${toy.character}",
                fontSize = 11.sp,
                color = McPlayDeepBlue,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Action Pill
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = if (isUnlocked) McPlayGreen else McPlayYellow,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = if (isUnlocked) "Inspect & Play" else "Tap to Unlock",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isUnlocked) Color.White else McPlayTextDark,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }
        }
    }
}

@Composable
fun ToyDetailModal(
    toy: Toy,
    isUnlocked: Boolean,
    onDismiss: () -> Unit,
    onUnlock: () -> Unit,
    onPlayGame: (GameType) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    var isPerformingAction by remember { mutableStateOf(false) }
    var actionEffectText by remember { mutableStateOf<String?>(null) }
    var rotationAngle by remember { mutableFloatStateOf(0f) }
    var visorDown by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
                .testTag("toy_detail_modal")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Top close bar & toy badge
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = McPlayYellow
                    ) {
                        Text(
                            text = "Toy #${toy.number}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = McPlayTextDark,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Color.Gray
                        )
                    }
                }

                // Interactive Toy Image Stage
                Box(
                    modifier = Modifier
                        .size(180.dp)
                        .padding(vertical = 8.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            Brush.radialGradient(
                                listOf(McPlayPeachBg.copy(alpha = 0.5f), Color.White)
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = toy.imageRes),
                        contentDescription = toy.name,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .size(160.dp)
                            .rotate(rotationAngle)
                    )

                    // Cyber visor overlay if Dee's Helmet
                    if (toy.id == "dees_helmet" && visorDown) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = McPlaySkyBlue.copy(alpha = 0.8f),
                            modifier = Modifier
                                .width(120.dp)
                                .height(40.dp)
                                .offset(y = (-10).dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = "⚡ HUD: TARGET LOCKED",
                                    color = Color.White,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Black
                                )
                            }
                        }
                    }

                    // Water splash particles overlay if water squirter
                    if (isPerformingAction && toy.id == "dot_water_squirter") {
                        Text(
                            text = "💦 💦 💦",
                            fontSize = 32.sp,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }

                    // Boomerang looping badge
                    if (isPerformingAction && toy.id == "dash_boomerang") {
                        Text(
                            text = "🪃 💨",
                            fontSize = 28.sp,
                            modifier = Modifier.align(Alignment.TopEnd)
                        )
                    }
                }

                // Rotation slider for 360 inspect
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 16.dp)
                ) {
                    Text(text = "Inspect 360°", fontSize = 11.sp, color = McPlayGray)
                    Slider(
                        value = rotationAngle,
                        onValueChange = { rotationAngle = it },
                        valueRange = -180f..180f,
                        colors = SliderDefaults.colors(
                            thumbColor = McPlayRed,
                            activeTrackColor = McPlayYellow
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 8.dp)
                    )
                }

                // Toy Name & Tagline
                Text(
                    text = toy.name,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    color = McPlayTextDark,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = toy.tagline,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = McPlayDeepBlue,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(vertical = 4.dp)
                )

                // Description
                Text(
                    text = toy.description,
                    fontSize = 13.sp,
                    color = McPlayGray,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(vertical = 4.dp)
                )

                // Fun Fact Callout
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = McPlayPeachBg,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "💡", fontSize = 18.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = toy.funFact,
                            fontSize = 12.sp,
                            color = McPlayTextDark
                        )
                    }
                }

                // Action feedback banner
                AnimatedVisibility(visible = actionEffectText != null) {
                    actionEffectText?.let { effect ->
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = McPlayGreen,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        ) {
                            Text(
                                text = effect,
                                color = Color.White,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(8.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Interactive "Try Toy Action" Button
                Button(
                    onClick = {
                        isPerformingAction = true
                        coroutineScope.launch {
                            when (toy.id) {
                                "dash_launcher_blocks" -> {
                                    SoundManager.playLaunch()
                                    actionEffectText = "🚀 Purple block launched! Target blocks knocked down!"
                                    delay(400)
                                    SoundManager.playSmash()
                                }
                                "dash_boomerang" -> {
                                    SoundManager.playBoomerangWhoosh()
                                    rotationAngle = 360f
                                    actionEffectText = "🪃 Whoosh! Boomerang completed a full loop back!"
                                }
                                "dot_water_squirter" -> {
                                    SoundManager.playWaterSquirt()
                                    actionEffectText = "💦 Splash! Refreshing water jet fired!"
                                }
                                "dees_helmet" -> {
                                    visorDown = !visorDown
                                    SoundManager.playScanBeep()
                                    actionEffectText = if (visorDown) "🥽 Visor engaged: Cyber scan active!" else "Visor raised!"
                                }
                                "del_pan" -> {
                                    SoundManager.playFlip()
                                    actionEffectText = "🥞 Sizzle! Pancake flipped perfectly in Del's pan!"
                                }
                                "dot_talking_toy" -> {
                                    SoundManager.playDotVoice(0)
                                    actionEffectText = "🎙️ Dot: 'Ready for adventure with Dash & Friends!'"
                                }
                                "secret_agent_dash" -> {
                                    SoundManager.playScanBeep()
                                    actionEffectText = "🔍 Detective Lens: Secret Happy Meal clue discovered!"
                                }
                                "dash_mask" -> {
                                    SoundManager.playClick()
                                    actionEffectText = "🎭 You are now disguised as Dash!"
                                }
                            }
                            delay(2200)
                            actionEffectText = null
                            isPerformingAction = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = McPlayGreen),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("try_toy_action_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Try Action",
                        tint = Color.White
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Try Action: ${toy.actionName}",
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Play Matching Game Button
                Button(
                    onClick = { onPlayGame(toy.associatedGame) },
                    colors = ButtonDefaults.buttonColors(containerColor = McPlayYellow),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("play_toy_game_button")
                ) {
                    Text(
                        text = "🎮 Play Game: ${toy.associatedGame.title}",
                        fontWeight = FontWeight.Bold,
                        color = McPlayTextDark
                    )
                }

                if (!isUnlocked) {
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedButton(
                        onClick = {
                            onUnlock()
                            SoundManager.playUnlockFanfare()
                            actionEffectText = "🎉 Toy unlocked into your collection!"
                        },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = McPlayRed),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("instant_unlock_toy_button")
                    ) {
                        Text("🔓 Scan & Unlock This Toy Now", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
