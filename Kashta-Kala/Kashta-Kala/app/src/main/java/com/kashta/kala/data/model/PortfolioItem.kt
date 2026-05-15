package com.kashta.kala.data.model

/**
 * Represents a completed work item in the carpenter's portfolio.
 */
data class PortfolioItem(
    val itemId: String = "",
    val title: String = "",
    val description: String = "",
    val imageEmoji: String = "🪵",
    val category: String = "",
    val completedDate: String = "",
    val customerName: String = "",
    val woodType: String = "",
    val price: Double = 0.0
)
