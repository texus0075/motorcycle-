package com.example.ui.viewmodel

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
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
import kotlinx.coroutines.flow.flatMapLatest
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

    // Direct Room query flow for finding motorcycles by model or brand name
    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val searchResults: StateFlow<List<MotorcycleEntity>> = _searchQuery
        .flatMapLatest { query ->
            repository.searchMotorcycles(query)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), PreloadedData.motorcycles)

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

    fun submitUserRating(bikeId: String, newRating: Float) {
        viewModelScope.launch {
            val bike = allMotorcycles.value.find { it.id == bikeId } ?: return@launch
            val newCount = bike.reviewCount + 1
            val updatedRating = (((bike.rating * bike.reviewCount) + newRating) / newCount)
            val rounded = (kotlin.math.round(updatedRating * 10) / 10.0).toFloat()
            val updatedBike = bike.copy(
                rating = rounded,
                reviewCount = newCount
            )
            repository.insertMotorcycle(updatedBike)
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

    // --- FIREBASE AUTHENTICATION ---
    val currentUser: StateFlow<FirebaseUser?> = repository.authService?.currentUser
        ?: MutableStateFlow<FirebaseUser?>(null).asStateFlow()

    fun signInWithEmail(email: String, pass: String, onResult: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            val service = repository.authService ?: run {
                onResult(false, "Firebase Auth not available")
                return@launch
            }
            when (val res = service.signInWithEmail(email, pass)) {
                is com.example.data.auth.AuthResult.Success -> onResult(true, null)
                is com.example.data.auth.AuthResult.Error -> onResult(false, res.message)
            }
        }
    }

    fun signUpWithEmail(email: String, pass: String, onResult: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            val service = repository.authService ?: run {
                onResult(false, "Firebase Auth not available")
                return@launch
            }
            when (val res = service.signUpWithEmail(email, pass)) {
                is com.example.data.auth.AuthResult.Success -> onResult(true, null)
                is com.example.data.auth.AuthResult.Error -> onResult(false, res.message)
            }
        }
    }

    fun signInAnonymously(onResult: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            val service = repository.authService ?: run {
                onResult(false, "Firebase Auth not available")
                return@launch
            }
            when (val res = service.signInAnonymously()) {
                is com.example.data.auth.AuthResult.Success -> onResult(true, null)
                is com.example.data.auth.AuthResult.Error -> onResult(false, res.message)
            }
        }
    }

    fun sendPhoneOtp(
        phoneNumber: String,
        activity: Activity,
        callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks
    ) {
        repository.authService?.sendPhoneOtp(phoneNumber, activity, callbacks)
    }

    fun verifyPhoneCredential(credential: PhoneAuthCredential, onResult: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            val service = repository.authService ?: run {
                onResult(false, "Firebase Auth not available")
                return@launch
            }
            when (val res = service.signInWithPhoneCredential(credential)) {
                is com.example.data.auth.AuthResult.Success -> onResult(true, null)
                is com.example.data.auth.AuthResult.Error -> onResult(false, res.message)
            }
        }
    }

    fun signOut() {
        repository.authService?.signOut()
    }

    // --- GEMINI AI ASSISTANT & ADVISOR ---
    data class AiChatMessage(
        val isUser: Boolean,
        val text: String,
        val timestamp: Long = System.currentTimeMillis()
    )

    private val _aiAdvisorMessages = MutableStateFlow<List<AiChatMessage>>(
        listOf(
            AiChatMessage(
                isUser = false,
                text = "👋 Welcome to MotoScope AI Advisor!\nI am powered by Google Gemini. Ask me about finding the best motorcycle for your budget, height, riding style, pillion comfort, or performance goals!"
            )
        )
    )
    val aiAdvisorMessages: StateFlow<List<AiChatMessage>> = _aiAdvisorMessages.asStateFlow()

    private val _isAiAdvisorLoading = MutableStateFlow(false)
    val isAiAdvisorLoading: StateFlow<Boolean> = _isAiAdvisorLoading.asStateFlow()

    fun askMotoAdvisor(query: String) {
        if (query.isBlank()) return
        val userMsg = AiChatMessage(isUser = true, text = query.trim())
        _aiAdvisorMessages.value = _aiAdvisorMessages.value + userMsg
        _isAiAdvisorLoading.value = true

        viewModelScope.launch {
            val service = repository.geminiAiService
            if (service == null) {
                _aiAdvisorMessages.value = _aiAdvisorMessages.value + AiChatMessage(
                    isUser = false,
                    text = "Gemini AI service is not initialized."
                )
                _isAiAdvisorLoading.value = false
                return@launch
            }

            // Build catalog context
            val bikes = allMotorcycles.value.take(30)
            val catalogContext = bikes.joinToString("\n") {
                "- ${it.modelName} [ID: ${it.id}]: ${it.category}, ${it.specs.displacementCc}cc, ${it.specs.maxPowerHp} HP, ${it.specs.kerbWeightKg} kg, Seat: ${it.specs.seatHeightMm}mm, Mileage: ${it.specs.mileageKmpl} kmpl, Price: ${it.priceDisplay}"
            }

            val result = service.askAdvisor(query.trim(), catalogContext)
            val reply = result.getOrElse { err ->
                "⚠️ Could not reach Gemini AI: ${err.localizedMessage ?: "Network error"}. Please check your internet connection and try again."
            }

            _aiAdvisorMessages.value = _aiAdvisorMessages.value + AiChatMessage(isUser = false, text = reply)
            _isAiAdvisorLoading.value = false
        }
    }

    fun clearAiAdvisorChat() {
        _aiAdvisorMessages.value = listOf(
            AiChatMessage(
                isUser = false,
                text = "Chat cleared. What motorcycle question would you like to explore next?"
            )
        )
    }

    // --- GEMINI AI COMPARISON VERDICT ---
    private val _aiComparisonVerdict = MutableStateFlow<String?>(null)
    val aiComparisonVerdict: StateFlow<String?> = _aiComparisonVerdict.asStateFlow()

    private val _isAiComparisonLoading = MutableStateFlow(false)
    val isAiComparisonLoading: StateFlow<Boolean> = _isAiComparisonLoading.asStateFlow()

    fun requestAiComparisonVerdict() {
        val rep = _comparisonReport.value ?: return
        val service = repository.geminiAiService ?: return

        _isAiComparisonLoading.value = true
        viewModelScope.launch {
            val result = service.generateComparisonAiVerdict(
                bikeA = rep.bikeA,
                bikeB = rep.bikeB,
                bikeC = rep.bikeC,
                priority = _comparisonPriority.value.displayName
            )
            _aiComparisonVerdict.value = result.getOrElse { err ->
                "⚠️ Gemini shootout unavailable: ${err.localizedMessage}"
            }
            _isAiComparisonLoading.value = false
        }
    }

    // --- GEMINI AI INDIVIDUAL BIKE INSIGHTS ---
    private val _aiBikeInsights = MutableStateFlow<Map<String, String>>(emptyMap())
    val aiBikeInsights: StateFlow<Map<String, String>> = _aiBikeInsights.asStateFlow()

    private val _isAiBikeInsightsLoading = MutableStateFlow(false)
    val isAiBikeInsightsLoading: StateFlow<Boolean> = _isAiBikeInsightsLoading.asStateFlow()

    fun requestBikeAiInsights(bikeId: String) {
        val bike = allMotorcycles.value.firstOrNull { it.id == bikeId } ?: return
        val service = repository.geminiAiService ?: return

        _isAiBikeInsightsLoading.value = true
        viewModelScope.launch {
            val result = service.analyzeMotorcycleInsights(bike)
            val currentMap = _aiBikeInsights.value.toMutableMap()
            currentMap[bikeId] = result.getOrElse { err ->
                "⚠️ Gemini analysis unavailable: ${err.localizedMessage}"
            }
            _aiBikeInsights.value = currentMap
            _isAiBikeInsightsLoading.value = false
        }
    }

    init {
        viewModelScope.launch {
            repository.syncCatalogWithDefaults()
            // If Firebase is available, perform cloud sync and start real-time listener
            if (repository.isFirebaseConfigured()) {
                repository.syncWithFirestore()
                repository.startRealtimeListener { updatedCount ->
                    _syncMessage.value = "Live Cloud Update: $updatedCount motorcycle specs synced in real-time!"
                }
            }
        }
    }
}
