package com.datotoda.btu_scores.view_models

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.datotoda.btu_scores.data.BtuScoresDatabase
import com.datotoda.btu_scores.models.Course
import com.datotoda.btu_scores.repositories.CourseRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CourseViewModel (application: Application): AndroidViewModel(application) {
    val readAllData: LiveData<List<Course>>
    private val repository: CourseRepository

    init {
        val courseDao = BtuScoresDatabase.getDatabase(application).getCourseDao()

        repository = CourseRepository(courseDao)

        readAllData = repository.readAllData
    }

    fun getCourseById(id: Int): LiveData<Course?> {
        val result = MutableLiveData<Course?>()
        viewModelScope.launch(Dispatchers.IO) {
            result.postValue(repository.getCourseById(id))
        }
        return result
    }

    fun getSyncCourses(): LiveData<List<Course?>> {
        val result = MutableLiveData<List<Course?>>()
        viewModelScope.launch(Dispatchers.IO) {
            result.postValue(repository.getSyncCourses())
        }
        return result
    }

    fun getMaxOrder(): LiveData<Int?> {
        val result = MutableLiveData<Int?>()
        viewModelScope.launch(Dispatchers.IO) {
            result.postValue(repository.getMaxOrder())
        }
        return result
    }

    fun addCourse(course: Course) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.addCourse(course)
        }
    }

    fun updateCourse(course: Course) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateCourse(course)
        }
    }

    fun deleteCourse(course: Course) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteCourse(course)
        }
    }

    fun deleteAllCourses() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteAllCourses()
        }
    }
}