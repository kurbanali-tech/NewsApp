package com.example.newsapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.newsapp.data.database.ArticleDatabase
import com.example.newsapp.data.model.Article
import com.example.newsapp.data.repository.NewsRepository
import com.example.newsapp.ui.navigation.NavGraph
import com.example.newsapp.ui.navigation.Screen
import com.example.newsapp.ui.theme.NewsAppTheme
import com.example.newsapp.viewmodel.NewsViewModel
import com.example.newsapp.viewmodel.NewsViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val database = ArticleDatabase(this)
        val repository = NewsRepository(database)
        val viewModelFactory = NewsViewModelFactory(repository)
        val viewModel = ViewModelProvider(this, viewModelFactory)[NewsViewModel::class.java]

        enableEdgeToEdge()

        setContent {
            NewsAppTheme {
                val navController = rememberNavController()
                var selectedArticle by remember { mutableStateOf<Article?>(null) }
                
                val items = listOf(Screen.Home, Screen.Categories, Screen.Favorites)

                Scaffold(
                    bottomBar = {
                        val navBackStackEntry by navController.currentBackStackEntryAsState()
                        val currentDestination = navBackStackEntry?.destination
                        
                        // Only show bottom bar on main screens
                        if (items.any { it.route == currentDestination?.route }) {
                            NavigationBar {
                                items.forEach { screen ->
                                    NavigationBarItem(
                                        icon = { screen.icon?.let { Icon(it, contentDescription = screen.title) } },
                                        label = { Text(screen.title) },
                                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                                        onClick = {
                                            navController.navigate(screen.route) {
                                                popUpTo(navController.graph.startDestinationId) {
                                                    saveState = true
                                                }
                                                launchSingleTop = true
                                                restoreState = true
                                            }
                                        }
                                    )
                                }
                            }
                        }
                    }
                ) { innerPadding ->
                    Surface(modifier = Modifier.padding(innerPadding)) {
                        NavGraph(
                            navController = navController,
                            viewModel = viewModel,
                            selectedArticle = selectedArticle,
                            onArticleClick = { article ->
                                selectedArticle = article
                                navController.navigate(Screen.ArticleDetail.route)
                            }
                        )
                    }
                }
            }
        }
    }
}
