package com.example.data

import com.example.R

enum class GameType(val title: String, val subtitle: String, val icon: String) {
    LAUNCHER_KNOCKDOWN("Dash Block Blast", "Aim & launch blocks to knock down targets!", "🎯"),
    BOOMERANG_CATCH("Dash Boomerang Flight", "Fling & catch Dash's curved boomerang!", "🪃"),
    WATER_SPLASH("Dot Water Squirter", "Squirt water jets to hit floating targets!", "💦"),
    PANCAKE_FLIP("Del Pan Pancake Sizzle", "Time the sizzle and flip pancakes high!", "🥞"),
    DOT_SOUNDBOARD("Dot Talking Studio", "Play funny quotes, beat loops, and voice clips!", "🎙️"),
    HELMET_RUNNER("Dee's Visor Scanner", "Scan sci-fi targets with Dee's cyber visor!", "🥽")
}

data class Toy(
    val id: String,
    val number: Int,
    val name: String,
    val character: String,
    val imageRes: Int,
    val tagline: String,
    val description: String,
    val actionName: String,
    val actionDescription: String,
    val funFact: String,
    val barcodePattern: String,
    val associatedGame: GameType
)

data class CollectionStats(
    val totalToys: Int = 8,
    val unlockedToyIds: Set<String> = setOf("dash_launcher_blocks", "dees_helmet")
) {
    val unlockedCount: Int get() = unlockedToyIds.size
    val isComplete: Boolean get() = unlockedCount >= totalToys
}
