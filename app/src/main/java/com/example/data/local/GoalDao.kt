package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.data.model.Goal
import com.example.data.model.GoalWithMilestones
import com.example.data.model.MilestoneItem
import kotlinx.coroutines.flow.Flow

@Dao
interface GoalDao {

    @Transaction
    @Query("SELECT * FROM goals WHERE isCompleted = :isCompleted ORDER BY targetDateEpochMs ASC")
    fun getGoalsWithMilestonesByCompletion(isCompleted: Boolean): Flow<List<GoalWithMilestones>>

    @Transaction
    @Query("SELECT * FROM goals ORDER BY createdAtEpochMs DESC")
    fun getAllGoalsWithMilestones(): Flow<List<GoalWithMilestones>>

    @Transaction
    @Query("SELECT * FROM goals WHERE id = :goalId")
    fun getGoalWithMilestonesById(goalId: Long): Flow<GoalWithMilestones?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGoal(goal: Goal): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGoals(goals: List<Goal>): List<Long>

    @Update
    suspend fun updateGoal(goal: Goal)

    @Delete
    suspend fun deleteGoal(goal: Goal)

    @Query("DELETE FROM goals WHERE id = :goalId")
    suspend fun deleteGoalById(goalId: Long)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMilestone(milestone: MilestoneItem): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMilestones(milestones: List<MilestoneItem>): List<Long>

    @Update
    suspend fun updateMilestone(milestone: MilestoneItem)

    @Delete
    suspend fun deleteMilestone(milestone: MilestoneItem)

    @Query("DELETE FROM milestones WHERE id = :id")
    suspend fun deleteMilestoneById(id: Long)

    @Query("UPDATE milestones SET isCompleted = :isCompleted, completedAtEpochMs = :completedAt WHERE id = :id")
    suspend fun setMilestoneCompletion(id: Long, isCompleted: Boolean, completedAt: Long?)

    @Query("UPDATE goals SET isCompleted = :isCompleted, completedAtEpochMs = :completedAt WHERE id = :id")
    suspend fun setGoalCompletion(id: Long, isCompleted: Boolean, completedAt: Long?)

    @Query("UPDATE goals SET notes = :notes WHERE id = :goalId")
    suspend fun updateGoalNotes(goalId: Long, notes: String)

    @Query("SELECT COUNT(*) FROM goals")
    suspend fun getGoalCount(): Int
}
