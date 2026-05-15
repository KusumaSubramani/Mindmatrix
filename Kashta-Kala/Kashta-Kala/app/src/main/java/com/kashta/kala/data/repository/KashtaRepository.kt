package com.kashta.kala.data.repository

import com.kashta.kala.data.model.FurnitureDesign
import com.kashta.kala.data.model.MaterialEstimate
import com.kashta.kala.data.model.PortfolioItem
import com.kashta.kala.data.model.PriceQuote
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import java.util.UUID

/**
 * Central repository managing all data operations for Kashta-Kala.
 * Uses in-memory dummy data as fallback when Firebase is not configured.
 * In production, swap the backing stores with Firestore calls.
 */
class KashtaRepository {

    // ── Wood Price Map (₹ per cubic foot) ───────────────────────────────────
    val woodPricePerCubicFoot = mapOf(
        "Teak" to 3500.0,
        "Sheesham" to 2800.0,
        "Pine" to 1800.0,
        "Mango" to 2200.0,
        "Sal" to 3000.0
    )

    val woodTypes = listOf("Teak", "Sheesham", "Pine", "Mango", "Sal")
    val furnitureTypes = listOf("Sofa", "Bed", "Cabinet", "Table", "Chair", "Custom")
    val categories = listOf("All", "Sofa", "Bed", "Cabinet", "Table", "Chair")

    // ── Furniture Design Catalog ────────────────────────────────────────────
    private val _designs = MutableStateFlow(
        mutableListOf(
            // Sofas
            FurnitureDesign("d1", "Royal Teak Sofa", "Sofa", "Premium 3-seater sofa with intricate carving and plush cushion support. Perfect for living rooms.", "🛋️", "Teak", 45000.0, "6ft × 2.5ft × 3ft", rating = 4.8f),
            FurnitureDesign("d2", "Modern L-Shape Sofa", "Sofa", "Contemporary L-shaped corner sofa with clean lines and modular design.", "🛋️", "Sheesham", 62000.0, "8ft × 6ft × 3ft", rating = 4.6f),
            FurnitureDesign("d3", "Classic Wooden Sofa", "Sofa", "Traditional Indian wooden sofa with lattice backrest and carved armrests.", "🛋️", "Mango", 28000.0, "5ft × 2ft × 3ft", rating = 4.3f),

            // Beds
            FurnitureDesign("d4", "King Size Poster Bed", "Bed", "Majestic king-size bed with four poster design and headboard carving.", "🛏️", "Teak", 75000.0, "7ft × 6.5ft × 5ft", rating = 4.9f),
            FurnitureDesign("d5", "Queen Storage Bed", "Bed", "Space-saving queen bed with hydraulic storage and minimalist design.", "🛏️", "Sheesham", 55000.0, "6.5ft × 5ft × 3.5ft", rating = 4.7f),
            FurnitureDesign("d6", "Single Bed with Drawers", "Bed", "Compact single bed with two pull-out storage drawers. Ideal for kids' rooms.", "🛏️", "Pine", 22000.0, "6ft × 3ft × 3ft", rating = 4.4f),

            // Cabinets
            FurnitureDesign("d7", "Wardrobe Cabinet 3-Door", "Cabinet", "Spacious three-door wardrobe with mirror, hanging rod, and shelf compartments.", "🗄️", "Teak", 48000.0, "6ft × 2ft × 7ft", rating = 4.5f),
            FurnitureDesign("d8", "Kitchen Cabinet Set", "Cabinet", "Modular kitchen cabinet set with upper and lower units, soft-close hinges.", "🗄️", "Pine", 35000.0, "8ft × 1.5ft × 7ft", rating = 4.6f),
            FurnitureDesign("d9", "Bookshelf Cabinet", "Cabinet", "Open bookshelf with five tiers and bottom cabinet doors.", "🗄️", "Sheesham", 18000.0, "3ft × 1ft × 6ft", rating = 4.2f),

            // Tables
            FurnitureDesign("d10", "Dining Table 6-Seater", "Table", "Elegant 6-seater dining table with solid wood top and carved legs.", "🪑", "Teak", 38000.0, "5ft × 3ft × 2.5ft", rating = 4.7f),
            FurnitureDesign("d11", "Study Table with Shelf", "Table", "Compact study desk with integrated bookshelf and cable management.", "🪑", "Mango", 15000.0, "4ft × 2ft × 3ft", rating = 4.3f),
            FurnitureDesign("d12", "Coffee Table Round", "Table", "Circular coffee table with glass top and wooden base frame.", "🪑", "Sal", 12000.0, "3ft × 3ft × 1.5ft", rating = 4.5f),

            // Chairs
            FurnitureDesign("d13", "Rocking Chair Classic", "Chair", "Traditional rocking chair with curved runners and comfortable backrest.", "💺", "Teak", 16000.0, "2ft × 2ft × 3.5ft", rating = 4.8f),
            FurnitureDesign("d14", "Dining Chair Set (4)", "Chair", "Set of 4 matching dining chairs with padded seats and ladder-back design.", "💺", "Sheesham", 24000.0, "1.5ft × 1.5ft × 3ft", rating = 4.4f),
            FurnitureDesign("d15", "Office Wooden Chair", "Chair", "Ergonomic wooden office chair with swivel base and lumbar support.", "💺", "Mango", 11000.0, "2ft × 2ft × 3.5ft", rating = 4.1f)
        )
    )
    val designs: Flow<List<FurnitureDesign>> = _designs.asStateFlow()

