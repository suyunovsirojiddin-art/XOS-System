package com.xos.personalsystem.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.xos.personalsystem.presentation.theme.XOSColors
import com.xos.personalsystem.presentation.theme.XOSFonts

@Composable
fun SystemPanel(
    title: String,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .border(
                width = 2.dp,
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        XOSColors.SystemBlueGlow,
                        XOSColors.SystemPurple
                    )
                ),
                shape = RoundedCornerShape(12.dp)
            ),
        colors = CardDefaults.cardColors(
            containerColor = XOSColors.Surface.copy(alpha = 0.9f)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column {
            // System header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(XOSColors.SurfaceDark)
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    color = XOSColors.SystemBlue,
                    fontFamily = XOSFonts.monospace,
                    fontSize = 16.sp,
                    letterSpacing = 2.sp
                )
                // Glowing dot
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    XOSColors.SystemBlue,
                                    Color.Transparent
                                )
                            ),
                            shape = RoundedCornerShape(50)
                        )
                )
            }
            // Content
            content()
        }
    }
}

@Composable
fun GlowingIndicator(
    color: Color = XOSColors.SystemBlue,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(12.dp)
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(
                        color,
                        Color.Transparent
                    )
                ),
                shape = RoundedCornerShape(50)
            )
    )
}
