package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "app_state")
data class AppState(
    @PrimaryKey val id: Int = 1,
    val username: String = "Player123",
    val coins: Int = 0,
    val lastLoginDateMillis: Long = 0L, // Timestamp marking last exact daily login 
    val lastFreeSpinTimeMillis: Long = 0L, // Track last hourly spin
    val extraSpinsAvailable: Int = 0,
    val referralCode: String = "",
    val referredCount: Int = 0,
    val scratchedCardsDateMillis: Long = 0L, // Representing the start of the day for scratch cards
    val scratchedCardsMask: Int = 0, // 6 bits for 6 cards
    val isDarkMode: Boolean = false,
    val completedSimpleTasksMask: Int = 0,
    val completedSimpleTasksDateMillis: Long = 0L,
    val userId: String = java.util.UUID.randomUUID().toString()
)
