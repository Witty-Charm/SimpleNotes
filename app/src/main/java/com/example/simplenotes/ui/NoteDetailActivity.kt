package com.example.simplenotes.ui

import android.app.DatePickerDialog
import android.icu.text.DateFormat
import android.icu.util.Calendar
import android.os.Bundle
import android.app.TimePickerDialog
import android.icu.text.SimpleDateFormat
import android.os.PersistableBundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TimePickerDialog
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import com.example.simplenotes.data.Note
import com.example.simplenotes.ui.theme.SimpleNotesTheme
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale

class NoteDetailActivity : ComponentActivity() {
    private val viewModel: NoteViewModel by viewModels {
        NoteViewModel.Factory
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val noteId = intent.getIntExtra("note_id", -1)

        setContent {
            SimpleNotesTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    NoteDetailScreen(
                        noteId = noteId,
                        viewModel = viewModel,
                        onBack = { finish() }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteDetailScreen(
    noteId: Int = -1,
    viewModel: NoteViewModel,
    onBack: () -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var priority by remember { mutableStateOf(0) }
    var reminderTime by remember { mutableStateOf<Long?>(null) }
    var showDatePicker by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    LaunchedEffect(noteId) {
        if (noteId != -1) {
            viewModel.getNoteById(noteId).collect { note ->
                title = note.title
                description = note.description
                priority = note.priority
                reminderTime = note.reminderTime
            }
        }
    }
    fun formatTime(timeInMillis: Long): String {
        val sdf = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
        return sdf.format(Date(timeInMillis))
    }

    @Composable
    fun DateTimePickerDialog(
        onDateTimeSelected: (Long) -> Unit,
        onDismiss: () -> Unit

    ) {
        val context = LocalContext.current
        val calendar = Calendar.getInstance()
        var showTimePicker by remember { mutableStateOf(false) }

        if (!showTimePicker) {
            DatePickerDialog(
                context,
                {_, year, month, dayOfMonth ->
                    calendar.set(year, month, dayOfMonth)
                    showTimePicker = true
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        } else {
            TimePickerDialog(
                context,
                { _, hourOfDay, minute ->
                    calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                    calendar.set(Calendar.MINUTE, minute)
                    onDateTimeSelected(calendar.timeInMillis)
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true
            ).show()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Note") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBackIosNew, "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            if (errorMessage.isNotEmpty()) {
                Text(
                    text = errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                )
            }

            TextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Title") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                singleLine = true
            )

            TextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(195.dp)
                    .padding(bottom = 16.dp),
                maxLines = 10
            )

            Text(
                text = "Priority",
                modifier = Modifier.padding(8.dp),
                style = MaterialTheme.typography.bodyMedium
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("Regular", "Important").forEachIndexed { index, label ->
                    Button(
                        onClick = { priority = index },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (priority == index)
                                MaterialTheme.colorScheme.primary
                            else
                                MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Text(label)
                    }
                }
            }

            Button(
                onClick = { showDatePicker = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            ) {
                Text(text = "Set a reminder")
            }


            if (reminderTime != null) {
                Text(
                    text = "Reminder: ${formatTime(reminderTime!!)}",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if (title.isBlank()) {
                        errorMessage = "The title cannot be empty"
                        return@Button
                    }

                    val note = Note(
                        id = if (noteId == -1) 0 else noteId,
                        title = title,
                        description = description,
                        priority = priority,
                        reminderTime = reminderTime,
                        updatedAt = System.currentTimeMillis()
                    )
                    if (noteId == -1) {
                        viewModel.insertNote(note)
                    } else {
                        viewModel.updateNote(note)
                    }

                    onBack()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Save")
            }
            if (showDatePicker) {
                DateTimePickerDialog(
                    onDateTimeSelected = { selectedTime ->
                        reminderTime = selectedTime
                        showDatePicker = false
                    },
                    onDismiss = { showDatePicker = false }
                )
            }
        }
    }

}