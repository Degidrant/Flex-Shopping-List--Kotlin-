package com.flexeiprata.flexbuylist.ui.main

import androidx.lifecycle.*
import com.flexeiprata.flexbuylist.db.ItemInList
import com.flexeiprata.flexbuylist.db.MainRepository
import com.flexeiprata.flexbuylist.db.RememberedNames
import kotlinx.coroutines.launch

class BottomSheetItemListAdderViewModel(private val repository: MainRepository, currentID: Int) : ViewModel() {

    var mutableLiveDataHints = MutableLiveData<String>()
    val lastPosition = repository.getItemLastPosition().asLiveData()
    val itemInList = repository.getItemById(currentID).asLiveData()
    val allHints = repository.getHints().asLiveData()
    val liveDataHints : LiveData<String> get() = mutableLiveDataHints

    fun insert(i: ItemInList) = viewModelScope.launch {
        repository.insertItemInList(i)
    }

    fun update(i: ItemInList) = viewModelScope.launch {
        repository.updateItemInList(i)
    }

    fun insertHint(hint: RememberedNames) = viewModelScope.launch {
        repository.insertHint(hint)
    }

}

class BottomSheetItemListAdderViewModelFactory(private val repository: MainRepository, private val currentID: Int) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BottomSheetItemListAdderViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return BottomSheetItemListAdderViewModel(repository, currentID) as T
        }
        throw IllegalStateException("Unknown ViewModel Class")
    }
}