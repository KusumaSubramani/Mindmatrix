package com.kashta.kala.viewmodel

import androidx.lifecycle.ViewModel
import com.kashta.kala.data.model.MaterialEstimate
import com.kashta.kala.data.model.PriceQuote
import com.kashta.kala.data.repository.KashtaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * ViewModel for the Material Estimator & Price Quote Generator screen.
 * Handles dimension inputs, area/volume calculations, and quote generation.
 */
class EstimatorViewModel : ViewModel() {

    private val repository = KashtaRepository()

    // Input fields
    private val _furnitureType = MutableStateFlow("Sofa")
    val furnitureType: StateFlow<String> = _furnitureType.asStateFlow()

    private val _woodType = MutableStateFlow("Teak")
    val woodType: StateFlow<String> = _woodType.asStateFlow()

    private val _length = MutableStateFlow("")
    val length: StateFlow<String> = _length.asStateFlow()

    private val _width = MutableStateFlow("")
    val width: StateFlow<String> = _width.asStateFlow()

    private val _height = MutableStateFlow("")
    val height: StateFlow<String> = _height.asStateFlow()

    private val _customerName = MutableStateFlow("")
    val customerName: StateFlow<String> = _customerName.asStateFlow()

    // Results
    private val _estimate = MutableStateFlow<MaterialEstimate?>(null)
    val estimate: StateFlow<MaterialEstimate?> = _estimate.asStateFlow()

    private val _quote = MutableStateFlow<PriceQuote?>(null)
    val quote: StateFlow<PriceQuote?> = _quote.asStateFlow()

    private val _showEstimate = MutableStateFlow(false)
    val showEstimate: StateFlow<Boolean> = _showEstimate.asStateFlow()

    private val _showQuote = MutableStateFlow(false)
    val showQuote: StateFlow<Boolean> = _showQuote.asStateFlow()

    val furnitureTypes = repository.furnitureTypes
    val woodTypes = repository.woodTypes
    val woodPrices = repository.woodPricePerCubicFoot

    fun updateFurnitureType(type: String) { _furnitureType.value = type }
    fun updateWoodType(type: String) { _woodType.value = type }
    fun updateLength(value: String) { _length.value = value }
    fun updateWidth(value: String) { _width.value = value }
    fun updateHeight(value: String) { _height.value = value }
    fun updateCustomerName(name: String) { _customerName.value = name }

    fun calculateEstimate() {
        val l = _length.value.toDoubleOrNull() ?: return
        val w = _width.value.toDoubleOrNull() ?: return
        val h = _height.value.toDoubleOrNull() ?: return

        val result = repository.calculateEstimate(
            furnitureType = _furnitureType.value,
            length = l,
            width = w,
            height = h
        )
        _estimate.value = result
        _showEstimate.value = true
        _showQuote.value = false
    }

    fun generateQuote() {
        val l = _length.value.toDoubleOrNull() ?: return
        val w = _width.value.toDoubleOrNull() ?: return
        val h = _height.value.toDoubleOrNull() ?: return
        val name = _customerName.value.ifBlank { "Customer" }

        val result = repository.generateQuote(
            customerName = name,
            furnitureType = _furnitureType.value,
            woodType = _woodType.value,
            length = l,
            width = w,
            height = h
        )
        _quote.value = result
        _showQuote.value = true
    }

    fun clearResults() {
        _estimate.value = null
        _quote.value = null
        _showEstimate.value = false
        _showQuote.value = false
        _length.value = ""
        _width.value = ""
        _height.value = ""
        _customerName.value = ""
    }
}
