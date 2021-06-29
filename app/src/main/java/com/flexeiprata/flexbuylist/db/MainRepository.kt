package com.flexeiprata.flexbuylist.db

import androidx.annotation.WorkerThread
import kotlinx.coroutines.flow.Flow

class MainRepository(private val mainDao: MainDao) {

    //Main (BuyList dep.)
    val allBuyLists: Flow<List<BuyList>> = mainDao.getAllLists()
    fun getListByID(currentListID: Int): Flow<BuyList?> = mainDao.getListByID(currentListID)
    fun getLastPosition(): Flow<Int?> = mainDao.getLargestOrder()

    @WorkerThread
    suspend fun insert(buyList: BuyList) = mainDao.insertList(buyList)

    @WorkerThread
    suspend fun updateEntireList(currentList: List<BuyList>?) = currentList?.let { mainDao.updateAllLists(it) }

    @WorkerThread
    suspend fun deleteList(buyList: BuyList) = mainDao.deleteList(buyList)

    @WorkerThread
    suspend fun clearList(parentID: Int) = mainDao.deleteAllChildren(parentID)

    @WorkerThread
    suspend fun updateList(buyList: BuyList) = mainDao.updateList(buyList)


    //Main (ItemInList dep.)
    fun getItemsByParentID(parentID: Int) = mainDao.getItemsByParent(parentID)
    fun getItemById(currentID: Int): Flow<ItemInList?> = mainDao.getItemByID(currentID)
    fun getTitle(parentID: Int): Flow<String> = mainDao.getTitle(parentID)
    fun getItemLastPosition() = mainDao.getItemLargestOrder()

    @WorkerThread
    suspend fun insertItemInList(itemInList: ItemInList) = mainDao.insertItemInList(itemInList)

    @WorkerThread
    suspend fun updateItemInList(itemInList: ItemInList) = mainDao.updateItemInList(itemInList)

    @WorkerThread
    suspend fun updateAllItemsInList(itemInListList: List<ItemInList>) = mainDao.updateAllItemsList(itemInListList)

    @WorkerThread
    suspend fun deleteItemInList(itemInList: ItemInList) = mainDao.deleteItemInList(itemInList)


    //SubMain (ItemInList - родитель, RememberedName dep.)
    fun getHints() = mainDao.getHints()

    @WorkerThread
    suspend fun insertHint(hint: RememberedNames) = mainDao.insertHint(hint)
}