package com.example.ui.screens

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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.CompareArrows
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.TwoWheeler
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.data.local.PreloadedData
import com.example.ui.components.MotorcycleCard
import com.example.ui.navigation.MotoScreen
import com.example.ui.theme.AmberOrange
import com.example.ui.theme.CyanNeon
import com.example.ui.theme.Slate400
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate800
import com.example.ui.theme.Slate850
import com.example.ui.theme.Slate900
import com.example.ui.theme.Slate950
import com.example.ui.viewmodel.MotoViewModel

@Composable
fun HomeScreen(
    viewModel: MotoViewModel,
    modifier: Modifier = Modifier
) {
    val allMotorcycles by viewModel.allMotorcycles.collectAsStateWithLifecycle()
    val allBrands by viewModel.allBrands.collectAsStateWithLifecycle()
    val trendingBikes by viewModel.trendingBikes.collectAsStateWithLifecycle()
    val newBikes by viewModel.newBikes.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val comparisonSlots by viewModel.comparisonSlotIds.collectAsStateWithLifecycle()
    val favorites by viewModel.favorites.collectAsStateWithLifecycle()
    val isSyncing by viewModel.isSyncing.collectAsStateWithLifecycle()
    val syncMessage by viewModel.syncMessage.collectAsStateWithLifecycle()
    val favIds = favorites.map { it.motorcycleId }.toSet()
    val compareIds = comparisonSlots.toSet()

    var showAiAdvisor by remember { mutableStateOf(false) }

    if (showAiAdvisor) {
        com.example.ui.components.AiAdvisorDialog(
            viewModel = viewModel,
            onDismiss = { showAiAdvisor = false }
        )
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Slate950)
            .testTag("home_screen"),
        contentPadding = PaddingValues(bottom = 100.dp)
    ) {
        // --- 1. HERO SECTION ---
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(380.dp)
                    .background(Slate900)
            ) {
                // Background Hero Motorcycle Banner
                AsyncImage(
                    model = "https://images.unsplash.com/photo-1558981403-c5f9899a28bc?auto=format&fit=crop&w=1200&q=80",
                    contentDescription = "Hero motorcycle",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // High-End Radial & Linear Gradient scrim
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Slate950.copy(alpha = 0.5f),
                                    Slate950.copy(alpha = 0.85f),
                                    Slate950
                                )
                            )
                        )
                )

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 20.dp, vertical = 24.dp),
                    verticalArrangement = Arrangement.Bottom
                ) {
                    var adminTapCount by remember { androidx.compose.runtime.mutableIntStateOf(0) }
                    // Badge
                    Box(
                        modifier = Modifier
                            .background(CyanNeon.copy(alpha = 0.15f), RoundedCornerShape(20.dp))
                            .border(1.dp, CyanNeon.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
                            .clickable {
                                adminTapCount++
                                if (adminTapCount >= 3) {
                                    adminTapCount = 0
                                    viewModel.navigateTo(MotoScreen.Admin)
                                }
                            }
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "GLOBAL MOTORCYCLE ENCYCLOPEDIA",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = CyanNeon,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Explore Every Ride.\nCompare Every Detail.",
                        style = MaterialTheme.typography.displayLarge.copy(
                            fontWeight = FontWeight.Black,
                            fontSize = 30.sp,
                            lineHeight = 36.sp
                        ),
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Discover international motorcycle manufacturers, explore certified engineering data, and compare up to 3 bikes with intelligent verdicts.",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = Slate400,
                            lineHeight = 20.sp
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Dual CTA Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = { viewModel.navigateTo(MotoScreen.Catalog) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = CyanNeon,
                                contentColor = Slate950
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("hero_explore_cta")
                        ) {
                            Icon(imageVector = Icons.Filled.TwoWheeler, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = "Explore Bikes", fontWeight = FontWeight.Bold)
                        }

                        OutlinedButton(
                            onClick = { viewModel.navigateTo(MotoScreen.Comparison) },
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = Color.White
                            ),
                            border = androidx.compose.foundation.BorderStroke(1.2.dp, Slate700),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("hero_compare_cta")
                        ) {
                            Icon(imageVector = Icons.Filled.CompareArrows, contentDescription = null, tint = CyanNeon, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = "Compare Now", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // --- 2. SEARCH BAR ---
        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { viewModel.updateSearchQuery(it) },
                    placeholder = { Text("Search motorcycle, brand or model...", color = Slate400) },
                    leadingIcon = {
                        Icon(imageVector = Icons.Filled.Search, contentDescription = "Search", tint = CyanNeon)
                    },
                    trailingIcon = {
                        IconButton(onClick = { viewModel.navigateTo(MotoScreen.Search) }) {
                            Icon(imageVector = Icons.Filled.Search, contentDescription = "Open Search Screen", tint = Slate400)
                        }
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(onSearch = {
                        viewModel.navigateTo(MotoScreen.Search)
                    }),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Slate900,
                        unfocusedContainerColor = Slate900,
                        focusedBorderColor = CyanNeon,
                        unfocusedBorderColor = Slate800,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("home_search_field")
                )

                // Quick natural prompt suggestions
                Spacer(modifier = Modifier.height(8.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    val promptSuggestions = listOf(
                        "Sport bikes under ₹5 lakh",
                        "Best beginner bikes",
                        "Yamaha vs KTM",
                        "400cc Adventure",
                        "Super Sport 600cc+"
                    )
                    items(promptSuggestions) { prompt ->
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = Slate850,
                            modifier = Modifier
                                .border(0.8.dp, Slate800, RoundedCornerShape(20.dp))
                                .clickable {
                                    viewModel.updateSearchQuery(prompt)
                                    viewModel.navigateTo(MotoScreen.Catalog)
                                }
                        ) {
                            Text(
                                text = prompt,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = Slate400,
                                    fontSize = 11.sp
                                ),
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                            )
                        }
                    }
                }
            }
        }

        // --- 2.1 GEMINI AI ADVISOR BANNER ---
        item {
            Surface(
                color = Slate900,
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.2.dp, CyanNeon.copy(alpha = 0.5f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
                    .clickable { showAiAdvisor = true }
                    .testTag("home_ai_advisor_banner")
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .background(CyanNeon.copy(alpha = 0.15f), CircleShape)
                                .border(1.dp, CyanNeon, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.AutoAwesome,
                                contentDescription = null,
                                tint = CyanNeon,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Ask Moto AI Advisor",
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
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Personalized bike guidance on budget, seat height & riding goals",
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                color = Slate400
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = { showAiAdvisor = true },
                        colors = ButtonDefaults.buttonColors(containerColor = CyanNeon, contentColor = Slate950),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text("Chat", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // --- 2.5 CATALOG SYNC BANNER ---
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp)
            ) {
                Surface(
                    color = Slate900,
                    shape = RoundedCornerShape(14.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Slate800),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(CircleShape)
                                    .background(CyanNeon.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.TwoWheeler,
                                    contentDescription = null,
                                    tint = CyanNeon,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "${allBrands.size} Global Brands • ${allMotorcycles.size} Models",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = Color.White
                                )
                                Text(
                                    text = if (isSyncing) "Syncing latest data..." else "Instant Offline Database Ready",
                                    fontSize = 11.sp,
                                    color = if (isSyncing) AmberOrange else Slate400
                                )
                            }
                        }

                        OutlinedButton(
                            onClick = { viewModel.syncCatalog() },
                            enabled = !isSyncing,
                            shape = RoundedCornerShape(8.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, if (isSyncing) Slate700 else CyanNeon),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = CyanNeon),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            modifier = Modifier.height(34.dp)
                        ) {
                            if (isSyncing) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(14.dp),
                                    strokeWidth = 2.dp,
                                    color = CyanNeon
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Filled.Refresh,
                                    contentDescription = "Sync",
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Sync", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                if (syncMessage != null) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Surface(
                        color = Color(0xFF10B981).copy(alpha = 0.15f),
                        shape = RoundedCornerShape(8.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF10B981)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { viewModel.dismissSyncMessage() }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "✓ ${syncMessage}",
                                color = Color.White,
                                fontSize = 12.sp,
                                modifier = Modifier.weight(1f)
                            )
                            Text(
                                text = "✕",
                                color = Slate400,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // --- 3. POPULAR BRANDS ---
        item {
            Column(modifier = Modifier.padding(top = 16.dp)) {
                SectionHeader(
                    title = "Popular Brands",
                    subtitle = "Explore factory lineups from around the world",
                    onSeeAll = { viewModel.navigateTo(MotoScreen.Brands) }
                )

                Spacer(modifier = Modifier.height(10.dp))

                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(allBrands) { brand ->
                        val count = allMotorcycles.count { it.brandId.equals(brand.id, ignoreCase = true) }
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .width(90.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(Slate850)
                                .border(1.dp, Slate800, RoundedCornerShape(14.dp))
                                .clickable {
                                    viewModel.navigateTo(MotoScreen.BrandDetail(brand.id))
                                }
                                .padding(vertical = 12.dp, horizontal = 6.dp)
                                .testTag("home_brand_${brand.id}")
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(CircleShape)
                                    .background(Slate950)
                                    .border(1.dp, Color(brand.accentColorHex), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = brand.name.take(2).uppercase(),
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Black,
                                        color = Color(brand.accentColorHex)
                                    )
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = brand.name,
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = Color.White,
                                maxLines = 1
                            )
                            Text(
                                text = "$count bikes",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, color = Slate400)
                            )
                        }
                    }
                }
            }
        }

        // --- 4. POPULAR COMPARISONS ---
        item {
            Column(modifier = Modifier.padding(top = 24.dp)) {
                SectionHeader(
                    title = "Popular Comparisons",
                    subtitle = "Side-by-side rivalries evaluated by our engine",
                    onSeeAll = { viewModel.navigateTo(MotoScreen.Comparison) }
                )

                Spacer(modifier = Modifier.height(10.dp))

                val rivalrySets = listOf(
                    Triple(
                        "Track Rivals: 150-300cc",
                        listOf("yamaha-r15-v4", "ktm-rc-200", "kawasaki-ninja-300"),
                        "Yamaha R15 V4 vs KTM RC 200 vs Ninja 300"
                    ),
                    Triple(
                        "Middleweight Hyper-Nakeds",
                        listOf("ktm-390-duke", "triumph-street-triple-765-rs"),
                        "KTM 390 Duke vs Street Triple 765 RS"
                    ),
                    Triple(
                        "Liter-Class Superbike Pinnacle",
                        listOf("ducati-panigale-v2", "bmw-s1000rr"),
                        "Ducati Panigale V2 vs BMW S 1000 RR"
                    ),
                    Triple(
                        "Twin-Cylinder A2 Sportbikes",
                        listOf("aprilia-rs-457", "kawasaki-ninja-300"),
                        "Aprilia RS 457 vs Kawasaki Ninja 300"
                    )
                )

                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(rivalrySets) { (label, ids, subtitle) ->
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = Slate850,
                            modifier = Modifier
                                .width(280.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .border(1.dp, Slate800, RoundedCornerShape(16.dp))
                                .clickable {
                                    viewModel.setComparisonBikes(ids)
                                    viewModel.navigateTo(MotoScreen.Comparison)
                                }
                                .padding(14.dp)
                                .testTag("popular_compare_item")
                        ) {
                            Column {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = label,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = CyanNeon,
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                    Text(
                                        text = "${ids.size} Bikes",
                                        style = MaterialTheme.typography.labelSmall.copy(color = Slate400)
                                    )
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = subtitle,
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.End,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = "Compare Now",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = AmberOrange,
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                        contentDescription = null,
                                        tint = AmberOrange,
                                        modifier = Modifier.size(12.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // --- 5. MOTORCYCLE CATEGORIES ---
        item {
            Column(modifier = Modifier.padding(top = 24.dp)) {
                SectionHeader(
                    title = "Motorcycle Categories",
                    subtitle = "Filter by riding discipline and frame architecture",
                    onSeeAll = { viewModel.navigateTo(MotoScreen.Catalog) }
                )

                Spacer(modifier = Modifier.height(10.dp))

                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(PreloadedData.categories) { cat ->
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Slate850,
                            modifier = Modifier
                                .border(1.dp, Slate800, RoundedCornerShape(12.dp))
                                .clickable {
                                    viewModel.selectCategoryFilter(cat)
                                    viewModel.navigateTo(MotoScreen.Catalog)
                                }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = cat,
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }

        // --- 6. TRENDING MOTORCYCLES ---
        item {
            Column(modifier = Modifier.padding(top = 24.dp)) {
                SectionHeader(
                    title = "Trending Motorcycles",
                    subtitle = "Hottest machines turning heads right now",
                    onSeeAll = { viewModel.navigateTo(MotoScreen.Catalog) }
                )

                Spacer(modifier = Modifier.height(12.dp))
            }
        }

        items(trendingBikes) { bike ->
            Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
                MotorcycleCard(
                    motorcycle = bike,
                    isInCompare = compareIds.contains(bike.id),
                    isFavorite = favIds.contains(bike.id),
                    onCardClick = {
                        viewModel.onMotorcycleViewed(bike.id)
                        viewModel.navigateTo(MotoScreen.MotorcycleDetail(bike.id))
                    },
                    onCompareToggle = {
                        if (compareIds.contains(bike.id)) {
                            viewModel.removeFromCompare(bike.id)
                        } else {
                            viewModel.addToCompare(bike.id)
                        }
                    },
                    onFavoriteToggle = {
                        viewModel.toggleFavorite(bike.id)
                    }
                )
            }
        }

        // --- 7. NEW & LATEST MOTORCYCLES ---
        item {
            Column(modifier = Modifier.padding(top = 20.dp)) {
                SectionHeader(
                    title = "New & Latest Launches",
                    subtitle = "Fresh homologations & 2024 model year releases",
                    onSeeAll = { viewModel.navigateTo(MotoScreen.Catalog) }
                )

                Spacer(modifier = Modifier.height(12.dp))
            }
        }

        items(newBikes.filter { !trendingBikes.contains(it) }.take(4)) { bike ->
            Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
                MotorcycleCard(
                    motorcycle = bike,
                    isInCompare = compareIds.contains(bike.id),
                    isFavorite = favIds.contains(bike.id),
                    onCardClick = {
                        viewModel.onMotorcycleViewed(bike.id)
                        viewModel.navigateTo(MotoScreen.MotorcycleDetail(bike.id))
                    },
                    onCompareToggle = {
                        if (compareIds.contains(bike.id)) {
                            viewModel.removeFromCompare(bike.id)
                        } else {
                            viewModel.addToCompare(bike.id)
                        }
                    },
                    onFavoriteToggle = {
                        viewModel.toggleFavorite(bike.id)
                    }
                )
            }
        }
    }
}

@Composable
fun SectionHeader(
    title: String,
    subtitle: String,
    onSeeAll: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = Color.White
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall.copy(color = Slate400)
            )
        }

        Text(
            text = "View All",
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                color = CyanNeon
            ),
            modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .clickable(onClick = onSeeAll)
                .padding(4.dp)
        )
    }
}