    fun getDesignsByCategory(category: String): Flow<List<FurnitureDesign>> {
        return _designs.map { list ->
            if (category == "All") list
            else list.filter { it.category == category }
        }
    }

    fun getFavoriteDesigns(): Flow<List<FurnitureDesign>> {
        return _designs.map { list -> list.filter { it.isFavorite } }
    }

    fun getFavoriteCount(): Flow<Int> {
        return _designs.map { list -> list.count { it.isFavorite } }
    }

    fun getTotalDesignCount(): Flow<Int> {
        return _designs.map { it.size }
    }

    fun toggleFavorite(designId: String) {
        val current = _designs.value.toMutableList()
        val index = current.indexOfFirst { it.designId == designId }
        if (index != -1) {
            current[index] = current[index].copy(isFavorite = !current[index].isFavorite)
            _designs.value = current
        }
    }

    fun searchDesigns(query: String): Flow<List<FurnitureDesign>> {
        return _designs.map { list ->
            if (query.isBlank()) list
            else list.filter {
                it.name.contains(query, ignoreCase = true) ||
                it.category.contains(query, ignoreCase = true) ||
                it.woodType.contains(query, ignoreCase = true)
            }
        }
    }

    // ── Material Estimation ─────────────────────────────────────────────────

    fun calculateEstimate(
        furnitureType: String,
        length: Double,
        width: Double,
        height: Double,
        unit: String = "ft"
    ): MaterialEstimate {
        val area = length * width
        val volume = length * width * height
        val wastagePercent = 10.0
        val woodRequired = volume * (1 + wastagePercent / 100)

        return MaterialEstimate(
            furnitureType = furnitureType,
            length = length,
            width = width,
            height = height,
            area = area,
            volume = volume,
            woodRequired = woodRequired,
            wastagePercent = wastagePercent,
            unit = unit
        )
    }

    // ── Price Quotes ────────────────────────────────────────────────────────
    private val _quotes = MutableStateFlow(
        mutableListOf(
            PriceQuote(
                quoteId = "q1",
                customerName = "Ramesh Kumar",
                furnitureType = "Bed",
                woodType = "Teak",
                dimensions = "6.5ft × 5ft × 3.5ft",
                materialCost = 39812.0,
                laborCost = 11944.0,
                totalCost = 51756.0,
                description = "This premium teak wood queen-size bed is crafted for durability and elegance. Features smooth finish and traditional joints.",
                createdAt = System.currentTimeMillis() - 86400000
            ),
            PriceQuote(
                quoteId = "q2",
                customerName = "Suresh Patil",
                furnitureType = "Cabinet",
                woodType = "Sheesham",
                dimensions = "6ft × 2ft × 7ft",
                materialCost = 25872.0,
                laborCost = 7762.0,
                totalCost = 33634.0,
                description = "Sturdy sheesham wood 3-door wardrobe with elegant grain pattern. Includes mirror panel and adjustable shelves.",
                createdAt = System.currentTimeMillis() - 172800000
            )
        )
    )
    val quotes: Flow<List<PriceQuote>> = _quotes.asStateFlow()

    fun getQuoteCount(): Flow<Int> {
        return _quotes.map { it.size }
    }

    fun getRecentQuotes(): Flow<List<PriceQuote>> {
        return _quotes.map { list -> list.sortedByDescending { it.createdAt } }
    }

