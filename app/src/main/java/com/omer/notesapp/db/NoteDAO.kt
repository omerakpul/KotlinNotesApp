package com.omer.notesapp.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.omer.notesapp.model.Notes
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Single

@Dao
interface NoteDAO {

    @Query("SELECT * FROM notes")
    fun getAllNotes(): Flowable<List<Notes>>

    @Query("SELECT * FROM Notes WHERE title LIKE :tit AND details LIKE :det LIMIT 1")
    fun findByName(tit: String, det: String?): List<Notes>

    @Query("SELECT* FROM Notes WHERE id = :noteId LIMIT 1")
    fun findById(noteId: Int) : Single<Notes>

    @Query("SELECT * FROM Notes WHERE title LIKE '%' || :query || '%'")
    fun searchNotes(query: String): Single<List<Notes>>

    @Insert
    fun insertNote(notes: Notes) : Completable

    @Update
    fun updateNote(notes: Notes) : Completable

    @Delete
    fun deleteNote(notes: Notes) : Completable

    @Query("DELETE FROM Notes WHERE id = :noteId")
    fun deleteNoteById(noteId: Int) : Completable
}