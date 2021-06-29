package com.flexeiprata.flexbuylist.ui.main

import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ArrayAdapter
import android.widget.FrameLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.ContextCompat
import androidx.core.view.get
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.preference.PreferenceManager
import com.flexeiprata.flexbuylist.R
import com.flexeiprata.flexbuylist.adapters.DEBU
import com.flexeiprata.flexbuylist.adapters.hideKeyboardFrom
import com.flexeiprata.flexbuylist.databinding.ItemsAdderBottomSheetBinding
import com.flexeiprata.flexbuylist.db.ContApplication
import com.flexeiprata.flexbuylist.db.ItemInList
import com.flexeiprata.flexbuylist.db.RememberedNames
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.chip.Chip
import java.lang.Exception
import java.lang.NumberFormatException

class BottomSheetItemListAdderFragment : BottomSheetDialogFragment() {

    private var _binding: ItemsAdderBottomSheetBinding? = null
    private val binding get() = _binding!!

    private lateinit var itemInList: ItemInList
    private lateinit var allHintsList: List<RememberedNames>
    private lateinit var cantArray: List<Int>
    private lateinit var typeArray: List<Int>
    private var usefulHints = mutableListOf<RememberedNames>()
    private var lastPosition = -1

    private val viewModel : BottomSheetItemListAdderViewModel by viewModels{
        val currentID = arguments?.getInt(CURRENT_ID, 0) as Int
        BottomSheetItemListAdderViewModelFactory((activity?.application as ContApplication).repository, currentID)
    }

