package com.kashta.kala.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kashta.kala.BuildConfig
import com.kashta.kala.data.model.ChatMessage
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID

/**
 * ViewModel for the AI Chat Assistant screen.
 * Integrates Google Gemini API for smart furniture design assistance.
 * Falls back to preset responses when the API key is not configured.
 */
class ChatViewModel : ViewModel() {

    private val _messages = MutableStateFlow(
        mutableListOf(
            ChatMessage(
                id = "welcome",
                role = "assistant",
                content = "🪵 Namaste! I'm your Kashta-Kala AI Assistant.\n\nI can help you with:\n• 🛋️ Suggesting furniture designs\n• 📐 Estimating wood requirements\n• 💰 Pricing guidance\n• 🎨 Modern design ideas\n• 📋 Portfolio tips\n\nHow can I help you today?"
            )
        )
    )
    val messages: StateFlow<List<ChatMessage>> = _messages.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val apiKey = BuildConfig.GEMINI_API_KEY
    private val isApiConfigured = apiKey != "YOUR_GEMINI_API_KEY_HERE" && apiKey.isNotBlank()

    private val generativeModel: GenerativeModel? = if (isApiConfigured) {
        GenerativeModel(
            modelName = "gemini-1.5-pro",
            apiKey = apiKey
        )
    } else null

    private val systemPrompt = """
        You are Kashta-Kala Assistant, a helpful AI for carpenters and furniture makers in India.
        You specialize in:
        - Suggesting furniture designs based on customer needs and room sizes
        - Estimating wood material requirements (Area = L×W, Volume = L×W×H)
        - Providing pricing guidance for different wood types (Teak ₹3500/cft, Sheesham ₹2800/cft, Pine ₹1800/cft, Mango ₹2200/cft, Sal ₹3000/cft)
        - Recommending modern and traditional furniture styles
        - Tips for running a carpentry business
        Keep responses concise, friendly, and use relevant furniture/wood emojis.
        Use Indian Rupees (₹) for all pricing.
    """.trimIndent()

    // Preset responses for when Gemini API is not configured
    private val presetResponses = mapOf(
        "bed" to "🛏️ Bed Design Suggestions:\n\n1. **King Size Poster Bed** (7×6.5 ft) — Teak, ₹75,000\n   Classic four-poster with carved headboard\n\n2. **Queen Storage Bed** (6.5×5 ft) — Sheesham, ₹55,000\n   Hydraulic storage, minimalist design\n\n3. **Single Bed with Drawers** (6×3 ft) — Pine, ₹22,000\n   Space-saving, perfect for kids\n\n📐 For a 6×5 bed, you need ~33 cft of wood (with 10% wastage).",

        "sofa" to "🛋️ Sofa Design Suggestions:\n\n1. **Royal Teak 3-Seater** (6×2.5 ft) — ₹45,000\n   Intricate carving, plush cushions\n\n2. **Modern L-Shape** (8×6 ft) — Sheesham, ₹62,000\n   Contemporary, modular design\n\n3. **Classic Wooden Sofa** (5×2 ft) — Mango, ₹28,000\n   Traditional Indian lattice backrest\n\n💡 Tip: L-shape sofas are trending in 2026!",

        "estimate" to "📐 Wood Estimation Guide:\n\n**Formulas:**\n• Area = Length × Width\n• Volume = Length × Width × Height\n• Add 10% wastage\n\n**Example — 6×5×3 ft Bed:**\n• Volume = 6 × 5 × 3 = 90 cft\n• With wastage = 99 cft\n• Teak cost = 99 × ₹3,500 = ₹3,46,500\n\nUse our Estimator tab for instant calculations!",

        "price" to "💰 Wood Pricing Guide (per cubic foot):\n\n🌳 **Teak** — ₹3,500/cft (Premium, most durable)\n🪵 **Sheesham** — ₹2,800/cft (Beautiful grain)\n🌲 **Pine** — ₹1,800/cft (Budget-friendly)\n🥭 **Mango** — ₹2,200/cft (Eco-friendly)\n🏔️ **Sal** — ₹3,000/cft (Weather-resistant)\n\n📋 Labor cost is typically 25-35% of material cost.",

        "cabinet" to "🗄️ Cabinet Design Ideas:\n\n1. **3-Door Wardrobe** (6×2×7 ft) — Teak, ₹48,000\n   Mirror panel, adjustable shelves\n\n2. **Modular Kitchen Set** (8×1.5×7 ft) — Pine, ₹35,000\n   Soft-close hinges, upper+lower units\n\n3. **Bookshelf Cabinet** (3×1×6 ft) — Sheesham, ₹18,000\n   Five tiers with bottom doors\n\n💡 Tip: Soft-close hinges add ₹200-400 per door.",

        "modern" to "🎨 Modern Furniture Trends 2026:\n\n1. **Minimalist Designs** — Clean lines, no excess carving\n2. **Multi-functional** — Storage beds, extendable tables\n3. **Mixed Materials** — Wood + metal + glass combinations\n4. **Light Woods** — Pine and mango are trending\n5. **Modular Furniture** — Easy to assemble/disassemble\n\n🛋️ L-shape sofas and platform beds are most requested!\n\nWant specific design ideas for any category?",

        "help" to "🪵 Here's what I can help with:\n\n1. 🛋️ **Design Ideas** — Ask about any furniture type\n2. 📐 **Wood Estimation** — Calculate material needed\n3. 💰 **Pricing** — Wood costs and quotations\n4. 🎨 **Trends** — Modern design ideas\n5. 📋 **Business Tips** — Growing your carpentry business\n\nJust ask me anything about furniture making!"
    )

