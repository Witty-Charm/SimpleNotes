package com.example.simplenotes.ui

import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.simplenotes.data.Note


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteContent(
    notes: List<Note>,
    noteText: String,
    noteTextEdited: String,
    onNoteTextChange: (String) -> Unit,
    onNoteTextChangeEdit: (String) -> Unit,
    onBeingEdited: Note?,
    onStartEdit: (Note) -> Unit,
    onSaveEdit: (Note) -> Unit,
    onCancelEdit: () -> Unit,
    onAddNote: () -> Unit,
    onDeleteNote: (Note) -> Unit,
    onToggleNote: (Note, Boolean) -> Unit,
    onClearAll: () -> Unit,
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    priorityFilter: String,
    onPriorityFilterChange: (String) -> Unit
) {
    val context = LocalContext.current
    Scaffold(topBar = { TopAppBar(title = { Text(text = "📝 Todo's list") }) }, bottomBar = {
        BottomAppBar(
            containerColor = MaterialTheme.colorScheme.background,
            tonalElevation = 0.dp,
            contentPadding = PaddingValues(horizontal = 16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = { onClearAll() },
                    modifier = Modifier.weight(0.4f),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Icon(Icons.Default.Delete, contentDescription = null)
                    Spacer(Modifier.width(4.dp))
                    Text(text = "Clear")
                }

                Button(
                    onClick = { onAddNote() },
                    modifier = Modifier.weight(0.6f),
                    shape = RoundedCornerShape(12.dp),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Spacer(Modifier.width(4.dp))
                    Text(text = "Add Note")
                }
            }
        }
    }) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(16.dp),
        ) {
            TextField(
                modifier = Modifier.fillMaxWidth(),
                label = {Text("Search notes")},
                value = searchQuery,
                onValueChange = onSearchChange,
                shape = RoundedCornerShape(12.dp)
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(9.dp),
                horizontalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                listOf("All", "Regular", "Important").forEach { label ->
                    Button(
                        onClick = { onPriorityFilterChange(label) },
                        modifier = Modifier.height(36.dp)
                    ) {
                        Text(label)
                    }
                }
            }

            OutlinedTextField(
                value = noteText,
                onValueChange = onNoteTextChange,
                label = { Text("Enter a note") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(notes) { note ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                val intent = Intent(context, NoteDetailActivity::class.java)
                                intent.putExtra("note_id", note.id)
                                context.startActivity(intent)
                            },
                        shape = RoundedCornerShape(12.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(12.dp)
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (note == onBeingEdited) {
                                OutlinedTextField(
                                    value = noteTextEdited,
                                    onValueChange = onNoteTextChangeEdit,
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(8.dp)
                                )
                                IconButton(onClick = { onSaveEdit(note) }) {
                                    Icon(Icons.Default.Check, contentDescription = "Save")
                                }
                                IconButton(onClick = onCancelEdit) {
                                    Icon(Icons.Default.Close, contentDescription = "Cancel")
                                }
                            } else {
                                Text(
                                    text = note.title,
                                    modifier = Modifier.weight(1f),
                                    style = androidx.compose.ui.text.TextStyle(fontSize = 16.sp)
                                )
                                IconButton(onClick = { onStartEdit(note) }) {
                                    Icon(Icons.Default.Edit, contentDescription = "Edit")
                                }
                            }

                            IconButton(onClick = { onDeleteNote(note) }) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete")
                            }
                            Checkbox(
                                checked = note.isDone, onCheckedChange = { onToggleNote(note, it) })
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun NoteScreen(viewModel: NoteViewModel) {
    val state by viewModel.state.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    var priorityFilter by remember { mutableStateOf("All") }

    val filteredNotes = state.notes.filter { note ->
        note.title.contains(searchQuery, ignoreCase = true) &&
                (priorityFilter == "All" ||
                        (priorityFilter == "Regular" && note.priority == 0) ||
                        (priorityFilter == "Important" && note.priority == 1))
    }


    NoteContent(
        notes = filteredNotes,
        noteText = state.noteText,
        noteTextEdited = state.editedNoteText,
        onNoteTextChange = { text ->
            viewModel.onEvent(NoteEvent.SetNote(text))
        },
        onNoteTextChangeEdit = { text ->
            viewModel.onEvent(NoteEvent.SetEditedNote(text))
        },

        onStartEdit = { note ->
            viewModel.onEvent(NoteEvent.StartEditing(note))
        },

        onCancelEdit = {
            viewModel.onEvent(NoteEvent.CancelEditing)
        },

        onSaveEdit = {
            viewModel.onEvent(NoteEvent.ApplyEditing)
        },

        onAddNote = {
            viewModel.onEvent(NoteEvent.SaveNote)
        },

        onDeleteNote = { note ->
            viewModel.onEvent(NoteEvent.DeleteNote(note))
        },

        onBeingEdited = state.editingNote,

        onToggleNote = { note, _ ->
            viewModel.onEvent(NoteEvent.ToggleNoteChecked(note))
        },

        onClearAll = { viewModel.deleteAll() },

        searchQuery = searchQuery,

        onSearchChange = { searchQuery = it},

        priorityFilter = priorityFilter,

        onPriorityFilterChange = { priorityFilter = it }

    )
}