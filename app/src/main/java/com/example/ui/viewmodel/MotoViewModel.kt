package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.engine.ComparisonEngine
import com.example.data.engine.NaturalSearchParser
import com.example.data.local.PreloadedData
import com.example.data.models.BrandEntity
import com.example.data.models.ComparisonReport
import com.example.data.models.FavoriteEntity
import com.example.data.models.MotorcycleEntity
import com.example.data.models.RecentlyViewedEntity
import com.example.data.models.SavedComparisonEntity
import com.example.data.models.UseCasePriority
import com.example.data.models.VariantEntity
import com.example.data.repository.MotorcycleRepository
import com.example.ui.navigation.MotoScreen
import com.example.ui.navigation.SortOption
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MotoViewModel(private val repository: MotorcycleRepository) : ViewModel() {

    // --- NAVIGATION BACKSTACK ---
    private val _screenStack = MutableStateFlow<List<MotoScreen>>(listOf(MotoScreen.Home))
    val currentScreen: StateFlow<MotoScreen> = _screenStack.combine(MutableStateFlow(Unit)) { stack, _ ->
        stack.lastOrNull() ?: MotoScreen.Home
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), MotoScreen.Home)

    fun navigateTo(screen: MotoScreen) {
        val current = _screenStack.value
        // If clicking on same root tab, avoid duplicate
        if (current.lastOrNull() == screen) return
        _screenStack.value = current + screen
    }

    fun navigateBack(): Boolean {
        val current = _screenStack.value
        if (current.size > 1) {
            _screenStack.value = current.dropLast(1)
            return true
        }
        return false
    }

    // --- DATABASE FLOWS WITH INSTANT PRELOADED DEFAULTS ---
    val allBrands: StateFlow<List<BrandEntity>> = repository.allBrands
        .stateIn(viewModelScope, SharingStarted.Eagerly, PreloadedData.brands)

    val allMotorcycles: StateFlow<List<MotorcycleEntity>> = repository.allMotorcycles
        .stateIn(viewModelScope, SharingStarted.Eagerly, PreloadedData.motorcycles)

    val trendingBikes: StateFlow<List<MotorcycleEntity>> = repository.trendingMotorcycles
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            PreloadedData.motorcycles.filter { it.isTrending }
        )

    val newBikes: StateFlow<List<MotorcycleEntity>> = repository.newMotorcycles
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            PreloadedData.motorcycles.filter { it.isNew }
        )

    val popularBikes: StateFlow<List<MotorcycleEntity>> = repository.popularMotorcycles
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            PreloadedData.motorcycles.filter { it.isPopular }
        )

    val favorites: StateFlow<List<FavoriteEntity>> = repository.allFavorites
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val recentlyViewed: StateFlow<List<RecentlyViewedEntity>> = repository.recentlyViewed
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val savedComparisons: StateFlow<List<SavedComparisonEntity>> = repository.savedComparisons
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // --- SEARCH & FILTER STATE ---
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedBrandId = MutableStateFlow<String?>(null)
    val selectedBrandId: StateFlow<String?> = _selectedBrandId.asStateFlow()

    private val _selectedCategory = MutableStateFlow<String?>(null)
    val selectedCategory: StateFlow<String?> = _selectedCategory.asStateFlow()

    private val _selectedSort = MutableStateFlow(SortOption.POPULARITY)
    val selectedSort: StateFlow<SortOption> = _selectedSort.asStateFlow()

    private val _maxPriceFilter = MutableStateFlow<Double?>(null)
    val maxPriceFilter: StateFlow<Double?> = _maxPriceFilter.asStateFlow()

    data class FilterState(
        val query: String,
        val brand: String?,
        val category: String?,
        val sort: SortOption,
        val maxPrice: Double?
    )

    private val _filterState = combine(
        _searchQuery,
        _selectedBrandId,
        _selectedCategory,
        _selectedSort,
        _maxPriceFilter
    ) { query, brand, category, sort, maxPrice ->
        FilterState(query, brand, category, sort, maxPrice)
    }

    // Filtered motorcycles stream
    val filteredMotorcycles: StateFlow<List<MotorcycleEntity>> = combine(
        allMotorcycles,
        _filterState
    ) { bikes, filter ->
        var list = bikes

        // 1. Natural / Keyword search parsing
        if (filter.query.isNotBlank()) {
            val parsedCriteria = NaturalSearchParser.parse(filter.query)
            list = list.filter { bike ->
                val directTextMatch = bike.modelName.contains(filter.query, ignoreCase = true) ||
                        bike.brandId.contains(filter.query, ignoreCase = true) ||
                        bike.category.contains(filter.query, ignoreCase = true)
                directTextMatch || NaturalSearchParser.matches(bike, parsedCriteria)
            }
        }

        // 2. Brand Filter
        if (filter.brand != null) {
            list = list.filter { it.brandId.equals(filter.brand, ignoreCase = true) }
        }

        // 3. Category Filter
        if (filter.category != null) {
            list = list.filter { it.category.equals(filter.category, ignoreCase = true) }
        }

        // 4. Max Price Filter
        if (filter.maxPrice != null) {
            list = list.filter { it.basePrice <= filter.maxPrice }
        }

        // 5. Sorting
        when (filter.sort) {
            SortOption.POPULARITY -> list.sortedByDescending { it.reviewCount }
            SortOption.PRICE_LOW_TO_HIGH -> list.sortedBy { it.basePrice }
            SortOption.PRICE_HIGH_TO_LOW -> list.sortedByDescending { it.basePrice }
            SortOption.POWER_HIGH_TO_LOW -> list.sortedByDescending { it.specs.maxPowerHp }
            SortOption.DISPLACEMENT_HIGH -> list.sortedByDescending { it.specs.displacementCc }
            SortOption.RATING -> list.sortedByDescending { it.rating }
            SortOption.NEWEST -> list.sortedByDescending { it.modelYear }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun selectBrandFilter(brandId: String?) {
        _selectedBrandId.value = brandId
    }

    fun selectCategoryFilter(category: String?) {
        _selectedCategory.value = category
    }

    fun selectSortOption(sort: SortOption) {
        _selectedSort.value = sort
    }

    fun setMaxPrice(price: Double?) {
        _maxPriceFilter.value = price
    }

    fun resetFilters() {
        _searchQuery.value = ""
        _selectedBrandId.value = null
        _selectedCategory.value = null
        _selectedSort.value = SortOption.POPULARITY
        _maxPriceFilter.value = null
    }

    // --- COMPARISON ENGINE STATE (Up to 3 Motorcycles) ---
    private val _comparisonSlotIds = MutableStateFlow<List<String>>(
        listOf("yamaha-r15-v4", "ktm-rc-200", "kawasaki-ninja-300") // Initial iconic trio
    )
    val comparisonSlotIds: StateFlow<List<String>> = _comparisonSlotIds.asStateFlow()

    private val _comparisonPriority = MutableStateFlow(UseCasePriority.BEST_OVERALL)
    val comparisonPriority: StateFlow<UseCasePriority> = _comparisonPriority.asStateFlow()

    fun setComparisonPriority(priority: UseCasePriority) {
        _comparisonPriority.value = priority
    }

    fun addToCompare(bikeId: String): Boolean {
        val current = _comparisonSlotIds.value.toMutableList()
        if (current.contains(bikeId)) return true
        if (current.size < 3) {
            current.add(bikeId)
            _comparisonSlotIds.value = current
            return true
        }
        return false // slot full
    }

    fun removeFromCompare(bikeId: String) {
        val current = _comparisonSlotIds.value.toMutableList()
        current.remove(bikeId)
        _comparisonSlotIds.value = current
    }

    fun setComparisonBikes(ids: List<String>) {
        _comparisonSlotIds.value = ids.take(3)
    }

    val comparisonReport: StateFlow<ComparisonReport?> = combine(
        allMotorcycles,
        _comparisonSlotIds,
        _comparisonPriority
    ) { bikes, slotIds, priority ->
        val bikeMap = bikes.associateBy { it.id }
        val bikeA = slotIds.getOrNull(0)?.let { bikeMap[it] }
        val bikeB = slotIds.getOrNull(1)?.let { bikeMap[it] }
        val bikeC = slotIds.getOrNull(2)?.let { bikeMap[it] }

        if (bikeA != null && bikeB != null) {
            ComparisonEngine.evaluate(bikeA, bikeB, bikeC, priority)
        } else {
            null
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    fun saveCurrentComparison() {
        viewModelScope.launch {
            val report = comparisonReport.value ?: return@launch
            val title = if (report.bikeC != null) {
                "${report.bikeA.modelName} vs ${report.bikeB.modelName} vs ${report.bikeC.modelName}"
            } else {
                "${report.bikeA.modelName} vs ${report.bikeB.modelName}"
            }
            val winnerName = when (report.overallWinnerIndex) {
                0 -> report.bikeA.modelName
                1 -> report.bikeB.modelName
                else -> report.bikeC?.modelName ?: report.bikeA.modelName
            }
            repository.saveComparison(
                bike1Id = report.bikeA.id,
                bike2Id = report.bikeB.id,
                bike3Id = report.bikeC?.id,
                title = title,
                primaryWinnerName = winnerName
            )
        }
    }

    // --- FAVORITES & RECENTLY VIEWED ---
    fun toggleFavorite(bikeId: String) {
        viewModelScope.launch {
            repository.toggleFavorite(bikeId)
        }
    }

    fun onMotorcycleViewed(bikeId: String) {
        viewModelScope.launch {
            repository.recordRecentlyViewed(bikeId)
        }
    }

    fun getVariantsForBike(bikeId: String): StateFlow<List<VariantEntity>> =
        repository.getVariants(bikeId)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // --- ADMIN ACTIONS ---
    fun addOrUpdateBrand(brand: BrandEntity) {
        viewModelScope.launch {
            repository.insertBrand(brand)
        }
    }

    fun deleteBrand(brand: BrandEntity) {
        viewModelScope.launch {
            repository.deleteBrand(brand)
        }
    }

    fun addOrUpdateMotorcycle(motorcycle: MotorcycleEntity) {
        viewModelScope.launch {
            repository.insertMotorcycle(motorcycle)
        }
    }

    fun deleteMotorcycle(motorcycle: MotorcycleEntity) {
        viewModelScope.launch {
            repository.deleteMotorcycle(motorcycle)
        }
    }

    fun addVariant(variant: VariantEntity) {
        viewModelScope.launch {
            repository.insertVariant(variant)
        }
    }

    private val _isSyncing = MutableStateFlow(false)
    val isSyncing: StateFlow<Boolean> = _isSyncing.asStateFlow()

    private val _syncMessage = MutableStateFlow<String?>(null)
    val syncMessage: StateFlow<String?> = _syncMessage.asStateFlow()

    val isFirebaseConfigured: Boolean
        get() = repository.isFirebaseConfigured()

    fun dismissSyncMessage() {
        _syncMessage.value = null
    }

    fun syncCatalog() {
        viewModelScope.launch {
            _isSyncing.value = true
            when (val result = repository.syncWithFirestore()) {
                is com.example.data.sync.SyncResult.Success -> {
                    _syncMessage.value = result.message
                }
                is com.example.data.sync.SyncResult.FirebaseNotConfigured -> {
                    _syncMessage.value = "Local DB Synced! ${PreloadedData.brands.size} Brands & ${PreloadedData.motorcycles.size} Models. (Firestore offline mode)"
                }
                is com.example.data.sync.SyncResult.Error -> {
                    _syncMessage.value = result.message
                }
            }
            _isSyncing.value = false
        }
    }

    fun uploadCatalogToFirestore() {
        viewModelScope.launch {
            _isSyncing.value = true
            when (val result = repository.uploadToFirestore()) {
                is com.example.data.sync.SyncResult.Success -> {
                    _syncMessage.value = result.message
                }
                is com.example.data.sync.SyncResult.FirebaseNotConfigured -> {
                    _syncMessage.value = "Firebase not initialized. Add google-services.json to push to Cloud Firestore."
                }
                is com.example.data.sync.SyncResult.Error -> {
                    _syncMessage.value = result.message
                }
            }
            _isSyncing.value = false
        }
    }

    fun resetCatalogToDefaults() {
        viewModelScope.launch {
            _isSyncing.value = true
            repository.resetDatabaseToDefaults()
            _isSyncing.value = false
            _syncMessage.value = "Reset to Default! ${PreloadedData.brands.size} Brands & ${PreloadedData.motorcycles.size} Models."
        }
    }

    init {
        viewModelScope.launch {
            repository.seedIfEmpty()
            // If Firebase is available, start live listener for instantaneous cloud updates
            if (repository.isFirebaseConfigured()) {
                repository.startRealtimeListener { updatedCount ->
                    _syncMessage.value = "Live Cloud Update: $updatedCount motorcycle specs synced in real-time!"
                }
            }
        }
    }
}
