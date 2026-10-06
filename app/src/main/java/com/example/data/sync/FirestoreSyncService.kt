package com.example.data.sync

import android.content.Context
import android.util.Log
import com.example.data.local.MotoDao
import com.example.data.local.PreloadedData
import com.example.data.models.BrandEntity
import com.example.data.models.MotorcycleEntity
import com.example.data.models.MotorcycleSpecs
import com.example.data.models.VariantEntity
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

sealed interface SyncResult {
    data class Success(val message: String, val bikesSynced: Int, val brandsSynced: Int) : SyncResult
    data class FirebaseNotConfigured(val message: String) : SyncResult
    data class Error(val message: String, val throwable: Throwable? = null) : SyncResult
}

class FirestoreSyncService(
    private val context: Context,
    private val dao: MotoDao
) {
    private val tag = "FirestoreSyncService"
    private val syncScope = kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.SupervisorJob() + Dispatchers.IO)

    fun isFirebaseAvailable(): Boolean {
        return try {
            FirebaseApp.getApps(context).isNotEmpty()
        } catch (e: Exception) {
            false
        }
    }

    private fun getFirestore(): FirebaseFirestore? {
        return try {
            if (isFirebaseAvailable()) {
                FirebaseFirestore.getInstance()
            } else {
                null
            }
        } catch (e: Exception) {
            Log.w(tag, "Firebase is not initialized: ${e.message}")
            null
        }
    }

    /**
     * Performs a bidirectional synchronization:
     * 1. Checks if Firestore has bikes.
     * 2. If Firestore has data, syncs from Firestore -> Room database.
     * 3. If Firestore is empty, pushes local Room/Preloaded dataset -> Firestore (auto-seed).
     */
    suspend fun syncWithFirestore(): SyncResult = withContext(Dispatchers.IO) {
        val firestore = getFirestore()
            ?: return@withContext SyncResult.FirebaseNotConfigured(
                "Firebase is not configured yet (google-services.json not found). Running in offline Room database mode."
            )

        try {
            // 1. Fetch remote motorcycles
            val bikeSnapshot = firestore.collection("motorcycles").get().await()
            val brandSnapshot = firestore.collection("brands").get().await()

            if (bikeSnapshot.isEmpty) {
                // Cloud is empty, seed Room dataset up to Firestore!
                Log.i(tag, "Firestore collections empty. Seeding preloaded catalog to Cloud Firestore...")
                val localBikes = dao.getAllMotorcycles().firstOrNull() ?: PreloadedData.motorcycles
                val localBrands = dao.getAllBrands().firstOrNull() ?: PreloadedData.brands
                val localVariants = PreloadedData.variants

                uploadCatalogToFirestore(localBrands, localBikes, localVariants)
                return@withContext SyncResult.Success(
                    message = "Cloud Firestore seeded with ${localBikes.size} motorcycles and ${localBrands.size} brands!",
                    bikesSynced = localBikes.size,
                    brandsSynced = localBrands.size
                )
            }

            // 2. Cloud has data: Parse and upsert into Room DB
            val remoteBrands = brandSnapshot.documents.mapNotNull { doc ->
                val data = doc.data ?: return@mapNotNull null
                mapToBrand(doc.id, data)
            }

            val remoteBikes = bikeSnapshot.documents.mapNotNull { doc ->
                val data = doc.data ?: return@mapNotNull null
                mapToMotorcycle(doc.id, data)
            }

            if (remoteBrands.isNotEmpty()) {
                dao.insertBrands(remoteBrands)
            }
            if (remoteBikes.isNotEmpty()) {
                dao.insertMotorcycles(remoteBikes)
            }

            Log.i(tag, "Successfully synced ${remoteBikes.size} bikes & ${remoteBrands.size} brands from Firestore to Room DB.")
            SyncResult.Success(
                message = "Live Sync Complete: ${remoteBikes.size} bikes and ${remoteBrands.size} brands updated dynamically from Cloud Firestore.",
                bikesSynced = remoteBikes.size,
                brandsSynced = remoteBrands.size
            )
        } catch (e: Exception) {
            Log.e(tag, "Firestore synchronization failed", e)
            SyncResult.Error(
                message = "Firestore sync error: ${e.localizedMessage ?: e.message}",
                throwable = e
            )
        }
    }

    /**
     * Uploads the entire local catalog (brands and motorcycles) into Firestore.
     */
    suspend fun uploadCatalogToFirestore(
        brands: List<BrandEntity>,
        motorcycles: List<MotorcycleEntity>,
        variants: List<VariantEntity> = emptyList()
    ): SyncResult = withContext(Dispatchers.IO) {
        val firestore = getFirestore()
            ?: return@withContext SyncResult.FirebaseNotConfigured(
                "Firebase is not configured yet (google-services.json missing)."
            )

        try {
            // Firestore write batch (max 500 ops per batch)
            // 1. Upload Brands
            val brandBatch = firestore.batch()
            for (brand in brands) {
                val docRef = firestore.collection("brands").document(brand.id)
                brandBatch.set(docRef, brandToMap(brand), SetOptions.merge())
            }
            brandBatch.commit().await()

            // 2. Upload Motorcycles in chunks of 400
            motorcycles.chunked(400).forEach { chunk ->
                val bikeBatch = firestore.batch()
                for (bike in chunk) {
                    val docRef = firestore.collection("motorcycles").document(bike.id)
                    bikeBatch.set(docRef, motorcycleToMap(bike), SetOptions.merge())
                }
                bikeBatch.commit().await()
            }

            // 3. Upload Variants if present
            if (variants.isNotEmpty()) {
                variants.chunked(400).forEach { chunk ->
                    val variantBatch = firestore.batch()
                    for (variant in chunk) {
                        val docRef = firestore.collection("variants").document(variant.id)
                        variantBatch.set(docRef, variantToMap(variant), SetOptions.merge())
                    }
                    variantBatch.commit().await()
                }
            }

            SyncResult.Success(
                message = "Uploaded ${motorcycles.size} motorcycles and ${brands.size} brands to Firebase Firestore successfully!",
                bikesSynced = motorcycles.size,
                brandsSynced = brands.size
            )
        } catch (e: Exception) {
            Log.e(tag, "Upload to Firestore failed", e)
            SyncResult.Error(
                message = "Failed to upload to Firestore: ${e.localizedMessage ?: e.message}",
                throwable = e
            )
        }
    }

    /**
     * Starts a real-time Firestore listener that listens for updates to any motorcycle
     * and immediately saves them to Room DB, ensuring zero app re-downloads!
     */
    fun startRealtimeListener(onUpdateReceived: (Int) -> Unit = {}): ListenerRegistration? {
        val firestore = getFirestore() ?: return null

        return try {
            firestore.collection("motorcycles")
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        Log.e(tag, "Real-time snapshot error: ${error.message}")
                        return@addSnapshotListener
                    }
                    if (snapshot != null && !snapshot.isEmpty) {
                        val updatedBikes = snapshot.documents.mapNotNull { doc ->
                            val data = doc.data ?: return@mapNotNull null
                            mapToMotorcycle(doc.id, data)
                        }
                        if (updatedBikes.isNotEmpty()) {
                            syncScope.launch {
                                dao.insertMotorcycles(updatedBikes)
                                withContext(Dispatchers.Main) {
                                    onUpdateReceived(updatedBikes.size)
                                }
                            }
                        }
                    }
                }
        } catch (e: Exception) {
            Log.w(tag, "Could not start real-time listener: ${e.message}")
            null
        }
    }

    /**
     * Backs up user favorites to /users/{userId} in Firestore
     */
    suspend fun backupUserFavorites(userId: String, favoriteBikeIds: List<String>): Boolean = withContext(Dispatchers.IO) {
        val firestore = getFirestore() ?: return@withContext false
        try {
            val userDoc = firestore.collection("users").document(userId)
            userDoc.set(mapOf("favorites" to favoriteBikeIds, "lastUpdated" to System.currentTimeMillis()), SetOptions.merge()).await()
            true
        } catch (e: Exception) {
            Log.e(tag, "Failed to backup user favorites", e)
            false
        }
    }

    /**
     * Restores user favorites from /users/{userId} in Firestore
     */
    suspend fun restoreUserFavorites(userId: String): List<String> = withContext(Dispatchers.IO) {
        val firestore = getFirestore() ?: return@withContext emptyList()
        try {
            val doc = firestore.collection("users").document(userId).get().await()
            @Suppress("UNCHECKED_CAST")
            (doc.get("favorites") as? List<String>) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    // --- MAPPERS ---
    private fun brandToMap(brand: BrandEntity): Map<String, Any?> = mapOf(
        "id" to brand.id,
        "name" to brand.name,
        "country" to brand.country,
        "foundedYear" to brand.foundedYear,
        "history" to brand.history,
        "websiteUrl" to brand.websiteUrl,
        "logoUrl" to brand.logoUrl,
        "accentColorHex" to brand.accentColorHex
    )

    private fun mapToBrand(id: String, data: Map<String, Any?>): BrandEntity = BrandEntity(
        id = id,
        name = data["name"] as? String ?: id,
        country = data["country"] as? String ?: "",
        foundedYear = (data["foundedYear"] as? Number)?.toInt() ?: 1900,
        history = data["history"] as? String ?: "",
        websiteUrl = data["websiteUrl"] as? String ?: "",
        logoUrl = data["logoUrl"] as? String ?: "",
        accentColorHex = (data["accentColorHex"] as? Number)?.toLong() ?: 0xFF38BDF8
    )

    private fun variantToMap(variant: VariantEntity): Map<String, Any?> = mapOf(
        "id" to variant.id,
        "motorcycleId" to variant.motorcycleId,
        "variantName" to variant.variantName,
        "price" to variant.price,
        "priceDisplay" to variant.priceDisplay,
        "highlightedFeatures" to variant.highlightedFeatures,
        "variantImageUrl" to variant.variantImageUrl,
        "colorName" to variant.colorName,
        "colorHex" to variant.colorHex
    )

    private fun motorcycleToMap(bike: MotorcycleEntity): Map<String, Any?> {
        val s = bike.specs
        return mapOf(
            "id" to bike.id,
            "brandId" to bike.brandId,
            "modelName" to bike.modelName,
            "category" to bike.category,
            "modelYear" to bike.modelYear,
            "basePrice" to bike.basePrice,
            "currency" to bike.currency,
            "priceDisplay" to bike.priceDisplay,
            "rating" to bike.rating.toDouble(),
            "reviewCount" to bike.reviewCount,
            "isTrending" to bike.isTrending,
            "isNew" to bike.isNew,
            "isPopular" to bike.isPopular,
            "heroImageUrl" to bike.heroImageUrl,
            "frontImageUrl" to bike.frontImageUrl,
            "sideImageUrl" to bike.sideImageUrl,
            "rearImageUrl" to bike.rearImageUrl,
            "cockpitImageUrl" to bike.cockpitImageUrl,
            "availableColorsJson" to bike.availableColorsJson,
            "source" to bike.source,
            "sourceUrl" to bike.sourceUrl,
            "lastUpdated" to bike.lastUpdated,
            // Specs
            "engineType" to s.engineType,
            "displacementCc" to s.displacementCc,
            "cylinders" to s.cylinders,
            "cooling" to s.cooling,
            "valves" to s.valves,
            "boreMm" to s.boreMm,
            "strokeMm" to s.strokeMm,
            "compressionRatio" to s.compressionRatio,
            "fuelSystem" to s.fuelSystem,
            "fuelType" to s.fuelType,
            "startingSystem" to s.startingSystem,
            "maxPowerHp" to s.maxPowerHp,
            "maxPowerRpm" to s.maxPowerRpm,
            "maxPowerDisplay" to s.maxPowerDisplay,
            "maxTorqueNm" to s.maxTorqueNm,
            "maxTorqueRpm" to s.maxTorqueRpm,
            "maxTorqueDisplay" to s.maxTorqueDisplay,
            "topSpeedKmh" to s.topSpeedKmh,
            "accel0To60Sec" to s.accel0To60Sec,
            "accel0To100Sec" to s.accel0To100Sec,
            "mileageKmpl" to s.mileageKmpl,
            "powerToWeightHpPerTon" to s.powerToWeightHpPerTon,
            "gearbox" to s.gearbox,
            "numberOfGears" to s.numberOfGears,
            "clutchType" to s.clutchType,
            "finalDrive" to s.finalDrive,
            "frameType" to s.frameType,
            "frontSuspension" to s.frontSuspension,
            "rearSuspension" to s.rearSuspension,
            "frontTravelMm" to s.frontTravelMm,
            "rearTravelMm" to s.rearTravelMm,
            "frontBrake" to s.frontBrake,
            "rearBrake" to s.rearBrake,
            "absSystem" to s.absSystem,
            "brakeDimensions" to s.brakeDimensions,
            "frontTyre" to s.frontTyre,
            "rearTyre" to s.rearTyre,
            "wheelType" to s.wheelType,
            "wheelSizeInches" to s.wheelSizeInches,
            "lengthMm" to s.lengthMm,
            "widthMm" to s.widthMm,
            "heightMm" to s.heightMm,
            "wheelbaseMm" to s.wheelbaseMm,
            "groundClearanceMm" to s.groundClearanceMm,
            "seatHeightMm" to s.seatHeightMm,
            "kerbWeightKg" to s.kerbWeightKg,
            "fuelTankCapacityL" to s.fuelTankCapacityL,
            "ridingModes" to s.ridingModes,
            "hasTractionControl" to s.hasTractionControl,
            "quickShifter" to s.quickShifter,
            "hasCruiseControl" to s.hasCruiseControl,
            "hasLaunchControl" to s.hasLaunchControl,
            "hasRideByWire" to s.hasRideByWire,
            "hasTftDisplay" to s.hasTftDisplay,
            "displayDetails" to s.displayDetails,
            "hasBluetooth" to s.hasBluetooth,
            "hasNavigation" to s.hasNavigation,
            "ledLighting" to s.ledLighting,
            "hasKeylessIgnition" to s.hasKeylessIgnition,
            "hasCorneringAbs" to s.hasCorneringAbs,
            "hasStabilityControl" to s.hasStabilityControl,
            "hasSlipperClutch" to s.hasSlipperClutch,
            "hasWheelieControl" to s.hasWheelieControl
        )
    }

    private fun mapToMotorcycle(id: String, data: Map<String, Any?>): MotorcycleEntity {
        val specs = MotorcycleSpecs(
            engineType = data["engineType"] as? String ?: "",
            displacementCc = (data["displacementCc"] as? Number)?.toDouble() ?: 0.0,
            cylinders = (data["cylinders"] as? Number)?.toInt() ?: 1,
            cooling = data["cooling"] as? String ?: "Liquid Cooled",
            valves = (data["valves"] as? Number)?.toInt() ?: 4,
            boreMm = (data["boreMm"] as? Number)?.toDouble() ?: 0.0,
            strokeMm = (data["strokeMm"] as? Number)?.toDouble() ?: 0.0,
            compressionRatio = data["compressionRatio"] as? String ?: "",
            fuelSystem = data["fuelSystem"] as? String ?: "Electronic Fuel Injection",
            fuelType = data["fuelType"] as? String ?: "Petrol",
            startingSystem = data["startingSystem"] as? String ?: "Electric Start",
            maxPowerHp = (data["maxPowerHp"] as? Number)?.toDouble() ?: 0.0,
            maxPowerRpm = (data["maxPowerRpm"] as? Number)?.toInt() ?: 0,
            maxPowerDisplay = data["maxPowerDisplay"] as? String ?: "",
            maxTorqueNm = (data["maxTorqueNm"] as? Number)?.toDouble() ?: 0.0,
            maxTorqueRpm = (data["maxTorqueRpm"] as? Number)?.toInt() ?: 0,
            maxTorqueDisplay = data["maxTorqueDisplay"] as? String ?: "",
            topSpeedKmh = (data["topSpeedKmh"] as? Number)?.toInt() ?: 0,
            accel0To60Sec = (data["accel0To60Sec"] as? Number)?.toDouble() ?: 0.0,
            accel0To100Sec = (data["accel0To100Sec"] as? Number)?.toDouble() ?: 0.0,
            mileageKmpl = (data["mileageKmpl"] as? Number)?.toDouble() ?: 0.0,
            powerToWeightHpPerTon = (data["powerToWeightHpPerTon"] as? Number)?.toDouble() ?: 0.0,
            gearbox = data["gearbox"] as? String ?: "6-Speed",
            numberOfGears = (data["numberOfGears"] as? Number)?.toInt() ?: 6,
            clutchType = data["clutchType"] as? String ?: "Assist & Slipper Clutch",
            finalDrive = data["finalDrive"] as? String ?: "Chain",
            frameType = data["frameType"] as? String ?: "Steel Diamond / Trellis",
            frontSuspension = data["frontSuspension"] as? String ?: "Inverted Telescopic Fork",
            rearSuspension = data["rearSuspension"] as? String ?: "Monoshock",
            frontTravelMm = (data["frontTravelMm"] as? Number)?.toInt() ?: 120,
            rearTravelMm = (data["rearTravelMm"] as? Number)?.toInt() ?: 130,
            frontBrake = data["frontBrake"] as? String ?: "Single Disc with ABS",
            rearBrake = data["rearBrake"] as? String ?: "Single Disc with ABS",
            absSystem = data["absSystem"] as? String ?: "Dual-Channel ABS",
            brakeDimensions = data["brakeDimensions"] as? String ?: "Front 300mm / Rear 240mm",
            frontTyre = data["frontTyre"] as? String ?: "110/70-17",
            rearTyre = data["rearTyre"] as? String ?: "150/60-17",
            wheelType = data["wheelType"] as? String ?: "Cast Aluminium Alloy",
            wheelSizeInches = (data["wheelSizeInches"] as? Number)?.toInt() ?: 17,
            lengthMm = (data["lengthMm"] as? Number)?.toInt() ?: 2000,
            widthMm = (data["widthMm"] as? Number)?.toInt() ?: 750,
            heightMm = (data["heightMm"] as? Number)?.toInt() ?: 1100,
            wheelbaseMm = (data["wheelbaseMm"] as? Number)?.toInt() ?: 1350,
            groundClearanceMm = (data["groundClearanceMm"] as? Number)?.toInt() ?: 160,
            seatHeightMm = (data["seatHeightMm"] as? Number)?.toInt() ?: 810,
            kerbWeightKg = (data["kerbWeightKg"] as? Number)?.toDouble() ?: 160.0,
            fuelTankCapacityL = (data["fuelTankCapacityL"] as? Number)?.toDouble() ?: 13.0,
            ridingModes = data["ridingModes"] as? String ?: "Standard",
            hasTractionControl = data["hasTractionControl"] as? Boolean ?: true,
            quickShifter = data["quickShifter"] as? String ?: "Bidirectional (Up/Down)",
            hasCruiseControl = data["hasCruiseControl"] as? Boolean ?: false,
            hasLaunchControl = data["hasLaunchControl"] as? Boolean ?: false,
            hasRideByWire = data["hasRideByWire"] as? Boolean ?: true,
            hasTftDisplay = data["hasTftDisplay"] as? Boolean ?: true,
            displayDetails = data["displayDetails"] as? String ?: "5-inch Full Color TFT",
            hasBluetooth = data["hasBluetooth"] as? Boolean ?: true,
            hasNavigation = data["hasNavigation"] as? Boolean ?: true,
            ledLighting = data["ledLighting"] as? String ?: "All-LED",
            hasKeylessIgnition = data["hasKeylessIgnition"] as? Boolean ?: false,
            hasCorneringAbs = data["hasCorneringAbs"] as? Boolean ?: false,
            hasStabilityControl = data["hasStabilityControl"] as? Boolean ?: false,
            hasSlipperClutch = data["hasSlipperClutch"] as? Boolean ?: true,
            hasWheelieControl = data["hasWheelieControl"] as? Boolean ?: false
        )

        return MotorcycleEntity(
            id = id,
            brandId = data["brandId"] as? String ?: "",
            modelName = data["modelName"] as? String ?: "",
            category = data["category"] as? String ?: "Sport",
            modelYear = (data["modelYear"] as? Number)?.toInt() ?: 2024,
            basePrice = (data["basePrice"] as? Number)?.toDouble() ?: 0.0,
            currency = data["currency"] as? String ?: "$",
            priceDisplay = data["priceDisplay"] as? String ?: "",
            rating = (data["rating"] as? Number)?.toFloat() ?: 4.6f,
            reviewCount = (data["reviewCount"] as? Number)?.toInt() ?: 100,
            isTrending = data["isTrending"] as? Boolean ?: false,
            isNew = data["isNew"] as? Boolean ?: false,
            isPopular = data["isPopular"] as? Boolean ?: false,
            heroImageUrl = data["heroImageUrl"] as? String ?: "",
            frontImageUrl = data["frontImageUrl"] as? String ?: "",
            sideImageUrl = data["sideImageUrl"] as? String ?: "",
            rearImageUrl = data["rearImageUrl"] as? String ?: "",
            cockpitImageUrl = data["cockpitImageUrl"] as? String ?: "",
            availableColorsJson = data["availableColorsJson"] as? String ?: "[]",
            source = data["source"] as? String ?: "Official Homologation",
            sourceUrl = data["sourceUrl"] as? String ?: "",
            lastUpdated = data["lastUpdated"] as? String ?: "2024",
            specs = specs
        )
    }
}
