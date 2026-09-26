package com.example.ui.screens.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.data.model.AnnouncementEntity
import com.example.data.model.EventEntity
import com.example.data.model.NewsEntity
import com.example.data.model.UserEntity
import com.example.data.repository.BirthdayItem
import com.example.ui.components.*

@Composable
fun HomeScreen(
    currentUser: UserEntity?,
    todayOrNextBirthday: BirthdayItem?,
    nextEvent: EventEntity?,
    latestNewsList: List<NewsEntity>,
    activeAnnouncements: List<AnnouncementEntity>,
    onNavigateToBirthdays: () -> Unit,
    onNavigateToEventDetail: (Long) -> Unit,
    onNavigateToNewsDetail: (Long) -> Unit,
    onNavigateToAnnouncements: () -> Unit,
    onNavigateToUsers: () -> Unit,
    onNavigateToLibrary: () -> Unit,
    onNavigateToEvents: () -> Unit,
    onNavigateToNews: () -> Unit,
    onNavigateToAdmin: () -> Unit,
    onNavigateToProfile: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            LegioTopBar(
                title = "LEGIO",
                subtitle = "Legión de Cristo Rey",
                showAdminBadge = currentUser?.isAdmin == true,
                onAdminClick = if (currentUser?.isAdmin == true) onNavigateToAdmin else null,
                onProfileClick = onNavigateToProfile,
                activeAnnouncementsCount = activeAnnouncements.size,
                onAnnouncementsClick = onNavigateToAnnouncements,
                showThesaurus = currentUser?.canAccessThesaurus == true
            )
        },
        containerColor = MaterialTheme.colorScheme.background,
        modifier = modifier
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(scrollState)
                .testTag("home_screen_content")
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            // 1. Avisos (Appears/Disappears depending on whether there's an active announcement)
            if (activeAnnouncements.isNotEmpty()) {
                AnnouncementBanner(
                    announcements = activeAnnouncements,
                    onClick = onNavigateToAnnouncements
                )
                Spacer(modifier = Modifier.height(4.dp))
            }

            // 2. Carrusel / Menú de Secciones Principales
            CarouselQuickAccess(
                onNavigateToUsers = onNavigateToUsers,
                onNavigateToLibrary = onNavigateToLibrary,
                onNavigateToEvents = onNavigateToEvents,
                onNavigateToNews = onNavigateToNews
            )

            Spacer(modifier = Modifier.height(12.dp))

            // 3. Cumpleaños (del día o próximo)
            SectionHeader(
                title = "Cumpleaños Legionarios",
                actionText = "Ver todos",
                onActionClick = onNavigateToBirthdays
            )
            BirthdayHighlightCard(
                birthdayItem = todayOrNextBirthday,
                onViewAllClick = onNavigateToBirthdays
            )

            Spacer(modifier = Modifier.height(12.dp))

            // 4. Próximo Evento
            SectionHeader(
                title = "Próximo Evento",
                actionText = "Ver calendario",
                onActionClick = onNavigateToEvents
            )
            UpcomingEventHighlightCard(
                event = nextEvent,
                onViewDetail = {
                    nextEvent?.let { onNavigateToEventDetail(it.id) } ?: onNavigateToEvents()
                }
            )

            Spacer(modifier = Modifier.height(12.dp))

            // 5. Cartelera
            SectionHeader(
                title = "Cartelera",
                actionText = "Ver cartelera",
                onActionClick = onNavigateToNews
            )
            if (latestNewsList.isNotEmpty()) {
                latestNewsList.take(2).forEach { news ->
                    NewsHighlightCard(
                        news = news,
                        onViewDetail = { onNavigateToNewsDetail(news.id) }
                    )
                }
            } else {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Text(
                        text = "No hay publicaciones en la cartelera.",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(16.dp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 6. Redes Sociales y Plataformas Oficiales
            SocialLinksRow()

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
