package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "news")
data class NewsEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val date: String,
    val summary: String,
    val content: String,
    val imageUrl: String = "",
    val category: String = "Institucional",
    val isHighlighted: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)
