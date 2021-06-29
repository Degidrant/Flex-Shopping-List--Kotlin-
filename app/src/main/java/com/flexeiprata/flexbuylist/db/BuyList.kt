package com.flexeiprata.flexbuylist.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "buy_lists")
data class BuyList(
    @PrimaryKey(autoGenerate = true) val ID: Int,
    @ColumnInfo(name = "order_all") var position: Int,
    @ColumnInfo(name = "name") var name: String,
    @ColumnInfo(name = "desc") var desc: String,
    @ColumnInfo(name = "date_created") val dateCreated: Date,
    @ColumnInfo(name = "date_assigned") var dateAssigned: Date?,
    @ColumnInfo(name = "pic_assigned") var picAssigned: Int
    )
