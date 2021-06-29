package com.flexeiprata.flexbuylist.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "items_table")
data class ItemInList(
    @PrimaryKey(autoGenerate = true) val ID: Int,
    @ColumnInfo(name = "parent_ID") val ParentID: Int,
    @ColumnInfo(name = "in_list_position") var listPosition: Int,
    @ColumnInfo(name = "item_name") var name: String,
    @ColumnInfo(name = "qty") var count: Float,
    @ColumnInfo(name = "qty_type") var countType: Int,
    @ColumnInfo(name = "item_type") var type: Int,
    @ColumnInfo(name = "item_status") var status: Boolean
)
