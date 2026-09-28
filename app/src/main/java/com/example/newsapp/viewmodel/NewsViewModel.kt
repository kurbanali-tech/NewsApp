package com.example.newsapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.newsapp.data.model.Article
import com.example.newsapp.data.model.NewsResponse
import com.example.newsapp.data.repository.NewsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.Response

class NewsViewModel(private val repository: NewsRepository) : ViewModel() {

    private val _breakingNews = MutableStateFlow<Resource<NewsResponse>>(Resource.Loading())
    val breakingNews: StateFlow<Resource<NewsResponse>> = _breakingNews.asStateFlow()

    private val _searchNews = MutableStateFlow<Resource<NewsResponse>>(Resource.Success(NewsResponse(emptyList(), "ok", 0)))
    val searchNews: StateFlow<Resource<NewsResponse>> = _searchNews.asStateFlow()

    private val _categoryNews = MutableStateFlow<Resource<NewsResponse>>(Resource.Loading())
    val categoryNews: StateFlow<Resource<NewsResponse>> = _categoryNews.asStateFlow()

    init {
        getBreakingNews("us")
    }

    fun getBreakingNews(countryCode: String) = viewModelScope.launch {
        _breakingNews.value = Resource.Loading()
        try {
            val response = repository.getTopHeadlines(countryCode, 1)
            _breakingNews.value = handleNewsResponse(response)
        } catch (e: Exception) {
            _breakingNews.value = Resource.Error(e.message ?: "An unknown error occurred")
        }
    }

    fun searchNews(searchQuery: String) = viewModelScope.launch {
        if (searchQuery.isEmpty()) {
            _searchNews.value = Resource.Success(NewsResponse(emptyList(), "ok", 0))
            return@launch
        }
        _searchNews.value = Resource.Loading()
        try {
            val response = repository.searchNews(searchQuery, 1)
            _searchNews.value = handleNewsResponse(response)
        } catch (e: Exception) {
            _searchNews.value = Resource.Error(e.message ?: "An unknown error occurred")
        }
    }

    fun getNewsByCategory(category: String) = viewModelScope.launch {
        _categoryNews.value = Resource.Loading()
        try {
            val response = repository.getNewsByCategory(category.lowercase())
            _categoryNews.value = handleNewsResponse(response)
        } catch (e: Exception) {
            _categoryNews.value = Resource.Error(e.message ?: "An unknown error occurred")
        }
    }

    private fun handleNewsResponse(response: Response<NewsResponse>): Resource<NewsResponse> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                return Resource.Success(resultResponse)
            }
        }
        return Resource.Error(response.message())
    }

    fun saveArticle(article: Article) = viewModelScope.launch {
        repository.upsert(article)
    }

    fun getFavoriteNews() = repository.getFavoriteNews()

    fun deleteArticle(article: Article) = viewModelScope.launch {
        repository.deleteArticle(article)
    }

    fun isFavorite(url: String) = repository.isFavorite(url)

    sealed class Resource<T>(
        val data: T? = null,
        val message: String? = null
    ) {
        class Success<T>(data: T) : Resource<T>(data)
        class Error<T>(message: String, data: T? = null) : Resource<T>(data, message)
        class Loading<T> : Resource<T>()
    }
}

class NewsViewModelFactory(private val repository: NewsRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NewsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return NewsViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
