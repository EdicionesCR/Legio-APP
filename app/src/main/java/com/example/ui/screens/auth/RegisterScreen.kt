package com.example.ui.screens.auth

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.MemberType
import com.example.ui.components.LegioTopBar
import com.example.ui.components.MemberAvatar
import com.example.ui.theme.LegioGoldAccent
import com.example.ui.theme.LegioGoldContainer
import com.example.ui.theme.LegioWineDark
import com.example.ui.theme.LegioWinePrimary

@Composable
fun RegisterScreen(
    onRegisterSuccess: (
        fullName: String,
        birthDate: String,
        city: String,
        phone: String,
        email: String,
        memberType: String,
        ordinationDate: String?,
        hideMyData: Boolean,
        avatarUrl: String
    ) -> Unit,
    onBackClick: () -> Unit
) {
    var fullName by remember { mutableStateOf("") }
    var birthDate by remember { mutableStateOf("04-12-1999") }
    var city by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    
    // Member Types: NONE, PRIEST, BROTHER, CONSECRATED_LAITY (mutually exclusive)
    var selectedMemberType by remember { mutableStateOf(MemberType.NONE.name) }
    var ordinationDate by remember { mutableStateOf("22-11-2020") }
    
    // Privacy: true = hide phone & email from non-admins
    var hideMyData by remember { mutableStateOf(false) }

    // Avatar: URI from device or empty (which auto-generates institutional initials avatar)
    var selectedAvatar by remember { mutableStateOf("") }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            selectedAvatar = uri.toString()
        }
    }

    var errorMessage by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            LegioTopBar(
                title = "Crear Perfil",
                subtitle = "Registro de Nuevo Miembro",
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
                .padding(horizontal = 20.dp, vertical = 12.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = "Ficha de Miembro Legionario",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold
                ),
                color = LegioWinePrimary
            )
            Text(
                text = "Completa tus datos personales para incorporarte a la comunidad.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(18.dp))

            // 1. Fotografía de Perfil y Avatar Automático
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Fotografía de Perfil",
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.padding(4.dp)
                    ) {
                        MemberAvatar(
                            imageUrl = selectedAvatar,
                            name = fullName.ifBlank { "Legio Miembro" },
                            size = 84.dp
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = if (selectedAvatar.isBlank())
                            "Se utilizará el avatar institucional con tus iniciales."
                        else
                            "Fotografía seleccionada.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(
                            onClick = {
                                photoPickerLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = LegioWinePrimary),
                            modifier = Modifier.testTag("btn_pick_photo")
                        ) {
                            Icon(Icons.Default.PhotoCamera, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Subir foto de galería")
                        }

                        if (selectedAvatar.isNotBlank()) {
                            OutlinedButton(
                                onClick = { selectedAvatar = "" },
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.testTag("btn_remove_photo")
                            ) {
                                Text("Usar iniciales")
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 2. Datos personales
            OutlinedTextField(
                value = fullName,
                onValueChange = { fullName = it },
                label = { Text("Nombre y Apellido *") },
                placeholder = { Text("Ej. Juan Manuel Álvarez") },
                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("input_register_name")
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Fecha de Cumpleaños DD-MM-AAAA
            OutlinedTextField(
                value = birthDate,
                onValueChange = { birthDate = it },
                label = { Text("Fecha de Cumpleaños (DD-MM-AAAA) *") },
                placeholder = { Text("04-12-1999") },
                leadingIcon = { Icon(Icons.Default.Cake, contentDescription = null) },
                singleLine = true,
                supportingText = { Text("Formato: DD-MM-AAAA (Ejemplo: 04-12-1999)") },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("input_register_birthdate")
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = city,
                onValueChange = { city = it },
                label = { Text("Lugar donde vive (Ciudad / Provincia) *") },
                placeholder = { Text("Ej. Buenos Aires, Argentina") },
                leadingIcon = { Icon(Icons.Default.Place, contentDescription = null) },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("input_register_city")
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = phone,
                onValueChange = { phone = it },
                label = { Text("Número de teléfono *") },
                placeholder = { Text("Ej. +54 9 11 5566-7788") },
                leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("input_register_phone")
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Correo electrónico *") },
                placeholder = { Text("nombre@ejemplo.com") },
                leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("input_register_email")
            )

            Spacer(modifier = Modifier.height(20.dp))

            // 3. Tipo de Miembro (Mutuamente excluyentes)
            Text(
                text = "Tipo de Miembro de la Legión",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = LegioWinePrimary
            )
            Text(
                text = "Selecciona una categoría exclusiva si corresponde a tu estado:",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Option: Soy Sacerdote
            MemberTypeSelectCard(
                title = "Soy Sacerdote",
                description = "Ministerio presbiteral y celebración de los Santos Sacramentos",
                isSelected = selectedMemberType == MemberType.PRIEST.name,
                onSelect = {
                    selectedMemberType = if (selectedMemberType == MemberType.PRIEST.name) MemberType.NONE.name else MemberType.PRIEST.name
                },
                testTag = "type_priest_card"
            )

            AnimatedVisibility(visible = selectedMemberType == MemberType.PRIEST.name) {
                Column(modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)) {
                    OutlinedTextField(
                        value = ordinationDate,
                        onValueChange = { ordinationDate = it },
                        label = { Text("Fecha de Ordenación Sacerdotal (DD-MM-AAAA)") },
                        placeholder = { Text("Ej. 28-10-2012") },
                        leadingIcon = { Icon(Icons.Default.CalendarToday, contentDescription = null) },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_ordination_date")
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Option: Soy Hermano
            MemberTypeSelectCard(
                title = "Soy Hermano",
                description = "Consagración como Hermano religioso en la Legión de Cristo Rey",
                isSelected = selectedMemberType == MemberType.BROTHER.name,
                onSelect = {
                    selectedMemberType = if (selectedMemberType == MemberType.BROTHER.name) MemberType.NONE.name else MemberType.BROTHER.name
                },
                testTag = "type_brother_card"
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Option: Soy Laico/a Consagrado/a
            MemberTypeSelectCard(
                title = "Soy Laico/a Consagrado/a",
                description = "Vida consagrada y apostolado en medio del mundo",
                isSelected = selectedMemberType == MemberType.CONSECRATED_LAITY.name,
                onSelect = {
                    selectedMemberType = if (selectedMemberType == MemberType.CONSECRATED_LAITY.name) MemberType.NONE.name else MemberType.CONSECRATED_LAITY.name
                },
                testTag = "type_consecrated_card"
            )

            Spacer(modifier = Modifier.height(20.dp))

            // 4. Privacidad de los Datos: "Quiero ocultar mis datos"
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (hideMyData) MaterialTheme.colorScheme.surfaceVariant else LegioGoldContainer
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (hideMyData) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                contentDescription = null,
                                tint = LegioWinePrimary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Quiero ocultar mis datos",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = LegioWinePrimary
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = if (hideMyData)
                                "Activado: Los demás miembros NO podrán ver tu número de teléfono ni correo electrónico. El Administrador sí podrá verlos. Los demás datos públicos permanecerán visibles."
                            else
                                "Desactivado: Los demás miembros pueden ver los datos de contacto de tu perfil institucional.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Switch(
                        checked = hideMyData,
                        onCheckedChange = { hideMyData = it },
                        modifier = Modifier.testTag("switch_hide_my_data")
                    )
                }
            }

            if (errorMessage != null) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = errorMessage!!,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error
                )
            }

            Spacer(modifier = Modifier.height(26.dp))

            Button(
                onClick = {
                    if (fullName.isBlank() || email.isBlank()) {
                        errorMessage = "Por favor completa al menos tu nombre y correo electrónico."
                    } else {
                        errorMessage = null
                        onRegisterSuccess(
                            fullName.trim(),
                            birthDate.trim(),
                            city.trim().ifBlank { "Argentina" },
                            phone.trim(),
                            email.trim(),
                            selectedMemberType,
                            if (selectedMemberType == MemberType.PRIEST.name) ordinationDate.trim() else null,
                            hideMyData,
                            selectedAvatar
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("btn_submit_register"),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = LegioWinePrimary,
                    contentColor = Color.White
                )
            ) {
                Text(
                    text = "Completar Registro y Entrar",
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                )
            }

            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}

@Composable
fun MemberTypeSelectCard(
    title: String,
    description: String,
    isSelected: Boolean,
    onSelect: () -> Unit,
    testTag: String
) {
    Card(
        onClick = onSelect,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) LegioWinePrimary.copy(alpha = 0.08f) else MaterialTheme.colorScheme.surface
        ),
        border = androidx.compose.foundation.BorderStroke(
            width = if (isSelected) 2.dp else 1.dp,
            color = if (isSelected) LegioWinePrimary else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
        ),
        modifier = Modifier
            .fillMaxWidth()
            .testTag(testTag)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(
                selected = isSelected,
                onClick = onSelect,
                colors = RadioButtonDefaults.colors(selectedColor = LegioWinePrimary)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    color = if (isSelected) LegioWinePrimary else MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
