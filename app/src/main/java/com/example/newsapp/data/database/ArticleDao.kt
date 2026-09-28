package com.example.newsapp.data.database

import androidx.room.*
import com.example.newsapp.data.model.Article
import kotlinx.coroutines.flow.Flow

@Dao
interface ArticleDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(article: Article): Long

    @Query("SELECT * FROM articles")
    fun getAllArticles(): Flow<List<Article>>

    @Delete
    suspend fun deleteArticle(article: Article)
    
    @Query("SELECT EXISTS(SELECT * FROM articles WHERE url = :url)")
    fun isFavorite(url: String): Flow<Boolean>
}
