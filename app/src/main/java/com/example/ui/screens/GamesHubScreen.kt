package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.GameType
import com.example.ui.Screen
import com.example.ui.theme.*

data class GameInfo(
    val type: GameType,
    val title: String,
    val subtitle: String,
    val character: String,
    val toyName: String,
    val toyImageRes: Int,
    val targetScreen: Screen,
    val accentColor: Color
)

@Composable
fun GamesHubScreen(
    onSelectScreen: (Screen) -> Unit,
    modifier: Modifier = Modifier
) {
    val games = listOf(
        GameInfo(
            type = GameType.LAUNCHER_KNOCKDOWN,
            title = "Dash Launcher Blast",
            subtitle = "Pull back, aim, and fling blocks to knock down stacked towers!",
            character = "Dash",
            toyName = "Dash Launcher Blocks",
            toyImageRes = R.drawable.toy_dash_launcher_blocks,
            targetScreen = Screen.GAME_LAUNCHER,
            accentColor = McPlayPurple
        ),
        GameInfo(
            type = GameType.BOOMERANG_CATCH,
            title = "Dash Boomerang Loop",
            subtitle = "Fling Dash's aerodynamic boomerang and catch it on the return!",
            character = "Dash",
            toyName = "Dash Boomerang",
            toyImageRes = R.drawable.toy_dash_boomerang,
            targetScreen = Screen.GAME_BOOMERANG,
            accentColor = McPlayYellow
        ),
        GameInfo(
            type = GameType.WATER_SPLASH,
            title = "Dot Water Squirter Splash",
            subtitle = "Blast water jets at moving pool targets before time runs out!",
            character = "Dot",
            toyName = "Dot Water Squirter",
            toyImageRes = R.drawable.toy_del_pan,
            targetScreen = Screen.GAME_WATER,
            accentColor = McPlaySkyBlue
        ),
        GameInfo(
            type = GameType.PANCAKE_FLIP,
            title = "Del Pan Sizzle & Flip",
            subtitle = "Flip sizzling golden pancakes high and stack them onto Del's plate!",
            character = "Del",
            toyName = "Del Pan",
            toyImageRes = R.drawable.toy_secret_agent_dash,
            targetScreen = Screen.GAME_PANCAKE,
            accentColor = McPlayOrange
        ),
        GameInfo(
            type = GameType.DOT_SOUNDBOARD,
            title = "Dot Talking Studio",
            subtitle = "Tap talking sound buttons, beat loops, and cheerful laughter clips!",
            character = "Dot",
            toyName = "Dot Talking Toy",
            toyImageRes = R.drawable.toy_dot_talking_toy,
            targetScreen = Screen.GAME_DOT_SOUNDBOARD,
            accentColor = McPlayRed
        ),
        GameInfo(
            type = GameType.HELMET_RUNNER,
            title = "Dee's Cyber Helmet Scan",
            subtitle = "Equip Dee's futuristic visor to scan and lock onto cyber targets!",
            character = "Dee",
            toyName = "Dee's Helmet",
            toyImageRes = R.drawable.toy_dees_helmet,
            targetScreen = Screen.GAME_HELMET,
            accentColor = McPlayGreen
        )
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(McPlayPeachBg)
    ) {
        // McPlay Power Header
        Surface(
            color = McPlayRed,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = McPlayYellow
                ) {
                    Text(
                        text = "McPlay POWER GAMES",
                        color = McPlayTextDark,
                        fontWeight = FontWeight.Black,
                        fontSize = 16.sp,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Play with your favorite Dash & Friends toys!",
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            modifier = Modifier
                .fillMaxSize()
                .testTag("games_hub_list")
        ) {
            items(games) { game ->
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onSelectScreen(game.targetScreen) }
                        .testTag("game_item_${game.type.name}")
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Toy Icon badge
                        Box(
                            modifier = Modifier
                                .size(70.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(game.accentColor.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(id = game.toyImageRes),
                                contentDescription = game.toyName,
                                modifier = Modifier.size(56.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = game.title,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = McPlayTextDark
                                )
                            }

                            Text(
                                text = "Toy: ${game.toyName}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = McPlayDeepBlue
                            )

                            Text(
                                text = game.subtitle,
                                fontSize = 12.sp,
                                color = McPlayGray,
                                maxLines = 2,
                                modifier = Modifier.padding(top = 2.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        // Play Button
                        Surface(
                            shape = CircleShape,
                            color = McPlayGreen,
                            modifier = Modifier.size(44.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.PlayArrow,
                                    contentDescription = "Play",
                                    tint = Color.White,
                                    modifier = Modifier.size(26.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
