package com.xos.personalsystem

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class XOSApplication : Application(), Configuration.Provider {
    
    @Inject
    lateinit var workerFactory: HiltWorkerFactory
    
    override fun onCreate() {
        super.onCreate()
        createNotificationChannels()
    }
    
    override fun getWorkManagerConfiguration(): Configuration {
        return Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()
    }
    
    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = getSystemService(NotificationManager::class.java)
            
            // System Channel
            val systemChannel = NotificationChannel(
                "xos_system",
                "XOS System",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "XOS System notifications"
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 500, 200, 500)
            }
            
            // Tasks Channel
            val tasksChannel = NotificationChannel(
                "xos_tasks",
                "XOS Tasks",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Daily task notifications"
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 300, 100, 300)
            }
            
            // Alarm Channel
            val alarmChannel = NotificationChannel(
                "xos_alarm",
                "XOS Alarm",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Wake-up alarm notifications"
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 1000, 500, 1000)
                setSound(null, null) // Custom sound handled separately
            }
            
            // Achievement Channel
            val achievementChannel = NotificationChannel(
                "xos_achievements",
                "XOS Achievements",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Achievement notifications"
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 200, 100, 200, 100, 500)
            }
            
            notificationManager.createNotificationChannels(
                listOf(systemChannel, tasksChannel, alarmChannel, achievementChannel)
            )
        }
    }
}
