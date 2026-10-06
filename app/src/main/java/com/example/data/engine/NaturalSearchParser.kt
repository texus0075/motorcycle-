package com.example.data.engine

import com.example.data.models.MotorcycleEntity

object NaturalSearchParser {

    data class ParsedCriteria(
        val category: String? = null,
        val brandKeyword: String? = null,
        val maxPrice: Double? = null,
        val minDisplacementCc: Double? = null,
        val maxDisplacementCc: Double? = null,
        val isBeginnerIntent: Boolean = false,
        val isTouringIntent: Boolean = false,
        val generalKeyword: String? = null
    )

    fun parse(query: String): ParsedCriteria {
        val q = query.trim().lowercase()
        var category: String? = null
        var brandKeyword: String? = null
        var maxPrice: Double? = null
        var minCc: Double? = null
        var maxCc: Double? = null
        var isBeginner = false
        var isTouring = false

        // Detect category
        when {
            q.contains("sport") || q.contains("super sport") || q.contains("supersport") -> category = "Sport"
            q.contains("naked") || q.contains("street") -> category = "Naked"
            q.contains("adv") || q.contains("adventure") || q.contains("dual sport") -> category = "Adventure / ADV"
            q.contains("cruiser") -> category = "Cruiser"
            q.contains("touring") -> category = "Touring"
            q.contains("classic") || q.contains("retro") -> category = "Retro / Classic"
            q.contains("scooter") -> category = "Scooter"
            q.contains("electric") || q.contains("ev") -> category = "Electric"
        }

        // Detect brand
        val brands = listOf("yamaha", "ktm", "kawasaki", "ducati", "bmw", "honda", "triumph", "royal enfield", "aprilia", "harley", "suzuki", "tvs", "cfmoto")
        for (b in brands) {
            if (q.contains(b)) {
                brandKeyword = b.replace(" ", "")
                break
            }
        }

        // Detect price constraints (e.g. under 5 lakh, under 3000, under 5000)
        val underPriceRegex = Regex("""under\s*([₹$]?)\s*(\d+(\.\d+)?)(\s*lakh|\s*k)?""")
        val match = underPriceRegex.find(q)
        if (match != null) {
            val numStr = match.groupValues[2]
            val suffix = match.groupValues[4]
            val num = numStr.toDoubleOrNull() ?: 0.0
            maxPrice = when {
                suffix.contains("lakh") -> num * 1200.0 // 1 Lakh INR approx $1,200
                suffix.contains("k") -> num * 1000.0
                num in 1000.0..100000.0 -> num
                num in 1.0..25.0 -> num * 1200.0 // assume lakh if small integer with "under"
                else -> num
            }
        }

        // Detect engine displacement intent (e.g. "600cc", "400cc", "300cc", "1000cc")
        val ccRegex = Regex("""(\d{3,4})\s*cc""")
        val ccMatch = ccRegex.find(q)
        if (ccMatch != null) {
            val targetCc = ccMatch.groupValues[1].toDoubleOrNull() ?: 0.0
            minCc = (targetCc - 120.0).coerceAtLeast(0.0)
            maxCc = targetCc + 120.0
        }

        // Detect intent keywords
        if (q.contains("beginner") || q.contains("first bike") || q.contains("starter")) {
            isBeginner = true
        }
        if (q.contains("touring") || q.contains("long distance") || q.contains("highway")) {
            isTouring = true
        }

        return ParsedCriteria(
            category = category,
            brandKeyword = brandKeyword,
            maxPrice = maxPrice,
            minDisplacementCc = minCc,
            maxDisplacementCc = maxCc,
            isBeginnerIntent = isBeginner,
            isTouringIntent = isTouring,
            generalKeyword = query.trim()
        )
    }

    fun matches(bike: MotorcycleEntity, criteria: ParsedCriteria): Boolean {
        if (criteria.category != null && !bike.category.contains(criteria.category, ignoreCase = true)) {
            return false
        }
        if (criteria.brandKeyword != null && !bike.brandId.contains(criteria.brandKeyword, ignoreCase = true)) {
            return false
        }
        if (criteria.maxPrice != null && bike.basePrice > criteria.maxPrice) {
            return false
        }
        if (criteria.minDisplacementCc != null && bike.specs.displacementCc < criteria.minDisplacementCc) {
            return false
        }
        if (criteria.maxDisplacementCc != null && bike.specs.displacementCc > criteria.maxDisplacementCc) {
            return false
        }
        if (criteria.isBeginnerIntent) {
            // Beginner bikes: manageable weight and accessible power
            if (bike.specs.maxPowerHp > 50.0 || bike.specs.kerbWeightKg > 190.0) return false
        }
        if (criteria.isTouringIntent) {
            // Touring intent: fuel tank capacity or comfortable category
            if (bike.specs.fuelTankCapacityL < 12.0) return false
        }
        return true
    }
}
