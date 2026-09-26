package com.example.ui.components

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R

data class SocialChannel(
    val name: String,
    @DrawableRes val logoRes: Int,
    val brandColor: Color,
    val url: String,
    val testTag: String
)

@Composable
fun SocialLinksRow(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    val channels = listOf(
        SocialChannel(
            name = "Facebook",
            logoRes = R.drawable.ic_social_facebook,
            brandColor = Color(0xFF1877F2),
            url = "https://www.facebook.com/legioncristorey",
            testTag = "social_facebook"
        ),
        SocialChannel(
            name = "WhatsApp",
            logoRes = R.drawable.ic_social_whatsapp,
            brandColor = Color(0xFF25D366),
            url = "https://chat.whatsapp.com/sample-legio-canal",
            testTag = "social_whatsapp"
        ),
        SocialChannel(
            name = "Instagram",
            logoRes = R.drawable.ic_social_instagram,
            brandColor = Color(0xFFE4405F),
            url = "https://www.instagram.com/legioncristorey",
            testTag = "social_instagram"
        ),
        SocialChannel(
            name = "Spotify",
            logoRes = R.drawable.ic_social_spotify,
            brandColor = Color(0xFF1DB954),
            url = "https://open.spotify.com/show/sample-legio-meditaciones",
            testTag = "social_spotify"
        ),
        SocialChannel(
            name = "YouTube",
            logoRes = R.drawable.ic_social_youtube,
            brandColor = Color(0xFFFF0000),
            url = "https://www.youtube.com/@legioncristorey",
            testTag = "social_youtube"
        )
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .testTag("card_social_channels"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Canales y Plataformas Oficiales",
                style = MaterialTheme.typography.titleSmall.copy(
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                ),
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Seguinos en nuestras redes y transmisiones",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                channels.forEach { channel ->
                    SocialButton(
                        channel = channel,
                        onClick = {
                            openUrl(context, channel.url, channel.name)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun SocialButton(
    channel: SocialChannel,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable(onClick = onClick)
            .testTag(channel.testTag)
            .padding(4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(46.dp)
                .clip(CircleShape)
                .background(channel.brandColor.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = channel.logoRes),
                contentDescription = channel.name,
                modifier = Modifier.size(26.dp)
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = channel.name,
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

fun openUrl(context: Context, url: String, label: String) {
    try {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        context.startActivity(intent)
    } catch (e: Exception) {
        Toast.makeText(context, "Abriendo $label...", Toast.LENGTH_SHORT).show()
    }
}