    val quickSuggestions = listOf(
        "Suggest a modern bed design",
        "Estimate wood for 6×5 bed",
        "Modern sofa ideas",
        "Wood pricing guide",
        "Cabinet design ideas"
    )

    fun sendMessage(userText: String) {
        if (userText.isBlank()) return

        val userMessage = ChatMessage(
            id = UUID.randomUUID().toString(),
            role = "user",
            content = userText
        )

        val currentList = _messages.value.toMutableList()
        currentList.add(userMessage)
        _messages.value = currentList

        viewModelScope.launch {
            _isLoading.value = true

            val responseText = if (isApiConfigured && generativeModel != null) {
                try {
                    val response = generativeModel.generateContent(
                        content {
                            text("$systemPrompt\n\nUser says: $userText")
                        }
                    )
                    response.text ?: "I couldn't process that. Please try again."
                } catch (e: Exception) {
                    "⚠️ Connection error: ${e.message}\n\nTrying offline mode..."
                        .also { getPresetResponse(userText) }
                }
            } else {
                getPresetResponse(userText)
            }

            val assistantMessage = ChatMessage(
                id = UUID.randomUUID().toString(),
                role = "assistant",
                content = responseText
            )

            val updatedList = _messages.value.toMutableList()
            updatedList.add(assistantMessage)
            _messages.value = updatedList

            _isLoading.value = false
        }
    }

    private fun getPresetResponse(input: String): String {
        val lower = input.lowercase()
        return when {
            lower.contains("bed") || lower.contains("cot") ->
                presetResponses["bed"]!!
            lower.contains("sofa") || lower.contains("couch") ->
                presetResponses["sofa"]!!
            lower.contains("estimate") || lower.contains("wood") || lower.contains("material") || lower.contains("calculate") ->
                presetResponses["estimate"]!!
            lower.contains("price") || lower.contains("cost") || lower.contains("rate") || lower.contains("₹") ->
                presetResponses["price"]!!
            lower.contains("cabinet") || lower.contains("wardrobe") || lower.contains("shelf") ->
                presetResponses["cabinet"]!!
            lower.contains("modern") || lower.contains("trend") || lower.contains("style") || lower.contains("design") ->
                presetResponses["modern"]!!
            lower.contains("help") || lower.contains("what") || lower.contains("can you") ->
                presetResponses["help"]!!
            lower.contains("table") ->
                "🪑 Table Suggestions:\n\n• **6-Seater Dining** (5×3 ft) — Teak, ₹38,000\n• **Study Table** (4×2 ft) — Mango, ₹15,000\n• **Coffee Table** (3×3 ft) — Sal, ₹12,000\n\nDining tables are our best sellers! Want details?"
            lower.contains("chair") ->
                "💺 Chair Recommendations:\n\n• **Rocking Chair** — Teak, ₹16,000 (Most popular!)\n• **Dining Chair Set (4)** — Sheesham, ₹24,000\n• **Office Chair** — Mango, ₹11,000\n\nRocking chairs make great gifts! Need a quote?"
            lower.contains("namaste") || lower.contains("hello") || lower.contains("hi") ->
                "🙏 Namaste! Welcome to Kashta-Kala!\n\nI'm here to help you with furniture designs, wood estimation, and pricing. What would you like to explore today?"
            else ->
                "🪵 Great question! Here are some things I can help with:\n\n• Type **\"bed\"** for bed design ideas\n• Type **\"sofa\"** for sofa suggestions\n• Type **\"estimate\"** for wood calculation help\n• Type **\"price\"** for wood pricing guide\n• Type **\"help\"** for all options\n\n(💡 Tip: Configure your Gemini API key in build.gradle for full AI-powered responses!)"
        }
    }
}
