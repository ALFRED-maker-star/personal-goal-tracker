package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.MilestoneDatabase
import com.example.data.model.Goal
import com.example.data.model.GoalWithMilestones
import com.example.data.model.MilestoneItem
import com.example.data.repository.GoalRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MilestoneViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: GoalRepository

    init {
        val db = MilestoneDatabase.getDatabase(application)
        repository = GoalRepository(db.goalDao())
        viewModelScope.launch {
            repository.seedSampleDataIfNeeded()
        }
    }

    private val _selectedCategoryFilter = MutableStateFlow<String?>("All")
    val selectedCategoryFilter: StateFlow<String?> = _selectedCategoryFilter.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // Raw active goals from DB
    val allActiveGoals: StateFlow<List<GoalWithMilestones>> = repository.activeGoals
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Filtered active goals
    val filteredActiveGoals: StateFlow<List<GoalWithMilestones>> = combine(
        allActiveGoals,
        _selectedCategoryFilter,
        _searchQuery
    ) { goals, filter, query ->
        goals.filter { gwm ->
            val matchesCategory = (filter == null || filter == "All" || gwm.goal.category.equals(filter, ignoreCase = true))
            val matchesQuery = query.isBlank() ||
                gwm.goal.title.contains(query, ignoreCase = true) ||
                gwm.goal.description.contains(query, ignoreCase = true)
            matchesCategory && matchesQuery
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Completed goals
    val completedGoals: StateFlow<List<GoalWithMilestones>> = repository.completedGoals
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // All goals for dropdown linkage
    val allGoals: StateFlow<List<GoalWithMilestones>> = repository.allGoals
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Selected Goal for Detail
    private val _selectedGoalId = MutableStateFlow<Long?>(null)
    val selectedGoalId: StateFlow<Long?> = _selectedGoalId.asStateFlow()

    val selectedGoalWithMilestones: StateFlow<GoalWithMilestones?> = _selectedGoalId.flatMapLatest { id ->
        if (id == null) flowOf(null)
        else repository.getGoalWithMilestones(id)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Goal currently being edited (null = new goal)
    private val _editingGoal = MutableStateFlow<Goal?>(null)
    val editingGoal: StateFlow<Goal?> = _editingGoal.asStateFlow()

    // Active celebration modal
    private val _celebrationGoal = MutableStateFlow<GoalWithMilestones?>(null)
    val celebrationGoal: StateFlow<GoalWithMilestones?> = _celebrationGoal.asStateFlow()

    // User settings
    private val _reminderFrequency = MutableStateFlow("Daily Morning (8:00 AM)")
    val reminderFrequency: StateFlow<String> = _reminderFrequency.asStateFlow()

    private val _themeMode = MutableStateFlow("LIGHT") // "SYSTEM", "LIGHT", "DARK"
    val themeMode: StateFlow<String> = _themeMode.asStateFlow()

    private val _defaultProgressStyle = MutableStateFlow("CIRCULAR") // "CIRCULAR", "LINEAR"
    val defaultProgressStyle: StateFlow<String> = _defaultProgressStyle.asStateFlow()

    fun setCategoryFilter(category: String?) {
        _selectedCategoryFilter.value = category
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun selectGoal(goalId: Long?) {
        _selectedGoalId.value = goalId
    }

    fun setEditingGoal(goal: Goal?) {
        _editingGoal.value = goal
    }

    fun clearCelebration() {
        _celebrationGoal.value = null
    }

    fun setReminderFrequency(freq: String) {
        _reminderFrequency.value = freq
    }

    fun setThemeMode(mode: String) {
        _themeMode.value = mode
    }

    fun setDefaultProgressStyle(style: String) {
        _defaultProgressStyle.value = style
    }

    fun saveGoal(
        title: String,
        description: String,
        category: String,
        targetDateEpochMs: Long,
        emoji: String,
        displayStyle: String,
        colorHex: String,
        existingId: Long = 0L,
        onSaved: (Long) -> Unit = {}
    ) {
        viewModelScope.launch {
            val goal = Goal(
                id = existingId,
                title = title.trim(),
                description = description.trim(),
                category = category,
                targetDateEpochMs = targetDateEpochMs,
                emoji = emoji,
                displayStyle = displayStyle,
                colorHex = colorHex
            )
            val newId = repository.saveGoal(goal)
            onSaved(newId)
        }
    }

    fun deleteGoal(goalId: Long, onDeleted: () -> Unit = {}) {
        viewModelScope.launch {
            repository.deleteGoal(goalId)
            if (_selectedGoalId.value == goalId) {
                _selectedGoalId.value = null
            }
            onDeleted()
        }
    }

    fun toggleGoalCompletion(goalId: Long, currentCompleted: Boolean) {
        viewModelScope.launch {
            val willBeCompleted = !currentCompleted
            repository.setGoalCompletion(goalId, willBeCompleted)
            if (willBeCompleted) {
                val target = repository.getGoalWithMilestones(goalId).firstOrNull()
                _celebrationGoal.value = target
            }
        }
    }

    fun saveMilestone(
        goalId: Long,
        title: String,
        dueDateEpochMs: Long,
        milestoneId: Long = 0L,
        onSaved: () -> Unit = {}
    ) {
        viewModelScope.launch {
            val item = MilestoneItem(
                id = milestoneId,
                goalId = goalId,
                title = title.trim(),
                dueDateEpochMs = dueDateEpochMs
            )
            repository.saveMilestone(item)
            onSaved()
        }
    }

    fun deleteMilestone(milestoneId: Long) {
        viewModelScope.launch {
            repository.deleteMilestone(milestoneId)
        }
    }

    fun toggleMilestoneCompletion(milestoneId: Long, currentCompleted: Boolean) {
        viewModelScope.launch {
            repository.toggleMilestoneCompletion(milestoneId, currentCompleted)
        }
    }

    fun updateGoalNotes(goalId: Long, notes: String) {
        viewModelScope.launch {
            repository.updateGoalNotes(goalId, notes)
        }
    }

    fun resetToSampleData() {
        viewModelScope.launch {
            repository.seedSampleDataIfNeeded(forceReset = true)
        }
    }
}
