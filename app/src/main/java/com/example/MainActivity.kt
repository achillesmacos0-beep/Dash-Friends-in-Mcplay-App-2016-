package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.example.ui.MainViewModel
import com.example.ui.Screen
import com.example.ui.components.LegalDialog
import com.example.ui.components.McPlayFooter
import com.example.ui.components.McPlayTopBar
import com.example.ui.components.ParentalGateDialog
import com.example.ui.screens.*
import com.example.ui.screens.games.*
import com.example.ui.theme.McPlayPeachBg
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                McPlayApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun McPlayApp(viewModel: MainViewModel) {
    val uiState by viewModel.uiState.collectAsState()

    // Handle back button for sub-screens
    if (uiState.currentScreen != Screen.HOME) {
        BackHandler {
            when (uiState.currentScreen) {
                Screen.GAME_LAUNCHER,
                Screen.GAME_BOOMERANG,
                Screen.GAME_WATER,
                Screen.GAME_PANCAKE,
                Screen.GAME_DOT_SOUNDBOARD,
                Screen.GAME_HELMET -> viewModel.navigateTo(Screen.GAMES_HUB)
                else -> viewModel.navigateTo(Screen.HOME)
            }
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets.safeDrawing,
        topBar = {
            McPlayTopBar(
                unlockedCount = uiState.unlockedToyIds.size,
                totalCount = 8,
                soundEnabled = uiState.soundEnabled,
                onHomeClick = { viewModel.navigateTo(Screen.HOME) },
                onToyBoxClick = { viewModel.navigateTo(Screen.TOYS_COLLECTION) },
                onToggleSound = { viewModel.toggleSound() }
            )
        },
        bottomBar = {
            if (uiState.currentScreen == Screen.HOME || uiState.currentScreen == Screen.TOYS_COLLECTION) {
                McPlayFooter(
                    onParentsClick = { viewModel.showParentalGate("parents_center") },
                    onLegalClick = { viewModel.showLegal(true) }
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(McPlayPeachBg)
        ) {
            when (uiState.currentScreen) {
                Screen.HOME -> {
                    HomeScreen(
                        unlockedCount = uiState.unlockedToyIds.size,
                        totalCount = 8,
                        onNavigate = { screen -> viewModel.navigateTo(screen) }
                    )
                }
                Screen.TOYS_COLLECTION -> {
                    ToysCollectionScreen(
                        unlockedToyIds = uiState.unlockedToyIds,
                        onToyClick = { toy -> viewModel.openToyDetail(toy) },
                        onNavigateScan = { viewModel.navigateTo(Screen.SCAN_TOY) },
                        onPlayGame = { gameType -> viewModel.openGameForToy(gameType) },
                        onUnlockToy = { toyId -> viewModel.unlockToy(toyId) }
                    )
                }
                Screen.SCAN_TOY -> {
                    ScanToyScreen(
                        unlockedToyIds = uiState.unlockedToyIds,
                        onToyUnlocked = { toyId -> viewModel.unlockToy(toyId) },
                        onNavigateCollection = { viewModel.navigateTo(Screen.TOYS_COLLECTION) },
                        onPlayGame = { gameType -> viewModel.openGameForToy(gameType) }
                    )
                }
                Screen.GAMES_HUB -> {
                    GamesHubScreen(
                        onSelectScreen = { screen -> viewModel.navigateTo(screen) }
                    )
                }
                Screen.GAME_LAUNCHER -> {
                    LauncherKnockdownGame(
                        onBack = { viewModel.navigateTo(Screen.GAMES_HUB) }
                    )
                }
                Screen.GAME_BOOMERANG -> {
                    BoomerangCatchGame(
                        onBack = { viewModel.navigateTo(Screen.GAMES_HUB) }
                    )
                }
                Screen.GAME_WATER -> {
                    WaterSplashGame(
                        onBack = { viewModel.navigateTo(Screen.GAMES_HUB) }
                    )
                }
                Screen.GAME_PANCAKE -> {
                    PancakeFlipGame(
                        onBack = { viewModel.navigateTo(Screen.GAMES_HUB) }
                    )
                }
                Screen.GAME_DOT_SOUNDBOARD -> {
                    DotSoundboardGame(
                        onBack = { viewModel.navigateTo(Screen.GAMES_HUB) }
                    )
                }
                Screen.GAME_HELMET -> {
                    HelmetRunnerGame(
                        onBack = { viewModel.navigateTo(Screen.GAMES_HUB) }
                    )
                }
            }
        }
    }

    // Parental Gate Dialog
    if (uiState.showParentalGate) {
        ParentalGateDialog(
            onDismiss = { viewModel.dismissParentalGate() },
            onUnlockAllToys = { viewModel.unlockAllToys() },
            onSuccess = { viewModel.dismissParentalGate() }
        )
    }

    // Legal Info Dialog
    if (uiState.showLegalDialog) {
        LegalDialog(
            onDismiss = { viewModel.showLegal(false) }
        )
    }
}
