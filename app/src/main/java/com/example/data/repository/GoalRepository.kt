package com.example.data.repository

import com.example.data.local.GoalDao
import com.example.data.model.Goal
import com.example.data.model.GoalWithMilestones
import com.example.data.model.MilestoneItem
import kotlinx.coroutines.flow.Flow
import java.util.concurrent.TimeUnit

class GoalRepository(private val goalDao: GoalDao) {

    val activeGoals: Flow<List<GoalWithMilestones>> =
        goalDao.getGoalsWithMilestonesByCompletion(isCompleted = false)

    val completedGoals: Flow<List<GoalWithMilestones>> =
        goalDao.getGoalsWithMilestonesByCompletion(isCompleted = true)

    val allGoals: Flow<List<GoalWithMilestones>> =
        goalDao.getAllGoalsWithMilestones()

    fun getGoalWithMilestones(id: Long): Flow<GoalWithMilestones?> =
        goalDao.getGoalWithMilestonesById(id)

    suspend fun saveGoal(goal: Goal): Long {
        return if (goal.id == 0L) {
            goalDao.insertGoal(goal)
        } else {
            goalDao.updateGoal(goal)
            goal.id
        }
    }

    suspend fun deleteGoal(goalId: Long) {
        goalDao.deleteGoalById(goalId)
    }

    suspend fun setGoalCompletion(goalId: Long, isCompleted: Boolean) {
        val completedAt = if (isCompleted) System.currentTimeMillis() else null
        goalDao.setGoalCompletion(goalId, isCompleted, completedAt)
    }

    suspend fun saveMilestone(milestone: MilestoneItem): Long {
        return if (milestone.id == 0L) {
            goalDao.insertMilestone(milestone)
        } else {
            goalDao.updateMilestone(milestone)
            milestone.id
        }
    }

    suspend fun deleteMilestone(milestoneId: Long) {
        goalDao.deleteMilestoneById(milestoneId)
    }

    suspend fun toggleMilestoneCompletion(milestoneId: Long, currentCompleted: Boolean) {
        val newStatus = !currentCompleted
        val completedAt = if (newStatus) System.currentTimeMillis() else null
        goalDao.setMilestoneCompletion(milestoneId, newStatus, completedAt)
    }

    suspend fun updateGoalNotes(goalId: Long, notes: String) {
        goalDao.updateGoalNotes(goalId, notes)
    }

