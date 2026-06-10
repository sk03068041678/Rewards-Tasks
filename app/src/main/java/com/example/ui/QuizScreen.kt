package com.example.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.ui.theme.CorrectGreen
import com.example.ui.theme.WrongRed

data class Question(val text: String, val options: List<String>, val correctIndex: Int)

val sampleQuestions = listOf(
    Question("What is 5 + 7?", listOf("10", "11", "12", "13"), 2),
    Question("What is the capital of Pakistan?", listOf("Karachi", "Islamabad", "Lahore", "Peshawar"), 1),
    Question("Which planet is known as the Red Planet?", listOf("Venus", "Mars", "Jupiter", "Saturn"), 1),
    Question("How many continents are there?", listOf("5", "6", "7", "8"), 2),
    Question("What is 8 x 4?", listOf("24", "32", "36", "40"), 1)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizScreen(viewModel: MainViewModel, navController: NavController, showAd: (() -> Unit) -> Unit) {
    var currentQuestionIndex by remember { mutableStateOf(0) }
    var score by remember { mutableStateOf(0) }
    var showResult by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Daily Quiz") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            if (showResult) {
                Text("Quiz Completed!", style = MaterialTheme.typography.headlineMedium)
                Spacer(modifier = Modifier.height(16.dp))
                Text("You scored $score/${sampleQuestions.size}", style = MaterialTheme.typography.titleLarge)
                Spacer(modifier = Modifier.height(8.dp))
                Text("Earned: ${score * 15} Coins", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
                
                Spacer(modifier = Modifier.height(32.dp))
                Button(onClick = { navController.popBackStack() }) {
                    Text("Go Back")
                }
            } else {
                val q = sampleQuestions[currentQuestionIndex]
                Text("Question ${currentQuestionIndex + 1} of ${sampleQuestions.size}", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(32.dp))
                Text(q.text, style = MaterialTheme.typography.headlineSmall)
                Spacer(modifier = Modifier.height(32.dp))
                
                q.options.forEachIndexed { index, option ->
                    OutlinedButton(
                        onClick = {
                            if (index == q.correctIndex) score++
                            if (currentQuestionIndex < sampleQuestions.size - 1) {
                                currentQuestionIndex++
                            } else {
                                viewModel.submitQuizScore(
                                    correctAnswers = score,
                                    showAd = showAd,
                                    onSuccess = { won -> 
                                        showResult = true
                                        // Update score visually or leave it as is, since we showed final
                                    }
                                )
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(56.dp).padding(vertical = 8.dp)
                    ) {
                        Text(option)
                    }
                }
            }
        }
    }
}
