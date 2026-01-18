package com.example.simplenotes.ui

import android.app.AlarmManager
import android.app.Application
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.simplenotes.data.Note
import com.example.simplenotes.data.NoteDAO
import com.example.simplenotes.data.NoteDatabase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class NoteViewModel(
    private val dao: NoteDAO,
    private val context: Context
) : ViewModel() {
    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as Application)
                val dao = NoteDatabase.getDatabase(application).dao()
                NoteViewModel(dao, application)
            }
        }
    }
    private val _state = MutableStateFlow(NoteState())
    val state: StateFlow<NoteState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            dao.getNotes().collect { notes ->
                _state.update { it.copy(notes = notes) }
            }
        }
    }

    fun deleteAll() {
        viewModelScope.launch {
            dao.deleteAll()
        }
    }
    fun onEvent(event: NoteEvent) {
        when(event) {
            is NoteEvent.SaveNote -> {
                val noteText = state.value.noteText
                if(noteText.isBlank()) return

                viewModelScope.launch {
                    dao.upsertNote(Note(title = noteText))
                    _state.update { it.copy(noteText = "") }
                }
            }
            is NoteEvent.SetNote -> {
                _state.update { it.copy(noteText = event.notes) }
            }
            is NoteEvent.DeleteNote -> {
                viewModelScope.launch {
                    dao.deleteNote(event.note)
                }
            }
            is NoteEvent.StartEditing -> {
                    _state.update {
                        it.copy(
                            isEditing = true,
                            editingNote = event.note,
                            editedNoteText = event.note.title
                        )
                    }
            }

            is NoteEvent.SaveEditedNote -> {
                viewModelScope.launch {
                    state.value.editingNote?.let { note ->
                        val updatedNote = note.copy(title = state.value.editedNoteText)
                        dao.upsertNote(updatedNote)
                        _state.update {
                            it.copy(
                                isEditing = false,
                                editingNote = null,
                                editedNoteText = ""
                            )
                        }

                    }
                }
            }
            is NoteEvent.CancelEditing -> {
                _state.update { it.copy(
                    isEditing = false,
                    editingNote = null,
                    editedNoteText = ""
                ) }
            }
            is NoteEvent.ApplyEditing -> {
                val editingNote = state.value.editingNote ?: return
                val updatedText = state.value.editedNoteText

                if (updatedText.isBlank()) return

                viewModelScope.launch {
                    dao.upsertNote(editingNote.copy(title = updatedText))
                    _state.update { it.copy(
                        isEditing = false,
                        editingNote = null,
                        editedNoteText = ""
                    ) }
                }
            }
            is NoteEvent.SetEditedNote -> {
                _state.update { it.copy(editedNoteText = event.notes) }
            }

            is NoteEvent.ToggleNoteChecked -> {
                val note = event.note
                val updatedNote = note.copy(isDone = !note.isDone)

                viewModelScope.launch {
                    dao.upsertNote(updatedNote)
                }
            }

            else -> {}
        }
    }
    fun getNoteById(id: Int): Flow<Note> {
        return dao.getNoteById(id)
    }

    fun insertNote(note: Note) {
        viewModelScope.launch {
            dao.upsertNote(note)
        }
    }

    fun updateNote(note: Note) {
        viewModelScope.launch {
            dao.upsertNote(note)
        }
    }

    fun scheduleReminder(note: Note, reminderTime: Long) {
        val intent = Intent(context, NotesNotificationReceiver::class.java)
        intent.putExtra("note_title", note.title)
        intent.putExtra("note_description", note.description)

        val noteId = note.id ?: return

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            noteId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.setAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            reminderTime,
            pendingIntent
        )
    }
}