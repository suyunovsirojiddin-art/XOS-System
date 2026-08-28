package com.xos.personalsystem.presentation.screens

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
import androidx.hilt.navigation.compose.hiltViewModel
import com.xos.personalsystem.domain.entities.Task
import com.xos.personalsystem.presentation.components.SystemPanel
import com.xos.personalsystem.presentation.components.XOSProgressBar
import com.xos.personalsystem.presentation.theme.XOSColors
import com.xos.personalsystem.presentation.theme.XOSFonts
import com.xos.personalsystem.presentation.viewmodels.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DailyTasksScreen(
    personalityId: String,
    viewModel: MainViewModel = hiltViewModel(),
    onBack: () -> Unit
) {
    val todayTasks by viewModel.todayTasks.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    
    // Calculate progress
    val totalTasks = todayTasks.size
    val completedTasks = todayTasks.count { it.isCompleted }
    val progress = if (totalTasks > 0) completedTasks.toFloat() / totalTasks else 0f
    
    Scaffold(
        containerColor = XOSColors.Background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "DAILY TASKS",
                        color = XOSColors.SystemBlue,
                        fontFamily = XOSFonts.monospace,
                        fontSize = 18.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = androidx.compose.material.icons.Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = XOSColors.TextPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = XOSColors.Background
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // Progress Overview
            SystemPanel(title = "PROGRESS OVERVIEW") {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "TOTAL: $totalTasks",
                            color = XOSColors.TextSecondary,
                            fontFamily = XOSFonts.monospace,
                            fontSize = 14.sp
                        )
                        Text(
                            text = "COMPLETED: $completedTasks",
                            color = XOSColors.SystemGreen,
                            fontFamily = XOSFonts.monospace,
                            fontSize = 14.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    XOSProgressBar(
                        progress = progress,
                        label = "Progress"
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Task List
            Text(
                text = "TASKS",
                color = XOSColors.SystemBlue,
                fontFamily = XOSFonts.monospace,
                fontSize = 16.sp,
                modifier = Modifier.padding(vertical = 8.dp)
            )
            
            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = XOSColors.SystemBlue)
                }
            } else if (todayTasks.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "NO TASKS FOR TODAY\n\nALL TASKS COMPLETED!",
                        color = XOSColors.SystemGreen,
                        fontFamily = XOSFonts.monospace,
                        fontSize = 16.sp,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(todayTasks) { task ->
                        TaskCard(
                            task = task,
                            onComplete = {
                                viewModel.completeTask(task.id, personalityId)
                            },
                            onFail = {
                                viewModel.failTask(task.id, personalityId)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TaskCard(
    task: Task,
    onComplete: () -> Unit,
    onFail: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (task.isCompleted) 
                XOSColors.SurfaceDark 
            else 
                XOSColors.Surface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = task.title,
                        color = if (task.isCompleted) 
                            XOSColors.TextMuted 
                        else 
                            XOSColors.TextPrimary,
                        fontFamily = XOSFonts.monospace,
                        fontSize = 16.sp
                    )
                    Text(
                        text = "${task.taskType.name} • ${task.difficulty.name} • ${task.estimatedDuration}m",
                        color = XOSColors.TextMuted,
                        fontSize = 12.sp,
                        fontFamily = XOSFonts.sansSerif
                    )
                }
                
                if (task.isCompleted) {
                    Text(
                        text = "✓ DONE",
                        color = XOSColors.SystemGreen,
                        fontFamily = XOSFonts.monospace,
                        fontSize = 14.sp
                    )
                } else {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = onComplete,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = XOSColors.SystemGreen
                            ),
                            modifier = Modifier.height(36.dp)
                        ) {
                            Text("✓", fontSize = 18.sp)
                        }
                        Button(
                            onClick = onFail,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = XOSColors.SystemRed
                            ),
                            modifier = Modifier.height(36.dp)
                        ) {
                            Text("✗", fontSize = 18.sp)
                        }
                    }
                }
            }
            
            if (task.requiresAIVerification) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "🤖 AI VERIFICATION REQUIRED",
                    color = XOSColors.SystemBlue,
                    fontSize = 10.sp,
                    fontFamily = XOSFonts.monospace
                )
            }
        }
    }
}
