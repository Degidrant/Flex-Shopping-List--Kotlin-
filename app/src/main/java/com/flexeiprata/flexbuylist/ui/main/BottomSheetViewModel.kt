package com.flexeiprata.flexbuylist.ui.main

import androidx.lifecycle.*
import com.flexeiprata.flexbuylist.db.BuyList
import com.flexeiprata.flexbuylist.db.MainRepository
import kotlinx.coroutines.launch

class BottomSheetViewModel (private val repository: MainRepository, currentListID: Int) : ViewModel() {

    val currentListData : LiveData<BuyList?> = repository.getListByID(currentListID).asLiveData()
    val lastPosition = repository.getLastPosition().asLiveData()


    fun insert(buyList: BuyList) = viewModelScope.launch {
        repository.insert(buyList)
    }

    fun update(buyList: BuyList) = viewModelScope.launch {
        repository.updateList(buyList)
    }

}

class BottomSheetViewModelFactory(private val repository: MainRepository, private val buyListID: Int) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BottomSheetViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return BottomSheetViewModel(repository, buyListID) as T
        }
        throw IllegalStateException("Unknown ViewModel Class")
    }
}