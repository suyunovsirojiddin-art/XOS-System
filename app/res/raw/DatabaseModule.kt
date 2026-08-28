package com.xos.personalsystem.di

import android.content.Context
import androidx.room.Room
import com.xos.personalsystem.data.local.database.XOSDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): XOSDatabase {
        return XOSDatabase.getDatabase(context)
    }
    
    @Provides
    @Singleton
    fun providePersonalityDao(database: XOSDatabase) = database.personalityDao()
    
    @Provides
    @Singleton
    fun provideGoalDao(database: XOSDatabase) = database.goalDao()
    
    @Provides
    @Singleton
    fun provideLevelDao(database: XOSDatabase) = database.levelDao()
    
    @Provides
    @Singleton
    fun provideTaskDao(database: XOSDatabase) = database.taskDao()
    
    @Provides
    @Singleton
    fun provideTaskCompletionDao(database: XOSDatabase) = database.taskCompletionDao()
    
    @Provides
    @Singleton
    fun provideProgressionDao(database: XOSDatabase) = database.progressionDao()
    
    @Provides
    @Singleton
    fun provideProgressionHistoryDao(database: XOSDatabase) = database.progressionHistoryDao()
    
    @Provides
    @Singleton
    fun provideAchievementDao(database: XOSDatabase) = database.achievementDao()
    
    @Provides
    @Singleton
    fun provideJournalEntryDao(database: XOSDatabase) = database.journalEntryDao()
    
    @Provides
    @Singleton
    fun provideLessonDao(database: XOSDatabase) = database.lessonDao()
    
    @Provides
    @Singleton
    fun provideSystemConfigDao(database: XOSDatabase) = database.systemConfigDao()
    
    @Provides
    @Singleton
    fun provideNotificationDao(database: XOSDatabase) = database.notificationDao()
    
    @Provides
    @Singleton
    fun provideFocusAppDao(database: XOSDatabase) = database.focusAppDao()
    
    @Provides
    @Singleton
    fun provideBackupDao(database: XOSDatabase) = database.backupDao()
}
