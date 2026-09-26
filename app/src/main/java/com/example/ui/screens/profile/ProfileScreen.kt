package com.example.ui.screens.profile

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.MemberType
import com.example.data.model.UserEntity
import com.example.ui.components.LegioTopBar
import com.example.ui.components.MemberAvatar
import com.example.ui.components.MemberTypeBadge
import com.example.ui.theme.LegioGoldAccent
import com.example.ui.theme.LegioGoldContainer
import com.example.ui.theme.LegioWineDark
import com.example.ui.theme.LegioWinePrimary

@Composable
fun ProfileScreen(
    currentUser: UserEntity?,
    onUpdateProfile: (UserEntity) -> Unit,
    onNavigateToAdmin: () -> Unit,
    onLogout: () -> Unit,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current

    var isEditing by remember { mutableStateOf(false) }

    var editFullName by remember(currentUser) { mutableStateOf(currentUser?.fullName ?: "") }
    var editBirthDate by remember(currentUser) { mutableStateOf(currentUser?.birthDate ?: "04-12-1999") }
    var editCity by remember(currentUser) { mutableStateOf(currentUser?.city ?: "") }
    var editPhone by remember(currentUser) { mutableStateOf(currentUser?.phone ?: "") }
    var editEmail by remember(currentUser) { mutableStateOf(currentUser?.email ?: "") }
    var editMemberType by remember(currentUser) { mutableStateOf(currentUser?.memberType ?: MemberType.NONE.name) }
    var editOrdinationDate by remember(currentUser) { mutableStateOf(currentUser?.ordinationDate ?: "28-10-2012") }
    var editHideMyData by remember(currentUser) { mutableStateOf(currentUser?.hideMyData ?: false) }
    var editAvatarUrl by remember(currentUser) { mutableStateOf(currentUser?.avatarUrl ?: "") }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            editAvatarUrl = uri.toString()
            currentUser?.let {
                onUpdateProfile(it.copy(avatarUrl = uri.toString()))
                Toast.makeText(context, "Foto de perfil actualizada", Toast.LENGTH_SHORT).show()
            }
        }
    }

    var isAdminMode by remember(currentUser) { mutableStateOf(currentUser?.isAdmin ?: false) }

    Scaffold(
        topBar = {
            LegioTopBar(
                title = "Mi Perfil",
                subtitle = "Configuración de Cuenta",
                showBack = true,
                onBackClick = onBackClick
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        if (currentUser == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Text("No hay sesión activa")
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(10.dp))

            // Avatar Section
            MemberAvatar(
                imageUrl = currentUser.avatarUrl,
                name = currentUser.fullName,
                size = 96.dp
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = {
                        photoPickerLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    },
                    modifier = Modifier.testTag("btn_change_profile_photo"),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.PhotoCamera, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Cambiar foto")
                }

                if (currentUser.avatarUrl.isNotBlank()) {
                    TextButton(
                        onClick = {
                            onUpdateProfile(currentUser.copy(avatarUrl = ""))
                            Toast.makeText(context, "Avatar restablecido a iniciales", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.testTag("btn_reset_initials_avatar")
                    ) {
                        Text("Usar iniciales", color = LegioWinePrimary)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = currentUser.fullName,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold
                )
            )

            Text(
                text = currentUser.email,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(6.dp))
            MemberTypeBadge(memberType = currentUser.memberType)

            Spacer(modifier = Modifier.height(20.dp))

            // Mis Datos Registrados Card / Modo Edición
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isEditing) "Editar Mis Datos" else "Mis Datos Registrados",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = LegioWinePrimary
                        )

                        TextButton(
                            onClick = {
                                if (isEditing) {
                                    // Save changes
                                    val isPriest = editMemberType == MemberType.PRIEST.name
                                    val updated = currentUser.copy(
                                        fullName = editFullName.trim(),
                                        birthDate = editBirthDate.trim(),
                                        city = editCity.trim(),
                                        phone = editPhone.trim(),
                                        email = editEmail.trim(),
                                        memberType = editMemberType,
                                        ordinationDate = if (isPriest) editOrdinationDate.trim() else null,
                                        hideMyData = editHideMyData
                                    )
                                    onUpdateProfile(updated)
                                    isEditing = false
                                    Toast.makeText(context, "Perfil guardado con éxito", Toast.LENGTH_SHORT).show()
                                } else {
                                    isEditing = true
                                }
                            },
                            modifier = Modifier.testTag("btn_edit_profile_toggle")
                        ) {
                            Icon(
                                imageVector = if (isEditing) Icons.Default.Check else Icons.Default.Edit,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = LegioWinePrimary
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (isEditing) "Guardar" else "Editar",
                                color = LegioWinePrimary,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    if (isEditing) {
                        // Edit inputs
                        OutlinedTextField(
                            value = editFullName,
                            onValueChange = { editFullName = it },
                            label = { Text("Nombre y Apellido") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = editBirthDate,
                            onValueChange = { editBirthDate = it },
                            label = { Text("Fecha de Cumpleaños (DD-MM-AAAA)") },
                            placeholder = { Text("04-12-1999") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = editCity,
                            onValueChange = { editCity = it },
                            label = { Text("Lugar donde vive (Ciudad / Provincia)") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = editPhone,
                            onValueChange = { editPhone = it },
                            label = { Text("Número de teléfono") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = editEmail,
                            onValueChange = { editEmail = it },
                            label = { Text("Correo electrónico") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = "Categoría de Miembro (Exclusiva)",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Spacer(modifier = Modifier.height(6.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            FilterChip(
                                selected = editMemberType == MemberType.PRIEST.name,
                                onClick = {
                                    editMemberType = if (editMemberType == MemberType.PRIEST.name) MemberType.NONE.name else MemberType.PRIEST.name
                                },
                                label = { Text("Sacerdote", fontSize = 12.sp) }
                            )
                            FilterChip(
                                selected = editMemberType == MemberType.BROTHER.name,
                                onClick = {
                                    editMemberType = if (editMemberType == MemberType.BROTHER.name) MemberType.NONE.name else MemberType.BROTHER.name
                                },
                                label = { Text("Hermano", fontSize = 12.sp) }
                            )
                            FilterChip(
                                selected = editMemberType == MemberType.CONSECRATED_LAITY.name,
                                onClick = {
                                    editMemberType = if (editMemberType == MemberType.CONSECRATED_LAITY.name) MemberType.NONE.name else MemberType.CONSECRATED_LAITY.name
                                },
                                label = { Text("Laico/a Consagrado/a", fontSize = 11.sp) }
                            )
                        }

                        if (editMemberType == MemberType.PRIEST.name) {
                            Spacer(modifier = Modifier.height(10.dp))
                            OutlinedTextField(
                                value = editOrdinationDate,
                                onValueChange = { editOrdinationDate = it },
                                label = { Text("Ordenación Sacerdotal (DD-MM-AAAA)") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    } else {
                        // Display view
                        ProfileDataRow("Ciudad", currentUser.city)
                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                        ProfileDataRow("Teléfono", currentUser.phone)
                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                        ProfileDataRow("Cumpleaños (DD-MM-AAAA)", currentUser.birthDate)
                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                        ProfileDataRow("Tipo de Miembro", currentUser.memberTypeDisplay)

                        if (currentUser.isPriest && !currentUser.ordinationDate.isNullOrBlank()) {
                            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                            ProfileDataRow("Ordenación Sacerdotal (DD-MM-AAAA)", currentUser.ordinationDate)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Privacy Toggle Card: "Quiero ocultar mis datos"
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (currentUser.hideMyData) MaterialTheme.colorScheme.surfaceVariant else LegioGoldContainer
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = if (currentUser.hideMyData) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = null,
                                    tint = LegioWinePrimary,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Quiero ocultar mis datos",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = LegioWinePrimary
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = if (currentUser.hideMyData)
                                    "Activado: Tu teléfono y correo están ocultos para los demás miembros de la Legión (solo el administrador puede verlos). Tus demás datos públicos continúan mostrándose normalmente."
                                else
                                    "Desactivado: Los demás miembros pueden ver tu teléfono y correo de contacto en tu perfil.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Switch(
                            checked = currentUser.hideMyData,
                            onCheckedChange = {
                                editHideMyData = it
                                onUpdateProfile(currentUser.copy(hideMyData = it))
                                Toast.makeText(
                                    context,
                                    if (it) "Datos de contacto ocultados para miembros" else "Datos visibles para miembros",
                                    Toast.LENGTH_SHORT
                                ).show()
                            },
                            modifier = Modifier.testTag("switch_profile_visibility")
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Admin Panel Card & Switcher (for prototype testing)
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.AdminPanelSettings,
                                contentDescription = null,
                                tint = LegioWinePrimary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Rol de Administrador",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Switch(
                            checked = isAdminMode,
                            onCheckedChange = {
                                isAdminMode = it
                                onUpdateProfile(currentUser.copy(isAdmin = it))
                            },
                            modifier = Modifier.testTag("switch_admin_mode")
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Permite gestionar cartelera, eventos, biblioteca, avisos y miembros de la Legión.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    if (isAdminMode) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = onNavigateToAdmin,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("btn_enter_admin_panel"),
                            colors = ButtonDefaults.buttonColors(containerColor = LegioWinePrimary)
                        ) {
                            Icon(Icons.Default.Dashboard, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Abrir Panel de Administración")
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Logout Button
            OutlinedButton(
                onClick = onLogout,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("btn_logout"),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
            ) {
                Icon(Icons.Default.Logout, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Cerrar Sesión")
            }

            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}

@Composable
fun ProfileDataRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(text = value, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium))
    }
}
