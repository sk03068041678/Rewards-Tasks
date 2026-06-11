package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface AppStateDao {
    @Query("SELECT * FROM app_state WHERE id = 1 LIMIT 1")
    fun getAppState(): Flow<AppState?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(state: AppState)

    @Update
    suspend fun update(state: AppState)
}
