package com.example.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [AppState::class], version = 5, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun appStateDao(): AppStateDao
}
