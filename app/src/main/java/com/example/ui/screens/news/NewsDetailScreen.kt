package com.example.ui.screens.news

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.NewsEntity
import com.example.ui.components.LegioTopBar
import com.example.ui.theme.LegioGoldAccent
import com.example.ui.theme.LegioGoldMuted
import com.example.ui.theme.LegioWineDark
import com.example.ui.theme.LegioWinePrimary

@Composable
fun NewsDetailScreen(
    news: NewsEntity?,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current

    Scaffold(
        topBar = {
            LegioTopBar(
                title = "Cartelera",
                subtitle = news?.category ?: "Publicación",
                showBack = true,
                onBackClick = onBackClick
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        if (news == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Text("Publicación no encontrada")
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            if (news.imageUrl.isNotBlank()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(230.dp)
                ) {
                    AsyncImage(
                        model = news.imageUrl,
                        contentDescription = news.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }

            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = LegioWinePrimary,
                        contentColor = Color.White
                    ) {
                        Text(
                            text = news.category,
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }

                    Text(
                        text = news.date,
                        style = MaterialTheme.typography.bodySmall,
                        color = LegioGoldMuted
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = news.title,
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        lineHeight = 28.sp
                    ),
                    color = MaterialTheme.colorScheme.onBackground
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Lead Paragraph (Summary)
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = LegioWinePrimary.copy(alpha = 0.05f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, LegioWinePrimary.copy(alpha = 0.15f))
                ) {
                    Text(
                        text = news.summary,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Medium,
                            lineHeight = 22.sp
                        ),
                        color = LegioWinePrimary,
                        modifier = Modifier.padding(14.dp)
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Full text content
                Text(
                    text = news.content,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        lineHeight = 25.sp,
                        letterSpacing = 0.3.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(28.dp))

                // Share Button
                OutlinedButton(
                    onClick = {
                        shareNews(context, news)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("btn_share_news"),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Outlined.Share, contentDescription = null, modifier = Modifier.size(18.dp), tint = LegioWinePrimary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Compartir publicación con hermanos legionarios", color = LegioWinePrimary)
                }

                Spacer(modifier = Modifier.height(30.dp))
            }
        }
    }
}

fun shareNews(context: Context, news: NewsEntity) {
    try {
        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_SUBJECT, news.title)
            putExtra(Intent.EXTRA_TEXT, "Legión de Cristo Rey — Cartelera:\n*${news.title}*\n${news.date}\n\n${news.summary}")
            type = "text/plain"
        }
        context.startActivity(Intent.createChooser(sendIntent, "Compartir publicación"))
    } catch (e: Exception) {
        Toast.makeText(context, "Compartiendo...", Toast.LENGTH_SHORT).show()
    }
}
