package com.example.simplenotes.ui

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.simplenotes.data.Note
import com.example.simplenotes.ui.theme.SimpleNotesTheme
import java.util.Date
import java.util.Locale

class NoteDetailActivity : ComponentActivity() {
    private val viewModel: NoteViewModel by viewModels { NoteViewModel.Factory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val noteId = intent.getIntExtra("note_id", -1)

        setContent {
            SimpleNotesTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
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

@Composable
fun NoteDetailScreen(
    noteId: Int,
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

    if (showDatePicker) {
        DateTimePickerDialog(
            onDateTimeSelected = { reminderTime = it; showDatePicker = false },
            onDismiss = { showDatePicker = false }
        )
    }

    NoteDetailContent(
        title = title,
        onTitleChange = { title = it },
        description = description,
        onDescriptionChange = { description = it },
        priority = priority,
        onPriorityChange = { priority = it },
        reminderTime = reminderTime,
        errorMessage = errorMessage,
        onBack = onBack,
        onReminderClick = { showDatePicker = true },
        onSave = {
            if (title.isBlank()) {
                errorMessage = "The title cannot be empty"
            } else {
                val note = Note(
                    id = if (noteId == -1) 0 else noteId,
                    title = title,
                    description = description,
                    priority = priority,
                    reminderTime = reminderTime,
                    updatedAt = System.currentTimeMillis()
                )
                if (noteId == -1) viewModel.insertNote(note) else viewModel.updateNote(note)
                reminderTime?.let { viewModel.scheduleReminder(note, it) }
                onBack()
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteDetailContent(
    title: String,
    onTitleChange: (String) -> Unit,
    description: String,
    onDescriptionChange: (String) -> Unit,
    priority: Int,
    onPriorityChange: (Int) -> Unit,
    reminderTime: Long?,
    errorMessage: String,
    onBack: () -> Unit,
    onReminderClick: () -> Unit,
    onSave: () -> Unit
) {
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
                Text(errorMessage, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(bottom = 16.dp))
            }

            TextField(
                value = title,
                onValueChange = onTitleChange,
                label = { Text("Title") },
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                singleLine = true
            )

            TextField(
                value = description,
                onValueChange = onDescriptionChange,
                label = { Text("Description") },
                modifier = Modifier.fillMaxWidth().height(195.dp).padding(bottom = 16.dp)
            )

            Text("Priority", style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(8.dp))

            Row(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("Regular", "Important").forEachIndexed { index, label ->
                    Button(
                        onClick = { onPriorityChange(index) },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (priority == index) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) { Text(label) }
                }
            }

            Button(onClick = onReminderClick, modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)) {
                Text("Set a reminder")
            }

            reminderTime?.let {
                val sdf = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
                Text("Reminder: ${sdf.format(Date(it))}", style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(bottom = 16.dp))
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = onSave, modifier = Modifier.fillMaxWidth()) {
                Text("Save")
            }
        }
    }
}

@Composable
fun DateTimePickerDialog(onDateTimeSelected: (Long) -> Unit, onDismiss: () -> Unit) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance()
    var showTimePicker by remember { mutableStateOf(false) }

    if (!showTimePicker) {
        DatePickerDialog(context, { _, y, m, d ->
            calendar.set(y, m, d)
            showTimePicker = true
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))
            .apply { setOnCancelListener { onDismiss() } }.show()
    } else {
        TimePickerDialog(context, { _, h, min ->
            calendar.set(Calendar.HOUR_OF_DAY, h)
            calendar.set(Calendar.MINUTE, min)
            onDateTimeSelected(calendar.timeInMillis)
        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true)
            .apply { setOnCancelListener { onDismiss() } }.show()
    }
}

@Preview(showBackground = true)
@Composable
fun FilledNotePreview() {
    SimpleNotesTheme {
        NoteDetailContent(
            title = "Купить продукты",
            onTitleChange = {},
            description = "Молоко, сыр, хлеб",
            onDescriptionChange = {},
            priority = 1,
            onPriorityChange = {},
            reminderTime = System.currentTimeMillis(),
            errorMessage = "",
            onBack = {},
            onReminderClick = {},
            onSave = {}
        )
    }
}