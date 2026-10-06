package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.models.BrandEntity
import com.example.data.models.FavoriteEntity
import com.example.data.models.MotorcycleEntity
import com.example.data.models.RecentlyViewedEntity
import com.example.data.models.SavedComparisonEntity
import com.example.data.models.VariantEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MotoDao {

    // --- BRANDS ---
    @Query("SELECT * FROM brands ORDER BY name ASC")
    fun getAllBrands(): Flow<List<BrandEntity>>

    @Query("SELECT * FROM brands WHERE id = :brandId LIMIT 1")
    suspend fun getBrandById(brandId: String): BrandEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBrand(brand: BrandEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBrands(brands: List<BrandEntity>)

    @Update
    suspend fun updateBrand(brand: BrandEntity)

    @Delete
    suspend fun deleteBrand(brand: BrandEntity)

    // --- MOTORCYCLES ---
    @Query("SELECT * FROM motorcycles ORDER BY rating DESC, modelName ASC")
    fun getAllMotorcycles(): Flow<List<MotorcycleEntity>>

    @Query("SELECT * FROM motorcycles WHERE id = :id LIMIT 1")
    suspend fun getMotorcycleById(id: String): MotorcycleEntity?

    @Query("SELECT * FROM motorcycles WHERE id = :id LIMIT 1")
    fun getMotorcycleFlow(id: String): Flow<MotorcycleEntity?>

    @Query("SELECT * FROM motorcycles WHERE brandId = :brandId ORDER BY basePrice ASC")
    fun getMotorcyclesByBrand(brandId: String): Flow<List<MotorcycleEntity>>

    @Query("SELECT * FROM motorcycles WHERE isTrending = 1 ORDER BY rating DESC")
    fun getTrendingMotorcycles(): Flow<List<MotorcycleEntity>>

    @Query("SELECT * FROM motorcycles WHERE isNew = 1 ORDER BY modelYear DESC")
    fun getNewMotorcycles(): Flow<List<MotorcycleEntity>>

    @Query("SELECT * FROM motorcycles WHERE isPopular = 1 ORDER BY reviewCount DESC")
    fun getPopularMotorcycles(): Flow<List<MotorcycleEntity>>

    @Query("SELECT * FROM motorcycles WHERE category = :category ORDER BY rating DESC")
    fun getMotorcyclesByCategory(category: String): Flow<List<MotorcycleEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMotorcycle(motorcycle: MotorcycleEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMotorcycles(motorcycles: List<MotorcycleEntity>)

    @Update
    suspend fun updateMotorcycle(motorcycle: MotorcycleEntity)

    @Delete
    suspend fun deleteMotorcycle(motorcycle: MotorcycleEntity)

    // --- VARIANTS ---
    @Query("SELECT * FROM variants WHERE motorcycleId = :motorcycleId ORDER BY price ASC")
    fun getVariantsForMotorcycle(motorcycleId: String): Flow<List<VariantEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVariants(variants: List<VariantEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVariant(variant: VariantEntity)

    @Delete
    suspend fun deleteVariant(variant: VariantEntity)

    // --- FAVORITES ---
    @Query("SELECT * FROM favorites ORDER BY savedAtTimestamp DESC")
    fun getAllFavorites(): Flow<List<FavoriteEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM favorites WHERE motorcycleId = :id)")
    fun isFavorite(id: String): Flow<Boolean>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addFavorite(favorite: FavoriteEntity)

    @Query("DELETE FROM favorites WHERE motorcycleId = :id")
    suspend fun removeFavorite(id: String)

    // --- RECENTLY VIEWED ---
    @Query("SELECT * FROM recently_viewed ORDER BY viewedAtTimestamp DESC LIMIT 10")
    fun getRecentlyViewed(): Flow<List<RecentlyViewedEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun recordRecentlyViewed(entry: RecentlyViewedEntity)

    // --- SAVED COMPARISONS ---
    @Query("SELECT * FROM saved_comparisons ORDER BY savedAtTimestamp DESC")
    fun getAllSavedComparisons(): Flow<List<SavedComparisonEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSavedComparison(comparison: SavedComparisonEntity)

    @Delete
    suspend fun deleteSavedComparison(comparison: SavedComparisonEntity)
}
