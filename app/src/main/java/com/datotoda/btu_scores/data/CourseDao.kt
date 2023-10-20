package com.datotoda.btu_scores.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.datotoda.btu_scores.models.Course

@Dao
interface CourseDao {
    @Query("SELECT * FROM courses ORDER BY `order` ASC")
    fun readAllData(): LiveData<List<Course>>

    @Query("SELECT * FROM courses WHERE id = :id")
    suspend fun getCourseById(id: Int): Course?

    @Query("SELECT * FROM courses WHERE sync = 1")
    suspend fun getSyncCourses(): List<Course?>

    @Query("SELECT MAX(`order`) FROM courses")
    suspend fun getMaxOrder(): Int?

    @Query("DELETE FROM courses")
    suspend fun deleteAllCourses()

    @Delete
    suspend fun deleteCourse(course: Course)

    @Update
    suspend fun updateCourse(course: Course)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addCourse(course: Course)
}