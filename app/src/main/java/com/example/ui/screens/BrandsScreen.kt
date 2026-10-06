package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.BrandCard
import com.example.ui.navigation.MotoScreen
import com.example.ui.theme.CyanNeon
import com.example.ui.theme.Slate400
import com.example.ui.theme.Slate800
import com.example.ui.theme.Slate900
import com.example.ui.theme.Slate950
import com.example.ui.viewmodel.MotoViewModel

@Composable
fun BrandsScreen(
    viewModel: MotoViewModel,
    modifier: Modifier = Modifier
) {
    val allBrands by viewModel.allBrands.collectAsStateWithLifecycle()
    val allMotorcycles by viewModel.allMotorcycles.collectAsStateWithLifecycle()

    var brandSearch by remember { mutableStateOf("") }
    var selectedCountry by remember { mutableStateOf<String?>(null) }

    val countries = remember(allBrands) {
        listOf("All") + allBrands.map { it.country }.distinct().sorted()
    }

    val filteredBrands = remember(allBrands, brandSearch, selectedCountry) {
        allBrands.filter { brand ->
            val matchesText = brand.name.contains(brandSearch, ignoreCase = true) ||
                    brand.country.contains(brandSearch, ignoreCase = true)
            val matchesCountry = selectedCountry == null || selectedCountry == "All" ||
                    brand.country.equals(selectedCountry, ignoreCase = true)
            matchesText && matchesCountry
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Slate950)
            .testTag("brands_screen")
    ) {
        // --- Header ---
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Slate900)
                .padding(16.dp)
        ) {
            Text(
                text = "Global Manufacturers",
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                color = Color.White
            )
            Text(
                text = "Discover international marques, factory heritage & technical philosophies",
                style = MaterialTheme.typography.bodySmall.copy(color = Slate400)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Search Bar
            OutlinedTextField(
                value = brandSearch,
                onValueChange = { brandSearch = it },
                placeholder = { Text("Search brands (e.g. Yamaha, KTM, Ducati)...", color = Slate400, fontSize = 13.sp) },
                leadingIcon = {
                    Icon(imageVector = Icons.Filled.Search, contentDescription = null, tint = CyanNeon)
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
                modifier = Modifier.fillMaxWidth().testTag("brands_search_input")
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Country Filter Chips
            LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                items(countries) { country ->
                    val isSelected = (selectedCountry == null && country == "All") || selectedCountry == country
                    FilterChipItem(
                        label = country,
                        isSelected = isSelected,
                        onClick = {
                            selectedCountry = if (country == "All") null else country
                        }
                    )
                }
            }
        }

        // --- Brand Cards List ---
        LazyColumn(
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 14.dp, bottom = 100.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            items(filteredBrands, key = { it.id }) { brand ->
                val bikeCount = allMotorcycles.count { it.brandId.equals(brand.id, ignoreCase = true) }
                BrandCard(
                    brand = brand,
                    modelCount = bikeCount,
                    onClick = {
                        viewModel.navigateTo(MotoScreen.BrandDetail(brand.id))
                    }
                )
            }
        }
    }
}
