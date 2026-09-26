package com.example.ui.screens.news

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.data.model.NewsEntity
import com.example.ui.components.LegioTopBar
import com.example.ui.components.NewsHighlightCard

@Composable
fun NewsScreen(
    newsList: List<NewsEntity>,
    onNewsClick: (Long) -> Unit,
    onNavigateToProfile: () -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }

    val filteredNews = remember(searchQuery, newsList) {
        if (searchQuery.isBlank()) newsList
        else newsList.filter {
            it.title.contains(searchQuery, ignoreCase = true) ||
                    it.summary.contains(searchQuery, ignoreCase = true) ||
                    it.category.contains(searchQuery, ignoreCase = true)
        }
    }

    Scaffold(
        topBar = {
            LegioTopBar(
                title = "Cartelera",
                subtitle = "Crónicas, Cartas y Avisos Oficiales",
                onProfileClick = onNavigateToProfile
            )
        },
        containerColor = MaterialTheme.colorScheme.background,
        modifier = modifier
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .testTag("input_search_news"),
                placeholder = { Text("Buscar en la cartelera...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Buscar") },
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("news_list_view"),
                contentPadding = PaddingValues(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(filteredNews, key = { it.id }) { news ->
                    NewsHighlightCard(
                        news = news,
                        onViewDetail = { onNewsClick(news.id) }
                    )
                }
            }
        }
    }
}
