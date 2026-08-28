package com.xos.personalsystem.core.backup

import android.content.Context
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.xos.personalsystem.data.local.database.XOSDatabase
import com.xos.personalsystem.data.local.entities.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.*
import java.text.SimpleDateFormat
import java.util.*
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BackupManager @Inject constructor(
    private val context: Context,
    private val database: XOSDatabase
) {
    
    private val gson = GsonBuilder()
        .setPrettyPrinting()
        .create()
    
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd_HH-mm-ss", Locale.getDefault())
    
    suspend fun createBackup(): File {
        return withContext(Dispatchers.IO) {
            val timestamp = dateFormat.format(Date())
            val backupFile = File(context.filesDir, "xos_backup_$timestamp.xos_backup")
            
            ZipOutputStream(BufferedOutputStream(FileOutputStream(backupFile))).use { zipOut ->
                // Backup database tables
                backupTable(zipOut, "personalities.json", database.personalityDao().getAll())
                backupTable(zipOut, "goals.json", database.goalDao().getAll())
                backupTable(zipOut, "levels.json", database.levelDao().getAll())
                backupTable(zipOut, "tasks.json", database.taskDao().getAll())
                backupTable(zipOut, "task_completions.json", database.taskCompletionDao().getAll())
                backupTable(zipOut, "progressions.json", database.progressionDao().getAll())
                backupTable(zipOut, "progression_history.json", database.progressionHistoryDao().getAll())
                backupTable(zipOut, "achievements.json", database.achievementDao().getAll())
                backupTable(zipOut, "journal_entries.json", database.journalEntryDao().getAll())
                backupTable(zipOut, "lessons.json", database.lessonDao().getAll())
                backupTable(zipOut, "system_config.json", database.systemConfigDao().getAll())
                backupTable(zipOut, "notifications.json", database.notificationDao().getAll())
                backupTable(zipOut, "focus_apps.json", database.focusAppDao().getAll())
                
                // Backup metadata
                val metadata = mapOf(
                    "version" to "1.0.0",
                    "createdAt" to System.currentTimeMillis().toString(),
                    "device" to android.os.Build.MODEL,
                    "androidVersion" to android.os.Build.VERSION.RELEASE
                )
                val metadataJson = gson.toJson(metadata)
                zipOut.putNextEntry(ZipEntry("metadata.json"))
                zipOut.write(metadataJson.toByteArray())
                zipOut.closeEntry()
            }
            
            backupFile
        }
    }
    
    private suspend fun <T> backupTable(
        zipOut: ZipOutputStream,
        fileName: String,
        data: T
    ) {
        val json = gson.toJson(data)
        zipOut.putNextEntry(ZipEntry(fileName))
        zipOut.write(json.toByteArray())
        zipOut.closeEntry()
    }
    
    suspend fun restoreBackup(backupFile: File): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                ZipInputStream(BufferedInputStream(FileInputStream(backupFile))).use { zipIn ->
                    var entry = zipIn.nextEntry
                    val restoredData = mutableMapOf<String, String>()
                    
                    while (entry != null) {
                        val content = zipIn.readBytes().toString(Charsets.UTF_8)
                        restoredData[entry.name] = content
                        entry = zipIn.nextEntry
                    }
                    
                    // Restore tables
                    restoreTable(restoredData["personalities.json"], database.personalityDao()::insert)
                    restoreTable(restoredData["goals.json"], database.goalDao()::insert)
                    restoreTable(restoredData["levels.json"], database.levelDao()::insert)
                    restoreTable(restoredData["tasks.json"], database.taskDao()::insert)
                    restoreTable(restoredData["task_completions.json"], database.taskCompletionDao()::insert)
                    restoreTable(restoredData["progressions.json"], database.progressionDao()::insert)
                    restoreTable(restoredData["progression_history.json"], database.progressionHistoryDao()::insert)
                    restoreTable(restoredData["achievements.json"], database.achievementDao()::insert)
                    restoreTable(restoredData["journal_entries.json"], database.journalEntryDao()::insert)
                    restoreTable(restoredData["lessons.json"], database.lessonDao()::insert)
                    restoreTable(restoredData["system_config.json"], database.systemConfigDao()::insert)
                    restoreTable(restoredData["notifications.json"], database.notificationDao()::insert)
                    restoreTable(restoredData["focus_apps.json"], database.focusAppDao()::insert)
                }
                true
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }
    }
    
    private suspend fun <T> restoreTable(
        json: String?,
        insertFunction: suspend (T) -> Unit
    ) {
        if (json == null) return
        
        val type = object : com.google.gson.reflect.TypeToken<List<T>>() {}.type
        val items: List<T> = gson.fromJson(json, type)
        items.forEach { insertFunction(it) }
    }
    
    fun listBackups(): List<File> {
        return context.filesDir.listFiles { file ->
            file.name.endsWith(".xos_backup")
        }?.sortedByDescending { it.lastModified() } ?: emptyList()
    }
    
    fun deleteBackup(backupFile: File): Boolean {
        return backupFile.delete()
    }
}
