package com.example.data.repository

import com.example.data.local.MotoDao
import com.example.data.local.PreloadedData
import com.example.data.models.BrandEntity
import com.example.data.models.FavoriteEntity
import com.example.data.models.MotorcycleEntity
import com.example.data.models.RecentlyViewedEntity
import com.example.data.models.SavedComparisonEntity
import com.example.data.models.VariantEntity
import com.example.data.sync.FirestoreSyncService
import com.example.data.sync.SyncResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull

class MotorcycleRepository(
    private val dao: MotoDao,
    val firestoreSyncService: FirestoreSyncService? = null,
    val authService: com.example.data.auth.FirebaseAuthService? = null,
    val geminiAiService: com.example.data.ai.GeminiAiService? = null
) {

    val allBrands: Flow<List<BrandEntity>> = dao.getAllBrands()
    val allMotorcycles: Flow<List<MotorcycleEntity>> = dao.getAllMotorcycles()
    val trendingMotorcycles: Flow<List<MotorcycleEntity>> = dao.getTrendingMotorcycles()
    val newMotorcycles: Flow<List<MotorcycleEntity>> = dao.getNewMotorcycles()
    val popularMotorcycles: Flow<List<MotorcycleEntity>> = dao.getPopularMotorcycles()
    val allFavorites: Flow<List<FavoriteEntity>> = dao.getAllFavorites()
    val recentlyViewed: Flow<List<RecentlyViewedEntity>> = dao.getRecentlyViewed()
    val savedComparisons: Flow<List<SavedComparisonEntity>> = dao.getAllSavedComparisons()

    fun getMotorcycle(id: String): Flow<MotorcycleEntity?> = dao.getMotorcycleFlow(id)

    suspend fun getMotorcycleById(id: String): MotorcycleEntity? = dao.getMotorcycleById(id)

    suspend fun getBrandById(brandId: String): BrandEntity? = dao.getBrandById(brandId)

    fun searchMotorcycles(query: String): Flow<List<MotorcycleEntity>> =
        if (query.isBlank()) dao.getAllMotorcycles() else dao.searchMotorcyclesByQuery(query.trim())

    fun getMotorcyclesByBrand(brandId: String): Flow<List<MotorcycleEntity>> =
        dao.getMotorcyclesByBrand(brandId)

    fun getVariants(motorcycleId: String): Flow<List<VariantEntity>> =
        dao.getVariantsForMotorcycle(motorcycleId)

    fun isFavorite(motorcycleId: String): Flow<Boolean> = dao.isFavorite(motorcycleId)

    suspend fun toggleFavorite(motorcycleId: String) {
        val currentFav = dao.isFavorite(motorcycleId).firstOrNull() ?: false
        if (currentFav) {
            dao.removeFavorite(motorcycleId)
        } else {
            dao.addFavorite(FavoriteEntity(motorcycleId = motorcycleId))
        }
    }

    suspend fun recordRecentlyViewed(motorcycleId: String) {
        dao.recordRecentlyViewed(RecentlyViewedEntity(motorcycleId = motorcycleId))
    }

    suspend fun saveComparison(
        bike1Id: String,
        bike2Id: String,
        bike3Id: String?,
        title: String,
        primaryWinnerName: String
    ) {
        dao.insertSavedComparison(
            SavedComparisonEntity(
                bike1Id = bike1Id,
                bike2Id = bike2Id,
                bike3Id = bike3Id,
                title = title,
                primaryWinnerName = primaryWinnerName
            )
        )
    }

    suspend fun deleteSavedComparison(comparison: SavedComparisonEntity) {
        dao.deleteSavedComparison(comparison)
    }

    // --- ADMIN / MUTATIONS ---
    suspend fun insertBrand(brand: BrandEntity) = dao.insertBrand(brand)
    suspend fun updateBrand(brand: BrandEntity) = dao.updateBrand(brand)
    suspend fun deleteBrand(brand: BrandEntity) = dao.deleteBrand(brand)

    suspend fun insertMotorcycle(motorcycle: MotorcycleEntity) = dao.insertMotorcycle(motorcycle)
    suspend fun updateMotorcycle(motorcycle: MotorcycleEntity) = dao.updateMotorcycle(motorcycle)
    suspend fun deleteMotorcycle(motorcycle: MotorcycleEntity) = dao.deleteMotorcycle(motorcycle)

    suspend fun insertVariant(variant: VariantEntity) = dao.insertVariant(variant)
    suspend fun deleteVariant(variant: VariantEntity) = dao.deleteVariant(variant)

    suspend fun syncCatalogWithDefaults() {
        dao.insertBrands(PreloadedData.brands)
        dao.insertMotorcycles(PreloadedData.motorcycles)
        dao.insertVariants(PreloadedData.variants)
    }

    suspend fun seedIfEmpty() {
        syncCatalogWithDefaults()
    }

    suspend fun resetDatabaseToDefaults() {
        syncCatalogWithDefaults()
    }

    // --- FIRESTORE DYNAMIC CLOUD SYNC ---
    suspend fun syncWithFirestore(): SyncResult {
        // Guarantee local baseline first
        syncCatalogWithDefaults()
        return firestoreSyncService?.syncWithFirestore()
            ?: SyncResult.FirebaseNotConfigured("Firestore sync unavailable. Using offline Room catalog.")
    }

    suspend fun uploadToFirestore(): SyncResult {
        val bikes = dao.getAllMotorcycles().firstOrNull() ?: PreloadedData.motorcycles
        val brands = dao.getAllBrands().firstOrNull() ?: PreloadedData.brands
        return firestoreSyncService?.uploadCatalogToFirestore(brands, bikes, PreloadedData.variants)
            ?: SyncResult.FirebaseNotConfigured("Firestore service unavailable.")
    }

    fun isFirebaseConfigured(): Boolean = firestoreSyncService?.isFirebaseAvailable() ?: false

    fun startRealtimeListener(onUpdated: (Int) -> Unit) =
        firestoreSyncService?.startRealtimeListener(onUpdated)
}
