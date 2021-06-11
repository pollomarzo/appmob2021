package com.example.pantry.data.items

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable


// TODO: make type an enum or something
@Entity(tableName = "item_table")
@Serializable
data class Item(
    // the actual numerical code this corresponds to
    @ColumnInfo(name = "code") val code: String,
    // name of the product
    val name: String = "",
    // should we autoGenerate? yeah
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    // description of the product
    val description: String = "",
    // amount we have in pantry
    val amount: Int = 1,
    // type of the product (here for future additions)
    val type: String = "")


