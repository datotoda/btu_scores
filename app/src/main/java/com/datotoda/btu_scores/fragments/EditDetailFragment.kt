package com.datotoda.btu_scores.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CompoundButton
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.datotoda.btu_scores.R
import com.datotoda.btu_scores.view_models.CourseViewModel


class EditDetailFragment : Fragment() {

    private val args: EditDetailFragmentArgs by navArgs()
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
        val view = inflater.inflate(R.layout.fragment_edit_detail, container, false)
        val editDetailSaveButton: Button = view.findViewById(R.id.edit_detail_save_button)
        val editDetailSyncSwitch: CompoundButton = view.findViewById(R.id.edit_detail_sync_switch)
        val editDetailTitleEditText: EditText = view.findViewById(R.id.edit_detail_title)
        val editDetailScoreEditText: EditText = view.findViewById(R.id.edit_detail_score)
        val editDetailOrderEditText: EditText = view.findViewById(R.id.edit_detail_order)


        courseViewModel.getCourseById(args.courseId).observe(viewLifecycleOwner) { course ->
            if (course == null) {
                findNavController().popBackStack()
            } else {
                editDetailSyncSwitch.apply {
                    isChecked = course.getSync()
                    isEnabled = course.getSync()
                }
                editDetailTitleEditText.setText(course.getTitle())
                editDetailScoreEditText.setText(course.getScoreString())
                editDetailOrderEditText.setText(course.getOrder().toString())
            }
        }

        editDetailSaveButton.setOnClickListener {
                editDetailSaveButtonClick()
        }
        editDetailSyncSwitch.setOnCheckedChangeListener {
                buttonView, _ -> editDetailSyncSwitchCheckedChange(buttonView)
        }

        return view
    }


    private fun editDetailMenuDeleteButtonClick() {
        makeDelete()
    }

    private fun editDetailMenuSaveButtonClick() {
        makeSave(popBack = true)
    }

    private fun editDetailSyncSwitchCheckedChange(view: CompoundButton) {
        view.text = getString(
            if (view.isChecked) R.string._synchronized else R.string.not_synchronized
        )
        makeUnSync(view)
    }

    private fun editDetailSaveButtonClick() {
        makeSave(popBack = true)
    }


    @Deprecated("Deprecated in Java", ReplaceWith(
        "inflater.inflate(R.menu.edit_detail_menu, menu)",
        "com.datotoda.btu_scores.R"
    )
    )
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.edit_detail_menu, menu)
    }

    override fun onDestroyView() {
        makeSave()
        super.onDestroyView()
    }

    @Deprecated("Deprecated in Java")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.edit_detail_menu_delete-> {
                editDetailMenuDeleteButtonClick()
                true
            }
            R.id.edit_detail_menu_save-> {
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
                courseViewModel.getCourseById(args.courseId).observe(viewLifecycleOwner) { course ->
                    course?.let {
                        courseViewModel.deleteCourse(it)
                        findNavController().popBackStack()
                    }
                }
            }
            .setNegativeButton(getString(R.string.dialog_cancel_button)) { _, _ -> }
            .create()
            .show()
    }

    private fun makeSave(popBack: Boolean = false) {
        fun find(id: Int): EditText = requireView().findViewById(id)
        val title: String = find(R.id.edit_detail_title).text.toString()
        val score: Double? = find(R.id.edit_detail_score).text.toString().toDoubleOrNull()
        val order: Int? = find(R.id.edit_detail_order).text.toString().toIntOrNull()

        courseViewModel.getCourseById(args.courseId).observe(viewLifecycleOwner) { course ->
            course?.apply {
                setTitle(title)
                setScore(score ?: 0.0)
                setOrder(order ?: 0)
                courseViewModel.updateCourse(this)
                if (popBack) {
                    findNavController().popBackStack()
                }
            }
        }
    }

    private fun makeUnSync(view: CompoundButton) {
        if (!(view.isChecked) && view.isEnabled) {
            view.apply {
                text = getString(R.string._synchronized)
                isChecked = true
            }
            AlertDialog
                .Builder(view.context)
                .setTitle(getString(R.string.edit_detail_turn_off_sync_dialog_title))
                .setMessage(getString(R.string.edit_detail_turn_off_sync_dialog_body))
                .setPositiveButton(getString(R.string.edit_detail_turn_off_sync_dialog_positive)) { _, _ ->
                    view.apply {
                        text = getString(R.string.not_synchronized)
                        isEnabled = false
                        isChecked = false
                    }
                    courseViewModel.getCourseById(args.courseId).observe(viewLifecycleOwner) { course ->
                        course?.apply {
                            setSync(false)
                            courseViewModel.updateCourse(this)
                        }
                    }
                }
                .setNegativeButton(getString(R.string.dialog_cancel_button)) { _, _ -> }
                .create()
                .show()
        }
    }
}
