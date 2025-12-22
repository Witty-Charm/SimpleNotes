package com.example.simplenotes.ui

import com.example.simplenotes.data.Note

data class NoteState(
    val notes: List<Note> = emptyList(),
    val noteText: String = "",
    val isEditing: Boolean = false,
    val editingNote: Note? = null,
    val editedNoteText: String = ""
)