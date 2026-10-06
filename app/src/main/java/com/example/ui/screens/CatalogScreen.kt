package com.example.ui.screens

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.TwoWheeler
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.local.PreloadedData
import com.example.ui.components.MotorcycleCard
import com.example.ui.navigation.MotoScreen
import com.example.ui.navigation.SortOption
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
fun CatalogScreen(
    viewModel: MotoViewModel,
    modifier: Modifier = Modifier
) {
    val bikes by viewModel.filteredMotorcycles.collectAsStateWithLifecycle()
    val allBrands by viewModel.allBrands.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val selectedBrandId by viewModel.selectedBrandId.collectAsStateWithLifecycle()
    val selectedCategory by viewModel.selectedCategory.collectAsStateWithLifecycle()
    val selectedSort by viewModel.selectedSort.collectAsStateWithLifecycle()
    val comparisonSlots by viewModel.comparisonSlotIds.collectAsStateWithLifecycle()
    val favorites by viewModel.favorites.collectAsStateWithLifecycle()
    val favIds = favorites.map { it.motorcycleId }.toSet()
    val compareIds = comparisonSlots.toSet()

    var showSortMenu by remember { mutableStateOf(false) }
    var showFilterPanel by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Slate950)
            .testTag("catalog_screen")
    ) {
        // --- Top Bar & Search ---
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Slate900)
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Motorcycle Catalog",
                        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )
                    Text(
                        text = "${bikes.size} machines matching criteria",
                        style = MaterialTheme.typography.bodySmall.copy(color = Slate400)
                    )
                }

                // Filter & Sort buttons
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = Slate850,
                            modifier = Modifier
                                .border(1.dp, Slate800, RoundedCornerShape(10.dp))
                                .clickable { showSortMenu = true }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                                .testTag("sort_dropdown_btn")
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
                            SortOption.entries.forEach { option ->
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            text = option.displayName,
                                            color = if (option == selectedSort) CyanNeon else Color.White,
                                            fontWeight = if (option == selectedSort) FontWeight.Bold else FontWeight.Normal
                                        )
                                    },
                                    onClick = {
                                        viewModel.selectSortOption(option)
                                        showSortMenu = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    IconButton(
                        onClick = { showFilterPanel = !showFilterPanel },
                        modifier = Modifier
                            .size(36.dp)
                            .background(if (showFilterPanel) CyanNeon else Slate850, RoundedCornerShape(10.dp))
                            .border(1.dp, Slate800, RoundedCornerShape(10.dp))
                            .testTag("filter_toggle_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Filled.FilterList,
                            contentDescription = "Filters",
                            tint = if (showFilterPanel) Slate950 else Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Search input field
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.updateSearchQuery(it) },
                placeholder = { Text("Search by model, 400cc, under ₹5L...", color = Slate400, fontSize = 13.sp) },
                leadingIcon = {
                    Icon(imageVector = Icons.Filled.Search, contentDescription = null, tint = CyanNeon, modifier = Modifier.size(18.dp))
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.updateSearchQuery("") }) {
                            Icon(imageVector = Icons.Filled.Close, contentDescription = "Clear", tint = Slate400, modifier = Modifier.size(16.dp))
                        }
                    }
                },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Slate950,
                    unfocusedContainerColor = Slate950,
                    focusedBorderColor = CyanNeon,
                    unfocusedBorderColor = Slate800,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("catalog_search_input")
            )
        }

        // --- Expandable Filter Panel ---
        AnimatedVisibility(visible = showFilterPanel) {
            Surface(
                color = Slate900,
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Slate800)
                    .padding(14.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "ACTIVE FILTERS",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = Slate400,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                        )
                        TextButton(onClick = { viewModel.resetFilters() }) {
                            Text("Reset All", color = AmberOrange, style = MaterialTheme.typography.labelSmall)
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // Brand Filter Chips
                    Text("Brand:", style = MaterialTheme.typography.labelSmall.copy(color = Slate400))
                    Spacer(modifier = Modifier.height(4.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        item {
                            FilterChipItem(
                                label = "All Brands",
                                isSelected = selectedBrandId == null,
                                onClick = { viewModel.selectBrandFilter(null) }
                            )
                        }
                        items(allBrands) { brand ->
                            FilterChipItem(
                                label = brand.name,
                                isSelected = selectedBrandId.equals(brand.id, ignoreCase = true),
                                onClick = {
                                    viewModel.selectBrandFilter(
                                        if (selectedBrandId.equals(brand.id, ignoreCase = true)) null else brand.id
                                    )
                                }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Category Filter Chips
                    Text("Category:", style = MaterialTheme.typography.labelSmall.copy(color = Slate400))
                    Spacer(modifier = Modifier.height(4.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        item {
                            FilterChipItem(
                                label = "All Categories",
                                isSelected = selectedCategory == null,
                                onClick = { viewModel.selectCategoryFilter(null) }
                            )
                        }
                        items(PreloadedData.categories) { cat ->
                            FilterChipItem(
                                label = cat,
                                isSelected = selectedCategory.equals(cat, ignoreCase = true),
                                onClick = {
                                    viewModel.selectCategoryFilter(
                                        if (selectedCategory.equals(cat, ignoreCase = true)) null else cat
                                    )
                                }
                            )
                        }
                    }
                }
            }
        }

        // --- Category Quick Horizontal Chips (Always visible) ---
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = if (selectedCategory == null) CyanNeon else Slate850,
                    modifier = Modifier
                        .clickable { viewModel.selectCategoryFilter(null) }
                        .border(1.dp, if (selectedCategory == null) CyanNeon else Slate800, RoundedCornerShape(10.dp))
                ) {
                    Text(
                        text = "All Categories",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = if (selectedCategory == null) Slate950 else Color.White
                        ),
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }
            items(PreloadedData.categories) { cat ->
                val isSelected = selectedCategory.equals(cat, ignoreCase = true)
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = if (isSelected) CyanNeon else Slate850,
                    modifier = Modifier
                        .clickable {
                            viewModel.selectCategoryFilter(if (isSelected) null else cat)
                        }
                        .border(1.dp, if (isSelected) CyanNeon else Slate800, RoundedCornerShape(10.dp))
                ) {
                    Text(
                        text = cat,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) Slate950 else Color.White
                        ),
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }
        }

        // --- Motorcycle List / Empty State ---
        if (bikes.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Filled.TwoWheeler,
                        contentDescription = null,
                        tint = Slate700,
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "No Motorcycles Found",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Try adjusting your search criteria or resetting filters.",
                        style = MaterialTheme.typography.bodyMedium.copy(color = Slate400),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = CyanNeon,
                        modifier = Modifier.clickable { viewModel.resetFilters() }
                    ) {
                        Text(
                            text = "Reset All Filters",
                            color = Slate950,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
                        )
                    }
                }
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 100.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                items(bikes, key = { it.id }) { bike ->
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

@Composable
fun FilterChipItem(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = if (isSelected) CyanNeon else Slate850,
        modifier = Modifier
            .clickable(onClick = onClick)
            .border(0.8.dp, if (isSelected) CyanNeon else Slate700, RoundedCornerShape(8.dp))
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                color = if (isSelected) Slate950 else Color.White,
                fontSize = 11.sp
            ),
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
        )
    }
}
