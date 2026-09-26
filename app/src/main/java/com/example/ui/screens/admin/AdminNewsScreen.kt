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
import com.example.data.model.NewsEntity
import com.example.ui.components.LegioTopBar
import com.example.ui.theme.LegioWinePrimary

@Composable
fun AdminNewsScreen(
    newsList: List<NewsEntity>,
    onSaveNews: (NewsEntity) -> Unit,
    onDeleteNews: (NewsEntity) -> Unit,
    onBackClick: () -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }
    var editingNews by remember { mutableStateOf<NewsEntity?>(null) }

    Scaffold(
        topBar = {
            LegioTopBar(
                title = "Gestión de Cartelera",
                subtitle = "Publicaciones y Artículos",
                showBack = true,
                onBackClick = onBackClick
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    editingNews = null
                    showDialog = true
                },
                containerColor = LegioWinePrimary,
                contentColor = Color.White,
                modifier = Modifier.testTag("fab_add_news")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Nueva Publicación")
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .testTag("admin_news_list"),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(newsList, key = { it.id }) { news ->
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
                                color = LegioWinePrimary.copy(alpha = 0.1f)
                            ) {
                                Text(
                                    text = news.category,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = LegioWinePrimary
                                    ),
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                            Text(
                                text = news.date,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = news.title,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontFamily = FontFamily.Serif,
                                fontWeight = FontWeight.Bold
                            )
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = news.summary,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 2
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            TextButton(
                                onClick = {
                                    editingNews = news
                                    showDialog = true
                                }
                            ) {
                                Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Editar")
                            }

                            TextButton(
                                onClick = { onDeleteNews(news) },
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
            NewsEditDialog(
                news = editingNews,
                onDismiss = { showDialog = false },
                onConfirm = { savedNews ->
                    onSaveNews(savedNews)
                    showDialog = false
                }
            )
        }
    }
}

@Composable
fun NewsEditDialog(
    news: NewsEntity?,
    onDismiss: () -> Unit,
    onConfirm: (NewsEntity) -> Unit
) {
    var title by remember { mutableStateOf(news?.title ?: "") }
    var category by remember { mutableStateOf(news?.category ?: "Institucional") }
    var summary by remember { mutableStateOf(news?.summary ?: "") }
    var content by remember { mutableStateOf(news?.content ?: "") }
    var imageUrl by remember { mutableStateOf(news?.imageUrl ?: "https://images.unsplash.com/photo-1548625361-195fe57871b6?auto=format&fit=crop&q=80&w=800") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (news == null) "Nueva Noticia" else "Editar Noticia") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Título") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = category,
                    onValueChange = { category = it },
                    label = { Text("Categoría (Institucional, Formación, etc.)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = summary,
                    onValueChange = { summary = it },
                    label = { Text("Breve Resumen") },
                    maxLines = 2,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = content,
                    onValueChange = { content = it },
                    label = { Text("Contenido Completo") },
                    minLines = 3,
                    maxLines = 5,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = imageUrl,
                    onValueChange = { imageUrl = it },
                    label = { Text("URL de Imagen") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        val toSave = news?.copy(
                            title = title.trim(),
                            category = category.trim(),
                            summary = summary.trim(),
                            content = content.trim(),
                            imageUrl = imageUrl.trim()
                        ) ?: NewsEntity(
                            title = title.trim(),
                            date = "Hoy",
                            category = category.trim(),
                            summary = summary.trim(),
                            content = content.trim(),
                            imageUrl = imageUrl.trim()
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
