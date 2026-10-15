package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.model.Goal
import com.example.data.model.MilestoneItem

@Database(
    entities = [Goal::class, MilestoneItem::class],
    version = 1,
    exportSchema = false
)
abstract class MilestoneDatabase : RoomDatabase() {
    abstract fun goalDao(): GoalDao

    companion object {
        @Volatile
        private var INSTANCE: MilestoneDatabase? = null

        fun getDatabase(context: Context): MilestoneDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MilestoneDatabase::class.java,
                    "milestone_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
