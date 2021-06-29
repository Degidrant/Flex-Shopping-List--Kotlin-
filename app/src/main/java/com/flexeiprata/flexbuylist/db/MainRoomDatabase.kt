package com.flexeiprata.flexbuylist.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.flexeiprata.flexbuylist.R
import com.flexeiprata.flexbuylist.adapters.ITEM_PRIME
import com.flexeiprata.flexbuylist.adapters.LIST_PRIME
import com.flexeiprata.flexbuylist.adapters.LIST_PRIME_DESC
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.util.*

@Database(entities = arrayOf(BuyList::class, ItemInList::class, RememberedNames::class), version = 1, exportSchema = false)
@TypeConverters(com.flexeiprata.flexbuylist.db.TypeConverters::class)
abstract class MainRoomDatabase : RoomDatabase() {

    abstract fun mainDao(): MainDao

    //синглтон по Гуглу
    companion object {
        @Volatile
        private var INSTANCE: MainRoomDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): MainRoomDatabase {
            return INSTANCE ?: synchronized(this){
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MainRoomDatabase::class.java,
                    "main_buy_list_database"
                )
                    .addCallback(MainDatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }

    }

    private class MainDatabaseCallback(private val scope: CoroutineScope) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let {
                scope.launch {
                    populateDatabase(it.mainDao())
                }
            }
        }

        suspend fun populateDatabase(dao: MainDao) {
            dao.deleteALl()

            dao.insertList(
                BuyList(0, 0, LIST_PRIME, LIST_PRIME_DESC,
            Calendar.getInstance().time, Calendar.getInstance().time, R.drawable.cardveggi)
            )

            dao.insertItemInList(ItemInList(0, 1, 0, ITEM_PRIME, 1F, R.string.cant_type_pc, R.string.type_none, false))
        }
    }
}