package com.flexeiprata.flexbuylist.db

import android.app.Application
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

//Класс расширающий Application по Гуглу
class ContApplication : Application() {
    private val applicationScope = CoroutineScope(SupervisorJob())

    val database by lazy {
        MainRoomDatabase.getDatabase(this, applicationScope)
    }

    val repository by lazy {
        MainRepository(database.mainDao())
    }
}