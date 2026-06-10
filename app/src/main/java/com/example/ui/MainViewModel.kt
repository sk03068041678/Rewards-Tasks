package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppState
import com.example.data.AppStateRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar
import kotlinx.coroutines.delay
import kotlin.random.Random

class MainViewModel(private val repository: AppStateRepository) : ViewModel() {
    
    private val _isLoadingTasks = kotlinx.coroutines.flow.MutableStateFlow(true)
    val isLoadingTasks: StateFlow<Boolean> = _isLoadingTasks

    val uiState: StateFlow<AppState> = repository.appStateFlow
        .map { it ?: AppState() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = AppState()
        )

    init {
        viewModelScope.launch {
            repository.initializeStateIfNeeded()
            // Simulate fetching task data from Firebase
            delay(1500)
            _isLoadingTasks.value = false
        }
    }

    fun claimDailyLogin(showAd: (() -> Unit) -> Unit, onSuccess: (Int) -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            val state = uiState.value
            val now = System.currentTimeMillis()
            if (!isSameDay(state.lastLoginDateMillis, now)) {
                showAd {
                    viewModelScope.launch {
                        repository.updateState { 
                            it.copy(
                                coins = it.coins + 10,
                                lastLoginDateMillis = now
                            )
                        }
                        onSuccess(10)
                    }
                }
            } else {
                onError("Daily reward already claimed today.")
            }
        }
    }

    fun watchAdForCoins(showAd: (() -> Unit) -> Unit, onComplete: (Int) -> Unit) {
        showAd {
            viewModelScope.launch {
                repository.updateState { it.copy(coins = it.coins + 20) }
                onComplete(20)
            }
        }
    }

    fun watchAdForExtraSpin(showAd: (() -> Unit) -> Unit, onComplete: () -> Unit) {
        showAd {
            viewModelScope.launch {
                repository.updateState { it.copy(extraSpinsAvailable = it.extraSpinsAvailable + 1) }
                onComplete()
            }
        }
    }

    fun spinWheel(showAd: (() -> Unit) -> Unit, onResult: (Int) -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            val state = uiState.value
            val now = System.currentTimeMillis()
            val canSpinFree = now - state.lastFreeSpinTimeMillis >= 3600_000 // 1 hour
            
            if (!canSpinFree && state.extraSpinsAvailable <= 0) {
                onError("No spins available. Watch an ad to get an extra spin!")
                return@launch
            }

            showAd {
                viewModelScope.launch {
                    val rewards = listOf(5, 10, 15, 20, 25, 30, 40, 50)
                    val won = rewards.random()
                    
                    repository.updateState {
                        if (canSpinFree) {
                            it.copy(lastFreeSpinTimeMillis = now, coins = it.coins + won)
                        } else {
                            it.copy(extraSpinsAvailable = it.extraSpinsAvailable - 1, coins = it.coins + won)
                        }
                    }
                    onResult(won)
                }
            }
        }
    }

    fun scratchCard(cardIndex: Int, showAd: (() -> Unit) -> Unit, onResult: (Int) -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            val state = uiState.value
            val now = System.currentTimeMillis()
            
            val isNewDay = !isSameDay(state.scratchedCardsDateMillis, now)
            val currentMask = if (isNewDay) 0 else state.scratchedCardsMask
            
            val isScratched = (currentMask and (1 shl cardIndex)) != 0
            if (isScratched) {
                onError("Card already scratched!")
                return@launch
            }

            showAd {
                viewModelScope.launch {
                    val won = Random.nextInt(5, 31)
                    val newMask = currentMask or (1 shl cardIndex)
                    
                    repository.updateState {
                        it.copy(
                            scratchedCardsDateMillis = now,
                            scratchedCardsMask = newMask,
                            coins = it.coins + won
                        )
                    }
                    onResult(won)
                }
            }
        }
    }

    fun submitQuizScore(correctAnswers: Int, showAd: (() -> Unit) -> Unit, onFailure: () -> Unit = {}, onSuccess: (Int) -> Unit) {
        val won = correctAnswers * 15
        if (won > 0) {
            showAd {
                viewModelScope.launch {
                    repository.updateState { it.copy(coins = it.coins + won) }
                    onSuccess(won)
                }
            }
        } else {
            onSuccess(0)
        }
    }

    fun processReferralCode(code: String, showAd: (() -> Unit) -> Unit, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            val state = uiState.value
            if (code.trim().uppercase() == state.referralCode) {
                onError("Cannot use your own referral code!")
                return@launch
            }
            if (code.length < 5) {
                onError("Invalid referral code!")
                return@launch
            }
            
            showAd {
                viewModelScope.launch {
                    repository.updateState {
                        it.copy(coins = it.coins + 50, referredCount = it.referredCount + 1)
                    }
                    onSuccess()
                }
            }
        }
    }
    
    fun processWithdrawal(amount: Int, showAd: (() -> Unit) -> Unit, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            val state = uiState.value
            if (state.coins < 1000) {
                onError("Minimum withdrawal is 1000 coins.")
                return@launch
            }
            if (amount < 1000 || amount > state.coins) {
                onError("Invalid withdrawal amount.")
                return@launch
            }
            
            showAd {
                viewModelScope.launch {
                    repository.updateState { it.copy(coins = it.coins - amount) }
                    onSuccess()
                }
            }
        }
    }

    fun completeSimpleTask(taskId: Int, rewardAmount: Int, showAd: (() -> Unit) -> Unit, onSuccess: (String) -> Unit) {
        viewModelScope.launch {
            val state = uiState.value
            val now = System.currentTimeMillis()
            val isNewDay = !isSameDay(state.completedSimpleTasksDateMillis, now)
            val currentMask = if (isNewDay) 0 else state.completedSimpleTasksMask
            val isCompleted = (currentMask and (1 shl taskId)) != 0
            
            if (!isCompleted) {
                showAd {
                    viewModelScope.launch {
                        val newMask = currentMask or (1 shl taskId)
                        repository.updateState { 
                            it.copy(
                                completedSimpleTasksDateMillis = now,
                                completedSimpleTasksMask = newMask,
                                coins = it.coins + rewardAmount
                            ) 
                        }
                        onSuccess("+$rewardAmount Coins")
                    }
                }
            }
        }
    }

    fun checkDailyTasksReset() {
        viewModelScope.launch {
            val state = uiState.value
            val now = System.currentTimeMillis()
            if (!isSameDay(state.completedSimpleTasksDateMillis, now)) {
                repository.updateState {
                    it.copy(
                        completedSimpleTasksDateMillis = now,
                        completedSimpleTasksMask = 0
                    )
                }
            }
        }
    }

    fun toggleDarkMode() {
        viewModelScope.launch {
            repository.updateState { it.copy(isDarkMode = !it.isDarkMode) }
        }
    }

    private fun isSameDay(time1: Long, time2: Long): Boolean {
        if (time1 == 0L) return false
        val cal1 = Calendar.getInstance().apply { timeInMillis = time1 }
        val cal2 = Calendar.getInstance().apply { timeInMillis = time2 }
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
               cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
    }
}
