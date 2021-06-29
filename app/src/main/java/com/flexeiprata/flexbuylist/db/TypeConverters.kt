package com.flexeiprata.flexbuylist.db

import androidx.room.TypeConverter
import java.util.*

class TypeConverters {

    @TypeConverter
    fun fromDate(date: Date?) : Long? = date?.time

    @TypeConverter
    fun toDate(mil: Long?) : Date? = mil?.let { Date(it) }

    @TypeConverter
    fun fromBoolean(boolean: Boolean) : Int = if (boolean) 1 else 2

    @TypeConverter
    fun toBoolean(int: Int) : Boolean = int == 1

}