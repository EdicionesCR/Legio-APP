package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "documents")
data class DocumentEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val category: String, // e.g. "Planes de formación", "Lecturas de profundización", "Documentación", "Liturgia de las Horas", "Cancioneros", "Documentos institucionales", "Otros"
    val description: String,
    val fileUrl: String,  // e.g. Google Drive link or PDF URL
    val fileType: String = "PDF",
    val pageCountOrSize: String = "Documento PDF",
    val dateAdded: String = "2026",
    val keywords: String = ""
)
