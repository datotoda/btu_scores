package com.datotoda.btu_scores.repositories

import androidx.lifecycle.LiveData
import com.datotoda.btu_scores.data.CourseDao
import com.datotoda.btu_scores.models.Course

class CourseRepository (private val courseDao: CourseDao) {
    val readAllData: LiveData<List<Course>> = courseDao.readAllData()

    suspend fun getCourseById(id: Int): Course? = courseDao.getCourseById(id)

    suspend fun getSyncCourses(): List<Course?> = courseDao.getSyncCourses()

    suspend fun getMaxOrder(): Int? = courseDao.getMaxOrder()


    suspend fun addCourse(course: Course) {
        courseDao.addCourse(course)
    }

    suspend fun updateCourse(course: Course) {
        courseDao.updateCourse(course)
    }

    suspend fun deleteCourse(course: Course) {
        courseDao.deleteCourse(course)
    }

    suspend fun deleteAllCourses() {
        courseDao.deleteAllCourses()
    }

}