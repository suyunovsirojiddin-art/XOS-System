package com.xos.personalsystem.core.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.xos.personalsystem.data.local.dao.SystemConfigDao
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AlarmManager @Inject constructor(
    private val context: Context,
    private val configDao: SystemConfigDao
) {
    
    companion object {
        private const val ALARM_REQUEST_CODE = 1001
        private const val CONFIG_KEY_WAKE_TIME = "alarm_wake_time"
    }
    
    suspend fun setAlarm(hour: Int, minute: Int) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            
            // If time has already passed today, set for tomorrow
            if (timeInMillis <= System.currentTimeMillis()) {
                add(Calendar.DAY_OF_YEAR, 1)
            }
        }
        
        val intent = Intent(context, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            ALARM_REQUEST_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (alarmManager.canScheduleExactAlarms()) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis,
                    pendingIntent
                )
            }
        } else {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                pendingIntent
            )
        }
        
        // Save wake time
        val config = com.xos.personalsystem.data.local.entities.SystemConfigEntity(
            key = CONFIG_KEY_WAKE_TIME,
            value = """{"hour": $hour, "minute": $minute}""",
            description = "Wake-up alarm time"
        )
        configDao.insert(config)
    }
    
    suspend fun cancelAlarm() {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            ALARM_REQUEST_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
        pendingIntent.cancel()
    }
    
    suspend fun getWakeTime(): Pair<Int, Int>? {
        val config = configDao.getByKey(CONFIG_KEY_WAKE_TIME)
        if (config == null) return null
        
        val json = com.google.gson.JsonParser.parseString(config.value).asJsonObject
        val hour = json.get("hour").asInt
        val minute = json.get("minute").asInt
        return Pair(hour, minute)
    }
}
