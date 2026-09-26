package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "events")
data class EventEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val startDate: String, // Format: YYYY-MM-DD
    val endDate: String,   // Format: YYYY-MM-DD
    val time: String,      // e.g. "09:00 - 18:30 hs"
    val location: String,
    val description: String,
    val imageUrl: String = "",
    val registrationUrl: String? = null,
    val requiresRegistration: Boolean = true,
    val isFeatured: Boolean = false
)
