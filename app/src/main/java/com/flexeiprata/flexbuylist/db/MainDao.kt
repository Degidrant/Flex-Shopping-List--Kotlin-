package com.flexeiprata.flexbuylist.db

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface MainDao {


    //BuyList запросы
    @Query("Select * From buy_lists Order By order_all Asc")
    fun getAllLists() : Flow<List<BuyList>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertList(buyList: BuyList)

    @Query("Delete From buy_lists")
    suspend fun deleteALl()

    @Query("Select Max(`order_all`) from buy_lists")
    fun getLargestOrder() : Flow<Int?>

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateAllLists(buyLists: List<BuyList>)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateList(buyList: BuyList)

    @Delete
    suspend fun deleteList(buyList: BuyList)

    @Query("Select * From buy_lists where id = :currentListID")
    fun getListByID(currentListID: Int): Flow<BuyList?>


    //ItemInList запросы
    @Query("Select * From items_table Where parent_ID = :parentID Order By in_list_position Asc")
    fun getItemsByParent(parentID: Int) : Flow<List<ItemInList>>

    @Query("Select * From items_table")
    fun getAllItems() : Flow<List<ItemInList>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItemInList(itemInList: ItemInList)

    @Query("Select * from items_table Where ID = :currentID")
    fun getItemByID(currentID: Int): Flow<ItemInList?>

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateItemInList(itemInList: ItemInList)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateAllItemsList(itemInListList: List<ItemInList>)

    @Delete
    suspend fun deleteItemInList(itemInList: ItemInList)

    @Query("Delete from items_table Where parent_ID = :parentID")
    suspend fun deleteAllChildren(parentID: Int)

    @Query("Select name from buy_lists where id = :id")
    fun getTitle(id: Int): Flow<String>

    @Query("Select Max(`in_list_position`) from items_table")
    fun getItemLargestOrder() : Flow<Int?>


    //RememberedNames запросы (запросы подсказок)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHint(names: RememberedNames)

    @Query("Select * from remembered_names")
    fun getHints() : Flow<List<RememberedNames>>
}