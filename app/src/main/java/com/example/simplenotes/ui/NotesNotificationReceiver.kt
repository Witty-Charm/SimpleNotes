package com.example.simplenotes.ui

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class NotesNotificationReceiver: BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {
        val noteTitle = intent?.getStringExtra("note_title") ?: "Reminder"
        val noteDescription = intent?.getStringExtra("note_description") ?: ""

        val service = NotesNotificationService(context)
        service.showNotification(noteTitle, noteDescription)
    }
}