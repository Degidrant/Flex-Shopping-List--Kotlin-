package com.flexeiprata.flexbuylist.ui.main

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.flexeiprata.flexbuylist.MainActivity
import com.flexeiprata.flexbuylist.R
import com.flexeiprata.flexbuylist.adapters.DragAndDropTouchHelper
import com.flexeiprata.flexbuylist.adapters.FIRST_LAUNCH_MAIN
import com.flexeiprata.flexbuylist.adapters.MainListRecyclerAdapter
import com.flexeiprata.flexbuylist.databinding.MainFragmentBinding
import com.flexeiprata.flexbuylist.db.BuyList
import com.flexeiprata.flexbuylist.db.ContApplication
import uk.co.samuelwall.materialtaptargetprompt.MaterialTapTargetPrompt
import java.util.*

class MainListFragment : Fragment() {

    private var _binding: MainFragmentBinding? = null
    private val binding get() = _binding!!
    private var globalList = mutableListOf<BuyList>()
    private var lastPosition : Int? = null


    private val viewModel : MainViewModel by viewModels {
        MainViewModelFactory((activity?.application as ContApplication).repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onResume() {
        super.onResume()
        (activity as MainActivity).supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(false)
            show()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = MainFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            R.id.ItemSettings -> {
                val action = MainListFragmentDirections.toSettings()
                findNavController().navigate(action)
                true
            }
            else ->  super.onOptionsItemSelected(item)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.mainRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
        }

        viewModel.allLists.observe(
            viewLifecycleOwner,
            {
                it?.let {
                    globalList = it.toMutableList()
                    updateUI()
                    lastPosition = globalList.size -1
                }
            }
        )

        val touchHelper = ItemTouchHelper(object : DragAndDropTouchHelper(){
            override fun saveInDatabase() {
                viewModel.savePositions(globalList)
            }

            override fun swapList(startPosition: Int, targetPosition: Int) {
                Collections.swap(globalList, startPosition, targetPosition)
            }

        })
        touchHelper.attachToRecyclerView(binding.mainRecyclerView)

        binding.fabAddMain.setOnClickListener {
            val bottomSheetAdderFragment = BottomSheetAdderFragment.newInstance(-1)

            bottomSheetAdderFragment.show(parentFragmentManager, "")

        }
      showPrompt()
    }

    @SuppressLint("CommitPrefEdits")
    private fun showPrompt(){
        val pref = PreferenceManager.getDefaultSharedPreferences(requireContext())

        if (!pref.getBoolean(FIRST_LAUNCH_MAIN, false)) {
            MaterialTapTargetPrompt.Builder(this)
                .setTarget(binding.fabAddMain)
                .setPrimaryText(getString(R.string.prompt1_primary))
                .setSecondaryText(getString(R.string.prompt1_secondary))
                .setBackButtonDismissEnabled(true)
                .setBackgroundColour(ContextCompat.getColor(requireContext(), R.color.blue_mild))
                .setPromptStateChangeListener { prompt, state ->
                    if (state == MaterialTapTargetPrompt.STATE_FOCAL_PRESSED ||
                            state == MaterialTapTargetPrompt.STATE_NON_FOCAL_PRESSED) {
                        val prefEditor = pref.edit()
                        prefEditor.apply {
                            putBoolean(FIRST_LAUNCH_MAIN, true)
                            apply()
                        }
                    }
                }
                .show()
        }
    }


    private fun updateUI(){
            val adapter = MainListRecyclerAdapter(globalList, viewModel, requireContext(), parentFragmentManager, lastPosition)
            binding.mainRecyclerView.adapter = adapter
            viewModel.lastPosition.observe(
                viewLifecycleOwner,
                {
                    it?.let {
                    viewModel.rememberedPosition = it
                    }
                }
            )
        }
}