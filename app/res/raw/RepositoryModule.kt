package com.xos.personalsystem.di

import com.xos.personalsystem.data.repositories.*
import com.xos.personalsystem.domain.engines.TaskEngine
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    
    @Provides
    @Singleton
    fun providePersonalityRepository(
        personalityDao: com.xos.personalsystem.data.local.dao.PersonalityDao
    ): PersonalityRepository {
        return PersonalityRepository(personalityDao)
    }
    
    @Provides
    @Singleton
    fun provideGoalRepository(
        goalDao: com.xos.personalsystem.data.local.dao.GoalDao
    ): GoalRepository {
        return GoalRepository(goalDao)
    }
    
    @Provides
    @Singleton
    fun provideTaskEngine(
        taskDao: com.xos.personalsystem.data.local.dao.TaskDao,
        taskCompletionDao: com.xos.personalsystem.data.local.dao.TaskCompletionDao,
        levelDao: com.xos.personalsystem.data.local.dao.LevelDao,
        progressionDao: com.xos.personalsystem.data.local.dao.ProgressionDao,
        progressionHistoryDao: com.xos.personalsystem.data.local.dao.ProgressionHistoryDao
    ): TaskEngine {
        return TaskEngine(
            taskDao,
            taskCompletionDao,
            levelDao,
            progressionDao,
            progressionHistoryDao
        )
    }
}
