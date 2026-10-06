package com.example.data.models

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "brands")
data class BrandEntity(
    @PrimaryKey val id: String,
    val name: String,
    val country: String,
    val foundedYear: Int,
    val history: String,
    val websiteUrl: String,
    val logoUrl: String,
    val accentColorHex: Long = 0xFF38BDF8
)

@Entity(tableName = "motorcycles")
data class MotorcycleEntity(
    @PrimaryKey val id: String,
    val brandId: String,
    val modelName: String,
    val category: String,
    val modelYear: Int,
    val basePrice: Double,
    val currency: String = "$",
    val priceDisplay: String,
    val rating: Float = 4.6f,
    val reviewCount: Int = 120,
    val isTrending: Boolean = false,
    val isNew: Boolean = false,
    val isPopular: Boolean = false,
    val heroImageUrl: String,
    val frontImageUrl: String = "",
    val sideImageUrl: String = "",
    val rearImageUrl: String = "",
    val cockpitImageUrl: String = "",
    val availableColorsJson: String = "[]", // Format: "ColorName:#HEX|Color2:#HEX"
    val source: String = "Official Manufacturer Technical Sheet",
    val sourceUrl: String = "",
    val lastUpdated: String = "2024",
    @Embedded val specs: MotorcycleSpecs
)

data class MotorcycleSpecs(
    // ENGINE
    val engineType: String = "",
    val displacementCc: Double = 0.0,
    val cylinders: Int = 1,
    val cooling: String = "Liquid Cooled",
    val valves: Int = 4,
    val boreMm: Double = 0.0,
    val strokeMm: Double = 0.0,
    val compressionRatio: String = "",
    val fuelSystem: String = "Electronic Fuel Injection",
    val fuelType: String = "Petrol",
    val startingSystem: String = "Electric Start",

    // PERFORMANCE
    val maxPowerHp: Double = 0.0,
    val maxPowerRpm: Int = 0,
    val maxPowerDisplay: String = "",
    val maxTorqueNm: Double = 0.0,
    val maxTorqueRpm: Int = 0,
    val maxTorqueDisplay: String = "",
    val topSpeedKmh: Int = 0,
    val accel0To60Sec: Double = 0.0,
    val accel0To100Sec: Double = 0.0,
    val mileageKmpl: Double = 0.0,
    val powerToWeightHpPerTon: Double = 0.0,

    // TRANSMISSION
    val gearbox: String = "6-Speed",
    val numberOfGears: Int = 6,
    val clutchType: String = "Assist & Slipper Clutch",
    val finalDrive: String = "Chain",

    // CHASSIS
    val frameType: String = "Steel Diamond / Trellis",
    val frontSuspension: String = "Inverted Telescopic Fork",
    val rearSuspension: String = "Monoshock",
    val frontTravelMm: Int = 120,
    val rearTravelMm: Int = 130,

    // BRAKES
    val frontBrake: String = "Single Disc with ABS",
    val rearBrake: String = "Single Disc with ABS",
    val absSystem: String = "Dual-Channel ABS",
    val brakeDimensions: String = "Front 300mm / Rear 240mm",

    // WHEELS & TYRES
    val frontTyre: String = "110/70-17",
    val rearTyre: String = "150/60-17",
    val wheelType: String = "Cast Aluminium Alloy",
    val wheelSizeInches: Int = 17,

    // DIMENSIONS
    val lengthMm: Int = 2000,
    val widthMm: Int = 750,
    val heightMm: Int = 1100,
    val wheelbaseMm: Int = 1350,
    val groundClearanceMm: Int = 160,
    val seatHeightMm: Int = 810,
    val kerbWeightKg: Double = 160.0,
    val fuelTankCapacityL: Double = 13.0,

    // ELECTRONICS & FEATURES
    val ridingModes: String = "Track, Urban, Rain",
    val hasTractionControl: Boolean = true,
    val quickShifter: String = "Bidirectional (Up/Down)",
    val hasCruiseControl: Boolean = false,
    val hasLaunchControl: Boolean = false,
    val hasRideByWire: Boolean = true,
    val hasTftDisplay: Boolean = true,
    val displayDetails: String = "5-inch Full Color TFT",
    val hasBluetooth: Boolean = true,
    val hasNavigation: Boolean = true,
    val ledLighting: String = "All-LED (Headlight, Taillight, DRL, Indicators)",
    val hasKeylessIgnition: Boolean = false,

    // SAFETY
    val hasCorneringAbs: Boolean = false,
    val hasStabilityControl: Boolean = false,
    val hasSlipperClutch: Boolean = true,
    val hasWheelieControl: Boolean = false
)

