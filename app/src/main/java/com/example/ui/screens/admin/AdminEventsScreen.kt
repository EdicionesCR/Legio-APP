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
import com.example.data.model.EventEntity
import com.example.ui.components.LegioTopBar
import com.example.ui.theme.LegioWinePrimary

@Composable
fun AdminEventsScreen(
    events: List<EventEntity>,
    onSaveEvent: (EventEntity) -> Unit,
    onDeleteEvent: (EventEntity) -> Unit,
    onBackClick: () -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }
    var editingEvent by remember { mutableStateOf<EventEntity?>(null) }

    Scaffold(
        topBar = {
            LegioTopBar(
                title = "Gestión de Eventos",
                subtitle = "Convocatorias y Retiros",
                showBack = true,
                onBackClick = onBackClick
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    editingEvent = null
                    showDialog = true
                },
                containerColor = LegioWinePrimary,
                contentColor = Color.White,
                modifier = Modifier.testTag("fab_add_event")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Nuevo Evento")
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .testTag("admin_events_list"),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(events, key = { it.id }) { event ->
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
                            Text(
                                text = "${event.startDate} — ${event.endDate}",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = LegioWinePrimary
                            )
                            if (event.requiresRegistration) {
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = Color(0xFF2E5A88).copy(alpha = 0.1f)
                                ) {
                                    Text(
                                        text = "Inscripción activa",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = Color(0xFF2E5A88),
                                            fontWeight = FontWeight.Bold
                                        ),
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = event.title,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontFamily = FontFamily.Serif,
                                fontWeight = FontWeight.Bold
                            )
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "${event.location} • ${event.time}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            TextButton(
                                onClick = {
                                    editingEvent = event
                                    showDialog = true
                                }
                            ) {
                                Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Editar")
                            }

                            TextButton(
                                onClick = { onDeleteEvent(event) },
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
            EventEditDialog(
                event = editingEvent,
                onDismiss = { showDialog = false },
                onConfirm = { savedEvent ->
                    onSaveEvent(savedEvent)
                    showDialog = false
                }
            )
        }
    }
}

@Composable
fun EventEditDialog(
    event: EventEntity?,
    onDismiss: () -> Unit,
    onConfirm: (EventEntity) -> Unit
) {
    var title by remember { mutableStateOf(event?.title ?: "") }
    var startDate by remember { mutableStateOf(event?.startDate ?: "2026-11-01") }
    var endDate by remember { mutableStateOf(event?.endDate ?: "2026-11-01") }
    var time by remember { mutableStateOf(event?.time ?: "09:00 - 18:00 hs") }
    var location by remember { mutableStateOf(event?.location ?: "") }
    var description by remember { mutableStateOf(event?.description ?: "") }
    var requiresRegistration by remember { mutableStateOf(event?.requiresRegistration ?: true) }
    var registrationUrl by remember { mutableStateOf(event?.registrationUrl ?: "https://forms.gle/sample-event") }
    var imageUrl by remember { mutableStateOf(event?.imageUrl ?: "https://images.unsplash.com/photo-1519817650390-64a93db51149?auto=format&fit=crop&q=80&w=800") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (event == null) "Nuevo Evento" else "Editar Evento") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Nombre del Evento") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = startDate,
                        onValueChange = { startDate = it },
                        label = { Text("Fecha Inicio") },
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = endDate,
                        onValueChange = { endDate = it },
                        label = { Text("Fecha Fin") },
                        modifier = Modifier.weight(1f)
                    )
                }
                OutlinedTextField(
                    value = location,
                    onValueChange = { location = it },
                    label = { Text("Lugar") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = time,
                    onValueChange = { time = it },
                    label = { Text("Horario") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Descripción") },
                    maxLines = 3,
                    modifier = Modifier.fillMaxWidth()
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Requiere inscripción")
                    Switch(checked = requiresRegistration, onCheckedChange = { requiresRegistration = it })
                }
                if (requiresRegistration) {
                    OutlinedTextField(
                        value = registrationUrl,
                        onValueChange = { registrationUrl = it },
                        label = { Text("Enlace a Formulario Google Forms") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        val toSave = event?.copy(
                            title = title.trim(),
                            startDate = startDate.trim(),
                            endDate = endDate.trim(),
                            time = time.trim(),
                            location = location.trim(),
                            description = description.trim(),
                            requiresRegistration = requiresRegistration,
                            registrationUrl = if (requiresRegistration) registrationUrl.trim() else null,
                            imageUrl = imageUrl.trim()
                        ) ?: EventEntity(
                            title = title.trim(),
                            startDate = startDate.trim(),
                            endDate = endDate.trim(),
                            time = time.trim(),
                            location = location.trim(),
                            description = description.trim(),
                            requiresRegistration = requiresRegistration,
                            registrationUrl = if (requiresRegistration) registrationUrl.trim() else null,
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
