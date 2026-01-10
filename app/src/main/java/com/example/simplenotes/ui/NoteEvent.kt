package com.example.simplenotes.ui

import com.example.simplenotes.data.Note

sealed interface NoteEvent {

    object SaveNote: NoteEvent

    object SaveEditedNote: NoteEvent
    object CancelEditing: NoteEvent
    object ApplyEditing: NoteEvent

    data class SetNote(val notes: String): NoteEvent
    data class SetEditedNote(val notes: String): NoteEvent
    data class DeleteNote(val note: Note): NoteEvent
    data class StartEditing(val note: Note): NoteEvent
    data class ToggleNoteChecked(val note: Note): NoteEvent
    data class OnSearchQueryChange(val query: String): NoteEvent
    data class OnPriorityFilterChange(val filter: String): NoteEvent


}