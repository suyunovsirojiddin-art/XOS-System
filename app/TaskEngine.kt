package com.xos.personalsystem.domain.engines

import com.xos.personalsystem.data.local.dao.*
import com.xos.personalsystem.data.local.entities.*
import com.xos.personalsystem.domain.entities.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TaskEngine @Inject constructor(
    private val taskDao: TaskDao,
    private val taskCompletionDao: TaskCompletionDao,
    private val levelDao: LevelDao,
    private val progressionDao: ProgressionDao,
    private val progressionHistoryDao: ProgressionHistoryDao
) {
    
    suspend fun generateDailyTasks(personalityId: String): List<Task> {
        val progression = progressionDao.getByPersonality(personalityId)
        if (progression == null) return emptyList()
        
        val currentGoal = progression.goalId
        val currentLevel = progression.levelId
        
        // Get tasks for current level
        val tasks = taskDao.getAllForLevel(currentLevel)
        
        // Check today's completions
        val today = getStartOfDay()
        val completions = taskCompletionDao.getForDate(personalityId, today)
        val completedTaskIds = completions.filter { it.status == "PASSED" }.map { it.taskId }
        
        // Filter pending tasks
        val pendingTasks = tasks.filter { task ->
            task.isActive && task.taskId !in completedTaskIds
        }
        
        return pendingTasks.map { it.toDomain() }
    }
    
    suspend fun getTodayTasks(personalityId: String): List<Task> {
        val today = getStartOfDay()
        val completions = taskCompletionDao.getForDate(personalityId, today)
        val pendingTaskIds = completions.filter { it.status == "PENDING" }.map { it.taskId }
        
        return pendingTaskIds.mapNotNull { taskId ->
            taskDao.getById(taskId)?.toDomain()
        }
    }
    
    suspend fun completeTask(taskId: String, personalityId: String): Result<Unit> {
        return try {
            val completion = taskCompletionDao.getByTaskAndPersonality(taskId, personalityId)
            if (completion == null) {
                // Create new completion
                val newCompletion = TaskCompletionEntity(
                    taskId = taskId,
                    personalityId = personalityId,
                    status = "PASSED"
                )
                taskCompletionDao.insert(newCompletion)
            } else {
                completion.status = "PASSED"
                completion.completionDate = System.currentTimeMillis()
                taskCompletionDao.update(completion)
            }
            
            // Update progression
            updateProgression(personalityId)
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun failTask(taskId: String, personalityId: String): Result<Unit> {
        return try {
            val completion = taskCompletionDao.getByTaskAndPersonality(taskId, personalityId)
            if (completion == null) {
                val newCompletion = TaskCompletionEntity(
                    taskId = taskId,
                    personalityId = personalityId,
                    status = "FAILED",
                    attemptsCount = 1,
                    lastAttemptDate = System.currentTimeMillis()
                )
                taskCompletionDao.insert(newCompletion)
            } else {
                completion.status = "FAILED"
                completion.attemptsCount += 1
                completion.lastAttemptDate = System.currentTimeMillis()
                taskCompletionDao.update(completion)
            }
            
            // Handle failure
            handleTaskFailure(personalityId)
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun verifyTaskAI(taskId: String, personalityId: String, response: String): AIVerificationResult {
        // This will be implemented with AI provider
        // For now, return a mock result
        return AIVerificationResult(
            passed = true,
            score = 1.0f,
            feedback = "Task verified successfully."
        )
    }
    
    private suspend fun updateProgression(personalityId: String) {
        val progression = progressionDao.getByPersonality(personalityId) ?: return
        val today = getStartOfDay()
        
        // Count completed tasks today
        val completedCount = taskCompletionDao.getCompletedCountForDate(personalityId, today)
        val totalTasks = taskCompletionDao.getPendingCountForDate(personalityId, today) + completedCount
        
        // Update progression
        progression.completedTasksCount = completedCount
        progression.totalTasksCount = totalTasks
        progression.progressPercentage = if (totalTasks > 0) {
            completedCount.toFloat() / totalTasks.toFloat() * 100
        } else {
            0f
        }
        
        // Check if all tasks are completed
        if (completedCount >= totalTasks && totalTasks > 0) {
            // All tasks completed - level up
            levelUp(personalityId)
        }
        
        progression.updatedAt = System.currentTimeMillis()
        progressionDao.update(progression)
    }
    
    private suspend fun levelUp(personalityId: String) {
        val progression = progressionDao.getByPersonality(personalityId) ?: return
        val currentGoal = progression.goalId
        val currentLevelNum = progression.currentLevelNumber
        
        // Get next level
        val nextLevel = levelDao.getByLevelNumber(currentGoal, currentLevelNum + 1)
        if (nextLevel != null) {
            // Level up
            progression.currentLevelNumber = currentLevelNum + 1
            progression.levelId = nextLevel.id
            progression.currentStreak += 1
            if (progression.currentStreak > progression.highestStreak) {
                progression.highestStreak = progression.currentStreak
            }
            if (progression.currentLevelNumber > progression.highestLevelReached) {
                progression.highestLevelReached = progression.currentLevelNumber
            }
            progression.consecutiveFailures = 0
            progression.isWarningActive = false
            
            // Add history entry
            val history = ProgressionHistoryEntity(
                personalityId = personalityId,
                eventType = "LEVEL_UP",
                levelNumber = progression.currentLevelNumber,
                details = "Advanced to Level ${progression.currentLevelNumber}",
                metadata = """{"streak": ${progression.currentStreak}}"""
            )
            progressionHistoryDao.insert(history)
            
            progressionDao.update(progression)
        } else {
            // Check if all levels complete - complete goal
            val goal = levelDao.getAllForGoal(currentGoal).firstOrNull()?.firstOrNull()?.toDomain()
            if (goal != null) {
                // Mark goal as complete
                val goalDao = TODO("Get goalDao instance")
                // goalDao.completeGoal(goal.id)
            }
        }
    }
    
    private suspend fun handleTaskFailure(personalityId: String) {
        val progression = progressionDao.getByPersonality(personalityId) ?: return
        
        progression.totalFailures += 1
        progression.consecutiveFailures += 1
        progression.currentStreak = 0
        
        // Check punishment rules
        if (progression.consecutiveFailures == 1) {
            // First failure - warning
            progression.isWarningActive = true
            
            // Add history entry
            val history = ProgressionHistoryEntity(
                personalityId = personalityId,
                eventType = "WARNING",
                levelNumber = progression.currentLevelNumber,
                details = "Warning: Complete tasks to avoid level drop",
                metadata = """{"consecutive_failures": 1}"""
            )
            progressionHistoryDao.insert(history)
            
        } else if (progression.consecutiveFailures >= 2) {
            // Level drop
            val currentLevelNum = progression.currentLevelNumber
            val newLevelNum = maxOf(1, currentLevelNum - 1)
            
            if (newLevelNum < currentLevelNum) {
                // Drop level
                progression.currentLevelNumber = newLevelNum
                progression.levelDrops += 1
                progression.isWarningActive = false
                
                // Get new level
                val goalId = progression.goalId
                val newLevel = levelDao.getByLevelNumber(goalId, newLevelNum)
                if (newLevel != null) {
                    progression.levelId = newLevel.id
                }
                
                // Add history entry
                val history = ProgressionHistoryEntity(
                    personalityId = personalityId,
                    eventType = "LEVEL_DOWN",
                    levelNumber = newLevelNum,
                    details = "Dropped from Level $currentLevelNum to Level $newLevelNum",
                    metadata = """{"consecutive_failures": ${progression.consecutiveFailures}}"""
                )
                progressionHistoryDao.insert(history)
            }
        }
        
        progression.updatedAt = System.currentTimeMillis()
        progressionDao.update(progression)
    }
    
    private fun getStartOfDay(): Long {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }
    
    private fun TaskEntity.toDomain(): Task {
        return Task(
            id = id,
            levelId = levelId,
            title = title,
            description = description,
            taskType = TaskType.valueOf(taskType),
            difficulty = Difficulty.valueOf(difficulty),
            estimatedDuration = estimatedDuration,
            deadline = deadline,
            isRequired = isRequired,
            requiresAIVerification = requiresAIVerification,
            recurrence = RecurrenceType.valueOf(recurrence),
            aiVerificationPrompt = aiVerificationPrompt,
            isActive = isActive,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }
}

data class AIVerificationResult(
    val passed: Boolean,
    val score: Float,
    val feedback: String
)
