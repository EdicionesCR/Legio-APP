package com.example.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cake
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.repository.BirthdayItem
import com.example.ui.components.LegioTopBar
import com.example.ui.components.MemberAvatar
import com.example.ui.components.PriestBadge
import com.example.ui.theme.LegioGoldAccent
import com.example.ui.theme.LegioGoldContainer
import com.example.ui.theme.LegioWinePrimary

@Composable
fun BirthdaysListScreen(
    birthdays: List<BirthdayItem>,
    onUserClick: (Long) -> Unit,
    onBackClick: () -> Unit
) {
    Scaffold(
        topBar = {
            LegioTopBar(
                title = "Cumpleaños",
                subtitle = "Calendario Comunitario",
                showBack = true,
                onBackClick = onBackClick
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .testTag("birthdays_list_content"),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = LegioGoldContainer)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(LegioGoldAccent),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Cake,
                                contentDescription = null,
                                tint = Color.Black
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Fraternidad Legionaria",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = Color(0xFF4A3402)
                            )
                            Text(
                                text = "Recordemos orar especialmente por nuestros hermanos en el día de su natalicio.",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF6B4E04)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(6.dp))
            }

            items(birthdays) { item ->
                Card(
                    onClick = { onUserClick(item.user.id) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (item.isToday) LegioGoldContainer else MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        MemberAvatar(
                            imageUrl = item.user.avatarUrl,
                            name = item.user.fullName,
                            size = 52.dp
                        )
                        Spacer(modifier = Modifier.width(14.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = item.user.fullName,
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontFamily = FontFamily.Serif,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = item.formattedBirthday,
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                                color = if (item.isToday) LegioWinePrimary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            if (item.user.memberType != "NONE") {
                                Spacer(modifier = Modifier.height(4.dp))
                                com.example.ui.components.MemberTypeBadge(memberType = item.user.memberType, mini = true)
                            }
                        }

                        // Days badge
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (item.isToday) LegioGoldAccent else MaterialTheme.colorScheme.surfaceVariant,
                            contentColor = if (item.isToday) Color.Black else MaterialTheme.colorScheme.onSurfaceVariant
                        ) {
                            Text(
                                text = if (item.isToday) "¡HOY!" else "En ${item.daysUntil} d",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
