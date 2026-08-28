package com.xos.personalsystem.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.xos.personalsystem.presentation.navigation.XOSNavHost
import com.xos.personalsystem.presentation.theme.XOSPersonalSystemTheme
import com.xos.personalsystem.presentation.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Make status bar and navigation bar transparent
        WindowCompat.setDecorFitsSystemWindows(window, false)
        
        setContent {
            XOSPersonalSystemTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val mainViewModel: MainViewModel = hiltViewModel()
                    XOSNavHost(mainViewModel = mainViewModel)
                }
            }
        }
    }
}
