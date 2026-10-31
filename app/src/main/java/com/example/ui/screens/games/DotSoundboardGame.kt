package com.example.ui.screens.games

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.*
import com.example.util.SoundManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class DotSoundQuote(
    val title: String,
    val quote: String,
    val emoji: String,
    val color: Color
)

@Composable
fun DotSoundboardGame(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    var activeSpeech by remember { mutableStateOf<String?>("Tap any button to hear Dot talk!") }
    var isSpeaking by remember { mutableStateOf(false) }

    val quotes = listOf(
        DotSoundQuote("Ready!", "Ready for adventure with Dash & Friends!", "🌟", McPlayRed),
        DotSoundQuote("High Five!", "Up high! Down low! You're awesome!", "✋", McPlayYellow),
        DotSoundQuote("Giggle", "Tee-hee! That tickles!", "😄", McPlayLightGreen),
        DotSoundQuote("Happy Meal", "I love my McDonald's Happy Meal!", "🍔", McPlayOrange),
        DotSoundQuote("Rock & Roll", "Dash and Friends rock the house!", "🎸", McPlayPurple),
        DotSoundQuote("Splash!", "Splish, splash! Backyard water party!", "💦", McPlaySkyBlue),
        DotSoundQuote("Cheer", "Hooray! We did it together!", "🎉", McPlayGreen),
        DotSoundQuote("Secret Agent", "Shh! Top-secret Dash & Friends mission!", "🕵️", Color(0xFF546E7A))
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFFCE4EC)) // Soft pink matching Dot's shirt
    ) {
        // Header
        Surface(
            color = Color(0xFFE91E63),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier.testTag("dot_soundboard_back_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }
                Text(
                    text = "Dot Talking Studio",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp
                )
            }
        }

        // Dot Character Avatar & Speech Bubble Stage
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Speech Bubble
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color.White,
                shadowElevation = 4.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp)
            ) {
                Text(
                    text = activeSpeech ?: "...",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = McPlayTextDark,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(14.dp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Dot Figure Avatar
            Box(
                modifier = Modifier
                    .size(110.dp)
                    .clip(CircleShape)
                    .background(Color.White)
                    .padding(6.dp),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.toy_dot_talking_toy),
                    contentDescription = "Dot",
                    modifier = Modifier
                        .size(90.dp)
                        .scale(if (isSpeaking) 1.1f else 1.0f)
                )
            }
        }

        // Soundboard Grid
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .testTag("dot_soundboard_grid")
        ) {
            itemsIndexed(quotes) { index, item ->
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            isSpeaking = true
                            activeSpeech = "Dot says: \"${item.quote}\""
                            SoundManager.playDotVoice(index)
                            coroutineScope.launch {
                                delay(1200)
                                isSpeaking = false
                            }
                        }
                        .testTag("sound_button_$index")
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = item.color.copy(alpha = 0.2f),
                            modifier = Modifier.size(40.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(text = item.emoji, fontSize = 20.sp)
                            }
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Column {
                            Text(
                                text = item.title,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = McPlayTextDark
                            )
                            Text(
                                text = "Tap to talk",
                                fontSize = 11.sp,
                                color = McPlayGray
                            )
                        }
                    }
                }
            }
        }
    }
}
