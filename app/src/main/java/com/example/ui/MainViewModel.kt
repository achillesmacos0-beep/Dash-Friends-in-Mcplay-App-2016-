package com.example.ui

import androidx.lifecycle.ViewModel
import com.example.data.CollectionStats
import com.example.data.GameType
import com.example.data.Toy
import com.example.data.ToyRepository
import com.example.util.SoundManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

enum class Screen {
    HOME,
    TOYS_COLLECTION,
    SCAN_TOY,
    GAMES_HUB,
    GAME_LAUNCHER,
    GAME_BOOMERANG,
    GAME_WATER,
    GAME_PANCAKE,
    GAME_DOT_SOUNDBOARD,
    GAME_HELMET
}

data class UiState(
    val currentScreen: Screen = Screen.HOME,
    val unlockedToyIds: Set<String> = setOf("dash_launcher_blocks", "dees_helmet"),
    val selectedToy: Toy? = null,
    val showToyDetailDialog: Boolean = false,
    val showParentalGate: Boolean = false,
    val parentalTargetScreen: String? = null,
    val showLegalDialog: Boolean = false,
    val scannedToySuccess: Toy? = null,
    val soundEnabled: Boolean = true,
    val activeCarouselIndex: Int = 0
)

class MainViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    fun navigateTo(screen: Screen) {
        SoundManager.playClick()
        _uiState.update { it.copy(currentScreen = screen) }
    }

    fun openToyDetail(toy: Toy) {
        SoundManager.playClick()
        _uiState.update { it.copy(selectedToy = toy, showToyDetailDialog = true) }
    }

    fun closeToyDetail() {
        SoundManager.playClick()
        _uiState.update { it.copy(showToyDetailDialog = false) }
    }

    fun unlockToy(toyId: String) {
        val toy = ToyRepository.getToyById(toyId) ?: return
        SoundManager.playUnlockFanfare()
        _uiState.update {
            it.copy(
                unlockedToyIds = it.unlockedToyIds + toyId,
                scannedToySuccess = toy
            )
        }
    }

    fun unlockAllToys() {
        SoundManager.playUnlockFanfare()
        val allIds = ToyRepository.allToys.map { it.id }.toSet()
        _uiState.update { it.copy(unlockedToyIds = allIds) }
    }

    fun clearScannedSuccess() {
        _uiState.update { it.copy(scannedToySuccess = null) }
    }

    fun openGameForToy(gameType: GameType) {
        SoundManager.playClick()
        val screen = when (gameType) {
            GameType.LAUNCHER_KNOCKDOWN -> Screen.GAME_LAUNCHER
            GameType.BOOMERANG_CATCH -> Screen.GAME_BOOMERANG
            GameType.WATER_SPLASH -> Screen.GAME_WATER
            GameType.PANCAKE_FLIP -> Screen.GAME_PANCAKE
            GameType.DOT_SOUNDBOARD -> Screen.GAME_DOT_SOUNDBOARD
            GameType.HELMET_RUNNER -> Screen.GAME_HELMET
        }
        _uiState.update { it.copy(currentScreen = screen, showToyDetailDialog = false) }
    }

    fun toggleSound() {
        val newSound = !_uiState.value.soundEnabled
        SoundManager.isSoundEnabled = newSound
        if (newSound) SoundManager.playClick()
        _uiState.update { it.copy(soundEnabled = newSound) }
    }

    fun setCarouselIndex(index: Int) {
        _uiState.update { it.copy(activeCarouselIndex = index) }
    }

    fun showParentalGate(target: String) {
        SoundManager.playClick()
        _uiState.update { it.copy(showParentalGate = true, parentalTargetScreen = target) }
    }

    fun dismissParentalGate() {
        _uiState.update { it.copy(showParentalGate = false, parentalTargetScreen = null) }
    }

    fun showLegal(show: Boolean) {
        SoundManager.playClick()
        _uiState.update { it.copy(showLegalDialog = show) }
    }

    fun getCollectionStats(): CollectionStats {
        return CollectionStats(
            totalToys = ToyRepository.allToys.size,
            unlockedToyIds = _uiState.value.unlockedToyIds
        )
    }
}
