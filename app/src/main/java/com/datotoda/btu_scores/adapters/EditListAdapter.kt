package com.datotoda.btu_scores.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.datotoda.btu_scores.R
import com.datotoda.btu_scores.fragments.EditListFragmentDirections
import com.datotoda.btu_scores.models.Course

class EditListAdapter: RecyclerView.Adapter<EditListAdapter.ViewHolder>() {

    private var courseList = emptyList<Course>()

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val itemCourseTitleView: TextView = itemView.findViewById(R.id.edit_list_item_course_title)
        val itemCourseScoreView: TextView = itemView.findViewById(R.id.edit_list_item_course_score)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return  ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.edit_list_item, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = courseList[position]

        holder.itemCourseTitleView.text = currentItem.getTitle()
        holder.itemCourseScoreView.text = currentItem.getScoreString()

        holder.itemView.setOnClickListener {
            val action = EditListFragmentDirections.actionEditListFragmentToEditDetailFragment(
                currentItem.getId()
            )
            holder.itemView.findNavController().navigate(action)
        }
    }

    fun setData(newList: List<Course>){
        this.courseList = newList
        notifyDataSetChanged()
    }

    override fun getItemCount() = courseList.size
}