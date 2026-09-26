package com.example.ui.components

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.LegioGoldAccent
import com.example.ui.theme.LegioWinePrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LegioTopBar(
    title: String,
    subtitle: String? = "Legión de Cristo Rey",
    showBack: Boolean = false,
    onBackClick: () -> Unit = {},
    showAdminBadge: Boolean = false,
    onAdminClick: (() -> Unit)? = null,
    onProfileClick: (() -> Unit)? = null,
    activeAnnouncementsCount: Int = 0,
    onAnnouncementsClick: (() -> Unit)? = null,
    showThesaurus: Boolean = false,
    onThesaurusClick: (() -> Unit)? = null
) {
    val context = LocalContext.current

    TopAppBar(
        title = {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        ),
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    if (showAdminBadge) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Surface(
                            shape = CircleShape,
                            color = LegioGoldAccent,
                            contentColor = Color.Black,
                            modifier = Modifier.padding(top = 2.dp)
                        ) {
                            Text(
                                text = "ADMIN",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
                if (!subtitle.isNullOrBlank()) {
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                    )
                }
            }
        },
        navigationIcon = {
            if (showBack) {
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier.testTag("top_bar_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Volver",
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            } else {
                Box(
                    modifier = Modifier
                        .padding(start = 10.dp, end = 4.dp)
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(LegioGoldAccent.copy(alpha = 0.15f))
                        .padding(2.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.escudo_vectoreado_2),
                        contentDescription = "Escudo Oficial LEGIO",
                        modifier = Modifier
                            .fillMaxSize(),
                        contentScale = ContentScale.Fit
                    )
                }
            }
        },
        actions = {
            // 1. Megáfono (Avisos)
            if (onAnnouncementsClick != null) {
                IconButton(
                    onClick = onAnnouncementsClick,
                    modifier = Modifier.testTag("top_bar_announcements_button")
                ) {
                    BadgedBox(
                        badge = {
                            if (activeAnnouncementsCount > 0) {
                                Badge(containerColor = LegioGoldAccent, contentColor = Color.Black) {
                                    Text(
                                        text = "$activeAnnouncementsCount",
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Campaign,
                            contentDescription = "Avisos",
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }

            // 2. Thesaurus (Cofre) — A la derecha del megáfono
            // Visible solo para Sacerdotes y Hermanos
            if (showThesaurus) {
                IconButton(
                    onClick = {
                        if (onThesaurusClick != null) {
                            onThesaurusClick()
                        } else {
                            try {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://thesauruscleri.va/es.html"))
                                context.startActivity(intent)
                            } catch (e: Exception) {
                                Toast.makeText(context, "Abriendo Thesaurus Cleri...", Toast.LENGTH_SHORT).show()
                            }
                        }
                    },
                    modifier = Modifier.testTag("top_bar_thesaurus_button")
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_thesaurus_chest),
                        contentDescription = "Thesaurus",
                        tint = LegioGoldAccent,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            // 3. Panel de Administrador (si corresponde)
            if (onAdminClick != null) {
                IconButton(
                    onClick = onAdminClick,
                    modifier = Modifier.testTag("top_bar_admin_button")
                ) {
                    Icon(
                        imageVector = Icons.Outlined.AdminPanelSettings,
                        contentDescription = "Panel Administrador",
                        tint = LegioGoldAccent
                    )
                }
            }

            // 4. Perfil de Usuario
            if (onProfileClick != null) {
                IconButton(
                    onClick = onProfileClick,
                    modifier = Modifier.testTag("top_bar_profile_button")
                ) {
                    Icon(
                        imageVector = Icons.Outlined.AccountCircle,
                        contentDescription = "Mi Perfil",
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
        )
    )
}
