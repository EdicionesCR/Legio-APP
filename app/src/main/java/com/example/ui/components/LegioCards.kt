package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.AnnouncementEntity
import com.example.data.model.EventEntity
import com.example.data.model.NewsEntity
import com.example.data.repository.BirthdayItem
import com.example.ui.theme.*

@Composable
fun SectionHeader(
    title: String,
    actionText: String? = null,
    onActionClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(20.dp)
                    .background(LegioGoldAccent, RoundedCornerShape(2.dp))
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                ),
                color = MaterialTheme.colorScheme.onBackground
            )
        }
        if (actionText != null && onActionClick != null) {
            TextButton(
                onClick = onActionClick,
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                modifier = Modifier.testTag("section_action_${title.lowercase().replace(" ", "_")}")
            ) {
                Text(
                    text = actionText,
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = LegioWinePrimary
                )
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    modifier = Modifier.size(14.dp),
                    tint = LegioWinePrimary
                )
            }
        }
    }
}

@Composable
fun BirthdayHighlightCard(
    birthdayItem: BirthdayItem?,
    onViewAllClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .testTag("card_birthday_highlight"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (birthdayItem?.isToday == true) LegioGoldContainer else MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(if (birthdayItem?.isToday == true) LegioGoldAccent else LegioWinePrimary.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Cake,
                            contentDescription = "Cumpleaños",
                            tint = if (birthdayItem?.isToday == true) Color.Black else LegioWinePrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = if (birthdayItem?.isToday == true) "¡Cumpleaños de Hoy!" else "Próximo Cumpleaños",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (birthdayItem?.isToday == true) Color(0xFF5A4004) else LegioWinePrimary
                            )
                        )
                        if (birthdayItem?.isToday != true) {
                            Text(
                                text = "En ${birthdayItem?.daysUntil ?: 0} días",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                TextButton(
                    onClick = onViewAllClick,
                    contentPadding = PaddingValues(horizontal = 8.dp)
                ) {
                    Text(
                        text = "Ver todos",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = LegioWinePrimary
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (birthdayItem != null) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    MemberAvatar(
                        imageUrl = birthdayItem.user.avatarUrl,
                        name = birthdayItem.user.fullName,
                        size = 56.dp
                    )
                    Spacer(modifier = Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = birthdayItem.user.fullName,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Fecha: ${birthdayItem.formattedBirthday}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        if (birthdayItem.user.memberType != "NONE") {
                            Spacer(modifier = Modifier.height(4.dp))
                            MemberTypeBadge(memberType = birthdayItem.user.memberType, mini = true)
                        }
                    }
                }
            } else {
                Text(
                    text = "No hay registros de cumpleaños próximos.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun UpcomingEventHighlightCard(
    event: EventEntity?,
    onViewDetail: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .clickable(onClick = onViewDetail)
            .testTag("card_upcoming_event_highlight"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            // Event representative visual banner
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
            ) {
                if (!event?.imageUrl.isNullOrBlank()) {
                    AsyncImage(
                        model = event!!.imageUrl,
                        contentDescription = event.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(LegioWinePrimary, LegioWineDark)
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Event,
                            contentDescription = null,
                            tint = LegioGoldAccent.copy(alpha = 0.5f),
                            modifier = Modifier.size(56.dp)
                        )
                    }
                }

                // Gradient overlay
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.7f))
                            )
                        )
                )

                // Date badge
                Surface(
                    modifier = Modifier
                        .padding(12.dp)
                        .align(Alignment.TopStart),
                    shape = RoundedCornerShape(8.dp),
                    color = LegioGoldAccent,
                    contentColor = Color.Black
                ) {
                    Text(
                        text = event?.startDate ?: "Próximamente",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                // Tag badge
                if (event?.requiresRegistration == true) {
                    Surface(
                        modifier = Modifier
                            .padding(12.dp)
                            .align(Alignment.TopEnd),
                        shape = RoundedCornerShape(8.dp),
                        color = Color.Black.copy(alpha = 0.6f),
                        contentColor = Color.White
                    ) {
                        Text(
                            text = "Inscripción previa",
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = event?.title ?: "No hay eventos próximos",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp
                    ),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(6.dp))

                if (event != null) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Outlined.Place,
                            contentDescription = null,
                            tint = LegioWinePrimary,
                            modifier = Modifier.size(16.dp)
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

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = event.time,
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Button(
                            onClick = onViewDetail,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = LegioWinePrimary,
                                contentColor = Color.White
                            ),
                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                            modifier = Modifier.testTag("btn_event_more_info")
                        ) {
                            Text("Más información", style = MaterialTheme.typography.labelMedium)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun NewsHighlightCard(
    news: NewsEntity,
    onViewDetail: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .clickable(onClick = onViewDetail)
            .testTag("card_news_${news.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            if (news.imageUrl.isNotBlank()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(130.dp)
                ) {
                    AsyncImage(
                        model = news.imageUrl,
                        contentDescription = news.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    Surface(
                        modifier = Modifier
                            .padding(10.dp)
                            .align(Alignment.TopStart),
                        shape = RoundedCornerShape(6.dp),
                        color = LegioWinePrimary,
                        contentColor = Color.White
                    ) {
                        Text(
                            text = news.category,
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }
            }

            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = news.date,
                    style = MaterialTheme.typography.labelSmall,
                    color = LegioGoldMuted
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = news.title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold
                    ),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = news.summary,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text(
                        text = "Leer noticia completa",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = LegioWinePrimary
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        tint = LegioWinePrimary,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun AnnouncementBanner(
    announcements: List<AnnouncementEntity>,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (announcements.isEmpty()) return

    val latest = announcements.first()
    val isHighPriority = latest.priority == "Alta"

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .clickable(onClick = onClick)
            .testTag("banner_announcement"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isHighPriority) LegioWinePrimary else Color(0xFF2C2522)
        )
    ) {
        Row(
            modifier = Modifier
                .padding(14.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(if (isHighPriority) LegioGoldAccent else Color(0xFF5A4942)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Campaign,
                    contentDescription = "Aviso importante",
                    tint = if (isHighPriority) Color.Black else LegioGoldAccent,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "AVISO IMPORTANTE",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = LegioGoldAccent
                    )
                    if (announcements.size > 1) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "(${announcements.size})",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White.copy(alpha = 0.7f)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = latest.title,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                tint = Color.White.copy(alpha = 0.8f),
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Composable
fun CarouselQuickAccess(
    onNavigateToUsers: () -> Unit,
    onNavigateToLibrary: () -> Unit,
    onNavigateToEvents: () -> Unit,
    onNavigateToNews: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        SectionHeader(title = "Secciones Principales")

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            QuickAccessTile(
                title = "Usuarios",
                subtitle = "Miembros",
                icon = Icons.Default.People,
                color = LegioWinePrimary,
                onClick = onNavigateToUsers,
                modifier = Modifier.weight(1f),
                testTag = "tile_users"
            )
            QuickAccessTile(
                title = "Biblioteca",
                subtitle = "Formación",
                icon = Icons.Default.MenuBook,
                color = Color(0xFF2E5A88),
                onClick = onNavigateToLibrary,
                modifier = Modifier.weight(1f),
                testTag = "tile_library"
            )
            QuickAccessTile(
                title = "Eventos",
                subtitle = "Calendario",
                icon = Icons.Default.Event,
                color = Color(0xFF7A4A1C),
                onClick = onNavigateToEvents,
                modifier = Modifier.weight(1f),
                testTag = "tile_events"
            )
            QuickAccessTile(
                title = "Cartelera",
                subtitle = "Crónicas",
                icon = Icons.Default.Feed,
                color = Color(0xFF3B6645),
                onClick = onNavigateToNews,
                modifier = Modifier.weight(1f),
                testTag = "tile_news"
            )
        }
    }
}

@Composable
fun QuickAccessTile(
    title: String,
    subtitle: String,
    icon: ImageVector,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    testTag: String = ""
) {
    Surface(
        onClick = onClick,
        modifier = modifier
            .height(96.dp)
            .testTag(testTag),
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 2.dp,
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = color,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun MemberAvatar(
    imageUrl: String,
    name: String,
    size: androidx.compose.ui.unit.Dp = 48.dp,
    modifier: Modifier = Modifier
) {
    val cleanUrl = imageUrl.trim()
    val hasValidPhoto = cleanUrl.isNotBlank() && cleanUrl != "null"

    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(
                if (hasValidPhoto) LegioWinePrimary.copy(alpha = 0.15f)
                else LegioWineDark
            )
            .border(
                width = if (size >= 80.dp) 2.5.dp else 1.5.dp,
                color = LegioGoldAccent,
                shape = CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        if (hasValidPhoto) {
            AsyncImage(
                model = cleanUrl,
                contentDescription = name,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            // Extract clean initials from Name and Surname (skipping ecclesiastical prefixes)
            val filteredWords = name.split(" ")
                .filter {
                    val w = it.trim().lowercase()
                    w.isNotEmpty() && !w.startsWith("p.") && !w.startsWith("padre") &&
                            !w.startsWith("hno.") && !w.startsWith("hermano") && !w.startsWith("hna.")
                }
            val initials = if (filteredWords.size >= 2) {
                "${filteredWords.first().first().uppercaseChar()}${filteredWords.last().first().uppercaseChar()}"
            } else if (filteredWords.isNotEmpty()) {
                filteredWords.first().take(2).uppercase()
            } else {
                "L"
            }

            val fontSizeSp = (size.value * 0.38f).coerceIn(11f, 32f).sp

            Text(
                text = initials,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    fontSize = fontSizeSp,
                    color = Color(0xFFFBF6E9),
                    letterSpacing = 0.5.sp
                )
            )
        }
    }
}

@Composable
fun MemberTypeBadge(
    memberType: String,
    mini: Boolean = false
) {
    val (label, bg, fg) = when (memberType) {
        "PRIEST" -> Triple("Sacerdote LCR", Color(0xFF261F21), LegioGoldAccent)
        "BROTHER" -> Triple("Hermano LCR", Color(0xFF1A2634), Color(0xFFE2D1A6))
        "CONSECRATED_LAITY" -> Triple("Laico/a Consagrado/a", Color(0xFF3B1D23), Color(0xFFFADBC8))
        else -> return
    }

    Surface(
        shape = RoundedCornerShape(if (mini) 4.dp else 6.dp),
        color = bg,
        contentColor = fg
    ) {
        Row(
            modifier = Modifier.padding(horizontal = if (mini) 6.dp else 8.dp, vertical = if (mini) 2.dp else 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = when (memberType) {
                    "PRIEST" -> Icons.Default.BrightnessAuto
                    "BROTHER" -> Icons.Default.Shield
                    else -> Icons.Default.Star
                },
                contentDescription = null,
                modifier = Modifier.size(if (mini) 11.dp else 14.dp),
                tint = fg
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = label,
                style = if (mini) MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontWeight = FontWeight.Bold)
                else MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
            )
        }
    }
}

@Composable
fun PriestBadge(mini: Boolean = false) {
    MemberTypeBadge(memberType = "PRIEST", mini = mini)
}
