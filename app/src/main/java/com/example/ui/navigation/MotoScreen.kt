package com.example.ui.navigation

sealed interface MotoScreen {
    data object Home : MotoScreen
    data object Catalog : MotoScreen
    data object Brands : MotoScreen
    data class BrandDetail(val brandId: String) : MotoScreen
    data class MotorcycleDetail(val bikeId: String) : MotoScreen
    data object Comparison : MotoScreen
    data object Favorites : MotoScreen
    data object Admin : MotoScreen
}

enum class SortOption(val displayName: String) {
    POPULARITY("Most Popular"),
    PRICE_LOW_TO_HIGH("Price: Low to High"),
    PRICE_HIGH_TO_LOW("Price: High to Low"),
    POWER_HIGH_TO_LOW("Highest Power (HP)"),
    DISPLACEMENT_HIGH("Largest Engine (CC)"),
    RATING("Highest Rated"),
    NEWEST("Newest Launch")
}
