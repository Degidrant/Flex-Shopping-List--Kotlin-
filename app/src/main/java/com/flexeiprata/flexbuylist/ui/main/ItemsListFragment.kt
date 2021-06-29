package com.flexeiprata.flexbuylist.ui.main

import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.flexeiprata.flexbuylist.MainActivity
import com.flexeiprata.flexbuylist.R
import com.flexeiprata.flexbuylist.adapters.*
import com.flexeiprata.flexbuylist.databinding.ItemsListFragmentBinding
import com.flexeiprata.flexbuylist.db.ContApplication
import com.flexeiprata.flexbuylist.db.ItemInList
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import uk.co.samuelwall.materialtaptargetprompt.MaterialTapTargetPrompt
import uk.co.samuelwall.materialtaptargetprompt.extras.focals.RectanglePromptFocal
import java.util.*

class ItemsListFragment : Fragment() {

    private var _binding: ItemsListFragmentBinding? = null
    private val binding get() = _binding!!
    private var parentID = 0
    private var globalList = mutableListOf<ItemInList>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            parentID = it.getInt("parentID")
        }
    }

    private val viewModel : ItemsListViewModel by viewModels {
        ItemsListViewModelFactory((activity?.application as ContApplication).repository, parentID)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ItemsListFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.recyclerViewItemsList.apply {
            layoutManager = LinearLayoutManager(requireContext())
            addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))
        }

        val touchHelper = ItemTouchHelper(object : DragDropSwipeAdapter(requireContext()){

            override fun swapList(startPosition: Int, targetPosition: Int) {
                Collections.swap(globalList, startPosition, targetPosition)
            }

            override fun saveInDatabase() {
                lifecycleScope.launch {
                    delay(200L)
                    viewModel.savePositions(globalList)
                }
            }

        })
        touchHelper.attachToRecyclerView(binding.recyclerViewItemsList)

        viewModel.itemsByParent.observe(
            viewLifecycleOwner,
            {

                it?.let {
                    if (it.size != globalList.size || compareUpdating(
                            it.toMutableList(),
                            globalList
                        )
                    ) updateUI(it)
                    globalList = it.toMutableList()
                }
            }
        )

        binding.apply {
            fabAddMain.setOnClickListener {
                val dialog = BottomSheetItemListAdderFragment.newInstance(0, parentID)
                dialog.show(parentFragmentManager, "")
            }
            fabAddSort.setOnClickListener {
                val sortList = mutableListOf<ItemInList>()
                sortList.addAll(globalList)
                sortList.sortWith(compareBy({ it.status }, { it.type }, { it.name }))
                viewModel.savePositions(sortList)
            }
        }

        viewModel.title.observe(
            viewLifecycleOwner,
            {
                (activity as MainActivity).supportActionBar?.title = it
            }
        )

    }

    @SuppressLint("CommitPrefEdits")
    private fun showPrompt(){
        val pref = PreferenceManager.getDefaultSharedPreferences(requireContext())

        if (!pref.getBoolean(FIRST_LAUNCH_LIST, false)) {

            MaterialTapTargetPrompt.Builder(this)
                .setTarget(binding.fabAddSort)
                .setPrimaryText(getString(R.string.prompt4_primary))
                .setSecondaryText(getString(R.string.prompt4_secondary))
                .setBackButtonDismissEnabled(true)
                .setBackgroundColour(ContextCompat.getColor(requireContext(), R.color.blue_mild))
                .setPromptStateChangeListener { _, state ->
                    if (state == MaterialTapTargetPrompt.STATE_NON_FOCAL_PRESSED||
                        state == MaterialTapTargetPrompt.STATE_FOCAL_PRESSED) {
                        val prefEditor = pref.edit()
                        prefEditor.apply {
                            putBoolean(FIRST_LAUNCH_LIST, true)
                            apply()
                        }
                        if (!pref.getBoolean(FIRST_LAUNCH_ELEMENT, false)) showSecondPrompt(pref)
                    }
                }.show()
        }
    }

    @SuppressLint("CommitPrefEdits")
    private fun showSecondPrompt(pref: SharedPreferences) {
        val recyclerView = binding.recyclerViewItemsList.findViewHolderForAdapterPosition(0)?.itemView
        MaterialTapTargetPrompt.Builder(this)
            .setTarget(recyclerView)
            .setPrimaryText(getString(R.string.prompt5_primary))
            .setSecondaryText(getString(R.string.prompt5_secondary))
            .setBackButtonDismissEnabled(true)
            .setBackgroundColour(ContextCompat.getColor(requireContext(), R.color.blue_mild))
            .setPromptFocal(RectanglePromptFocal())
            .setPromptStateChangeListener { _, state ->
                if (state == MaterialTapTargetPrompt.STATE_NON_FOCAL_PRESSED||
                    state == MaterialTapTargetPrompt.STATE_FOCAL_PRESSED) {
                    val prefEditor = pref.edit()
                    prefEditor.apply {
                        putBoolean(FIRST_LAUNCH_ELEMENT, true)
                        apply()
                    }
                }
            }.show()
    }

    private fun compareUpdating(list1: MutableList<ItemInList>, list2: MutableList<ItemInList>): Boolean {
        var returnVal = false
        if (list2.size == 0) return true
            for (i in 0 until list1.size)
                if (list1[i].name != list2[i].name ||
                    list1[i].count != list2[i].count ||
                    list1[i].countType != list2[i].countType ||
                    list1[i].type != list2[i].type
                ) returnVal = true
        return returnVal
    }

    private fun updateUI(list: List<ItemInList>) {
        val adapter = ItemsListRecyclerAdapter(list.toMutableList(), viewModel, requireContext(), parentFragmentManager, binding.fabAddMain)
        Log.d(DEBU, "ITEM COUNT = ${adapter.itemCount}")
        binding.recyclerViewItemsList.apply {
            this.adapter = adapter
            showPrompt()
        }
    }

}