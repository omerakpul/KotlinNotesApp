package com.omer.notesapp.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Notes(
    @ColumnInfo(name = "title")
    val title : String,
    @ColumnInfo(name = "details")
    val details : String?

) {
    @PrimaryKey(autoGenerate = true)
    var id = 0
}