@Entity(tableName = "variants")
data class VariantEntity(
    @PrimaryKey val id: String,
    val motorcycleId: String,
    val variantName: String,
    val price: Double,
    val priceDisplay: String,
    val highlightedFeatures: String = "",
    val variantImageUrl: String = "",
    val colorName: String = "",
    val colorHex: String = "#000000"
)

@Entity(tableName = "favorites")
data class FavoriteEntity(
    @PrimaryKey val motorcycleId: String,
    val savedAtTimestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "recently_viewed")
data class RecentlyViewedEntity(
    @PrimaryKey val motorcycleId: String,
    val viewedAtTimestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "saved_comparisons")
data class SavedComparisonEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val bike1Id: String,
    val bike2Id: String,
    val bike3Id: String? = null,
    val title: String,
    val primaryWinnerName: String,
    val savedAtTimestamp: Long = System.currentTimeMillis()
)

// UI and Comparison helper models
enum class UseCasePriority(val displayName: String, val icon: String, val description: String) {
    BEST_OVERALL("Best Overall", "🏆", "Balanced assessment across all dimensions"),
    PERFORMANCE("Performance", "🔥", "Maximum power, torque, top speed & power-to-weight ratio"),
    DAILY_COMMUTE("Daily Commute", "🏙️", "Low weight, agile ergonomics, high mileage & city comfort"),
    MILEAGE("Mileage & Economy", "⛽", "Exceptional fuel efficiency and tank range"),
    TOURING("Touring & Highway", "🛣️", "Wind protection, tank capacity, relaxed ergonomics & stability"),
    BEGINNER("Beginner Friendly", "👨‍🎓", "Manageable power, accessible seat height, forgiving weight & safety aids"),
    BUDGET("Budget & Value", "💰", "Most features and performance per dollar spent"),
    COMFORT("Comfort & Ergonomics", "🛋️", "Upright posture, plush suspension travel & low vibration"),
    FEATURES_TECH("Features & Tech", "⚡", "TFT screens, ride-by-wire, quickshifter & connectivity"),
    SPORT_RIDING("Sport & Track", "🏁", "Aggressive geometry, braking hardware & track modes")
}

data class ColorOption(
    val name: String,
    val hexColor: String
)

data class CategoryScore(
    val categoryName: String,
    val scoreA: Int,
    val scoreB: Int,
    val scoreC: Int? = null,
    val leaderIndex: Int // 0, 1, or 2
)

data class ComparisonReport(
    val bikeA: MotorcycleEntity,
    val bikeB: MotorcycleEntity,
    val bikeC: MotorcycleEntity? = null,
    val priority: UseCasePriority,
    val overallScoreA: Int,
    val overallScoreB: Int,
    val overallScoreC: Int? = null,
    val overallWinnerIndex: Int,
    val performanceWinnerIndex: Int,
    val mileageWinnerIndex: Int,
    val valueWinnerIndex: Int,
    val safetyWinnerIndex: Int,
    val cityWinnerIndex: Int,
    val touringWinnerIndex: Int,
    val beginnerWinnerIndex: Int,
    val categoryScores: List<CategoryScore>,
    val writtenVerdict: String,
    val keyAdvantagesA: List<String>,
    val keyAdvantagesB: List<String>,
    val keyAdvantagesC: List<String> = emptyList()
)
