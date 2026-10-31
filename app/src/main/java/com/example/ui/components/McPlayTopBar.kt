package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.VolumeMute
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

@Composable
fun McPlayTopBar(
    unlockedCount: Int,
    totalCount: Int = 8,
    soundEnabled: Boolean,
    onHomeClick: () -> Unit,
    onToyBoxClick: () -> Unit,
    onToggleSound: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .shadow(4.dp),
        color = Color.White
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // "Hey kids, this is advertising" notice
            Text(
                text = "Hey kids, this is advertising",
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF888888),
                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // McPlay Iconic 3D Logo
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clickable { onHomeClick() }
                        .testTag("mcplay_logo")
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .background(
                                Brush.verticalGradient(listOf(McPlayRed, McPlayDarkRed)),
                                RoundedCornerShape(8.dp)
                            )
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Mc",
                                color = McPlayYellow,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Black,
                                fontFamily = FontFamily.SansSerif
                            )
                            Text(
                                text = "Play",
                                color = Color.White,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Black,
                                fontFamily = FontFamily.SansSerif
                            )
                        }
                    }
                }

                // Action controls: Sound, Happy Meal Toy Box counter, Home button
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Sound Toggle
                    IconButton(
                        onClick = onToggleSound,
                        modifier = Modifier
                            .size(44.dp)
                            .testTag("sound_toggle_button")
                    ) {
                        Icon(
                            imageVector = if (soundEnabled) Icons.Default.VolumeUp else Icons.Default.VolumeMute,
                            contentDescription = "Toggle sound",
                            tint = McPlayRed,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    // Happy Meal Toy Box with notification badge
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clickable { onToyBoxClick() }
                            .testTag("toy_chest_counter_badge"),
                        contentAlignment = Alignment.Center
                    ) {
                        // Happy Meal Box icon
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .shadow(2.dp, RoundedCornerShape(8.dp))
                                .background(
                                    Brush.verticalGradient(listOf(McPlayRed, McPlayDarkRed)),
                                    RoundedCornerShape(8.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                // Arches
                                Text(
                                    text = "M",
                                    color = McPlayYellow,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Black
                                )
                                Text(
                                    text = "😊",
                                    fontSize = 12.sp
                                )
                            }
                        }

                        // Badge with count
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .offset(x = 4.dp, y = (-2).dp)
                                .size(20.dp)
                                .clip(CircleShape)
                                .background(McPlayYellow),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "$unlockedCount",
                                color = McPlayTextDark,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // Home Button (red circular button with white house icon)
                    IconButton(
                        onClick = onHomeClick,
                        modifier = Modifier
                            .size(44.dp)
                            .shadow(2.dp, CircleShape)
                            .background(
                                Brush.verticalGradient(listOf(McPlayRed, McPlayDarkRed)),
                                CircleShape
                            )
                            .testTag("home_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Home,
                            contentDescription = "Go to home",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        }
    }
}
