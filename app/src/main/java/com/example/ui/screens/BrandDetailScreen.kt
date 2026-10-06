package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.BackHandler
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
import androidx.compose.material.icons.filled.Language
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.ui.components.MotorcycleCard
import com.example.ui.navigation.MotoScreen
import com.example.ui.navigation.SortOption
import com.example.ui.theme.CyanNeon
import com.example.ui.theme.Slate400
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate800
import com.example.ui.theme.Slate850
import com.example.ui.theme.Slate900
import com.example.ui.theme.Slate950
import com.example.ui.viewmodel.MotoViewModel

@Composable
fun BrandDetailScreen(
    brandId: String,
    viewModel: MotoViewModel,
    modifier: Modifier = Modifier
) {
    BackHandler {
        viewModel.navigateBack()
    }

    val context = LocalContext.current
    val allBrands by viewModel.allBrands.collectAsStateWithLifecycle()
    val allMotorcycles by viewModel.allMotorcycles.collectAsStateWithLifecycle()
    val comparisonSlots by viewModel.comparisonSlotIds.collectAsStateWithLifecycle()
    val favorites by viewModel.favorites.collectAsStateWithLifecycle()
    val favIds = favorites.map { it.motorcycleId }.toSet()
    val compareIds = comparisonSlots.toSet()

    val brand = allBrands.find { it.id.equals(brandId, ignoreCase = true) }
    var selectedCategory by remember { mutableStateOf<String?>(null) }
    var selectedSort by remember { mutableStateOf(SortOption.POPULARITY) }
    var showSortMenu by remember { mutableStateOf(false) }

    val brandBikes = remember(allMotorcycles, brandId, selectedCategory, selectedSort) {
        var list = allMotorcycles.filter { it.brandId.equals(brandId, ignoreCase = true) }
        if (selectedCategory != null) {
            list = list.filter { it.category.equals(selectedCategory, ignoreCase = true) }
        }
        when (selectedSort) {
            SortOption.POPULARITY -> list.sortedByDescending { it.reviewCount }
            SortOption.PRICE_LOW_TO_HIGH -> list.sortedBy { it.basePrice }
            SortOption.PRICE_HIGH_TO_LOW -> list.sortedByDescending { it.basePrice }
            SortOption.POWER_HIGH_TO_LOW -> list.sortedByDescending { it.specs.maxPowerHp }
            SortOption.DISPLACEMENT_HIGH -> list.sortedByDescending { it.specs.displacementCc }
            SortOption.RATING -> list.sortedByDescending { it.rating }
            SortOption.NEWEST -> list.sortedByDescending { it.modelYear }
        }
    }

    val availableCategories = remember(allMotorcycles, brandId) {
        allMotorcycles.filter { it.brandId.equals(brandId, ignoreCase = true) }
            .map { it.category }.distinct()
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Slate950)
            .testTag("brand_detail_screen")
    ) {
        // --- Top Navigation App Bar ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Slate900)
                .padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { viewModel.navigateBack() },
                modifier = Modifier.testTag("brand_back_btn")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White
                )
            }
            Text(
                text = brand?.name ?: "Brand Details",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = Color.White
            )
        }

        if (brand == null) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("Brand not found", color = Slate400)
            }
            return
        }

        LazyColumn(
            contentPadding = PaddingValues(bottom = 100.dp)
        ) {
            // --- Brand Hero Banner ---
            item {
                Surface(
                    color = Slate900,
                    modifier = Modifier.fillMaxWidth().border(0.5.dp, Slate800)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(60.dp)
                                        .clip(CircleShape)
                                        .background(Slate950)
                                        .border(2.dp, Color(brand.accentColorHex), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    AsyncImage(
                                        model = brand.logoUrl,
                                        contentDescription = brand.name,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.size(60.dp)
                                    )
                                    Text(
                                        text = brand.name.take(2).uppercase(),
                                        style = MaterialTheme.typography.headlineSmall.copy(
                                            fontWeight = FontWeight.Black,
                                            color = Color(brand.accentColorHex)
                                        )
                                    )
                                }

                                Spacer(modifier = Modifier.width(14.dp))

                                Column {
                                    Text(
                                        text = brand.name,
                                        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                                        color = Color.White
                                    )
                                    Text(
                                        text = "${brand.country} • Founded in ${brand.foundedYear}",
                                        style = MaterialTheme.typography.bodyMedium.copy(color = Slate400)
                                    )
                                }
                            }

                            // Website Button
                            if (brand.websiteUrl.isNotBlank()) {
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = Slate850,
                                    modifier = Modifier
                                        .border(1.dp, Slate700, RoundedCornerShape(10.dp))
                                        .clickable {
                                            try {
                                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(brand.websiteUrl))
                                                context.startActivity(intent)
                                            } catch (_: Exception) {}
                                        }
                                        .padding(horizontal = 10.dp, vertical = 6.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Filled.Language,
                                            contentDescription = "Website",
                                            tint = CyanNeon,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "Official Site",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                color = Color.White,
                                                fontWeight = FontWeight.Bold
                                            )
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // History
                        Text(
                            text = brand.history,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = Slate400,
                                lineHeight = 20.sp
                            )
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // Stats bar
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Slate950, RoundedCornerShape(12.dp))
                                .border(1.dp, Slate800, RoundedCornerShape(12.dp))
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "${brandBikes.size}",
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = CyanNeon
                                    )
                                )
                                Text(
                                    text = "Models Available",
                                    style = MaterialTheme.typography.labelSmall.copy(color = Slate400)
                                )
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "${availableCategories.size}",
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                )
                                Text(
                                    text = "Categories",
                                    style = MaterialTheme.typography.labelSmall.copy(color = Slate400)
                                )
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = brand.country,
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                )
                                Text(
                                    text = "Origin",
                                    style = MaterialTheme.typography.labelSmall.copy(color = Slate400)
                                )
                            }
                        }
                    }
                }
            }

            // --- Filter & Sorting Row ---
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Models Lineup (${brandBikes.size})",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )

                    Box {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Slate850,
                            modifier = Modifier
                                .border(1.dp, Slate800, RoundedCornerShape(8.dp))
                                .clickable { showSortMenu = true }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = selectedSort.displayName,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = CyanNeon,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }

                        DropdownMenu(
                            expanded = showSortMenu,
                            onDismissRequest = { showSortMenu = false },
                            modifier = Modifier.background(Slate900)
                        ) {
                            SortOption.entries.forEach { opt ->
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            text = opt.displayName,
                                            color = if (opt == selectedSort) CyanNeon else Color.White
                                        )
                                    },
                                    onClick = {
                                        selectedSort = opt
                                        showSortMenu = false
                                    }
                                )
                            }
                        }
                    }
                }
            }

            // --- Category Filters for this brand ---
            if (availableCategories.size > 1) {
                item {
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(bottom = 12.dp)
                    ) {
                        item {
                            FilterChipItem(
                                label = "All (${brandBikes.size})",
                                isSelected = selectedCategory == null,
                                onClick = { selectedCategory = null }
                            )
                        }
                        items(availableCategories) { cat ->
                            FilterChipItem(
                                label = cat,
                                isSelected = selectedCategory == cat,
                                onClick = { selectedCategory = if (selectedCategory == cat) null else cat }
                            )
                        }
                    }
                }
            }

            // --- Motorcycles Grid/List ---
            items(brandBikes, key = { it.id }) { bike ->
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
}
