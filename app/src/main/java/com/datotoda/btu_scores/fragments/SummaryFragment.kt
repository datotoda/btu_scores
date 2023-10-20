package com.datotoda.btu_scores.fragments

import android.content.Context
import android.net.ConnectivityManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.datotoda.btu_scores.R
import com.datotoda.btu_scores.adapters.SummaryAdapter
import com.datotoda.btu_scores.apis.BtuParserApiDecoder
import com.datotoda.btu_scores.models.Course
import com.datotoda.btu_scores.view_models.CourseViewModel
import fuel.Fuel
import fuel.get
import kotlinx.coroutines.launch
import kotlin.math.round

class SummaryFragment : Fragment() {
    private fun isNetworkConnected(): Boolean {
        val cm = requireActivity().getSystemService(Context.CONNECTIVITY_SERVICE)
        return cm is ConnectivityManager && cm.activeNetworkInfo != null && cm.activeNetworkInfo!!.isConnected
    }

    private val courseViewModel: CourseViewModel by lazy {
        ViewModelProvider(this)[CourseViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        makeRefresh(showToast = false)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_summary, container, false)
        val summaryRecyclerView: RecyclerView = view.findViewById(R.id.summary_recycler_view)
        val summaryTextView: TextView = view.findViewById(R.id.summary_text_view)
        val summaryLinearLayout: LinearLayout = view.findViewById(R.id.summary_linear_layout)

        summaryRecyclerView.apply {
            val summaryAdapter = SummaryAdapter()
            val linearLayoutManager = LinearLayoutManager(context)

            courseViewModel.readAllData.observe(viewLifecycleOwner) { coursesList ->
                summaryAdapter.setData(coursesList)
                summaryLinearLayout.isVisible = coursesList.isNotEmpty()
                summaryTextView.text = (
                        if (coursesList.isNotEmpty())
                            round(
                                coursesList.map {
                                        course: Course -> course.getScore()
                                }.average() * 100000
                            ) / 100000
                        else 0
                ).toString()
            }

            adapter = summaryAdapter
            layoutManager = linearLayoutManager
            setHasFixedSize(true)
        }

        return view
    }


    private fun summaryMenuRefreshButtonClick() {
        makeRefresh(showToast = true)
    }

    private fun summaryMenuEditButtonClick() {
        findNavController().navigate(R.id.action_summaryFragment_to_editListFragment)
    }


    @Deprecated("Deprecated in Java", ReplaceWith(
        "inflater.inflate(R.menu.summary_menu, menu)",
        "com.datotoda.btu_scores.R"
    )
    )
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.summary_menu, menu)
    }

    @Deprecated("Deprecated in Java")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.summary_menu_refresh -> {
                summaryMenuRefreshButtonClick()
                true
            }
            R.id.summary_menu_edit-> {
                summaryMenuEditButtonClick()
                true
            }
            else -> false
        }
    }

    private fun makeRefresh(showToast: Boolean = false) {
        if (!isNetworkConnected()) {
            if (showToast) {
                Toast.makeText(context, getString(R.string.no_internet_you_are_offline), Toast.LENGTH_SHORT).show()
            }
            return
        }
        courseViewModel.viewModelScope.launch {
            if (showToast) {
                Toast.makeText(context, getString(R.string.summary_refreshing), Toast.LENGTH_SHORT).show()
                Fuel.get(getString(R.string.btu_parser_check_url))
            }

            val request = Fuel.get(getString(R.string.btu_parser_data_url))

            if (request.statusCode == 200) {
                val btuParserApiDecoder = BtuParserApiDecoder(request.body)

                courseViewModel.getSyncCourses().observe(viewLifecycleOwner) { coursesList ->
                    coursesList.forEach { course ->
                        course?.apply {
                            courseViewModel.updateCourse(
                                btuParserApiDecoder.updateCourseInstance(
                                    this
                                )
                            )
                        }
                    }
                    if (showToast) {
                        Toast.makeText(context,
                            getString(R.string.summary_end_refresh), Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                if (showToast){
                    Toast.makeText(context,
                        getString(R.string.summary_refresh_is_unavailable), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}