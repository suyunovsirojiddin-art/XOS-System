package com.xos.personalsystem.di

import com.xos.personalsystem.core.alarm.AlarmManager
import com.xos.personalsystem.core.backup.BackupManager
import com.xos.personalsystem.core.focus.FocusModeManager
import com.xos.personalsystem.core.notification.NotificationManager
import com.xos.personalsystem.core.youtube.YouTubeService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CoreModule {
    
    @Provides
    @Singleton
    fun provideAlarmManager(
        @ApplicationContext context: android.content.Context,
        configDao: com.xos.personalsystem.data.local.dao.SystemConfigDao
    ): AlarmManager {
        return AlarmManager(context, configDao)
    }
    
    @Provides
    @Singleton
    fun provideBackupManager(
        @ApplicationContext context: android.content.Context,
        database: com.xos.personalsystem.data.local.database.XOSDatabase
    ): BackupManager {
        return BackupManager(context, database)
    }
    
    @Provides
    @Singleton
    fun provideFocusModeManager(
        @ApplicationContext context: android.content.Context,
        focusAppDao: com.xos.personalsystem.data.local.dao.FocusAppDao
    ): FocusModeManager {
        return FocusModeManager(context, focusAppDao)
    }
    
    @Provides
    @Singleton
    fun provideYouTubeService(): YouTubeService {
        return YouTubeService()
    }
}
