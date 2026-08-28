package com.xos.personalsystem.core.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.xos.personalsystem.R
import com.xos.personalsystem.presentation.AlarmActivity

class AlarmReceiver : BroadcastReceiver() {
    
    override fun onReceive(context: Context, intent: Intent) {
        // Start alarm activity
        val alarmIntent = Intent(context, AlarmActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
        }
        context.startActivity(alarmIntent)
        
        // Show notification
        showAlarmNotification(context)
    }
    
    private fun showAlarmNotification(context: Context) {
        val notification = NotificationCompat.Builder(context, "xos_alarm")
            .setContentTitle("⏰ XOS WAKE-UP ALARM")
            .setContentText("Time to wake up! Complete the math challenge to dismiss.")
            .setSmallIcon(R.drawable.ic_alarm)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setFullScreenIntent(
                NotificationCompat.Builder(context, "xos_alarm")
                    .build()
                    .contentIntent,
                true
            )
            .setAutoCancel(false)
            .setOngoing(true)
            .build()
        
        NotificationManagerCompat.from(context).notify(1001, notification)
    }
}
