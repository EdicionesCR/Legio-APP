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
import com.example.data.model.UserEntity
import com.example.ui.components.LegioTopBar
import com.example.ui.components.MemberAvatar
import com.example.ui.components.PriestBadge
import com.example.ui.theme.LegioGoldAccent
import com.example.ui.theme.LegioWinePrimary

@Composable
fun AdminUsersScreen(
    users: List<UserEntity>,
    onUpdateUser: (UserEntity) -> Unit,
    onDeleteUser: (UserEntity) -> Unit,
    onBackClick: () -> Unit
) {
    Scaffold(
        topBar = {
            LegioTopBar(
                title = "Gestión de Miembros",
                subtitle = "${users.size} Miembros Registrados",
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
                .testTag("admin_users_list"),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(users, key = { it.id }) { user ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            MemberAvatar(
                                imageUrl = user.avatarUrl,
                                name = user.fullName,
                                size = 48.dp
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = user.fullName,
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontFamily = FontFamily.Serif,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                                Text(
                                    text = "${user.email} • ${user.city}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                if (user.memberType != "NONE") {
                                    Spacer(modifier = Modifier.height(2.dp))
                                    com.example.ui.components.MemberTypeBadge(memberType = user.memberType, mini = true)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))

                        Spacer(modifier = Modifier.height(8.dp))

                        // Controls: Hide data toggle & Admin status toggle
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Oculta datos: ",
                                    style = MaterialTheme.typography.bodySmall
                                )
                                Switch(
                                    checked = user.hideMyData,
                                    onCheckedChange = {
                                        onUpdateUser(user.copy(hideMyData = it))
                                    }
                                )
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Admin: ",
                                    style = MaterialTheme.typography.bodySmall
                                )
                                Switch(
                                    checked = user.isAdmin,
                                    onCheckedChange = {
                                        onUpdateUser(user.copy(isAdmin = it))
                                    }
                                )
                            }

                            IconButton(
                                onClick = { onDeleteUser(user) },
                                colors = IconButtonDefaults.iconButtonColors(contentColor = MaterialTheme.colorScheme.error)
                            ) {
                                Icon(Icons.Default.Delete, contentDescription = "Eliminar miembro")
                            }
                        }
                    }
                }
            }
        }
    }
}
