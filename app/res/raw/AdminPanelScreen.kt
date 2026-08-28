package com.xos.personalsystem.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.xos.personalsystem.domain.entities.*
import com.xos.personalsystem.presentation.components.SystemPanel
import com.xos.personalsystem.presentation.theme.XOSColors
import com.xos.personalsystem.presentation.theme.XOSFonts
import com.xos.personalsystem.presentation.viewmodels.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminPanelScreen(
    viewModel: MainViewModel = hiltViewModel(),
    onBack: () -> Unit
) {
    val personalities by viewModel.personalities.collectAsState()
    val goals by viewModel.goals.collectAsState()
    var selectedPersonalityId by remember { mutableStateOf<String?>(null) }
    var showAddGoalDialog by remember { mutableStateOf(false) }
    var showAddTaskDialog by remember { mutableStateOf(false) }
    
    Scaffold(
        containerColor = XOSColors.Background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "XOS ADMIN PANEL",
                        color = XOSColors.SystemBlue,
                        fontFamily = XOSFonts.monospace,
                        fontSize = 18.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = XOSColors.TextPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = XOSColors.Background
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddGoalDialog = true },
                containerColor = XOSColors.SystemBlue
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Goal")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Text(
                text = "ADMIN CONTROL",
                color = XOSColors.SystemBlue,
                fontFamily = XOSFonts.monospace,
                fontSize = 20.sp,
                letterSpacing = 4.sp
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Admin Options Grid
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                AdminOptionCard(
                    icon = Icons.Default.Person,
                    title = "Personalities",
                    onClick = { /* Navigate to personality management */ }
                )
                AdminOptionCard(
                    icon = Icons.Default.Flag,
                    title = "Goals",
                    onClick = { /* Navigate to goal management */ }
                )
                AdminOptionCard(
                    icon = Icons.Default.List,
                    title = "Tasks",
                    onClick = { /* Navigate to task management */ }
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                AdminOptionCard(
                    icon = Icons.Default.Settings,
                    title = "System",
                    onClick = { /* Navigate to system settings */ }
                )
                AdminOptionCard(
                    icon = Icons.Default.NotificationImportant,
                    title = "Notifications",
                    onClick = { /* Navigate to notification settings */ }
                )
                AdminOptionCard(
                    icon = Icons.Default.Lock,
                    title = "Focus",
                    onClick = { /* Navigate to focus mode */ }
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Current Goals
            Text(
                text = "CURRENT GOALS",
                color = XOSColors.SystemBlue,
                fontFamily = XOSFonts.monospace,
                fontSize = 16.sp,
                modifier = Modifier.padding(vertical = 8.dp)
            )
            
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(goals) { goal ->
                    GoalAdminCard(
                        goal = goal,
                        onEdit = { /* Edit goal */ },
                        onDelete = { viewModel.completeGoal(goal.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun AdminOptionCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .weight(1f)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = XOSColors.Surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = XOSColors.SystemBlue,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = title,
                color = XOSColors.TextSecondary,
                fontSize = 12.sp,
                fontFamily = XOSFonts.monospace
            )
        }
    }
}

@Composable
fun GoalAdminCard(
    goal: Goal,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = XOSColors.Surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = goal.name,
                    color = XOSColors.TextPrimary,
                    fontFamily = XOSFonts.monospace,
                    fontSize = 14.sp
                )
                if (goal.description.isNotBlank()) {
                    Text(
                        text = goal.description,
                        color = XOSColors.TextMuted,
                        fontSize = 12.sp
                    )
                }
            }
            Row {
                IconButton(onClick = onEdit) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = "Edit",
                        tint = XOSColors.SystemBlue
                    )
                }
                IconButton(onClick = onDelete) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = XOSColors.SystemRed
                    )
                }
            }
        }
    }
}
