package com.example.ui.screens.events

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.EventEntity
import com.example.ui.components.LegioTopBar
import com.example.ui.theme.LegioGoldAccent
import com.example.ui.theme.LegioGoldContainer
import com.example.ui.theme.LegioWineDark
import com.example.ui.theme.LegioWinePrimary

@Composable
fun EventDetailScreen(
    event: EventEntity?,
    onNavigateToRegistration: (Long) -> Unit,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current

    Scaffold(
        topBar = {
            LegioTopBar(
                title = "Detalle del Evento",
                subtitle = event?.startDate ?: "Convocatoria",
                showBack = true,
                onBackClick = onBackClick
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        if (event == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Text("Evento no encontrado")
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            // Hero Image
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
            ) {
                if (event.imageUrl.isNotBlank()) {
                    AsyncImage(
                        model = event.imageUrl,
                        contentDescription = event.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(LegioWineDark),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Event,
                            contentDescription = null,
                            tint = LegioGoldAccent,
                            modifier = Modifier.size(64.dp)
                        )
                    }
                }

                // Dark gradient
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.75f))
                            )
                        )
                )

                // Date badge
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = LegioGoldAccent,
                    contentColor = Color.Black,
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(16.dp)
                ) {
                    Text(
                        text = if (event.startDate == event.endDate) event.startDate else "${event.startDate} al ${event.endDate}",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }
            }

            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = event.title,
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.onBackground
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Logistics Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        EventMetaRow(
                            icon = Icons.Outlined.CalendarToday,
                            label = "Fechas",
                            value = "${event.startDate}  —  ${event.endDate}"
                        )
                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 10.dp),
                            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                        )
                        EventMetaRow(
                            icon = Icons.Outlined.Schedule,
                            label = "Horario",
                            value = event.time
                        )
                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 10.dp),
                            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                        )
                        EventMetaRow(
                            icon = Icons.Outlined.Place,
                            label = "Lugar",
                            value = event.location
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Actions: Inscribirme + Compartir por WhatsApp
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    if (event.requiresRegistration) {
                        Button(
                            onClick = { onNavigateToRegistration(event.id) },
                            modifier = Modifier
                                .weight(1f)
                                .height(50.dp)
                                .testTag("btn_event_register"),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = LegioWinePrimary,
                                contentColor = Color.White
                            )
                        ) {
                            Icon(Icons.Default.AppRegistration, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Inscribirme", fontWeight = FontWeight.Bold)
                        }
                    }

                    Button(
                        onClick = {
                            shareEventOnWhatsApp(context, event)
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp)
                            .testTag("btn_share_whatsapp"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF25D366),
                            contentColor = Color.White
                        )
                    ) {
                        Icon(Icons.Default.Chat, contentDescription = null, modifier = Modifier.size(18.dp), tint = Color.White)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("WhatsApp", fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Description Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Text(
                            text = "Descripción del Encuentro",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontFamily = FontFamily.Serif,
                                fontWeight = FontWeight.Bold
                            ),
                            color = LegioWinePrimary
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = event.description,
                            style = MaterialTheme.typography.bodyMedium,
                            lineHeight = 22.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                Spacer(modifier = Modifier.height(30.dp))
            }
        }
    }
}

@Composable
fun EventMetaRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(LegioWinePrimary.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = LegioWinePrimary,
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

fun shareEventOnWhatsApp(context: Context, event: EventEntity) {
    try {
        val shareMessage = """
¡Hermano legionario!
Te comparto la información del próximo evento de la *Legión de Cristo Rey*:

📌 *${event.title}*
🗓 *Fechas:* ${event.startDate} al ${event.endDate}
⏰ *Horario:* ${event.time}
📍 *Lugar:* ${event.location}

${event.description}

${if (event.requiresRegistration && !event.registrationUrl.isNullOrBlank()) "📝 Formulario de Inscripción:\n" + event.registrationUrl else ""}

«¡Viva Cristo Rey y Santa María de Guadalupe!»
        """.trimIndent()

        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, shareMessage)
            type = "text/plain"
            setPackage("com.whatsapp")
        }
        context.startActivity(sendIntent)
    } catch (e: Exception) {
        // Fallback to normal chooser if WhatsApp isn't directly matching package
        val fallbackIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, "Convocatoria Legión de Cristo Rey: ${event.title}\nFechas: ${event.startDate} - ${event.endDate}\nLugar: ${event.location}")
            type = "text/plain"
        }
        context.startActivity(Intent.createChooser(fallbackIntent, "Compartir evento"))
    }
}