    companion object {
        const val CURRENT_ID = "current_id"
        const val PARENT_ID = "parent_id"
        fun newInstance(currentID: Int, parentID: Int) : BottomSheetItemListAdderFragment {
            val args = Bundle().apply {
                putSerializable(CURRENT_ID, currentID)
                putSerializable(PARENT_ID, parentID)
            }
            return BottomSheetItemListAdderFragment().apply {
                arguments = args
            }
        }
        fun getFormattedCant(count: Float, countType: Int): String {
            if (count == 0F) return ""
            return when (countType) {
                R.string.cant_type_pc -> count.toInt().toString()
                R.string.cant_type_kg -> count.toString()
                R.string.cant_type_g -> count.toInt().toString()
                R.string.cant_type_l -> count.toString()
                else -> count.toString()
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val parentID = arguments?.getInt(PARENT_ID, 0) as Int
        val currentID = arguments?.getInt(CURRENT_ID, 0) as Int
        itemInList = ItemInList(currentID, parentID, 0, "", 0F, R.string.cant_type_pc, R.string.type_none, false)
        allHintsList = listOf()
    }

    override fun onResume() {
        super.onResume()
        setMeasureAdapter(itemInList.countType)
        setTypeAdapter(itemInList.type)
    }

    private fun setTypeAdapter(type: Int) {

        val sp = PreferenceManager.getDefaultSharedPreferences(context)
        val typeArrayOption = sp.getString("group_pref", "4")?.toInt()

        typeArray = when(typeArrayOption) {
            4 -> listOf(R.string.type_none, R.string.type_product, R.string.type_veg_fruit, R.string.type_cereal, R.string.type_milk, R.string.type_meat,
                R.string.type_snacks, R.string.type_sweets, R.string.type_drinks, R.string.type_non_food)
            3 -> listOf(R.string.type_none, R.string.type_product, R.string.type_veg_fruit, R.string.type_cereal, R.string.type_milk, R.string.type_meat, R.string.type_sweets, R.string.type_drinks)
            2 -> listOf(R.string.type_none, R.string.type_product, R.string.type_veg_fruit, R.string.type_drinks)
            1 -> listOf(R.string.type_none, R.string.type_product)
            else -> listOf(R.string.type_none, R.string.type_product)
        }
        val typeArrayInStrings = mutableListOf<String>()
        for (i in typeArray) typeArrayInStrings.add(getString(i))


        val typeAdapter = ArrayAdapter(requireContext(), R.layout.icon_text_adapter, typeArrayInStrings)
        binding.apply {

            menuTypeText.setText(getString(type))
            changeIcons(type)
            menuTypeText.setAdapter(typeAdapter)
        }
    }

    private fun setMeasureAdapter(countType: Int) {
        cantArray = listOf(R.string.cant_type_pc, R.string.cant_type_kg, R.string.cant_type_g, R.string.cant_type_l)
        val cantArrayInStrings = mutableListOf<String>()
        for (i in cantArray) cantArrayInStrings.add(getString(i))
        val cantAdapter = ArrayAdapter(requireContext(), R.layout.icon_text_adapter, cantArrayInStrings)
        binding.apply {
            menuMeasureText.setText(getString(countType))
            menuMeasureText.setAdapter(cantAdapter)
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ItemsAdderBottomSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.itemInList.observe(
            viewLifecycleOwner,
            {
                it?.let {
                    itemInList = it
                    adaptUI(it)
                }
            }
        )

        viewModel.liveDataHints.observe(
            viewLifecycleOwner,
            {
                binding.chipGroupHelper.removeAllViews()
                usefulHints.removeAll(usefulHints)
                for (i in allHintsList){
                    if (!it.equals("") && i.string.contains(it, ignoreCase = true)) {
                        val chip = Chip(requireContext(), null, R.attr.CustomChipChoice)
                        chip.text = i.string
                        usefulHints.add(i)
                        binding.chipGroupHelper.addView(chip)
                    }
                }
                val constSet = ConstraintSet()
                constSet.clone(requireContext(), R.layout.items_adder_bottom_sheet)
                if (usefulHints.size > 0) {
                    constSet.connect(R.id.textHolderName, ConstraintSet.TOP, R.id.scrollForChips, ConstraintSet.BOTTOM, 0)
                }
                constSet.applyTo(binding.root)
                if (!binding.textInputName.isFocused)
                for (i in 0 until binding.chipGroupHelper.childCount){
                    val chip = binding.chipGroupHelper.get(i) as Chip
                    if (chip.text == it) chip.isChecked = true
                }
            }
        )

        viewModel.allHints.observe(
            viewLifecycleOwner,
            {
                it?.let {
                    allHintsList = it
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
            if (itemInList.ID > 0) {
                fabAdder.setImageDrawable(
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.ic_edit
                    )
                )
            }


            chipGroupHelper.setOnCheckedChangeListener { _, checkedId ->
                val chip = chipGroupHelper.findViewById<Chip>(checkedId)
                val chipText = chip?.text?.toString() ?: ""
                var usedHint : RememberedNames? = null
               for (i in usefulHints) {
                    if (i.string == chipText && chipText != textInputName.text.toString()) {
                        usedHint = i
                        textInputName.clearFocus()
                        break
                    }
                }
                usedHint?.let {
                    textInputName.apply {
                        setText(it.string)
                    }
                    menuMeasureText.apply {
                        setText(it.cantType)
                        clearFocus()
                    }
                    itemInList.countType = it.cantType
                    menuTypeText.setText(it.type)
                    changeIcons(it.type)
                }
                hideKeyboardFrom(requireContext(), root)
                setMeasureAdapter(itemInList.countType)
                setTypeAdapter(itemInList.type)
                }


            textInputName.addTextChangedListener {
                viewModel.mutableLiveDataHints.value = it?.toString()
            }

            textInputQuantity.setText(getFormattedCant(itemInList.count, itemInList.countType))
            changeIcons(itemInList.type)
            menuTypeText.setOnItemClickListener { _, _, position, _ ->
               val text = typeArray[position]
                itemInList.type = text
                changeIcons(text)
            }

            textInputQuantity.addTextChangedListener {
                try {
                    itemInList.count = it.toString().toFloat()
                }
                catch (ex : Exception){
                    ex.printStackTrace()
                }
            }

            textInputName.addTextChangedListener {
                itemInList.name = it.toString()
            }

            menuMeasureText.setOnItemClickListener { _, _, position, _ ->
                val text = cantArray[position]

                try {
                    textInputQuantity.setText(getFormattedCant(textInputQuantity.text.toString().toFloat(), text))
                }
                catch (ex: NumberFormatException) {
                    ex.printStackTrace()
                }
                itemInList.countType = text
            }

            menuMeasureText.setOnFocusChangeListener { _, hasFocus ->
                if (hasFocus) {
                    hideKeyboardFrom(requireContext(), binding.root)
                }
            }
            textInputName.setOnKeyListener { v, keyCode, _ ->
                if (keyCode == 66) menuMeasureHolder.requestFocus()
                true
            }

            menuTypeText.setOnFocusChangeListener { _, hasFocus ->
                if (hasFocus) hideKeyboardFrom(requireContext(), binding.root)
            }

            fabAdder.setOnClickListener {
                if (itemInList.ID == 0) {
                    itemInList.listPosition = lastPosition + 1
                    Log.d(DEBU, "Trying to insert in position ${itemInList.listPosition}")
                    viewModel.insert(itemInList)
                }
                else {
                    viewModel.update(itemInList)
                    Log.d(DEBU, "Trying to update ITEM ID = ${itemInList.ID}, PARENT ID = ${itemInList.ParentID}")
                }

                val hint = RememberedNames(itemInList.name, itemInList.type, itemInList.countType)
                viewModel.insertHint(hint)
                this@BottomSheetItemListAdderFragment.dismiss()
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

    }

    private fun changeIcons(res: Int){
        val iconAdder = when(res){
            R.string.type_veg_fruit -> R.drawable.ad_fruit
            R.string.type_cereal -> R.drawable.ad_cereal
            R.string.type_milk -> R.drawable.ad_milk
            R.string.type_meat -> R.drawable.ad_meat
            R.string.type_snacks -> R.drawable.ad_snacks
            R.string.type_sweets -> R.drawable.ad_sweet
            R.string.type_drinks -> R.drawable.ad_drink
            R.string.type_none -> R.drawable.ad_univ
            R.string.type_product -> R.drawable.ad_products
            R.string.type_non_food -> R.drawable.ad_nonfood

            else -> R.drawable.ad_univ
        }

        val mainRes = when(res){
            R.string.type_veg_fruit -> R.drawable.tb_veggies
            R.string.type_cereal -> R.drawable.tb_cereal
            R.string.type_milk -> R.drawable.tb_milk
            R.string.type_meat -> R.drawable.tb_meat
            R.string.type_snacks -> R.drawable.tb_snacks
            R.string.type_sweets -> R.drawable.tb_sweets
            R.string.type_drinks -> R.drawable.tb_drinks
            R.string.type_none -> R.drawable.tb_none
            R.string.type_product -> R.drawable.tb_products
            R.string.type_non_food -> R.drawable.tb_non_food

            else -> R.drawable.tb_none
        }

        binding.apply {
            imageViewMain.setImageResource(mainRes)
            imageViewMain.startAnimation(AnimationUtils.loadAnimation(context, R.anim.fader))
            menuTypeHolder.setStartIconDrawable(iconAdder)
            itemInList.type = res
        }
    }

    private fun adaptUI(i: ItemInList) {
        binding.apply {
            setTypeAdapter(i.type)
            setMeasureAdapter(i.countType)
            textInputName.setText(i.name)
            textInputQuantity.setText(getFormattedCant(i.count, i.countType))
        }

    }


}