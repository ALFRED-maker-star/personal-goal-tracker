package com.example.data.model

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.Relation
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

@Entity(
    tableName = "goals",
    indices = [Index(value = ["isCompleted"]), Index(value = ["category"])]
)
data class Goal(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val description: String = "",
    val category: String = "Career",
    val targetDateEpochMs: Long,
    val emoji: String = "🎯",
    val displayStyle: String = "CIRCULAR", // "CIRCULAR" or "LINEAR"
    val createdAtEpochMs: Long = System.currentTimeMillis(),
    val isCompleted: Boolean = false,
    val completedAtEpochMs: Long? = null,
    val colorHex: String = "#7C3AED",
    val notes: String = ""
)

@Entity(
    tableName = "milestones",
    foreignKeys = [
        ForeignKey(
            entity = Goal::class,
            parentColumns = ["id"],
            childColumns = ["goalId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["goalId"])]
)
data class MilestoneItem(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val goalId: Long,
    val title: String,
    val dueDateEpochMs: Long,
    val isCompleted: Boolean = false,
    val completedAtEpochMs: Long? = null,
    val sortOrder: Int = 0
)

data class GoalWithMilestones(
    @Embedded
    val goal: Goal,
    @Relation(
        parentColumn = "id",
        entityColumn = "goalId"
    )
    val milestones: List<MilestoneItem>
) {
    val totalMilestones: Int get() = milestones.size
    val completedMilestones: Int get() = milestones.count { it.isCompleted }

    val progressPercent: Float
        get() {
            if (goal.isCompleted) return 1.0f
            if (milestones.isEmpty()) return 0.0f
            return (completedMilestones.toFloat() / totalMilestones.toFloat()).coerceIn(0f, 1f)
        }

    val progressPercentageInt: Int
        get() = (progressPercent * 100).toInt()

    val daysRemainingCount: Long
        get() {
            val now = System.currentTimeMillis()
            val diff = goal.targetDateEpochMs - now
            return TimeUnit.MILLISECONDS.toDays(diff)
        }

    val deadlineCountdownText: String
        get() {
            if (goal.isCompleted) {
                val dateStr = goal.completedAtEpochMs?.let {
                    SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(Date(it))
                } ?: "Completed"
                return "Completed on $dateStr"
            }
            val days = daysRemainingCount
            return when {
                days < -1 -> "Overdue by ${-days} days"
                days == -1L -> "Overdue by 1 day"
                days == 0L -> "Due today!"
                days == 1L -> "1 day left"
                else -> "$days days left"
            }
        }

    val formattedTargetDate: String
        get() = SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(Date(goal.targetDateEpochMs))
}

object GoalCategories {
    val all = listOf(
        CategoryInfo("Career", "💼", "#7C3AED"),
        CategoryInfo("Health", "🏃", "#EC4899"),
        CategoryInfo("Learning", "📚", "#3B82F6"),
        CategoryInfo("Finance", "💰", "#10B981"),
        CategoryInfo("Personal", "🧘", "#8B5CF6"),
        CategoryInfo("Creative", "🎨", "#F59E0B")
    )

    fun getIcon(name: String): String = all.find { it.name.equals(name, ignoreCase = true) }?.icon ?: "🎯"
    fun getColorHex(name: String): String = all.find { it.name.equals(name, ignoreCase = true) }?.colorHex ?: "#7C3AED"
}

data class CategoryInfo(
    val name: String,
    val icon: String,
    val colorHex: String
)

val EMOJI_OPTIONS = listOf(
    "🎯", "🚀", "💪", "📚", "💎", "🎨", "🌿", "🏆",
    "🏃", "💼", "🧠", "🔥", "✨", "⭐", "🏔️", "💰",
    "🧘", "🎸", "🌱", "🎓", "⚡", "🌍", "💡", "🛠️"
)
