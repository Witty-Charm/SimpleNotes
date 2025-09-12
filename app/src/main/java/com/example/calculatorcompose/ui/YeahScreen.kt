@file:Suppress("UNCHECKED_CAST")

package com.example.calculatorcompose.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.calculatorcompose.Note
import com.example.calculatorcompose.ViewModel



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun YeahContent(
    notes: List<Note>,
    noteText: String,
    onNoteTextChange: (String) -> Unit,
    onBeingEdited: Note?,
    onStartEdit: (Note) -> Unit,
    onSaveEdit: (Note) -> Unit,
    onCancelEdit: () -> Unit,
    onAddNote: () -> Unit,
    onDeleteNote: (Note) -> Unit,
    onToggleNote: (Note, Boolean) -> Unit,
    onClearAll: () -> Unit
) {
    Scaffold(topBar = { TopAppBar(title = { Text(text = "Notes") }) }, bottomBar = {
        BottomAppBar {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = { onClearAll() }) {
                    Text(text = "Clear")
                }
                Button(
                    onClick = { onAddNote() }) {
                    Text(text = "Add")
                }
            }
        }
    }) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(12.dp)
        ) {
            OutlinedTextField(
                value = noteText,
                onValueChange = onNoteTextChange,
                label = { Text("Enter a note") },
                modifier = Modifier.fillMaxWidth()
            )
            LazyColumn {
                items(notes) { note ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        if (note == onBeingEdited) {
                            OutlinedTextField(
                                value = noteText,
                                onValueChange = onNoteTextChange,
                                modifier = Modifier.weight(1f)
                            )
                            IconButton(onClick =  { onSaveEdit(note) } ) {
                                Icon(Icons.Default.Check, contentDescription = "Save")
                            }
                            IconButton(onClick = onCancelEdit) {
                                Icon(Icons.Default.Close, contentDescription = "Cancel")
                            }
                        } else {
                            Text(note.name, modifier = Modifier.weight(1f))
                            IconButton(onClick = { onStartEdit(note) }) {
                                Icon(Icons.Default.Edit, contentDescription = "Edit")
                            }
                        }
                        }
                        IconButton(onClick = { onDeleteNote(note) }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete")
                        }
                        Checkbox(
                            checked = note.isDone,
                            onCheckedChange = { onToggleNote(note, it) }
                        )
                    }
                }
            }
        }
    }

@Composable
fun YeahScreen(viewModel: ViewModel) {
    YeahContent(
        notes = viewModel.notes,
        noteText = viewModel.noteText,
        onNoteTextChange = { viewModel.noteText = it },
        onStartEdit = { note ->
            viewModel.noteBeingEdited = note
            viewModel.noteText = note.name
        },
        onSaveEdit = { note ->
            viewModel.editNote(note, viewModel.noteText)
            viewModel.noteBeingEdited = null
            viewModel.noteText = ""
        },
        onAddNote = { viewModel.addNote() },
        onDeleteNote = { note -> viewModel.deleteNote(note) },
        onBeingEdited = viewModel.noteBeingEdited,
        onToggleNote = { note, isDone -> viewModel.toggleNote(note, isDone)},
        onCancelEdit = {},
        onClearAll = { viewModel.clearAll() })
}

//@Preview(showBackground = true)
//@Composable
//fun YeahPreview() {
//    YeahContent(
//        notes = listOf(
//        Note("Learn Compose"), Note("Write Preview", isDone = true)
//    ),
//        noteText = "Draft note",
//        onNoteTextChange = {},
//        onAddNote = {},
//        onDeleteNote = {},
//        onClearAll = {},
//        onSaveEdit = {},
//        onStartEdit = {},
//        onBeingEdited = { },
//        onToggleNote = {} as (Note, Boolean) -> Unit)
//}