    fun generateQuote(
        customerName: String,
        furnitureType: String,
        woodType: String,
        length: Double,
        width: Double,
        height: Double
    ): PriceQuote {
        val volume = length * width * height
        val woodWithWastage = volume * 1.10 // 10% wastage
        val pricePerCft = woodPricePerCubicFoot[woodType] ?: 2500.0
        val materialCost = woodWithWastage * pricePerCft
        val laborCost = materialCost * 0.30 // 30% labor
        val totalCost = materialCost + laborCost

        val description = generateQuoteDescription(furnitureType, woodType, length, width, height)

        val quote = PriceQuote(
            quoteId = UUID.randomUUID().toString().take(8),
            customerName = customerName,
            furnitureType = furnitureType,
            woodType = woodType,
            dimensions = "${length}ft × ${width}ft × ${height}ft",
            materialCost = Math.round(materialCost * 100.0) / 100.0,
            laborCost = Math.round(laborCost * 100.0) / 100.0,
            totalCost = Math.round(totalCost * 100.0) / 100.0,
            description = description,
            createdAt = System.currentTimeMillis()
        )

        val updated = _quotes.value.toMutableList().apply { add(0, quote) }
        _quotes.value = updated
        return quote
    }

    private fun generateQuoteDescription(
        furnitureType: String,
        woodType: String,
        length: Double,
        width: Double,
        height: Double
    ): String {
        val qualityMap = mapOf(
            "Teak" to "premium, naturally durable",
            "Sheesham" to "elegant, richly grained",
            "Pine" to "lightweight, sustainably sourced",
            "Mango" to "eco-friendly, uniquely patterned",
            "Sal" to "extremely sturdy, weather-resistant"
        )
        val quality = qualityMap[woodType] ?: "high-quality"

        return when (furnitureType) {
            "Sofa" -> "This $quality $woodType wood sofa (${length}×${width}×${height} ft) is handcrafted for lasting comfort and style. Features precision joinery and a smooth polished finish that enhances any living space."
            "Bed" -> "This $quality $woodType wood bed (${length}×${width}×${height} ft) is designed for restful sleep and timeless beauty. Built with traditional mortise-and-tenon joints for maximum strength."
            "Cabinet" -> "This $quality $woodType wood cabinet (${length}×${width}×${height} ft) offers generous storage with refined aesthetics. Includes adjustable shelves and soft-close hardware."
            "Table" -> "This $quality $woodType wood table (${length}×${width}×${height} ft) combines functionality with artisan craftsmanship. The solid wood top provides a warm, inviting surface for daily use."
            "Chair" -> "This $quality $woodType wood chair (${length}×${width}×${height} ft) is ergonomically shaped for comfort and built to last generations. Features hand-carved details and a smooth satin finish."
            else -> "This custom $quality $woodType wood furniture piece (${length}×${width}×${height} ft) is crafted to your exact specifications with attention to detail and superior joinery."
        }
    }

    // ── Portfolio ────────────────────────────────────────────────────────────
    private val _portfolio = MutableStateFlow(
        mutableListOf(
            PortfolioItem("p1", "Royal Bedroom Set", "Complete king-size bed with matching nightstands and dresser. Hand-carved floral motifs.", "🛏️", "Bed", "2026-04-15", "Anand Sharma", "Teak", 120000.0),
            PortfolioItem("p2", "Modern Kitchen Cabinets", "Full modular kitchen with upper and lower cabinets, lazy susan corner unit.", "🗄️", "Cabinet", "2026-04-20", "Priya Deshpande", "Pine", 85000.0),
            PortfolioItem("p3", "Living Room Sofa Set", "3+2+1 sofa set with traditional carving and premium cushions.", "🛋️", "Sofa", "2026-03-28", "Mahesh Kulkarni", "Sheesham", 95000.0),
            PortfolioItem("p4", "Conference Table", "12-seater conference table with cable management and polished finish.", "🪑", "Table", "2026-04-02", "TechCorp Office", "Teak", 65000.0)
        )
    )
    val portfolio: Flow<List<PortfolioItem>> = _portfolio.asStateFlow()

    fun getPortfolioCount(): Flow<Int> {
        return _portfolio.map { it.size }
    }

    fun addPortfolioItem(item: PortfolioItem): PortfolioItem {
        val newItem = item.copy(
            itemId = UUID.randomUUID().toString().take(8)
        )
        val updated = _portfolio.value.toMutableList().apply { add(0, newItem) }
        _portfolio.value = updated
        return newItem
    }
}
