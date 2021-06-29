package com.flexeiprata.flexbuylist.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "remembered_names")
data class RememberedNames(
    @PrimaryKey @ColumnInfo(name = "string") val string: String, //необходимо для того, чтобы переписывать подсказки в случае изменения
    @ColumnInfo(name = "type") val type: Int,
    @ColumnInfo(name = "cant_type") val cantType: Int
)
