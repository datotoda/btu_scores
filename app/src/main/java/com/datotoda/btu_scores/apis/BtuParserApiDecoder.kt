package com.datotoda.btu_scores.apis

import com.datotoda.btu_scores.models.Course
import org.json.JSONArray
import org.json.JSONObject

class BtuParserApiDecoder(val data: String) {
    private val json: JSONObject = JSONObject(data)
    private val keys: JSONArray = json.getJSONArray("keys")
    fun keysList(): List<String> {
        val result = mutableListOf<String>()
        for (i in 0 until keys.length()){
            result.add(keys.getString(i))
        }
        return result
    }
    fun keysArray(): Array<String> = keysList().map { title -> "$title (${getScoreOfString(title)})" }.toTypedArray()

    private val grades: JSONObject = json.getJSONObject("grades")
    private fun getScoreOf(title: String) = if (keysList().contains(title)) grades.getDouble(title) else 0.0

    fun updateCourseInstance(course: Course): Course {
        course.setScore(
            getScoreOf(
                course.getTitle()
            )
        )
        return course
    }

    private fun getScoreOfString(title: String): String {
        val score = getScoreOf(title)
        return when(score % 1) {
            0.0 -> score.toInt().toString()
            else -> score.toString()
        }
    }
}