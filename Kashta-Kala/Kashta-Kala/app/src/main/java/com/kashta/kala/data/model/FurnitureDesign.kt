package com.kashta.kala.data.model

/**
 * Represents a furniture design in the catalog.
 */
data class FurnitureDesign(
    val designId: String = "",
    val name: String = "",
    val category: String = "",          // "Sofa", "Bed", "Cabinet", "Table", "Chair"
    val description: String = "",
    val imageEmoji: String = "🪑",      // Emoji placeholder for visual representation
    val woodType: String = "Teak",      // "Teak", "Sheesham", "Pine", "Mango", "Sal"
    val estimatedPrice: Double = 0.0,
    val dimensions: String = "",        // e.g., "6ft × 3ft × 3ft"
    val isFavorite: Boolean = false,
    val rating: Float = 4.5f
)
