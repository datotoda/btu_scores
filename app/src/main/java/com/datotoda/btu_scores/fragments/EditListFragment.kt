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
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.DividerItemDecoration
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import com.datotoda.btu_scores.R
import com.datotoda.btu_scores.adapters.EditListAdapter
import com.datotoda.btu_scores.apis.BtuParserApiDecoder
import com.datotoda.btu_scores.models.Course
import com.datotoda.btu_scores.view_models.CourseViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import fuel.Fuel
import fuel.get
import kotlinx.coroutines.launch


class EditListFragment : Fragment() {
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
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view =  inflater.inflate(R.layout.fragment_edit_list, container, false)
        val editListRecyclerView: RecyclerView = view.findViewById(R.id.edit_list_recycler_view)
        val editListFloatingActionButton: FloatingActionButton = view.findViewById(R.id.edit_list_floating_action_button)

        editListRecyclerView.apply {
            val editListAdapter = EditListAdapter()
            val linearLayoutManager = LinearLayoutManager(context)

            courseViewModel.readAllData.observe(viewLifecycleOwner) { coursesList ->
                editListAdapter.setData(coursesList)
            }

            adapter = editListAdapter
            layoutManager = linearLayoutManager
            addItemDecoration(
                DividerItemDecoration(
                    context,
                    linearLayoutManager.orientation
                )
            )
            setHasFixedSize(true)
        }

        editListFloatingActionButton.setOnClickListener { editListFloatingActionButtonClick() }

        return view
    }


    private fun editDetailMenuDeleteButtonClick() {
        makeDelete()
    }

    private fun editDetailMenuDownloadButtonClick() {
        makeDownload()
    }

    private fun editDetailMenuSaveButtonClick() {
        findNavController().popBackStack()
    }

    private fun editListFloatingActionButtonClick() {
        makeCreateNewCourse()
    }

    @Deprecated("Deprecated in Java", ReplaceWith(
        "inflater.inflate(R.menu.edit_list_menu, menu)",
        "com.datotoda.btu_scores.R"
    )
    )
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.edit_list_menu, menu)
    }

    @Deprecated("Deprecated in Java")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.edit_list_menu_delete-> {
                editDetailMenuDeleteButtonClick()
                true
            }
            R.id.edit_list_menu_download-> {
                editDetailMenuDownloadButtonClick()
                true
            }
            R.id.edit_list_menu_save-> {
                editDetailMenuSaveButtonClick()
                true
            }
            else -> false
        }
    }


    private fun makeDelete() {
        AlertDialog
            .Builder(requireContext())
            .setTitle(getString(R.string.edit_detail_delete_dialog_title))
            .setMessage(getString(R.string.edit_detail_delete_dialog_body))
            .setPositiveButton(getString(R.string.edit_detail_delete_dialog_positive)) { _, _ ->
                courseViewModel.deleteAllCourses()
            }
            .setNegativeButton(getString(R.string.dialog_cancel_button)) { _, _ -> }
            .create()
            .show()
    }

    private fun makeDownload() {
        if (!isNetworkConnected()) {
            Toast.makeText(context, getString(R.string.no_internet_you_are_offline), Toast.LENGTH_SHORT).show()
            return
        }
        courseViewModel.viewModelScope.launch {
            val r = Fuel.get(getString(R.string.firebase_get_domain_url))
            if (r.statusCode != 200) {
                return@launch
            }
            val domain = r.body.replace("\"", "")

            val request = Fuel.get(url = "https://$domain/${getString(R.string.btu_parser_data_path)}")

            if (request.statusCode == 200) {
                val btuParserApiDecoder = BtuParserApiDecoder(request.body)
                val isCheckedList = BooleanArray(btuParserApiDecoder.keysList().size)
                var maxOrder = 0
                courseViewModel.getMaxOrder().observe(viewLifecycleOwner) { maxOrderLive ->
                    maxOrder = maxOrderLive ?: 0
                }

                fun importCourses(isCheckedList: BooleanArray = BooleanArray(0), importAll: Boolean = false) {

                    btuParserApiDecoder.keysList().filterIndexed { i, _ -> importAll || isCheckedList[i] }.forEach { title ->
                        maxOrder += 100
                        val course = Course(title, 0.0, true, maxOrder)
                        courseViewModel.addCourse(
                            btuParserApiDecoder.updateCourseInstance(
                                course
                            )
                        )
                    }
                }

                AlertDialog
                    .Builder(requireContext())
                    .setTitle(getString(R.string.edit_list_download_dialog_title))
                    .setMultiChoiceItems(btuParserApiDecoder.keysArray(), null) {
                            _, which, isChecked -> isCheckedList[which] = isChecked
                    }
                    .setNeutralButton(getString(R.string.edit_list_download_dialog_neutral)) { _, _ ->
                        importCourses(importAll = true)
                    }
                    .setPositiveButton(getString(R.string.edit_list_download_dialog_positive)) { _, _ ->
                        importCourses(isCheckedList = isCheckedList)
                    }
                    .setNegativeButton(getString(R.string.dialog_cancel_button)) { _, _ -> }
                    .create()
                    .show()

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
                }
            }
        }
    }


    private fun makeCreateNewCourse() {
        courseViewModel.getMaxOrder().observe(viewLifecycleOwner) { maxOrder ->
            val order = maxOrder ?: 0
            courseViewModel.addCourse(
                Course(getString(R.string.new_course_title), 0.0, false, order + 100)
            )
            val editListRecyclerView: RecyclerView = requireView().findViewById(R.id.edit_list_recycler_view)
            editListRecyclerView.adapter?.let { editListRecyclerView.smoothScrollToPosition(it.itemCount) }
        }
    }
}