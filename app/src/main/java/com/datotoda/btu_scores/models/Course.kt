package com.datotoda.btu_scores.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "courses")
data class Course (
    private var title: String,
    private var score: Double,
    private var sync: Boolean,
    private var order: Int,
) {
    @PrimaryKey(autoGenerate = true)
    private var id: Int = 0
    fun getId() = id
    fun getTitle() = title
    fun getScore() = score
    fun getScoreString(): String {
        return when(score % 1) {
            0.0 -> score.toInt().toString()
            else -> score.toString()
        }
    }
    fun getSync() = sync
    fun getOrder() = order

    fun setId(id: Int) {
        this.id = id
    }
    fun setTitle(title: String) {
        this.title = title
    }
    fun setScore(score: Double) {
        this.score = score
    }
    fun setSync(sync: Boolean) {
        this.sync = sync
    }
    fun setOrder(order: Int) {
        this.order = order
    }
}
