package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "announcements")
data class AnnouncementEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val message: String,
    val date: String,
    val priority: String = "Normal", // "Alta", "Media", "Normal"
    val isActive: Boolean = true
)
