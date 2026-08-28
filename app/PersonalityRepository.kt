package com.xos.personalsystem.data.repositories

import com.xos.personalsystem.data.local.dao.PersonalityDao
import com.xos.personalsystem.data.local.entities.PersonalityEntity
import com.xos.personalsystem.domain.entities.Personality
import com.xos.personalsystem.domain.entities.PersonalityType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PersonalityRepository @Inject constructor(
    private val personalityDao: PersonalityDao
) {
    
    fun getAllPersonalities(): Flow<List<Personality>> {
        return personalityDao.getAll().map { entities ->
            entities.map { it.toDomain() }
        }
    }
    
    suspend fun getPersonality(id: String): Personality? {
        return personalityDao.getById(id)?.toDomain()
    }
    
    suspend fun getActivePersonality(): Personality? {
        return personalityDao.getActive()?.toDomain()
    }
    
    suspend fun getXOSPersonality(): Personality? {
        return personalityDao.getXOS()?.toDomain()
    }
    
    suspend fun createPersonality(name: String, type: PersonalityType): Personality {
        val entity = PersonalityEntity(
            name = name,
            type = type.name
        )
        personalityDao.insert(entity)
        return entity.toDomain()
    }
    
    suspend fun updatePersonality(personality: Personality) {
        personalityDao.update(personality.toEntity())
    }
    
    suspend fun deletePersonality(id: String) {
        val entity = personalityDao.getById(id) ?: return
        personalityDao.delete(entity)
    }
    
    suspend fun setActivePersonality(id: String) {
        personalityDao.clearActive()
        personalityDao.setActive(id)
    }
    
    private fun PersonalityEntity.toDomain(): Personality {
        return Personality(
            id = id,
            name = name,
            type = PersonalityType.valueOf(type),
            isActive = isActive,
            orderIndex = orderIndex,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }
    
    private fun Personality.toEntity(): PersonalityEntity {
        return PersonalityEntity(
            id = id,
            name = name,
            type = type.name,
            isActive = isActive,
            orderIndex = orderIndex,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }
}
