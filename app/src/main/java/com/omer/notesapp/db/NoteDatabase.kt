package com.omer.notesapp.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.omer.notesapp.model.Notes


@Database(entities = [Notes::class], version = 1)
abstract class NoteDatabase : RoomDatabase() {

    abstract fun noteDao(): NoteDAO

}