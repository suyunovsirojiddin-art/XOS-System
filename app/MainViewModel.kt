package com.xos.personalsystem.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xos.personalsystem.data.repositories.*
import com.xos.personalsystem.domain.entities.*
import com.xos.personalsystem.domain.engines.TaskEngine
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val personalityRepository: PersonalityRepository,
    private val goalRepository: GoalRepository,
    private val taskEngine: TaskEngine
) : ViewModel() {
    
    // Personalities
    private val _personalities = MutableStateFlow<List<Personality>>(emptyList())
    val personalities: StateFlow<List<Personality>> = _personalities.asStateFlow()
    
    // Current Personality
    private val _currentPersonality = MutableStateFlow<Personality?>(null)
    val currentPersonality: StateFlow<Personality?> = _currentPersonality.asStateFlow()
    
    // Goals for current personality
    private val _goals = MutableStateFlow<List<Goal>>(emptyList())
    val goals: StateFlow<List<Goal>> = _goals.asStateFlow()
    
    // Today's tasks
    private val _todayTasks = MutableStateFlow<List<Task>>(emptyList())
    val todayTasks: StateFlow<List<Task>> = _todayTasks.asStateFlow()
    
    // Loading state
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    init {
        loadPersonalities()
    }
    
    fun loadPersonalities() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                personalityRepository.getAllPersonalities().collect { personalities ->
                    _personalities.value = personalities
                    // Set active personality if exists
                    val active = personalities.find { it.isActive }
                    if (active != null) {
                        _currentPersonality.value = active
                        loadGoalsForPersonality(active.id)
                        loadTodayTasks(active.id)
                    }
                }
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun selectPersonality(personalityId: String) {
        viewModelScope.launch {
            personalityRepository.setActivePersonality(personalityId)
            loadPersonalities()
        }
    }
    
    fun createPersonality(name: String, type: PersonalityType) {
        viewModelScope.launch {
            personalityRepository.createPersonality(name, type)
            loadPersonalities()
        }
    }
    
    fun deletePersonality(personalityId: String) {
        viewModelScope.launch {
            personalityRepository.deletePersonality(personalityId)
            loadPersonalities()
        }
    }
    
    fun loadGoalsForPersonality(personalityId: String) {
        viewModelScope.launch {
            goalRepository.getGoalsForPersonality(personalityId).collect { goals ->
                _goals.value = goals
            }
        }
    }
    
    fun loadTodayTasks(personalityId: String) {
        viewModelScope.launch {
            val tasks = taskEngine.getTodayTasks(personalityId)
            _todayTasks.value = tasks
        }
    }
    
    fun createGoal(
        personalityId: String,
        name: String,
        description: String = "",
        deadline: Long = 0
    ) {
        viewModelScope.launch {
            goalRepository.createGoal(personalityId, name, description, deadline)
            loadGoalsForPersonality(personalityId)
        }
    }
    
    fun completeGoal(goalId: String) {
        viewModelScope.launch {
            goalRepository.completeGoal(goalId)
            loadGoalsForPersonality(_currentPersonality.value?.id ?: return@launch)
        }
    }
    
    fun completeTask(taskId: String, personalityId: String) {
        viewModelScope.launch {
            taskEngine.completeTask(taskId, personalityId)
            loadTodayTasks(personalityId)
        }
    }
    
    fun failTask(taskId: String, personalityId: String) {
        viewModelScope.launch {
            taskEngine.failTask(taskId, personalityId)
            loadTodayTasks(personalityId)
        }
    }
}
