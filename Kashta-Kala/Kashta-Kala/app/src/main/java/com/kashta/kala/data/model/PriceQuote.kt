package com.kashta.kala.data.model

/**
 * Represents a generated price quotation for a furniture piece.
 */
data class PriceQuote(
    val quoteId: String = "",
    val customerName: String = "",
    val furnitureType: String = "",
    val woodType: String = "",
    val dimensions: String = "",        // "6ft × 5ft × 3ft"
    val materialCost: Double = 0.0,
    val laborCost: Double = 0.0,
    val totalCost: Double = 0.0,
    val description: String = "",       // AI-generated or template quote description
    val createdAt: Long = System.currentTimeMillis()
)
