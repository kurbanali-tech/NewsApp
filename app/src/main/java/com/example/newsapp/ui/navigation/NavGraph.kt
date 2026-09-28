package com.example.newsapp.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.newsapp.data.model.Article
import com.example.newsapp.ui.screens.CategoriesScreen
import com.example.newsapp.ui.screens.DetailScreen
import com.example.newsapp.ui.screens.FavoritesScreen
import com.example.newsapp.ui.screens.HomeScreen
import com.example.newsapp.viewmodel.NewsViewModel

@Composable
fun NavGraph(
    navController: NavHostController,
    viewModel: NewsViewModel,
    onArticleClick: (Article) -> Unit,
    selectedArticle: Article?
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable(Screen.Home.route) {
            HomeScreen(viewModel = viewModel, onArticleClick = onArticleClick)
        }
        composable(Screen.Categories.route) {
            CategoriesScreen(viewModel = viewModel, onArticleClick = onArticleClick)
        }
        composable(Screen.Favorites.route) {
            FavoritesScreen(viewModel = viewModel, onArticleClick = onArticleClick)
        }
        composable(Screen.ArticleDetail.route) {
            selectedArticle?.let { article ->
                DetailScreen(
                    article = article,
                    viewModel = viewModel,
                    onBackClick = { navController.popBackStack() }
                )
            }
        }
    }
}
