package com.datotoda.btu_scores.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.datotoda.btu_scores.models.Course

@Database(
    entities = [Course::class],
    version = 1
)
abstract class BtuScoresDatabase: RoomDatabase() {
    abstract fun getCourseDao(): CourseDao

    companion object {
        @Volatile
        private var INSTANCE: BtuScoresDatabase? = null

        fun getDatabase(context: Context): BtuScoresDatabase {
            val temInstance = INSTANCE
            if (temInstance != null) {
                return temInstance
            }

            synchronized(this) {
                val instance = Room
                    .databaseBuilder(context.applicationContext, BtuScoresDatabase::class.java, "btuScoresDatabase")
                    .build()
                INSTANCE = instance
                return instance
            }
        }
    }
}