package com.kashta.kala.viewmodel

import androidx.lifecycle.ViewModel
import com.kashta.kala.data.model.PortfolioItem
import com.kashta.kala.data.repository.KashtaRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * ViewModel for the Portfolio screen.
 * Manages the carpenter's completed work showcase.
 */
class PortfolioViewModel : ViewModel() {

    private val repository = KashtaRepository()

    val portfolioItems: Flow<List<PortfolioItem>> = repository.portfolio

    private val _showAddDialog = MutableStateFlow(false)
    val showAddDialog: StateFlow<Boolean> = _showAddDialog.asStateFlow()

    // Input fields for new portfolio item
    private val _title = MutableStateFlow("")
    val title: StateFlow<String> = _title.asStateFlow()

    private val _description = MutableStateFlow("")
    val description: StateFlow<String> = _description.asStateFlow()

    private val _category = MutableStateFlow("Sofa")
    val category: StateFlow<String> = _category.asStateFlow()

    private val _customerName = MutableStateFlow("")
    val customerName: StateFlow<String> = _customerName.asStateFlow()

    private val _woodType = MutableStateFlow("Teak")
    val woodType: StateFlow<String> = _woodType.asStateFlow()

    private val _price = MutableStateFlow("")
    val price: StateFlow<String> = _price.asStateFlow()

    val categories = repository.furnitureTypes.dropLast(1) // Remove "Custom"
    val woodTypes = repository.woodTypes

    fun showDialog() { _showAddDialog.value = true }
    fun hideDialog() {
        _showAddDialog.value = false
        clearInputs()
    }

    fun updateTitle(value: String) { _title.value = value }
    fun updateDescription(value: String) { _description.value = value }
    fun updateCategory(value: String) { _category.value = value }
    fun updateCustomerName(value: String) { _customerName.value = value }
    fun updateWoodType(value: String) { _woodType.value = value }
    fun updatePrice(value: String) { _price.value = value }

    fun addPortfolioItem() {
        if (_title.value.isBlank()) return

        val categoryEmoji = when (_category.value) {
            "Sofa" -> "🛋️"
            "Bed" -> "🛏️"
            "Cabinet" -> "🗄️"
            "Table" -> "🪑"
            "Chair" -> "💺"
            else -> "🪵"
        }

        val item = PortfolioItem(
            title = _title.value,
            description = _description.value.ifBlank { "Handcrafted ${_category.value.lowercase()} made with ${_woodType.value} wood." },
            imageEmoji = categoryEmoji,
            category = _category.value,
            completedDate = java.time.LocalDate.now().toString(),
            customerName = _customerName.value.ifBlank { "Walk-in Customer" },
            woodType = _woodType.value,
            price = _price.value.toDoubleOrNull() ?: 0.0
        )
        repository.addPortfolioItem(item)
        hideDialog()
    }

    private fun clearInputs() {
        _title.value = ""
        _description.value = ""
        _category.value = "Sofa"
        _customerName.value = ""
        _woodType.value = "Teak"
        _price.value = ""
    }
}
