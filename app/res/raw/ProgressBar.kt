package com.xos.personalsystem.presentation.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.xos.personalsystem.presentation.theme.XOSColors
import com.xos.personalsystem.presentation.theme.XOSFonts

@Composable
fun XOSProgressBar(
    progress: Float,
    label: String = "",
    color: Color = XOSColors.SystemBlue,
    modifier: Modifier = Modifier
) {
    val animatedProgress = remember { mutableStateOf(0f) }
    
    LaunchedEffect(progress) {
        animate(
            initialValue = animatedProgress.value,
            targetValue = progress,
            animationSpec = tween(
                durationMillis = 1000,
                easing = FastOutSlowInEasing
            )
        ) { value, _ ->
            animatedProgress.value = value
        }
    }
    
    Column(modifier = modifier) {
        if (label.isNotBlank()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = label,
                    color = XOSColors.TextSecondary,
                    fontSize = 12.sp,
                    fontFamily = XOSFonts.monospace
                )
                Text(
                    text = "${(animatedProgress.value * 100).toInt()}%",
                    color = XOSColors.SystemBlue,
                    fontSize = 12.sp,
                    fontFamily = XOSFonts.monospace
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
        }
        
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(XOSColors.SurfaceDark)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(animatedProgress.value)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(4.dp))
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                color,
                                color.copy(alpha = 0.5f)
                            )
                        )
                    )
            )
        }
    }
}
