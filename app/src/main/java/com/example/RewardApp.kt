package com.example

import android.app.Application
import androidx.room.Room
import com.example.data.AppDatabase
import com.example.data.AppStateRepository
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions

class RewardApp : Application() {
    
    lateinit var database: AppDatabase
        private set
        
    lateinit var repository: AppStateRepository
        private set

    override fun onCreate() {
        super.onCreate()
        
        if (BuildConfig.FIREBASE_API_KEY.isNotEmpty() && BuildConfig.FIREBASE_PROJECT_ID.isNotEmpty()) {
            val options = FirebaseOptions.Builder()
                .setApiKey(BuildConfig.FIREBASE_API_KEY)
                .setApplicationId(if (BuildConfig.FIREBASE_APP_ID.isNotEmpty()) BuildConfig.FIREBASE_APP_ID else "1:1234567890:android:abcdef")
                .setProjectId(BuildConfig.FIREBASE_PROJECT_ID)
                .build()
            try {
                FirebaseApp.initializeApp(this, options)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        database = Room.databaseBuilder(
            this,
            AppDatabase::class.java,
            "reward_tasks_app_database"
        )
        .fallbackToDestructiveMigration(true)
        .build()
        repository = AppStateRepository(database.appStateDao())
    }
}
