package com.flexeiprata.flexbuylist.ui.main

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.preference.PreferenceManager
import com.flexeiprata.flexbuylist.R
import com.flexeiprata.flexbuylist.adapters.FIRST_LAUNCH_BOTTOM_ADDER
import com.flexeiprata.flexbuylist.databinding.ModalBottomListBinding
import com.flexeiprata.flexbuylist.db.BuyList
import com.flexeiprata.flexbuylist.db.ContApplication
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.datepicker.MaterialDatePicker
import uk.co.samuelwall.materialtaptargetprompt.MaterialTapTargetPrompt
import java.util.*

class BottomSheetAdderFragment : BottomSheetDialogFragment() {

    private var _binding: ModalBottomListBinding? = null
    private val binding get() = _binding!!

    private var squareImageResource = R.drawable.cardveggi
    private var lastPosition: Int = -1

    private lateinit var buyList: BuyList

    private val viewModel : BottomSheetViewModel by viewModels {
        val buyListID = arguments?.getSerializable(ID_SERIALIZABLE) as Int
        BottomSheetViewModelFactory((activity?.application as ContApplication).repository, buyListID)
    }

    companion object{
        const val ID_SERIALIZABLE = "buy_list_id"
        fun newInstance(buyListID: Int) : BottomSheetAdderFragment{
            val args = Bundle().apply {
                putSerializable(ID_SERIALIZABLE, buyListID)
            }
            return BottomSheetAdderFragment().apply {
                arguments = args
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //стартовый список покупок
        buyList = BuyList(0, 0, "", "", Calendar.getInstance().time, null, R.drawable.cardveggi)
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ModalBottomListBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.currentListData.observe(
            viewLifecycleOwner,
            {
                it?.let {
                    buyList = it
                    adaptUI()
                }
            }
        )

        viewModel.lastPosition.observe(
            viewLifecycleOwner,
            {
                it?.let {
                    lastPosition = it
                }
            }
        )

        binding.apply {
            textInputModalTitle.addTextChangedListener {
                it?.let{
                    buyList.name = it.toString()
                }
            }

            textInputModalDesc.addTextChangedListener{
                it?.let {
                    buyList.desc = it.toString()
                }
            }

        }


        binding.apply {
            chipGroupAdder.setOnCheckedChangeListener { group, checkedId ->
                val imageResource =
                when (checkedId) {
                    binding.chip1.id -> {
                        squareImageResource = R.drawable.shopiconlist
                        R.drawable.shopfullpic
                    }
                    binding.chip2.id -> {
                        squareImageResource = R.drawable.cardveggi
                        R.drawable.veggisfull
                    }
                    binding.chip3.id -> {
                        squareImageResource = R.drawable.toolsicon
                        R.drawable.toolsfullpic
                    }
                    else -> {
                        squareImageResource = R.drawable.cardveggi
                        R.drawable.veggisfull
                    }
                }
                val anima = AnimationUtils.loadAnimation(context, com.google.android.material.R.anim.abc_fade_in)
                imageViewAdder.setImageResource(imageResource)
                imageViewAdder.startAnimation(anima)
                buyList.picAssigned = squareImageResource
            }
        }

        binding.apply {
            fabFinalAdd.setOnClickListener {
                if (id <= 0) {
                    buyList.position = lastPosition + 1
                    viewModel.insert(buyList)
                }
                else {
                    viewModel.update(buyList)
                }
                this@BottomSheetAdderFragment.dismiss()
            }
            fabCalendar.setOnClickListener {
                val datePicker =
                    MaterialDatePicker.Builder.datePicker()
                        .setTitleText(requireContext().getString(R.string.select_date))
                        .setSelection(buyList.dateAssigned?.time ?: MaterialDatePicker.todayInUtcMilliseconds())
                        .build()
                datePicker.addOnPositiveButtonClickListener {
                    buyList.dateAssigned = Date(it)
                }
                datePicker.show(parentFragmentManager, "")
            }
        }

        //при изменение ориентации необходимо для того, чтобы диалоговое окно выдвинулось полноценно
        if (activity?.resources?.configuration?.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            dialog?.setOnShowListener { dialog ->
                val d = dialog as BottomSheetDialog
                val bottomSheet = d.findViewById<View>(R.id.design_bottom_sheet) as FrameLayout
                val bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            }
        }
        showPrompt()
    }

    @SuppressLint("CommitPrefEdits")
    private fun showPrompt(){
        val pref = PreferenceManager.getDefaultSharedPreferences(requireContext())
        if (!pref.getBoolean(FIRST_LAUNCH_BOTTOM_ADDER, false)) {
            MaterialTapTargetPrompt.Builder(this)
                .setTarget(binding.fabCalendar)
                .setPrimaryText(getString(R.string.prompt2_primary))
                .setSecondaryText(getString(R.string.prompt2_secondary))
                .setBackButtonDismissEnabled(true)
                .setBackgroundColour(ContextCompat.getColor(requireContext(), R.color.blue_mild))
                .setPromptStateChangeListener { prompt, state ->
                    if (state == MaterialTapTargetPrompt.STATE_FOCAL_PRESSED ||
                        state == MaterialTapTargetPrompt.STATE_NON_FOCAL_PRESSED) {
                        val prefEditor = pref.edit()
                        prefEditor.apply {
                            putBoolean(FIRST_LAUNCH_BOTTOM_ADDER, true)
                            apply()
                        }
                    }
                }
                .show()
        }
    }

    private fun adaptUI() {

        binding.apply {
            buyList.apply {
                textInputModalTitle.setText(this.name)
                textInputModalDesc.setText(this.desc)
                squareImageResource = this.picAssigned
                fabFinalAdd.setImageResource(R.drawable.ic_edit)
            }
            var imageRes = R.drawable.veggisfull
            chipGroupAdder.clearCheck()
            when(squareImageResource) {
                R.drawable.cardveggi -> {
                    imageRes = R.drawable.veggisfull
                    chip2.isChecked = true
                }
                R.drawable.toolsicon -> {
                    imageRes = R.drawable.toolsfullpic
                    chip3.isChecked = true
                }
                R.drawable.shopiconlist -> {
                    imageRes =R.drawable.shopfullpic
                    chip1.isCheckable = true
                }
            }

            imageViewAdder.setImageResource(imageRes)
        }
    }
}