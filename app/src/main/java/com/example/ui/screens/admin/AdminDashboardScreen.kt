package com.example.ui.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.LegioTopBar
import com.example.ui.theme.LegioGoldAccent
import com.example.ui.theme.LegioWineDark
import com.example.ui.theme.LegioWinePrimary

@Composable
fun AdminDashboardScreen(
    userCount: Int,
    eventCount: Int,
    newsCount: Int,
    documentCount: Int,
    announcementCount: Int,
    onNavigateToManageNews: () -> Unit,
    onNavigateToManageEvents: () -> Unit,
    onNavigateToManageLibrary: () -> Unit,
    onNavigateToManageUsers: () -> Unit,
    onNavigateToManageAnnouncements: () -> Unit,
    onBackClick: () -> Unit
) {
    Scaffold(
        topBar = {
            LegioTopBar(
                title = "Administración",
                subtitle = "Panel de Control Institucional",
                showBack = true,
                onBackClick = onBackClick
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Overview banner
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = LegioWineDark)
            ) {
                Row(
                    modifier = Modifier.padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(LegioGoldAccent.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AdminPanelSettings,
                            contentDescription = null,
                            tint = LegioGoldAccent,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column {
                        Text(
                            text = "Gobierno y Gestión",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontFamily = FontFamily.Serif,
                                fontWeight = FontWeight.Bold
                            ),
                            color = Color.White
                        )
                        Text(
                            text = "Exclusivo para autoridades de la Legión de Cristo Rey",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Módulos de Administración",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold
                ),
                color = LegioWinePrimary
            )

            Spacer(modifier = Modifier.height(12.dp))

            AdminActionCard(
                title = "Gestión de Cartelera",
                description = "Publicar comunicados, crónicas y cartas pastorales ($newsCount publicaciones)",
                icon = Icons.Outlined.Feed,
                color = Color(0xFF3B6645),
                onClick = onNavigateToManageNews,
                testTag = "admin_btn_news"
            )

            Spacer(modifier = Modifier.height(10.dp))

            AdminActionCard(
                title = "Gestión de Eventos",
                description = "Crear encuentros, retiros, ejercicios y links de inscripción ($eventCount eventos)",
                icon = Icons.Outlined.Event,
                color = Color(0xFF7A4A1C),
                onClick = onNavigateToManageEvents,
                testTag = "admin_btn_events"
            )

            Spacer(modifier = Modifier.height(10.dp))

            AdminActionCard(
                title = "Gestión de Biblioteca",
                description = "Organizar recursos, categorías y enlaces a Google Drive ($documentCount documentos)",
                icon = Icons.Outlined.MenuBook,
                color = Color(0xFF2E5A88),
                onClick = onNavigateToManageLibrary,
                testTag = "admin_btn_library"
            )

            Spacer(modifier = Modifier.height(10.dp))

            AdminActionCard(
                title = "Gestión de Avisos",
                description = "Publicar o dar de baja avisos urgentes en la pantalla de inicio ($announcementCount avisos)",
                icon = Icons.Outlined.Campaign,
                color = Color(0xFFB3261E),
                onClick = onNavigateToManageAnnouncements,
                testTag = "admin_btn_announcements"
            )

            Spacer(modifier = Modifier.height(10.dp))

            AdminActionCard(
                title = "Gestión de Usuarios",
                description = "Supervisar altas de miembros, privacidad y permisos ($userCount registrados)",
                icon = Icons.Outlined.People,
                color = LegioWinePrimary,
                onClick = onNavigateToManageUsers,
                testTag = "admin_btn_users"
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun AdminActionCard(
    title: String,
    description: String,
    icon: ImageVector,
    color: Color,
    onClick: () -> Unit,
    testTag: String
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .testTag(testTag),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.outline
            )
        }
    }
}
