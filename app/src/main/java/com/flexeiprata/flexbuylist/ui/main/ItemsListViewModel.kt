package com.flexeiprata.flexbuylist.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.flexeiprata.flexbuylist.db.ItemInList
import com.flexeiprata.flexbuylist.db.MainRepository
import kotlinx.coroutines.launch

class ItemsListViewModel(private val repository: MainRepository, parentID: Int) : ViewModel() {
    val itemsByParent = repository.getItemsByParentID(parentID).asLiveData()
    val title = repository.getTitle(parentID).asLiveData()

    fun updateItemInList(itemInList: ItemInList) = viewModelScope.launch {
        repository.updateItemInList(itemInList)
    }

    private fun updateEntireItemList(itemInListList : List<ItemInList>) = viewModelScope.launch {
        repository.updateAllItemsInList(itemInListList)
    }

    fun deleteItemInList(itemInList: ItemInList) = viewModelScope.launch {
        repository.deleteItemInList(itemInList)
    }

    fun insertItemInList(i: ItemInList) = viewModelScope.launch {
        repository.insertItemInList(i)
    }

    fun savePositions(itemInListList: MutableList<ItemInList>){
        var index = 0
        for (item in itemInListList) {
            item.listPosition = index++
        }
        val staticList = itemInListList.toList()
        updateEntireItemList(staticList)
    }

}

class ItemsListViewModelFactory(private val repository: MainRepository, val parentID: Int) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ItemsListViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ItemsListViewModel(repository, parentID) as T
        }
        throw IllegalStateException("Unknown ViewModel Class")
    }
}