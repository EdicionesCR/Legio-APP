package com.example.ui.screens.events

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.List
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.EventEntity
import com.example.ui.components.LegioTopBar
import com.example.ui.theme.LegioGoldAccent
import com.example.ui.theme.LegioGoldContainer
import com.example.ui.theme.LegioWineDark
import com.example.ui.theme.LegioWinePrimary
import java.util.Calendar

@Composable
fun EventsScreen(
    events: List<EventEntity>,
    onEventClick: (Long) -> Unit,
    onNavigateToProfile: () -> Unit,
    modifier: Modifier = Modifier
) {
    // 0 = Calendar View, 1 = List View
    var selectedViewMode by remember { mutableIntStateOf(0) }

    Scaffold(
        topBar = {
            LegioTopBar(
                title = "Eventos",
                subtitle = "Calendario y Convocatorias",
                onProfileClick = onNavigateToProfile
            )
        },
        containerColor = MaterialTheme.colorScheme.background,
        modifier = modifier
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // View Mode Switcher: Calendario vs Próximos Eventos
            SingleChoiceSegmentedButtonRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                SegmentedButton(
                    selected = selectedViewMode == 0,
                    onClick = { selectedViewMode = 0 },
                    shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2),
                    icon = {
                        Icon(
                            imageVector = Icons.Outlined.CalendarMonth,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                    },
                    modifier = Modifier.testTag("tab_events_calendar")
                ) {
                    Text("Calendario")
                }
                SegmentedButton(
                    selected = selectedViewMode == 1,
                    onClick = { selectedViewMode = 1 },
                    shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2),
                    icon = {
                        Icon(
                            imageVector = Icons.Outlined.List,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                    },
                    modifier = Modifier.testTag("tab_events_list")
                ) {
                    Text("Próximos (${events.size})")
                }
            }

            if (selectedViewMode == 0) {
                EventsCalendarView(
                    events = events,
                    onEventClick = onEventClick
                )
            } else {
                EventsListView(
                    events = events,
                    onEventClick = onEventClick
                )
            }
        }
    }
}

@Composable
fun EventsCalendarView(
    events: List<EventEntity>,
    onEventClick: (Long) -> Unit
) {
    var currentMonthOffset by remember { mutableIntStateOf(0) } // 0 = Octubre 2026, 1 = Noviembre 2026

    val monthName = if (currentMonthOffset == 0) "Octubre 2026" else "Noviembre 2026"
    val daysInMonth = if (currentMonthOffset == 0) 31 else 30
    val startDayOfWeek = if (currentMonthOffset == 0) 4 else 0 // 0=Sunday, 4=Thursday

    // Filter events for this month
    val monthEvents = remember(currentMonthOffset, events) {
        val targetMonthStr = if (currentMonthOffset == 0) "-10-" else "-11-"
        events.filter { it.startDate.contains(targetMonthStr) }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("events_calendar_view"),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Month Selector Header
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = { if (currentMonthOffset > 0) currentMonthOffset-- },
                            enabled = currentMonthOffset > 0
                        ) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Mes anterior")
                        }

                        Text(
                            text = monthName,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontFamily = FontFamily.Serif,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            ),
                            color = LegioWinePrimary
                        )

                        IconButton(
                            onClick = { if (currentMonthOffset < 1) currentMonthOffset++ },
                            enabled = currentMonthOffset < 1
                        ) {
                            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Mes siguiente")
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Days of week row
                    val dayLabels = listOf("D", "L", "M", "M", "J", "V", "S")
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        dayLabels.forEach { label ->
                            Text(
                                text = label,
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.width(36.dp),
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Calendar Grid (5 rows of 7 days)
                    val totalSlots = 35
                    for (row in 0 until 5) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            for (col in 0 until 7) {
                                val slotIndex = row * 7 + col
                                val dayNumber = slotIndex - startDayOfWeek + 1
                                val isValidDay = dayNumber in 1..daysInMonth

                                if (isValidDay) {
                                    val formattedDay = String.format("%02d", dayNumber)
                                    val targetDate = if (currentMonthOffset == 0) "2026-10-$formattedDay" else "2026-11-$formattedDay"
                                    val hasEvent = monthEvents.any { it.startDate <= targetDate && it.endDate >= targetDate }

                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clip(CircleShape)
                                            .background(
                                                if (hasEvent) LegioWinePrimary else Color.Transparent
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "$dayNumber",
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                fontWeight = if (hasEvent) FontWeight.Bold else FontWeight.Normal
                                            ),
                                            color = if (hasEvent) Color.White else MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                } else {
                                    Spacer(modifier = Modifier.size(36.dp))
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                    }
                }
            }
        }

        // Events scheduled in this month
        item {
            Text(
                text = "Convocatorias en $monthName",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        if (monthEvents.isEmpty()) {
            item {
                Text(
                    text = "No hay eventos programados en este mes.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            items(monthEvents) { event ->
                EventListItemCard(
                    event = event,
                    onClick = { onEventClick(event.id) }
                )
            }
        }
    }
}

@Composable
fun EventsListView(
    events: List<EventEntity>,
    onEventClick: (Long) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("events_list_view"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(events, key = { it.id }) { event ->
            EventListItemCard(
                event = event,
                onClick = { onEventClick(event.id) }
            )
        }
    }
}

@Composable
fun EventListItemCard(
    event: EventEntity,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .testTag("card_event_${event.id}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            if (event.imageUrl.isNotBlank()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                ) {
                    AsyncImage(
                        model = event.imageUrl,
                        contentDescription = event.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    Surface(
                        modifier = Modifier
                            .padding(8.dp)
                            .align(Alignment.TopEnd),
                        shape = RoundedCornerShape(6.dp),
                        color = LegioGoldAccent,
                        contentColor = Color.Black
                    ) {
                        Text(
                            text = event.startDate,
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }
            }

            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = event.title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold
                    ),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Outlined.Place,
                        contentDescription = null,
                        tint = LegioWinePrimary,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = event.location,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Schedule,
                        contentDescription = null,
                        tint = LegioWinePrimary,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = event.time,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (event.requiresRegistration) {
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = LegioWinePrimary.copy(alpha = 0.1f)
                        ) {
                            Text(
                                text = "Requiere inscripción",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = LegioWinePrimary
                                ),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    } else {
                        Text(
                            text = "Entrada libre para legionarios",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Text(
                        text = "Ver detalles →",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = LegioWinePrimary
                    )
                }
            }
        }
    }
}
