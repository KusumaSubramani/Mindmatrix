package com.kashta.kala.viewmodel

import androidx.lifecycle.ViewModel
import com.kashta.kala.data.repository.KashtaRepository
import kotlinx.coroutines.flow.Flow

/**
 * ViewModel for the Home/Dashboard screen.
 * Provides summary statistics and quick-access data.
 */
class HomeViewModel : ViewModel() {

    private val repository = KashtaRepository()

    val totalDesigns: Flow<Int> = repository.getTotalDesignCount()
    val favoriteCount: Flow<Int> = repository.getFavoriteCount()
    val quoteCount: Flow<Int> = repository.getQuoteCount()
    val portfolioCount: Flow<Int> = repository.getPortfolioCount()

    val recentQuotes = repository.getRecentQuotes()
    val featuredDesigns = repository.getDesignsByCategory("All")
}
