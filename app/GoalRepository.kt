package com.xos.personalsystem.data.repositories

import com.xos.personalsystem.data.local.dao.GoalDao
import com.xos.personalsystem.data.local.entities.GoalEntity
import com.xos.personalsystem.domain.entities.Goal
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GoalRepository @Inject constructor(
    private val goalDao: GoalDao
) {
    
    fun getGoalsForPersonality(personalityId: String): Flow<List<Goal>> {
        return goalDao.getAllForPersonality(personalityId).map { entities ->
            entities.map { it.toDomain() }
        }
    }
    
    suspend fun getGoal(id: String): Goal? {
        return goalDao.getById(id)?.toDomain()
    }
    
    suspend fun getCurrentGoal(personalityId: String): Goal? {
        return goalDao.getCurrentGoal(personalityId)?.toDomain()
    }
    
    suspend fun createGoal(
        personalityId: String,
        name: String,
        description: String = "",
        deadline: Long = 0
    ): Goal {
        val orderIndex = goalDao.getIncompleteCount(personalityId)
        val entity = GoalEntity(
            personalityId = personalityId,
            name = name,
            description = description,
            orderIndex = orderIndex,
            deadline = deadline
        )
        goalDao.insert(entity)
        return entity.toDomain()
    }
    
    suspend fun updateGoal(goal: Goal) {
        goalDao.update(goal.toEntity())
    }
    
    suspend fun deleteGoal(id: String) {
        val entity = goalDao.getById(id) ?: return
        goalDao.delete(entity)
    }
    
    suspend fun completeGoal(id: String) {
        val entity = goalDao.getById(id) ?: return
        entity.isCompleted = true
        entity.completedAt = System.currentTimeMillis()
        goalDao.update(entity)
    }
    
    private fun GoalEntity.toDomain(): Goal {
        return Goal(
            id = id,
            personalityId = personalityId,
            name = name,
            description = description,
            orderIndex = orderIndex,
            deadline = deadline,
            isCompleted = isCompleted,
            completedAt = completedAt,
            isActive = isActive,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }
    
    private fun Goal.toEntity(): GoalEntity {
        return GoalEntity(
            id = id,
            personalityId = personalityId,
            name = name,
            description = description,
            orderIndex = orderIndex,
            deadline = deadline,
            isCompleted = isCompleted,
            completedAt = completedAt,
            isActive = isActive,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }
}
