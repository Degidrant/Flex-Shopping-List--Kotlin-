package com.flexeiprata.flexbuylist.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.Animation
import androidx.fragment.app.FragmentManager
import androidx.navigation.findNavController
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.RecyclerView
import com.flexeiprata.flexbuylist.databinding.BuyMainListAdapterBinding
import com.flexeiprata.flexbuylist.db.BuyList
import com.flexeiprata.flexbuylist.ui.main.BottomSheetAdderFragment
import com.flexeiprata.flexbuylist.ui.main.MainListFragmentDirections
import com.flexeiprata.flexbuylist.ui.main.MainViewModel
import java.text.SimpleDateFormat


class MainListRecyclerAdapter(val list : MutableList<BuyList>, private val parentViewModel: MainViewModel, private val context: Context,
                              private val fragmentManager: FragmentManager, private val lastPosition: Int?) : RecyclerView.Adapter<MainListRecyclerAdapter.InnerViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainListRecyclerAdapter.InnerViewHolder {
        val binding = BuyMainListAdapterBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return InnerViewHolder(binding)
    }


    override fun onBindViewHolder(holder: InnerViewHolder, position: Int) {
        holder.bind(list[position])
        Log.d(DEBU, "Информация [${list[position].name}](Позиция/последняя позиция/позиций всего де-факто/позиция де-юре) $position/$lastPosition/${parentViewModel.lastPosition.value}/${list[position].position}")
        setAnimation(holder, position)
    }

    override fun getItemCount(): Int = list.size

    //анимирование добавления элемента в список
   private fun setAnimation(viewHolder: InnerViewHolder, position: Int) {
        lastPosition?.let {
        if (position > it ) {
            try {
                expandAction(viewHolder.itemView)
            } catch (ex: IllegalStateException) {
                ex.printStackTrace()
            }
        }
        }
    }

    @SuppressLint("SimpleDateFormat")
    inner class InnerViewHolder(val binding: BuyMainListAdapterBinding) : RecyclerView.ViewHolder(binding.root), DragAndDropTouchHelper.ItemTouchHelperViewHolder {
        fun bind(itemList: BuyList) {
            binding.apply {
                imageViewIco.setImageResource(itemList.picAssigned)
                textViewName.text = itemList.name
                textViewDesc.text = itemList.desc
                val sp = PreferenceManager.getDefaultSharedPreferences(context)
                val dateFormat = sp.getString("dateformat", "dd.MM.YY")  //получение выбранного из настроек форматы даты
                val formatter = SimpleDateFormat(dateFormat)

                itemList.dateAssigned?.let {
                    val dateAssignedInString = formatter.format(it)
                    textViewSubDesc.text = dateAssignedInString
                }
                buttonDelete.setOnClickListener {
                    val anno = object : Animation.AnimationListener{
                        override fun onAnimationStart(animation: Animation?) {
                        }

                        //Обновление списка только по окончанию анимации
                        override fun onAnimationEnd(animation: Animation?) {
                            parentViewModel.deleteList(itemList)
                            val listForUpdate = mutableListOf<BuyList>()
                            listForUpdate.addAll(list)
                            listForUpdate.removeAt(adapterPosition)
                            parentViewModel.savePositions(listForUpdate)
                        }

                        override fun onAnimationRepeat(animation: Animation?) {

                        }

                    }
                    val anima = collapse(itemView)
                    anima.setAnimationListener(anno)
                    itemView.startAnimation(anima)
                }
                buttonEdit.setOnClickListener {
                    val bottomSheetAdderFragment = BottomSheetAdderFragment.newInstance(itemList.ID)
                    bottomSheetAdderFragment.show(fragmentManager, "")
                }

                buttonShow.setOnClickListener {
                    val action = MainListFragmentDirections.toItemList(
                        itemList.ID
                    )
                    itemView.findNavController().navigate(action)
                }



            }
        }

        override fun onItemSelected() {

        }

        override fun onItemClear() {

        }
    }


}