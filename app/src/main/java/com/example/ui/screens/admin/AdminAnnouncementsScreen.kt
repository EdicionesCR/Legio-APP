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
import com.example.data.model.AnnouncementEntity
import com.example.ui.components.LegioTopBar
import com.example.ui.theme.LegioWinePrimary

@Composable
fun AdminAnnouncementsScreen(
    announcements: List<AnnouncementEntity>,
    onSaveAnnouncement: (AnnouncementEntity) -> Unit,
    onDeleteAnnouncement: (AnnouncementEntity) -> Unit,
    onBackClick: () -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }
    var editingItem by remember { mutableStateOf<AnnouncementEntity?>(null) }

    Scaffold(
        topBar = {
            LegioTopBar(
                title = "Gestión de Avisos",
                subtitle = "Banners y Alertas de Inicio",
                showBack = true,
                onBackClick = onBackClick
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    editingItem = null
                    showDialog = true
                },
                containerColor = LegioWinePrimary,
                contentColor = Color.White,
                modifier = Modifier.testTag("fab_add_announcement")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Nuevo Aviso")
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .testTag("admin_announcements_list"),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(announcements, key = { it.id }) { item ->
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
                                color = if (item.priority == "Alta") Color(0xFFB3261E).copy(alpha = 0.15f) else Color.LightGray.copy(alpha = 0.3f)
                            ) {
                                Text(
                                    text = "Prioridad ${item.priority}",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = if (item.isActive) "Activo" else "Inactivo",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (item.isActive) LegioWinePrimary else Color.Gray
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Switch(
                                    checked = item.isActive,
                                    onCheckedChange = {
                                        onSaveAnnouncement(item.copy(isActive = it))
                                    }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = item.title,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontFamily = FontFamily.Serif,
                                fontWeight = FontWeight.Bold
                            )
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = item.message,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            TextButton(
                                onClick = {
                                    editingItem = item
                                    showDialog = true
                                }
                            ) {
                                Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Editar")
                            }

                            TextButton(
                                onClick = { onDeleteAnnouncement(item) },
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
            AnnouncementEditDialog(
                announcement = editingItem,
                onDismiss = { showDialog = false },
                onConfirm = { savedItem ->
                    onSaveAnnouncement(savedItem)
                    showDialog = false
                }
            )
        }
    }
}

@Composable
fun AnnouncementEditDialog(
    announcement: AnnouncementEntity?,
    onDismiss: () -> Unit,
    onConfirm: (AnnouncementEntity) -> Unit
) {
    var title by remember { mutableStateOf(announcement?.title ?: "") }
    var message by remember { mutableStateOf(announcement?.message ?: "") }
    var priority by remember { mutableStateOf(announcement?.priority ?: "Alta") }
    var isActive by remember { mutableStateOf(announcement?.isActive ?: true) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (announcement == null) "Nuevo Aviso" else "Editar Aviso") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Título del Aviso") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = message,
                    onValueChange = { message = it },
                    label = { Text("Mensaje Completo") },
                    minLines = 2,
                    maxLines = 4,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = priority,
                    onValueChange = { priority = it },
                    label = { Text("Prioridad (Alta, Media, Normal)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Mostrar en pantalla de inicio")
                    Switch(checked = isActive, onCheckedChange = { isActive = it })
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        val toSave = announcement?.copy(
                            title = title.trim(),
                            message = message.trim(),
                            priority = priority.trim(),
                            isActive = isActive
                        ) ?: AnnouncementEntity(
                            title = title.trim(),
                            message = message.trim(),
                            date = "Hoy",
                            priority = priority.trim(),
                            isActive = isActive
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
