package com.example.calculatorcompose

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class ViewModel {
    var notes = mutableStateListOf<Note>()
        private set

    var noteText by mutableStateOf("")

    var noteBeingEdited by mutableStateOf<Note?>(null)

    fun addNote() {
        if (noteText.isNotBlank()) {
            notes.add(Note(noteText))
            noteText = ""
        }
        }

    fun deleteNote(note: Note) {
        notes.remove(note)
    }

    fun editNote(note: Note, newText: String) {
        val index = notes.indexOf(note)
        if (index != -1) {
            notes[index] = note.copy(name = newText)
        }
    }

    fun toggleNote(note: Note, isDone: Boolean) {
        val index = notes.indexOf(note)
        if (index != -1){
            notes[index] = note.copy(isDone = isDone)
        }
    }

    fun clearAll(){
        notes.clear()
    }


}