package com.example.simplenotes.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDAO {
    @Upsert
    suspend fun upsertNote(note: Note)

    @Delete
    suspend fun deleteNote(note: Note)

    @Query("SELECT * FROM note")
    fun getNotes(): Flow<List<Note>>

    @Query("DELETE FROM note")
    suspend fun deleteAll()

    @Query("SELECT * FROM note WHERE id = :id")
    fun getNoteById(id: Int): Flow<Note>



}