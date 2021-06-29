package com.flexeiprata.flexbuylist.ui.main

import androidx.lifecycle.*
import com.flexeiprata.flexbuylist.db.BuyList
import com.flexeiprata.flexbuylist.db.MainRepository
import kotlinx.coroutines.launch

class MainViewModel(private val repository: MainRepository) : ViewModel() {
    var rememberedPosition: Int = 100000 //большое значение для инициализации
    val allLists: LiveData<List<BuyList>> = repository.allBuyLists.asLiveData()

    fun insert(buyList: BuyList) = viewModelScope.launch {
        repository.insert(buyList)
    }

    fun updateEntireList(stlist: List<BuyList>?) = viewModelScope.launch {
        repository.updateEntireList(stlist)
    }

    val lastPosition = repository.getLastPosition().asLiveData()



    fun deleteList(buyList: BuyList) = viewModelScope.launch {
        val id = buyList.ID
        repository.deleteList(buyList)
        repository.clearList(id)
    }

    fun savePositions(buyLists: MutableList<BuyList>){
        var index = 0
        for (i in buyLists) {
            i.position = index++
        }
        val staticList = buyLists.toList()
        updateEntireList(staticList)
        rememberedPosition = (buyLists.size - 1)
    }

}

class MainViewModelFactory(private val repository: MainRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(repository) as T
        }
        throw IllegalStateException("Unknown ViewModel Class")
    }
}