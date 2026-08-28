package com.xos.personalsystem.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.xos.personalsystem.data.local.dao.*
import com.xos.personalsystem.data.local.entities.*
import com.xos.personalsystem.data.local.converters.Converters

@Database(
    entities = [
        PersonalityEntity::class,
        GoalEntity::class,
        LevelEntity::class,
        TaskEntity::class,
        TaskCompletionEntity::class,
        ProgressionEntity::class,
        ProgressionHistoryEntity::class,
        AchievementEntity::class,
        JournalEntryEntity::class,
        LessonEntity::class,
        SystemConfigEntity::class,
        NotificationEntity::class,
        FocusAppEntity::class,
        BackupEntity::class
    ],
    version = 1,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class XOSDatabase : RoomDatabase() {
    
    abstract fun personalityDao(): PersonalityDao
    abstract fun goalDao(): GoalDao
    abstract fun levelDao(): LevelDao
    abstract fun taskDao(): TaskDao
    abstract fun taskCompletionDao(): TaskCompletionDao
    abstract fun progressionDao(): ProgressionDao
    abstract fun progressionHistoryDao(): ProgressionHistoryDao
    abstract fun achievementDao(): AchievementDao
    abstract fun journalEntryDao(): JournalEntryDao
    abstract fun lessonDao(): LessonDao
    abstract fun systemConfigDao(): SystemConfigDao
    abstract fun notificationDao(): NotificationDao
    abstract fun focusAppDao(): FocusAppDao
    abstract fun backupDao(): BackupDao

    companion object {
        @Volatile
        private var INSTANCE: XOSDatabase? = null

        fun getDatabase(context: Context): XOSDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    XOSDatabase::class.java,
                    "xos_database"
                )
                .addCallback(DatabaseCallback())
                .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                // Insert default data
            }
        }
    }
}
