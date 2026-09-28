package com.example.newsapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.newsapp.data.model.Article
import com.example.newsapp.ui.components.ErrorState
import com.example.newsapp.ui.components.LoadingState
import com.example.newsapp.ui.components.NewsCard
import com.example.newsapp.viewmodel.NewsViewModel

@Composable
fun HomeScreen(
    viewModel: NewsViewModel,
    onArticleClick: (Article) -> Unit
) {
    val breakingNewsState by viewModel.breakingNews.collectAsState()
    val searchNewsState by viewModel.searchNews.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    var isSearching by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize()) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = {
                searchQuery = it
                isSearching = it.isNotEmpty()
                viewModel.searchNews(it)
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            placeholder = { Text("Search news...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            singleLine = true,
            shape = MaterialTheme.shapes.medium
        )

        val currentState = if (isSearching) searchNewsState else breakingNewsState

        when (currentState) {
            is NewsViewModel.Resource.Loading -> LoadingState()
            is NewsViewModel.Resource.Success -> {
                val articles = currentState.data?.articles ?: emptyList()
                if (articles.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No news found")
                    }
                } else {
                    LazyColumn {
                        items(articles) { article ->
                            NewsCard(article = article, onClick = { onArticleClick(article) })
                        }
                    }
                }
            }
            is NewsViewModel.Resource.Error -> {
                ErrorState(message = currentState.message ?: "Unknown Error") {
                    if (isSearching) viewModel.searchNews(searchQuery)
                    else viewModel.getBreakingNews("us")
                }
            }
        }
    }
}
