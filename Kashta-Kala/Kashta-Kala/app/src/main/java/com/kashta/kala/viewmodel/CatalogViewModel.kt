package com.kashta.kala.viewmodel

import androidx.lifecycle.ViewModel
import com.kashta.kala.data.model.FurnitureDesign
import com.kashta.kala.data.repository.KashtaRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest

/**
 * ViewModel for the Design Catalog screen.
 * Handles category filtering, search, and favorites.
 */
class CatalogViewModel : ViewModel() {

    private val repository = KashtaRepository()

    private val _selectedCategory = MutableStateFlow("All")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    val categories = repository.categories

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val designs: Flow<List<FurnitureDesign>> = _selectedCategory.flatMapLatest { category ->
        repository.getDesignsByCategory(category)
    }

    fun selectCategory(category: String) {
        _selectedCategory.value = category
    }

    fun updateSearch(query: String) {
        _searchQuery.value = query
    }

    fun toggleFavorite(designId: String) {
        repository.toggleFavorite(designId)
    }
}
