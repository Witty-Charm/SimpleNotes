package com.example.simplenotes.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Note(
    val name: String,
    val isDone: Boolean = false,
    @PrimaryKey(autoGenerate = true)
    val id: Int? = null,
)