package com.example.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import java.util.UUID
import com.google.firebase.firestore.FirebaseFirestore

class AppStateRepository(private val dao: AppStateDao) {
    val appStateFlow: Flow<AppState?> = dao.getAppState()

    suspend fun initializeStateIfNeeded() {
        if (dao.getAppState().first() == null) {
            val randomReferral = UUID.randomUUID().toString().substring(0, 6).uppercase()
            dao.insert(AppState(referralCode = randomReferral))
        }
    }

    suspend fun updateState(updater: (AppState) -> AppState) {
        val currentState = dao.getAppState().first() ?: return
        val newState = updater(currentState)
        dao.update(newState)
        syncToFirestore(newState)
    }
    
    private fun syncToFirestore(state: AppState) {
        try {
            val db = FirebaseFirestore.getInstance()
            val userMap = mapOf(
                "userId" to state.userId,
                "username" to state.username,
                "coins" to state.coins,
                "lastLoginDateMillis" to state.lastLoginDateMillis,
                "lastFreeSpinTimeMillis" to state.lastFreeSpinTimeMillis,
                "extraSpinsAvailable" to state.extraSpinsAvailable,
                "referredCount" to state.referredCount,
                "completedSimpleTasksMask" to state.completedSimpleTasksMask,
                "lastUpdated" to System.currentTimeMillis()
            )
            db.collection("users").document(state.userId).set(userMap)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
