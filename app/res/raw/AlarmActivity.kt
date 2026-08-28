package com.xos.personalsystem.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.core.app.NotificationManagerCompat
import com.xos.personalsystem.core.alarm.MathChallengeGenerator
import com.xos.personalsystem.presentation.theme.XOSColors
import com.xos.personalsystem.presentation.theme.XOSFonts
import kotlinx.coroutines.delay

class AlarmActivity : ComponentActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Make full screen
        window.setFlags(
            android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN,
            android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        
        setContent {
            AlarmScreen(
                onDismiss = {
                    // Cancel notification
                    NotificationManagerCompat.from(this).cancel(1001)
                    finish()
                }
            )
        }
    }
}

@Composable
fun AlarmScreen(onDismiss: () -> Unit) {
    val generator = remember { MathChallengeGenerator() }
    val questions = remember { generator.generateQuestions(10) }
    var currentIndex by remember { mutableStateOf(0) }
    var userAnswer by remember { mutableStateOf("") }
    var isCorrect by remember { mutableStateOf(false) }
    var showError by remember { mutableStateOf(false) }
    var completedCount by remember { mutableStateOf(0) }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(XOSColors.Background)
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Header
        Text(
            text = "╔══════════════════════╗",
            color = XOSColors.SystemBlue,
            fontSize = 14.sp,
            fontFamily = XOSFonts.monospace
        )
        Text(
            text = "║  WAKE-UP ALARM  ║",
            color = XOSColors.SystemBlue,
            fontSize = 24.sp,
            fontFamily = XOSFonts.monospace
        )
        Text(
            text = "╚══════════════════════╝",
            color = XOSColors.SystemBlue,
            fontSize = 14.sp,
            fontFamily = XOSFonts.monospace
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Progress
        Text(
            text = "COMPLETE ${completedCount}/10 QUESTIONS",
            color = XOSColors.SystemBlue,
            fontSize = 16.sp,
            fontFamily = XOSFonts.monospace
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Progress bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .background(XOSColors.SurfaceDark)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(completedCount.toFloat() / 10f)
                    .fillMaxHeight()
                    .background(XOSColors.SystemBlue)
            )
        }
        
        Spacer(modifier = Modifier.height(48.dp))
        
        // Question
        if (currentIndex < questions.size) {
            val question = questions[currentIndex]
            
            Text(
                text = "QUESTION ${currentIndex + 1}",
                color = XOSColors.TextSecondary,
                fontSize = 14.sp,
                fontFamily = XOSFonts.monospace
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "${question.first} ${question.second} ${question.third} = ?",
                color = XOSColors.TextPrimary,
                fontSize = 48.sp,
                fontFamily = XOSFonts.monospace
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Input
            OutlinedTextField(
                value = userAnswer,
                onValueChange = { 
                    if (it.all { char -> char.isDigit() || char == '-' }) {
                        userAnswer = it 
                    }
                },
                label = { Text("Your Answer") },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = XOSColors.SystemBlue,
                    unfocusedBorderColor = XOSColors.TextMuted
                ),
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            if (showError) {
                Text(
                    text = "✗ INCORRECT! TRY AGAIN",
                    color = XOSColors.SystemRed,
                    fontSize = 14.sp,
                    fontFamily = XOSFonts.monospace
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
            
            Button(
                onClick = {
                    val answer = userAnswer.toIntOrNull()
                    if (answer != null && answer == question.fourth) {
                        isCorrect = true
                        showError = false
                        completedCount++
                        currentIndex++
                        userAnswer = ""
                        
                        if (completedCount >= 10) {
                            // All questions complete
                            onDismiss()
                        }
                    } else {
                        showError = true
                        userAnswer = ""
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = XOSColors.SystemBlue
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("SUBMIT", color = Color.White, fontFamily = XOSFonts.monospace)
            }
        }
    }
}
