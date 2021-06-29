package com.flexeiprata.flexbuylist.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import androidx.core.content.ContextCompat
import androidx.core.widget.ImageViewCompat
import androidx.fragment.app.FragmentManager
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.RecyclerView
import com.flexeiprata.flexbuylist.R
import com.flexeiprata.flexbuylist.databinding.ItemInListAdapterBinding
import com.flexeiprata.flexbuylist.db.ItemInList
import com.flexeiprata.flexbuylist.ui.main.BottomSheetItemListAdderFragment
import com.flexeiprata.flexbuylist.ui.main.ItemsListViewModel
import com.google.android.material.snackbar.Snackbar

class ItemsListRecyclerAdapter(private val list: MutableList<ItemInList>,private val parentViewModel: ItemsListViewModel, private val context: Context, private val parentFragmentManager: FragmentManager, private val snackbarAnchor: View) :
    RecyclerView.Adapter<ItemsListRecyclerAdapter.InnerViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ItemsListRecyclerAdapter.InnerViewHolder {
        val binding = ItemInListAdapterBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return InnerViewHolder(binding)
    }

    override fun onBindViewHolder(holder: InnerViewHolder, position: Int) {
        holder.bind(list[position], this)
    }

    override fun getItemCount(): Int = list.size

    inner class InnerViewHolder(val binding: ItemInListAdapterBinding) : RecyclerView.ViewHolder(binding.root),  DragDropSwipeAdapter.ItemDragDropMoveViewHolder{
        private lateinit var i: ItemInList
        private lateinit var r: ItemsListRecyclerAdapter


        fun bind(i: ItemInList, r: ItemsListRecyclerAdapter){
            this.i = i
            this.r = r
            binding.apply {
                checkBoxNameStatus.text = i.name
                val formattedQTY = BottomSheetItemListAdderFragment.getFormattedCant(i.count, i.countType)
                //реализация пустого TextView, если количество не заполнено
                textViewCant.text = if (formattedQTY != "")
                    String.format(context.getString(R.string.placeholder_cant), formattedQTY, context.getString(i.countType))
                        else ""
                val imageRes = when(i.type){
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

                itemView.setOnClickListener {
                    checkBoxNameStatus.isChecked = !checkBoxNameStatus.isChecked
                }

                checkBoxNameStatus.isChecked = i.status
                changeColors(i.status, context)

                checkBoxNameStatus.setOnCheckedChangeListener { _, isChecked ->
                    i.status = isChecked
                    parentViewModel.updateItemInList(i)
                    changeColors(isChecked, context)
                }
                imageViewIcon.setImageResource(imageRes)
            }
        }

        private fun changeColors(checked: Boolean, context: Context) {
            if (checked) {
               binding.apply {
                    checkBoxNameStatus.setTextColor(ContextCompat.getColor(context, R.color.gray_mild))
                    ImageViewCompat.setImageTintList(imageViewIcon, ColorStateList.valueOf(ContextCompat.getColor(context, R.color.gray_mild)))
                    textViewCant.setTextColor(ContextCompat.getColor(context,R.color.gray_mild))
                }
            }
            else {
                binding.apply {
                    checkBoxNameStatus.setTextColor(ContextCompat.getColor(context, R.color.black))
                    ImageViewCompat.setImageTintList(imageViewIcon, ColorStateList.valueOf(ContextCompat.getColor(context, R.color.red_acc)))
                    textViewCant.setTextColor(ContextCompat.getColor(context,R.color.black))
                }
            }
        }



        override fun onItemSelected() {

        }

        override fun onItemClear() {

        }

        override fun onItemDelete() {
            val listener = object : Animation.AnimationListener{
                override fun onAnimationStart(animation: Animation?) {
                }

                //обновление базы данных только при окончании анимации
                @SuppressLint("ShowToast")
                override fun onAnimationEnd(animation: Animation?) {

                    val message = String.format(context.getString(R.string.undo_message), i.name)

                    val sp = PreferenceManager.getDefaultSharedPreferences(context)
                    val isSnackBarNeeded = sp.getBoolean("undo_delete", true)

                    //создание snackbar для подтверждения удаления
                    if (isSnackBarNeeded) {
                        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG)
                            .setAnchorView(snackbarAnchor)
                            .setAction(context.getString(R.string.undo)) {
                                parentViewModel.insertItemInList(i)
                            }.setBackgroundTint(ContextCompat.getColor(context, R.color.white))
                            .setActionTextColor(ContextCompat.getColor(context, R.color.blue_hard))
                            .setTextColor(ContextCompat.getColor(context, R.color.red_acc))

                            .show()
                    }
                    parentViewModel.deleteItemInList(i)
                    val listForUpdate = mutableListOf<ItemInList>()
                    listForUpdate.addAll(list)
                    listForUpdate.removeAt(adapterPosition)
                    parentViewModel.savePositions(listForUpdate)
                }

                override fun onAnimationRepeat(animation: Animation?) {

                }

            }
            val anima = collapse(itemView)
            anima.setAnimationListener(listener)
            itemView.startAnimation(anima)
        }

        override fun onItemEdit() {
            r.notifyItemChanged(adapterPosition)
            val bottomSheetItemListAdderFragment = BottomSheetItemListAdderFragment.newInstance(i.ID, i.ParentID)
            bottomSheetItemListAdderFragment.show(parentFragmentManager, "")
        }

    }

}