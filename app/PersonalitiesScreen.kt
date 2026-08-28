package com.xos.personalsystem.presentation.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.xos.personalsystem.domain.entities.Personality
import com.xos.personalsystem.presentation.components.SystemPanel
import com.xos.personalsystem.presentation.theme.XOSColors
import com.xos.personalsystem.presentation.viewmodels.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PersonalitiesScreen(
    viewModel: MainViewModel = hiltViewModel(),
    onPersonalitySelected: (String) -> Unit
) {
    val personalities by viewModel.personalities.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(XOSColors.Background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Header
            Text(
                text = "╔══════════════════════╗",
                color = XOSColors.SystemBlue,
                fontSize = 12.sp,
                fontFamily = XOSFonts.monospace
            )
            Text(
                text = "║  XOS PERSONAL SYSTEM ║",
                color = XOSColors.SystemBlue,
                fontSize = 16.sp,
                fontFamily = XOSFonts.monospace
            )
            Text(
                text = "╚══════════════════════╝",
                color = XOSColors.SystemBlue,
                fontSize = 12.sp,
                fontFamily = XOSFonts.monospace
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                text = "PERSONALITIES",
                color = XOSColors.SystemBlue,
                fontSize = 20.sp,
                fontFamily = XOSFonts.monospace,
                letterSpacing = 4.sp
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Personality List
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(personalities) { personality ->
                    PersonalityCard(
                        personality = personality,
                        onClick = { onPersonalitySelected(personality.id) }
                    )
                }
                
                // Add Personality Button
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showAddDialog = true },
                        colors = CardDefaults.cardColors(
                            containerColor = XOSColors.Surface.copy(alpha = 0.5f)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Add Personality",
                                tint = XOSColors.SystemBlue
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "ADD NEW PERSONALITY",
                                color = XOSColors.SystemBlue,
                                fontFamily = XOSFonts.monospace
                            )
                        }
                    }
                }
            }
        }
        
        // Add Personality Dialog
        if (showAddDialog) {
            AddPersonalityDialog(
                onDismiss = { showAddDialog = false },
                onAdd = { name, type ->
                    viewModel.createPersonality(name, type)
                    showAddDialog = false
                }
            )
        }
    }
}

@Composable
fun PersonalityCard(
    personality: Personality,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = if (personality.isActive) 
                XOSColors.SurfaceLight 
            else 
                XOSColors.Surface
        ),
        shape = RoundedCornerShape(12.dp),
        border = if (personality.isActive) {
            androidx.compose.foundation.BorderStroke(
                2.dp,
                Brush.horizontalGradient(
                    colors = listOf(
                        XOSColors.SystemBlue,
                        XOSColors.SystemPurple
                    )
                )
            )
        } else null
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(
                        if (personality.type.name == "XOS") 
                            XOSColors.SystemBlue 
                        else 
                            XOSColors.SystemPurple
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = personality.name.first().toString(),
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = personality.name,
                        color = XOSColors.TextPrimary,
                        fontSize = 18.sp,
                        fontFamily = XOSFonts.monospace
                    )
                    if (personality.isActive) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(XOSColors.SystemGreen)
                        )
                    }
                }
                Text(
                    text = "TYPE: ${personality.type.name}",
                    color = XOSColors.TextMuted,
                    fontSize = 12.sp,
                    fontFamily = XOSFonts.monospace
                )
            }
            
            Spacer(modifier = Modifier.weight(1f))
            
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = null,
                tint = XOSColors.TextMuted
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPersonalityDialog(
    onDismiss: () -> Unit,
    onAdd: (String, PersonalityType) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var type by remember { mutableStateOf(PersonalityType.NORMAL) }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "ADD PERSONALITY",
                color = XOSColors.SystemBlue,
                fontFamily = XOSFonts.monospace
            )
        },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = XOSColors.SystemBlue,
                        unfocusedBorderColor = XOSColors.TextMuted
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    FilterChip(
                        selected = type == PersonalityType.NORMAL,
                        onClick = { type = PersonalityType.NORMAL },
                        label = { Text("Normal") }
                    )
                    FilterChip(
                        selected = type == PersonalityType.XOS,
                        onClick = { type = PersonalityType.XOS },
                        label = { Text("XOS") }
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = { if (name.isNotBlank()) onAdd(name, type) },
                enabled = name.isNotBlank()
            ) {
                Text("ADD", color = XOSColors.SystemBlue)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("CANCEL", color = XOSColors.TextMuted)
            }
        },
        containerColor = XOSColors.Surface
    )
}
