package com.example.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Games
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.Screen
import com.example.ui.theme.*

@Composable
fun HomeScreen(
    unlockedCount: Int,
    totalCount: Int,
    onNavigate: (Screen) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    // Gentle pulse animation for the main "see toys" button
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val scalePulse by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "see_toys_pulse"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(McPlayPeachBg)
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Authentically Styled Dash & Friends Stage Banner
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp)
                .shadow(6.dp, RoundedCornerShape(16.dp))
                .clip(RoundedCornerShape(16.dp))
                .background(Color.White)
        ) {
            Column {
                // Banner Image (matches user uploaded screenshot)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.banner_dash_friends),
                        contentDescription = "McDonald's Dash & Friends McPlay Stage with Dot, Dee, and Dash",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.fillMaxSize()
                    )

                    // Overlay "SEE TOYS" interactive button directly over the center banner
                    Column(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .offset(y = 8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Button(
                            onClick = { onNavigate(Screen.TOYS_COLLECTION) },
                            colors = ButtonDefaults.buttonColors(containerColor = McPlayGreen),
                            shape = RoundedCornerShape(24.dp),
                            elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp),
                            contentPadding = PaddingValues(horizontal = 28.dp, vertical = 10.dp),
                            modifier = Modifier
                                .scale(scalePulse)
                                .testTag("banner_see_toys_button")
                        ) {
                            Text(
                                text = "see toys",
                                color = Color.White,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Black,
                                fontFamily = FontFamily.SansSerif
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Carousel Dots Indicator (matching 5 dots from original McPlay screen)
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            repeat(5) { index ->
                                Box(
                                    modifier = Modifier
                                        .size(if (index == 1) 10.dp else 8.dp)
                                        .clip(CircleShape)
                                        .background(if (index == 1) McPlayGreen else Color.White.copy(alpha = 0.85f))
                                )
                            }
                        }
                    }
                }

                // McPlay POWER Bar
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(
                                listOf(Color(0xFFFFF7ED), Color(0xFFFFE6CC))
                            )
                        )
                        .clickable { onNavigate(Screen.GAMES_HUB) }
                        .padding(vertical = 10.dp)
                        .testTag("mcplay_power_banner_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = McPlayRed,
                            shadowElevation = 2.dp
                        ) {
                            Text(
                                text = "McPlay POWER",
                                color = McPlayYellow,
                                fontWeight = FontWeight.Black,
                                fontSize = 16.sp,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 4.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Play Games! 🎮",
                            fontWeight = FontWeight.Bold,
                            color = McPlayTextDark,
                            fontSize = 15.sp
                        )
                    }
                }
            }
        }

        // Action Cards Grid: "See Toys", "Scan Your Toy", "Play Games"
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // 1. Primary "SEE TOYS" Collection Card
            Card(
                onClick = { onNavigate(Screen.TOYS_COLLECTION) },
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("card_see_toys")
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(Brush.radialGradient(listOf(McPlayLightGreen, McPlayGreen))),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Toys",
                            tint = Color.White,
                            modifier = Modifier.size(32.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "See Toys Collection",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = McPlayTextDark
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = McPlayYellow
                            ) {
                                Text(
                                    text = "$unlockedCount/$totalCount",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = McPlayTextDark,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Text(
                            text = "8 Dash & Friends toys including Launcher Blocks, Boomerang, Water Squirter & more!",
                            fontSize = 13.sp,
                            color = McPlayGray
                        )
                    }
                }
            }

            // 2. "SCAN YOUR TOY" Camera / Hologram Scanner Card
            Card(
                onClick = { onNavigate(Screen.SCAN_TOY) },
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("card_scan_your_toy")
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(Brush.radialGradient(listOf(McPlaySkyBlue, McPlayDeepBlue))),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CameraAlt,
                            contentDescription = "Scan Toy",
                            tint = Color.White,
                            modifier = Modifier.size(30.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Scan Your Toy",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = McPlayTextDark
                        )
                        Text(
                            text = "Scan your Happy Meal toy to unlock 3D toy actions and exclusive mini-games!",
                            fontSize = 13.sp,
                            color = McPlayGray
                        )
                    }
                }
            }

            // 3. "PLAY GAMES" Mini-Games Arcade
            Card(
                onClick = { onNavigate(Screen.GAMES_HUB) },
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("card_play_games")
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(Brush.radialGradient(listOf(McPlayYellow, McPlayOrange))),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.SportsEsports,
                            contentDescription = "Play Games",
                            tint = Color.White,
                            modifier = Modifier.size(32.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Play Games",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = McPlayTextDark
                        )
                        Text(
                            text = "Block Knockdown, Boomerang Flight, Water Splash & Pancake Sizzle!",
                            fontSize = 13.sp,
                            color = McPlayGray
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}
