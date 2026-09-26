package com.example.ui.screens.admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DocumentEntity
import com.example.ui.components.LegioTopBar
import com.example.ui.theme.LegioWinePrimary

@Composable
fun AdminLibraryScreen(
    documents: List<DocumentEntity>,
    categories: List<String>,
    onSaveDocument: (DocumentEntity) -> Unit,
    onDeleteDocument: (DocumentEntity) -> Unit,
    onBackClick: () -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }
    var editingDoc by remember { mutableStateOf<DocumentEntity?>(null) }

    Scaffold(
        topBar = {
            LegioTopBar(
                title = "Gestión de Biblioteca",
                subtitle = "Catálogo y Enlaces Externos",
                showBack = true,
                onBackClick = onBackClick
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    editingDoc = null
                    showDialog = true
                },
                containerColor = LegioWinePrimary,
                contentColor = Color.White,
                modifier = Modifier.testTag("fab_add_document")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Nuevo Documento")
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .testTag("admin_library_list"),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(documents, key = { it.id }) { doc ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = Color(0xFF2E5A88).copy(alpha = 0.1f)
                            ) {
                                Text(
                                    text = doc.category,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = Color(0xFF2E5A88),
                                        fontWeight = FontWeight.Bold
                                    ),
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                            Text(
                                text = doc.pageCountOrSize,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = doc.title,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontFamily = FontFamily.Serif,
                                fontWeight = FontWeight.Bold
                            )
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = doc.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 2
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Enlace: ${doc.fileUrl}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.outline,
                            maxLines = 1
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            TextButton(
                                onClick = {
                                    editingDoc = doc
                                    showDialog = true
                                }
                            ) {
                                Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Editar")
                            }

                            TextButton(
                                onClick = { onDeleteDocument(doc) },
                                colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                            ) {
                                Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Eliminar")
                            }
                        }
                    }
                }
            }
        }

        if (showDialog) {
            DocumentEditDialog(
                document = editingDoc,
                categories = categories.filter { it != "Todas" },
                onDismiss = { showDialog = false },
                onConfirm = { savedDoc ->
                    onSaveDocument(savedDoc)
                    showDialog = false
                }
            )
        }
    }
}

@Composable
fun DocumentEditDialog(
    document: DocumentEntity?,
    categories: List<String>,
    onDismiss: () -> Unit,
    onConfirm: (DocumentEntity) -> Unit
) {
    var title by remember { mutableStateOf(document?.title ?: "") }
    var selectedCategory by remember { mutableStateOf(document?.category ?: categories.firstOrNull() ?: "Planes de formación") }
    var description by remember { mutableStateOf(document?.description ?: "") }
    var fileUrl by remember { mutableStateOf(document?.fileUrl ?: "https://drive.google.com/sample-file") }
    var pageCountOrSize by remember { mutableStateOf(document?.pageCountOrSize ?: "24 págs. • 2.1 MB") }
    var keywords by remember { mutableStateOf(document?.keywords ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (document == null) "Nuevo Recurso" else "Editar Recurso") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Título del Documento") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = selectedCategory,
                    onValueChange = { selectedCategory = it },
                    label = { Text("Categoría") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Breve Descripción") },
                    maxLines = 3,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = fileUrl,
                    onValueChange = { fileUrl = it },
                    label = { Text("Enlace a Google Drive o Archivo") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = pageCountOrSize,
                    onValueChange = { pageCountOrSize = it },
                    label = { Text("Páginas / Tamaño") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = keywords,
                    onValueChange = { keywords = it },
                    label = { Text("Palabras Clave (para buscador)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        val toSave = document?.copy(
                            title = title.trim(),
                            category = selectedCategory.trim(),
                            description = description.trim(),
                            fileUrl = fileUrl.trim(),
                            pageCountOrSize = pageCountOrSize.trim(),
                            keywords = keywords.trim()
                        ) ?: DocumentEntity(
                            title = title.trim(),
                            category = selectedCategory.trim(),
                            description = description.trim(),
                            fileUrl = fileUrl.trim(),
                            pageCountOrSize = pageCountOrSize.trim(),
                            keywords = keywords.trim()
                        )
                        onConfirm(toSave)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = LegioWinePrimary)
            ) {
                Text("Guardar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}
