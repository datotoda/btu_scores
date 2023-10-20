package com.datotoda.btu_scores.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.datotoda.btu_scores.R
import com.datotoda.btu_scores.models.Course

class SummaryAdapter: RecyclerView.Adapter<SummaryAdapter.ViewHolder>() {

    private var courseList = emptyList<Course>()

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val itemCourseTitleView: TextView = itemView.findViewById(R.id.summary_item_course_title)
        val itemCourseScoreView: TextView = itemView.findViewById(R.id.summary_item_course_score)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return  ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.summary_item, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = courseList[position]

        holder.itemCourseTitleView.text = currentItem.getTitle()
        holder.itemCourseScoreView.text = currentItem.getScoreString()

    }

    fun setData(newList: List<Course>){
        this.courseList = newList
        notifyDataSetChanged()
    }

    override fun getItemCount() = courseList.size
}