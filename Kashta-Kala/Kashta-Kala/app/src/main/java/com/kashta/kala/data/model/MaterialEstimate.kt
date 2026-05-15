package com.kashta.kala.data.model

/**
 * Represents the result of a material estimation calculation.
 */
data class MaterialEstimate(
    val furnitureType: String = "",
    val length: Double = 0.0,
    val width: Double = 0.0,
    val height: Double = 0.0,
    val area: Double = 0.0,             // L × W
    val volume: Double = 0.0,           // L × W × H
    val woodRequired: Double = 0.0,     // in cubic feet (with wastage)
    val wastagePercent: Double = 10.0,
    val unit: String = "ft"             // "ft" or "m"
)
