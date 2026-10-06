package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CompareArrows
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.data.models.VariantEntity
import com.example.ui.components.DetailSpecRow
import com.example.ui.components.EmiCalculatorCard
import com.example.ui.navigation.MotoScreen
import com.example.ui.theme.AmberOrange
import com.example.ui.theme.CyanNeon
import com.example.ui.theme.Slate400
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate800
import com.example.ui.theme.Slate850
import com.example.ui.theme.Slate900
import com.example.ui.theme.Slate950
import com.example.ui.theme.TrackGreen
import com.example.ui.viewmodel.MotoViewModel

@Composable
fun MotorcycleDetailScreen(
    bikeId: String,
    viewModel: MotoViewModel,
    modifier: Modifier = Modifier
) {
    BackHandler {
        viewModel.navigateBack()
    }

    val context = LocalContext.current
    val allMotorcycles by viewModel.allMotorcycles.collectAsStateWithLifecycle()
    val comparisonSlots by viewModel.comparisonSlotIds.collectAsStateWithLifecycle()
    val favorites by viewModel.favorites.collectAsStateWithLifecycle()
    val variants by viewModel.getVariantsForBike(bikeId).collectAsStateWithLifecycle()

    val bike = allMotorcycles.find { it.id == bikeId }
    val isFavorite = favorites.any { it.motorcycleId == bikeId }
    val isInCompare = comparisonSlots.contains(bikeId)

    val aiInsightsMap by viewModel.aiBikeInsights.collectAsStateWithLifecycle()
    val isAiLoading by viewModel.isAiBikeInsightsLoading.collectAsStateWithLifecycle()
    val currentInsight = aiInsightsMap[bikeId]
    var expandAiInsights by remember { mutableStateOf(false) }

    // State for gallery angle selection
    var selectedAngle by remember { mutableStateOf("Hero") }
    // State for selected variant
    var selectedVariant by remember { mutableStateOf<VariantEntity?>(null) }
    // State for rating modal
    var showRatingDialog by remember { mutableStateOf(false) }
    var userRatingScore by remember { mutableFloatStateOf(5f) }
    var ratingSubmitted by remember { mutableStateOf(false) }

    // Section expansion toggles
    var expandEngine by remember { mutableStateOf(true) }
    var expandPerformance by remember { mutableStateOf(true) }
    var expandTransmission by remember { mutableStateOf(false) }
    var expandChassis by remember { mutableStateOf(false) }
    var expandBrakes by remember { mutableStateOf(false) }
    var expandWheels by remember { mutableStateOf(false) }
    var expandDimensions by remember { mutableStateOf(true) }
    var expandElectronics by remember { mutableStateOf(true) }
    var expandSafety by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Slate950)
            .testTag("motorcycle_detail_screen")
    ) {
        // --- Top Bar ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Slate900)
                .padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = { viewModel.navigateBack() },
                    modifier = Modifier.testTag("detail_back_btn")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }
                Text(
                    text = bike?.modelName ?: "Motorcycle Details",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = Color.White
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = { viewModel.toggleFavorite(bikeId) },
                    modifier = Modifier.testTag("detail_fav_btn")
                ) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Filled.Bookmark else Icons.Filled.BookmarkBorder,
                        contentDescription = "Favorite",
                        tint = if (isFavorite) AmberOrange else Slate400
                    )
                }
            }
        }

        if (bike == null) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("Motorcycle not found", color = Slate400)
            }
            return
        }

        val activeImage = when (selectedAngle) {
            "Front" -> if (bike.frontImageUrl.isNotBlank()) bike.frontImageUrl else bike.heroImageUrl
            "Side" -> if (bike.sideImageUrl.isNotBlank()) bike.sideImageUrl else bike.heroImageUrl
            "Rear" -> if (bike.rearImageUrl.isNotBlank()) bike.rearImageUrl else bike.heroImageUrl
            "Cockpit" -> if (bike.cockpitImageUrl.isNotBlank()) bike.cockpitImageUrl else bike.heroImageUrl
            else -> selectedVariant?.variantImageUrl?.takeIf { it.isNotBlank() } ?: bike.heroImageUrl
        }

        val activePriceDisplay = selectedVariant?.priceDisplay ?: bike.priceDisplay

        LazyColumn(
            contentPadding = PaddingValues(bottom = 120.dp)
        ) {
            // --- 1. Gallery Section ---
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(280.dp)
                        .background(Slate950)
                ) {
                    AsyncImage(
                        model = activeImage,
                        contentDescription = "${bike.modelName} $selectedAngle",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )

                    // Brand watermark tag
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(14.dp)
                            .background(Slate950.copy(alpha = 0.85f), RoundedCornerShape(8.dp))
                            .border(1.dp, Slate700, RoundedCornerShape(8.dp))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "${bike.brandId.uppercase()} • ${bike.modelYear}",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = CyanNeon,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }

                    // Angle Selector Pill Row at bottom of image
                    Row(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 12.dp)
                            .background(Slate950.copy(alpha = 0.85f), RoundedCornerShape(20.dp))
                            .border(1.dp, Slate800, RoundedCornerShape(20.dp))
                            .padding(horizontal = 6.dp, vertical = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        val angles = listOf("Hero", "Front", "Side", "Rear", "Cockpit")
                        angles.forEach { angle ->
                            val isSelected = selectedAngle == angle
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = if (isSelected) CyanNeon else Color.Transparent,
                                modifier = Modifier
                                    .clickable { selectedAngle = angle }
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = angle,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = if (isSelected) Slate950 else Slate400,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp
                                    )
                                )
                            }
                        }
                    }
                }
            }

            // --- 2. Title, Rating & Pricing Block ---
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Slate900)
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = bike.modelName,
                                style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Black),
                                color = Color.White
                            )
                            Text(
                                text = "${bike.category} • Certified Model Year ${bike.modelYear}",
                                style = MaterialTheme.typography.bodyMedium.copy(color = Slate400)
                            )
                        }

                        // Rating badge (clickable to rate)
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .background(Slate850, RoundedCornerShape(8.dp))
                                .border(1.dp, Slate800, RoundedCornerShape(8.dp))
                                .clickable { showRatingDialog = true }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Icon(imageVector = Icons.Filled.Star, contentDescription = "Rate Bike", tint = AmberOrange, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "${bike.rating}", fontWeight = FontWeight.Bold, color = Color.White)
                            Text(text = " (${bike.reviewCount})", color = Slate400, fontSize = 11.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Price display
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = if (selectedVariant != null) "VARIANT PRICE" else "BASE EX-SHOWROOM / MSRP",
                                style = MaterialTheme.typography.labelSmall.copy(color = Slate400, fontSize = 10.sp)
                            )
                            Text(
                                text = activePriceDisplay,
                                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                                color = TrackGreen
                            )
                        }

                        // Add to Compare Action Button
                        Button(
                            onClick = {
                                if (isInCompare) {
                                    viewModel.removeFromCompare(bike.id)
                                } else {
                                    viewModel.addToCompare(bike.id)
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isInCompare) CyanNeon else Slate800,
                                contentColor = if (isInCompare) Slate950 else Color.White
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.testTag("detail_compare_btn")
                        ) {
                            Icon(
                                imageVector = if (isInCompare) Icons.Filled.Check else Icons.Filled.CompareArrows,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isInCompare) "Comparing (In Dock)" else "Add to Compare",
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // --- EMI & On-Road Estimator ---
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Slate950)
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    EmiCalculatorCard(
                        basePrice = selectedVariant?.price ?: bike.basePrice,
                        currencySymbol = bike.currency
                    )
                }
            }

            // --- 2.8 GEMINI AI RIDER & OWNERSHIP INSIGHTS CARD ---
            item {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Slate900,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                        .border(1.2.dp, CyanNeon.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                        .testTag("detail_gemini_insights_card")
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    expandAiInsights = !expandAiInsights
                                    if (expandAiInsights && currentInsight == null) {
                                        viewModel.requestBikeAiInsights(bikeId)
                                    }
                                },
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .background(CyanNeon.copy(alpha = 0.15f), CircleShape)
                                        .border(1.dp, CyanNeon, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.AutoAwesome,
                                        contentDescription = null,
                                        tint = CyanNeon,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = "Gemini AI Rider Analysis",
                                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                            color = Color.White
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Surface(
                                            color = CyanNeon.copy(alpha = 0.2f),
                                            shape = RoundedCornerShape(4.dp)
                                        ) {
                                            Text(
                                                text = "GEMINI 2.5",
                                                color = CyanNeon,
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Black,
                                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                    Text(
                                        text = "Ergonomics, pillion comfort & maintenance insights",
                                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                        color = Slate400
                                    )
                                }
                            }

                            Icon(
                                imageVector = if (expandAiInsights) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                                contentDescription = null,
                                tint = CyanNeon
                            )
                        }

                        AnimatedVisibility(visible = expandAiInsights) {
                            Column(modifier = Modifier.padding(top = 12.dp)) {
                                if (isAiLoading) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(vertical = 10.dp)
                                    ) {
                                        CircularProgressIndicator(
                                            color = CyanNeon,
                                            modifier = Modifier.size(18.dp),
                                            strokeWidth = 2.dp
                                        )
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Text(
                                            text = "Gemini is evaluating geometry & rider telemetry...",
                                            style = MaterialTheme.typography.bodySmall.copy(color = Slate400, fontSize = 12.sp)
                                        )
                                    }
                                } else if (currentInsight != null) {
                                    Surface(
                                        shape = RoundedCornerShape(10.dp),
                                        color = Slate950,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .border(1.dp, Slate800, RoundedCornerShape(10.dp))
                                            .padding(12.dp)
                                    ) {
                                        Column {
                                            Text(
                                                text = currentInsight,
                                                style = MaterialTheme.typography.bodyMedium.copy(
                                                    color = Color.White,
                                                    lineHeight = 20.sp,
                                                    fontSize = 12.5.sp
                                                )
                                            )
                                            Spacer(modifier = Modifier.height(10.dp))
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.End
                                            ) {
                                                IconButton(
                                                    onClick = { viewModel.requestBikeAiInsights(bikeId) },
                                                    modifier = Modifier.size(28.dp)
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Filled.Refresh,
                                                        contentDescription = "Regenerate",
                                                        tint = CyanNeon,
                                                        modifier = Modifier.size(16.dp)
                                                    )
                                                }
                                            }
                                        }
                                    }
                                } else {
                                    Button(
                                        onClick = { viewModel.requestBikeAiInsights(bikeId) },
                                        colors = ButtonDefaults.buttonColors(containerColor = CyanNeon, contentColor = Slate950),
                                        shape = RoundedCornerShape(10.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text("Generate AI Rider Report", fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // --- 3. Variants Selector (if available) ---
            if (variants.isNotEmpty()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Slate950)
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "OFFICIAL VARIANTS (${variants.size})",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = Slate400,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            items(variants) { variant ->
                                val isSelected = selectedVariant?.id == variant.id
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = if (isSelected) Slate850 else Slate900,
                                    modifier = Modifier
                                        .width(220.dp)
                                        .border(
                                            width = if (isSelected) 1.5.dp else 0.8.dp,
                                            color = if (isSelected) CyanNeon else Slate800,
                                            shape = RoundedCornerShape(12.dp)
                                        )
                                        .clickable {
                                            selectedVariant = if (isSelected) null else variant
                                        }
                                        .padding(12.dp)
                                        .testTag("variant_${variant.id}")
                                ) {
                                    Column {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = variant.variantName,
                                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                                color = Color.White,
                                                maxLines = 1
                                            )
                                            if (isSelected) {
                                                Icon(imageVector = Icons.Filled.Check, contentDescription = null, tint = CyanNeon, modifier = Modifier.size(16.dp))
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = variant.priceDisplay,
                                            style = MaterialTheme.typography.bodyMedium.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = TrackGreen
                                            )
                                        )

                                        if (variant.highlightedFeatures.isNotBlank()) {
                                            Spacer(modifier = Modifier.height(6.dp))
                                            Text(
                                                text = variant.highlightedFeatures,
                                                style = MaterialTheme.typography.bodySmall.copy(color = Slate400, fontSize = 11.sp),
                                                maxLines = 2
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // --- 4. Key Performance Telemetry Bar ---
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .background(Slate900, RoundedCornerShape(14.dp))
                        .border(1.dp, Slate800, RoundedCornerShape(14.dp))
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    TelemetryItem(label = "POWER", value = "${bike.specs.maxPowerHp} HP")
                    TelemetryItem(label = "TORQUE", value = "${bike.specs.maxTorqueNm} Nm")
                    TelemetryItem(label = "TOP SPEED", value = "${bike.specs.topSpeedKmh} km/h")
                    TelemetryItem(label = "0-100 KM/H", value = "${bike.specs.accel0To100Sec}s")
                }
            }

            // --- 5. Expandable Technical Specification Modules ---
            item {
                Text(
                    text = "TECHNICAL SPECIFICATIONS",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = Slate400,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    ),
                    modifier = Modifier.padding(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 8.dp)
                )
            }

            // ENGINE MODULE
            item {
                SpecExpandableSection(
                    title = "Engine & Powertrain",
                    isExpanded = expandEngine,
                    onToggle = { expandEngine = !expandEngine }
                ) {
                    val s = bike.specs
                    DetailSpecRow("Engine Type", s.engineType)
                    DetailSpecRow("Displacement", "${s.displacementCc} cc")
                    DetailSpecRow("Cylinders", "${s.cylinders}")
                    DetailSpecRow("Cooling System", s.cooling)
                    DetailSpecRow("Valves", "${s.valves} valves")
                    DetailSpecRow("Bore & Stroke", "${s.boreMm} mm x ${s.strokeMm} mm")
                    DetailSpecRow("Compression Ratio", s.compressionRatio)
                    DetailSpecRow("Fuel System", s.fuelSystem)
                    DetailSpecRow("Fuel Type", s.fuelType)
                    DetailSpecRow("Starting", s.startingSystem)
                }
            }

            // PERFORMANCE MODULE
            item {
                SpecExpandableSection(
                    title = "Performance & Dyno Metrics",
                    isExpanded = expandPerformance,
                    onToggle = { expandPerformance = !expandPerformance }
                ) {
                    val s = bike.specs
                    DetailSpecRow("Maximum Power", s.maxPowerDisplay)
                    DetailSpecRow("Maximum Torque", s.maxTorqueDisplay)
                    DetailSpecRow("Top Speed", "${s.topSpeedKmh} km/h")
                    DetailSpecRow("0 to 60 km/h", "${s.accel0To60Sec} seconds")
                    DetailSpecRow("0 to 100 km/h", "${s.accel0To100Sec} seconds")
                    DetailSpecRow("Fuel Efficiency / Mileage", "${s.mileageKmpl} km/l")
                    DetailSpecRow("Power-to-Weight Ratio", "${s.powerToWeightHpPerTon} HP/ton")
                }
            }

            // TRANSMISSION MODULE
            item {
                SpecExpandableSection(
                    title = "Transmission & Drivetrain",
                    isExpanded = expandTransmission,
                    onToggle = { expandTransmission = !expandTransmission }
                ) {
                    val s = bike.specs
                    DetailSpecRow("Gearbox", s.gearbox)
                    DetailSpecRow("Number of Gears", "${s.numberOfGears} Gears")
                    DetailSpecRow("Clutch System", s.clutchType)
                    DetailSpecRow("Final Drive", s.finalDrive)
                }
            }

            // CHASSIS & SUSPENSION MODULE
            item {
                SpecExpandableSection(
                    title = "Chassis & Suspension",
                    isExpanded = expandChassis,
                    onToggle = { expandChassis = !expandChassis }
                ) {
                    val s = bike.specs
                    DetailSpecRow("Frame Architecture", s.frameType)
                    DetailSpecRow("Front Suspension", s.frontSuspension)
                    DetailSpecRow("Rear Suspension", s.rearSuspension)
                    DetailSpecRow("Front Suspension Travel", "${s.frontTravelMm} mm")
                    DetailSpecRow("Rear Suspension Travel", "${s.rearTravelMm} mm")
                }
            }

            // BRAKES MODULE
            item {
                SpecExpandableSection(
                    title = "Brakes & Stopping Hardware",
                    isExpanded = expandBrakes,
                    onToggle = { expandBrakes = !expandBrakes }
                ) {
                    val s = bike.specs
                    DetailSpecRow("Front Brake", s.frontBrake)
                    DetailSpecRow("Rear Brake", s.rearBrake)
                    DetailSpecRow("ABS System", s.absSystem)
                    DetailSpecRow("Disc Dimensions", s.brakeDimensions)
                }
            }

            // WHEELS & TYRES MODULE
            item {
                SpecExpandableSection(
                    title = "Wheels & Tyres",
                    isExpanded = expandWheels,
                    onToggle = { expandWheels = !expandWheels }
                ) {
                    val s = bike.specs
                    DetailSpecRow("Front Tyre Size", s.frontTyre)
                    DetailSpecRow("Rear Tyre Size", s.rearTyre)
                    DetailSpecRow("Wheel Construction", s.wheelType)
                    DetailSpecRow("Rim Diameter", "${s.wheelSizeInches} inches")
                }
            }

            // DIMENSIONS & WEIGHT MODULE
            item {
                SpecExpandableSection(
                    title = "Dimensions & Capacities",
                    isExpanded = expandDimensions,
                    onToggle = { expandDimensions = !expandDimensions }
                ) {
                    val s = bike.specs
                    DetailSpecRow("Overall Dimensions (L x W x H)", "${s.lengthMm} x ${s.widthMm} x ${s.heightMm} mm")
                    DetailSpecRow("Wheelbase", "${s.wheelbaseMm} mm")
                    DetailSpecRow("Ground Clearance", "${s.groundClearanceMm} mm")
                    DetailSpecRow("Seat Height", "${s.seatHeightMm} mm")
                    DetailSpecRow("Kerb Weight", "${s.kerbWeightKg} kg")
                    DetailSpecRow("Fuel Tank Capacity", "${s.fuelTankCapacityL} Litres")
                }
            }

            // ELECTRONICS & FEATURES MODULE
            item {
                SpecExpandableSection(
                    title = "Electronics & Connectivity",
                    isExpanded = expandElectronics,
                    onToggle = { expandElectronics = !expandElectronics }
                ) {
                    val s = bike.specs
                    DetailSpecRow("Riding Modes", s.ridingModes)
                    DetailSpecRow("Traction Control", if (s.hasTractionControl) "Equipped (Multi-level)" else "Not available")
                    DetailSpecRow("Quick Shifter", s.quickShifter)
                    DetailSpecRow("Cruise Control", if (s.hasCruiseControl) "Equipped" else "Not available")
                    DetailSpecRow("Launch Control", if (s.hasLaunchControl) "Equipped" else "Not available")
                    DetailSpecRow("Ride-by-Wire", if (s.hasRideByWire) "Electronic Throttle" else "Cable Actuated")
                    DetailSpecRow("Cockpit Instrument", s.displayDetails)
                    DetailSpecRow("Bluetooth / Telemetry", if (s.hasBluetooth) "Connected" else "Not available")
                    DetailSpecRow("Turn-by-turn Navigation", if (s.hasNavigation) "Supported" else "Not available")
                    DetailSpecRow("Lighting Setup", s.ledLighting)
                    DetailSpecRow("Keyless Ignition", if (s.hasKeylessIgnition) "Smart Key" else "Conventional Key")
                }
            }

            // SAFETY SYSTEMS MODULE
            item {
                SpecExpandableSection(
                    title = "Safety & Rider Aids",
                    isExpanded = expandSafety,
                    onToggle = { expandSafety = !expandSafety }
                ) {
                    val s = bike.specs
                    DetailSpecRow("ABS Configuration", s.absSystem)
                    DetailSpecRow("Cornering / Lean-Sensitive ABS", if (s.hasCorneringAbs) "Equipped" else "Not available")
                    DetailSpecRow("Electronic Stability Control", if (s.hasStabilityControl) "Equipped" else "Not available")
                    DetailSpecRow("Slipper & Assist Clutch", if (s.hasSlipperClutch) "Equipped" else "Not available")
                    DetailSpecRow("Wheelie Control", if (s.hasWheelieControl) "Equipped" else "Not available")
                }
            }

            // --- 6. Verified Source & Attribution Card ---
            item {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = Slate900,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                        .border(1.dp, Slate800, RoundedCornerShape(14.dp))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Filled.VerifiedUser,
                                contentDescription = null,
                                tint = CyanNeon,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "DATA VERIFICATION & SOURCE ATTRIBUTION",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        DetailSpecRow("Official Source", bike.source)
                        DetailSpecRow("Last Homologation Sync", bike.lastUpdated)

                        if (bike.sourceUrl.isNotBlank()) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(
                                modifier = Modifier
                                    .clickable {
                                        try {
                                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(bike.sourceUrl))
                                            context.startActivity(intent)
                                        } catch (_: Exception) {}
                                    },
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "View Manufacturer Source Sheet",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = CyanNeon,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(
                                    imageVector = Icons.Filled.OpenInNew,
                                    contentDescription = null,
                                    tint = CyanNeon,
                                    modifier = Modifier.size(12.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // --- Rating & Review Dialog ---
        if (showRatingDialog) {
            AlertDialog(
                onDismissRequest = { showRatingDialog = false },
                containerColor = Slate900,
                title = {
                    Text(
                        text = "Rate ${bike.modelName}",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )
                },
                text = {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "How would you rate this motorcycle's ride quality and performance?",
                            style = MaterialTheme.typography.bodyMedium.copy(color = Slate400),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            for (star in 1..5) {
                                Icon(
                                    imageVector = if (star <= userRatingScore) Icons.Filled.Star else Icons.Filled.StarBorder,
                                    contentDescription = "Star $star",
                                    tint = AmberOrange,
                                    modifier = Modifier
                                        .size(42.dp)
                                        .clickable { userRatingScore = star.toFloat() }
                                        .padding(4.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "${userRatingScore.toInt()} / 5 Stars",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = AmberOrange
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            viewModel.submitUserRating(bike.id, userRatingScore)
                            showRatingDialog = false
                            ratingSubmitted = true
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = CyanNeon, contentColor = Slate950),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Submit Review", fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showRatingDialog = false }) {
                        Text("Cancel", color = Slate400)
                    }
                }
            )
        }
    }
}

@Composable
fun TelemetryItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 10.sp,
                color = Slate400,
                fontWeight = FontWeight.Bold
            )
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Black,
                color = Color.White
            )
        )
    }
}

@Composable
fun SpecExpandableSection(
    title: String,
    isExpanded: Boolean,
    onToggle: () -> Unit,
    content: @Composable () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = Slate900,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 5.dp)
            .border(1.dp, Slate800, RoundedCornerShape(12.dp))
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onToggle)
                    .padding(14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = Color.White
                )
                Icon(
                    imageVector = if (isExpanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                    contentDescription = null,
                    tint = CyanNeon
                )
            }

            AnimatedVisibility(visible = isExpanded) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 14.dp, end = 14.dp, bottom = 14.dp)
                ) {
                    content()
                }
            }
        }
    }
}