    suspend fun seedSampleDataIfNeeded(forceReset: Boolean = false) {
        val count = goalDao.getGoalCount()
        if (count > 0 && !forceReset) return

        val now = System.currentTimeMillis()
        val oneDay = TimeUnit.DAYS.toMillis(1)

        // Sample 1: Half Marathon
        val g1 = Goal(
            title = "Run a Half Marathon",
            description = "Train consistently to complete the 21.1 km city marathon with confidence and strong endurance.",
            category = "Health",
            targetDateEpochMs = now + (32 * oneDay),
            emoji = "🏃",
            displayStyle = "CIRCULAR",
            colorHex = "#EC4899",
            notes = "Training schedule: Tuesday tempo runs, Thursday intervals, Sunday long recovery runs. Hydrate and stretch!"
        )
        val id1 = goalDao.insertGoal(g1)
        goalDao.insertMilestones(
            listOf(
                MilestoneItem(goalId = id1, title = "Complete 5K without walking breaks", dueDateEpochMs = now + (7 * oneDay), isCompleted = true, completedAtEpochMs = now - (2 * oneDay)),
                MilestoneItem(goalId = id1, title = "Reach 10K continuous trail run", dueDateEpochMs = now + (15 * oneDay), isCompleted = true, completedAtEpochMs = now - (1 * oneDay)),
                MilestoneItem(goalId = id1, title = "Do 15K long run with hydration gel test", dueDateEpochMs = now + (22 * oneDay), isCompleted = false),
                MilestoneItem(goalId = id1, title = "18K peak simulation run", dueDateEpochMs = now + (28 * oneDay), isCompleted = false),
                MilestoneItem(goalId = id1, title = "Race day: cross the 21.1K finish line!", dueDateEpochMs = now + (32 * oneDay), isCompleted = false)
            )
        )

        // Sample 2: Launch Milestone App
        val g2 = Goal(
            title = "Launch Mobile App MVP",
            description = "Build and ship version 1.0 with delightful UI, snappy local database, and positive reviews.",
            category = "Career",
            targetDateEpochMs = now + (18 * oneDay),
            emoji = "🚀",
            displayStyle = "LINEAR",
            colorHex = "#7C3AED",
            notes = "Focus on micro-interactions, responsive progress ring, and gold celebration particles."
        )
        val id2 = goalDao.insertGoal(g2)
        goalDao.insertMilestones(
            listOf(
                MilestoneItem(goalId = id2, title = "Design design system & color tokens", dueDateEpochMs = now + (3 * oneDay), isCompleted = true, completedAtEpochMs = now - (3 * oneDay)),
                MilestoneItem(goalId = id2, title = "Build Room database layer and entities", dueDateEpochMs = now + (6 * oneDay), isCompleted = true, completedAtEpochMs = now - (1 * oneDay)),
                MilestoneItem(goalId = id2, title = "Implement circular & linear gradient progress bars", dueDateEpochMs = now + (10 * oneDay), isCompleted = true, completedAtEpochMs = now),
                MilestoneItem(goalId = id2, title = "Complete checklist and notes persistence", dueDateEpochMs = now + (14 * oneDay), isCompleted = false),
                MilestoneItem(goalId = id2, title = "Deploy to production & celebrate", dueDateEpochMs = now + (18 * oneDay), isCompleted = false)
            )
        )

        // Sample 3: Read 12 Inspiring Books
        val g3 = Goal(
            title = "Read 12 Books This Year",
            description = "Expand perspectives in philosophy, systems design, and biography by reading 20 mins every morning.",
            category = "Learning",
            targetDateEpochMs = now + (75 * oneDay),
            emoji = "📚",
            displayStyle = "CIRCULAR",
            colorHex = "#3B82F6",
            notes = "Currently reading: Atomic Habits by James Clear. Next up: Deep Work by Cal Newport."
        )
        val id3 = goalDao.insertGoal(g3)
        goalDao.insertMilestones(
            listOf(
                MilestoneItem(goalId = id3, title = "Book 1: Atomic Habits", dueDateEpochMs = now + (10 * oneDay), isCompleted = true, completedAtEpochMs = now - (4 * oneDay)),
                MilestoneItem(goalId = id3, title = "Book 2: Deep Work", dueDateEpochMs = now + (25 * oneDay), isCompleted = false),
                MilestoneItem(goalId = id3, title = "Book 3: Thinking in Systems", dueDateEpochMs = now + (45 * oneDay), isCompleted = false),
                MilestoneItem(goalId = id3, title = "Book 4: The Psychology of Money", dueDateEpochMs = now + (60 * oneDay), isCompleted = false)
            )
        )

        // Sample 4: Completed Goal - Emergency Fund
        val g4 = Goal(
            title = "Save $10,000 Emergency Fund",
            description = "Build a 6-month financial safety net in a high-yield savings account for peace of mind.",
            category = "Finance",
            targetDateEpochMs = now - (5 * oneDay),
            emoji = "💰",
            displayStyle = "LINEAR",
            isCompleted = true,
            completedAtEpochMs = now - (5 * oneDay),
            colorHex = "#10B981",
            notes = "Successfully automated monthly transfers! Achieved complete financial cushion."
        )
        val id4 = goalDao.insertGoal(g4)
        goalDao.insertMilestones(
            listOf(
                MilestoneItem(goalId = id4, title = "Save first $2,500 milestone", dueDateEpochMs = now - (40 * oneDay), isCompleted = true, completedAtEpochMs = now - (38 * oneDay)),
                MilestoneItem(goalId = id4, title = "Cross $5,000 halfway mark", dueDateEpochMs = now - (25 * oneDay), isCompleted = true, completedAtEpochMs = now - (22 * oneDay)),
                MilestoneItem(goalId = id4, title = "Reach $7,500 milestone", dueDateEpochMs = now - (12 * oneDay), isCompleted = true, completedAtEpochMs = now - (10 * oneDay)),
                MilestoneItem(goalId = id4, title = "Hit full $10,000 target balance", dueDateEpochMs = now - (5 * oneDay), isCompleted = true, completedAtEpochMs = now - (5 * oneDay))
            )
        )

        // Sample 5: Completed Goal - Master Sourdough Baking
        val g5 = Goal(
            title = "Master Artisan Sourdough",
            description = "Cultivate wild yeast starter and bake open-crumb golden sourdough boules.",
            category = "Creative",
            targetDateEpochMs = now - (15 * oneDay),
            emoji = "🥖",
            displayStyle = "CIRCULAR",
            isCompleted = true,
            completedAtEpochMs = now - (15 * oneDay),
            colorHex = "#F59E0B",
            notes = "Nailed 78% hydration and Dutch oven steam bake. Family loved the crust!"
        )
        val id5 = goalDao.insertGoal(g5)
        goalDao.insertMilestones(
            listOf(
                MilestoneItem(goalId = id5, title = "Create active bubbling starter", dueDateEpochMs = now - (30 * oneDay), isCompleted = true, completedAtEpochMs = now - (28 * oneDay)),
                MilestoneItem(goalId = id5, title = "Bake first loaf with ear rise", dueDateEpochMs = now - (20 * oneDay), isCompleted = true, completedAtEpochMs = now - (19 * oneDay)),
                MilestoneItem(goalId = id5, title = "Perfect blistered crust & open crumb", dueDateEpochMs = now - (15 * oneDay), isCompleted = true, completedAtEpochMs = now - (15 * oneDay))
            )
        )
    }
}
