package com.xos.personalsystem.presentation.components

import android.content.Context
import android.media.MediaPlayer
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.xos.personalsystem.domain.entities.NotificationType
import com.xos.personalsystem.presentation.theme.XOSColors
import com.xos.personalsystem.presentation.theme.XOSFonts

@Composable
fun SystemNotification(
    message: String,
    type: NotificationType,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    
    // Tovush chiqarish
    LaunchedEffect(type) {
        playNotificationSound(context, type)
    }
    
    AnimatedVisibility(
        visible = true,
        enter = slideInVertically() + fadeIn(),
        exit = slideOutVertically() + fadeOut()
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            colors = CardDefaults.cardColors(
                containerColor = XOSColors.Surface
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = when (type) {
                                NotificationType.WARNING -> "⚠️"
                                NotificationType.ACHIEVEMENT -> "🏆"
                                NotificationType.DAILY_TASK -> "📋"
                                NotificationType.LEVEL_CHANGE -> "⬆️"
                                else -> "📢"
                            },
                            fontSize = 20.sp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = type.name,
                            color = when (type) {
                                NotificationType.WARNING -> XOSColors.Warning
                                NotificationType.ACHIEVEMENT -> XOSColors.SystemGold
                                NotificationType.DAILY_TASK -> XOSColors.SystemBlue
                                NotificationType.LEVEL_CHANGE -> XOSColors.SystemPurple
                                else -> XOSColors.TextPrimary
                            },
                            fontFamily = XOSFonts.monospace,
                            fontSize = 14.sp
                        )
                    }
                    Text(
                        text = message,
                        color = XOSColors.TextPrimary,
                        fontSize = 14.sp,
                        fontFamily = XOSFonts.sansSerif
                    )
                }
                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Dismiss",
                        tint = XOSColors.TextMuted
                    )
                }
            }
        }
    }
}

// Tovush funksiyasi
private fun playNotificationSound(context: Context, type: NotificationType) {
    try {
        val soundId = when (type) {
            NotificationType.WARNING -> {
                // "TISS" ovoz effekti
                android.media.MediaPlayer.create(
                    context,
                    android.media.MediaPlayer.create(context, android.media.R.raw.warning_sound)
                )
            }
            NotificationType.ACHIEVEMENT -> {
                android.media.MediaPlayer.create(
                    context,
                    android.media.MediaPlayer.create(context, android.media.R.raw.achievement_sound)
                )
            }
            NotificationType.DAILY_TASK -> {
                android.media.MediaPlayer.create(
                    context,
                    android.media.MediaPlayer.create(context, android.media.R.raw.task_sound)
                )
            }
            else -> {
                android.media.MediaPlayer.create(
                    context,
                    android.media.MediaPlayer.create(context, android.media.R.raw.system_sound)
                )
            }
        }
        sound?.start()
    } catch (e: Exception) {
        // Tovush bo'lmasa, hech narsa qilma
    }
}
