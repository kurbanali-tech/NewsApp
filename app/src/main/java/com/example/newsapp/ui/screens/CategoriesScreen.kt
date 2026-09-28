package com.example.newsapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.newsapp.data.model.Article
import com.example.newsapp.ui.components.ErrorState
import com.example.newsapp.ui.components.LoadingState
import com.example.newsapp.ui.components.NewsCard
import com.example.newsapp.viewmodel.NewsViewModel

@Composable
fun CategoriesScreen(
    viewModel: NewsViewModel,
    onArticleClick: (Article) -> Unit
) {
    val categories = listOf("General", "Business", "Technology", "Sports", "Entertainment", "Health", "Science")
    var selectedCategory by remember { mutableStateOf(categories[0]) }
    val categoryNewsState by viewModel.categoryNews.collectAsState()

    LaunchedEffect(selectedCategory) {
        viewModel.getNewsByCategory(selectedCategory)
    }

    Column(modifier = Modifier.fillMaxSize()) {
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(categories) { category ->
                FilterChip(
                    selected = selectedCategory == category,
                    onClick = { selectedCategory = category },
                    label = { Text(category) }
                )
            }
        }

        when (categoryNewsState) {
            is NewsViewModel.Resource.Loading -> LoadingState()
            is NewsViewModel.Resource.Success -> {
                LazyColumn {
                    items(categoryNewsState.data?.articles ?: emptyList()) { article ->
                        NewsCard(article = article, onClick = { onArticleClick(article) })
                    }
                }
            }
            is NewsViewModel.Resource.Error -> {
                ErrorState(message = categoryNewsState.message ?: "Unknown Error") {
                    viewModel.getNewsByCategory(selectedCategory)
                }
            }
        }
    }
}
