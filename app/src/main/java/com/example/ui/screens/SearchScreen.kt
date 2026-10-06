package com.example.ui.screens

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.TwoWheeler
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.MotorcycleCard
import com.example.ui.navigation.MotoScreen
import com.example.ui.theme.CyanNeon
import com.example.ui.theme.Slate400
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate800
import com.example.ui.theme.Slate850
import com.example.ui.theme.Slate900
import com.example.ui.theme.Slate950
import com.example.ui.viewmodel.MotoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    viewModel: MotoViewModel,
    modifier: Modifier = Modifier
) {
    BackHandler {
        viewModel.navigateBack()
    }

    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val searchResults by viewModel.searchResults.collectAsStateWithLifecycle()
    val comparisonSlots by viewModel.comparisonSlotIds.collectAsStateWithLifecycle()
    val favorites by viewModel.favorites.collectAsStateWithLifecycle()
    val favIds = favorites.map { it.motorcycleId }.toSet()
    val compareIds = comparisonSlots.toSet()

    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current
    var showAiAdvisor by remember { mutableStateOf(false) }

    if (showAiAdvisor) {
        com.example.ui.components.AiAdvisorDialog(
            viewModel = viewModel,
            onDismiss = { showAiAdvisor = false }
        )
    }

    LaunchedEffect(Unit) {
        if (searchQuery.isBlank()) {
            focusRequester.requestFocus()
        }
    }

    val quickBrandChips = listOf(
        "Yamaha", "Kawasaki", "Honda", "Ducati", "BMW",
        "KTM", "Royal Enfield", "Suzuki", "Triumph", "Bajaj", "Harley"
    )

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .testTag("search_screen"),
        topBar = {
            TopAppBar(
                title = {
                    TextField(
                        value = searchQuery,
                        onValueChange = { viewModel.updateSearchQuery(it) },
                        placeholder = {
                            Text(
                                text = "Search model or brand...",
                                color = Slate400,
                                fontSize = 14.sp
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Filled.Search,
                                contentDescription = "Search Icon",
                                tint = CyanNeon,
                                modifier = Modifier.size(20.dp)
                            )
                        },
                        trailingIcon = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                if (searchQuery.isNotEmpty()) {
                                    IconButton(
                                        onClick = { viewModel.updateSearchQuery("") },
                                        modifier = Modifier.testTag("search_clear_button").size(28.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Filled.Clear,
                                            contentDescription = "Clear search",
                                            tint = Slate400,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                                IconButton(
                                    onClick = {
                                        if (searchQuery.isNotBlank()) {
                                            viewModel.askMotoAdvisor(searchQuery)
                                        }
                                        showAiAdvisor = true
                                    },
                                    modifier = Modifier.testTag("search_ai_btn").size(28.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.AutoAwesome,
                                        contentDescription = "Ask AI",
                                        tint = CyanNeon,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                        keyboardActions = KeyboardActions(
                            onSearch = { keyboardController?.hide() }
                        ),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Slate850,
                            unfocusedContainerColor = Slate850,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            disabledIndicatorColor = Color.Transparent,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .focusRequester(focusRequester)
                            .testTag("search_query_input")
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = { viewModel.navigateBack() },
                        modifier = Modifier.testTag("search_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Slate950
                )
            )
        },
        containerColor = Slate950
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Quick Brand Filter Chips
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Slate950)
            ) {
                items(quickBrandChips) { brandName ->
                    val isSelected = searchQuery.equals(brandName, ignoreCase = true)
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = if (isSelected) CyanNeon else Slate900,
                        modifier = Modifier
                            .border(
                                width = 1.dp,
                                color = if (isSelected) CyanNeon else Slate800,
                                shape = RoundedCornerShape(16.dp)
                            )
                            .clickable {
                                if (isSelected) {
                                    viewModel.updateSearchQuery("")
                                } else {
                                    viewModel.updateSearchQuery(brandName)
                                }
                            }
                    ) {
                        Text(
                            text = brandName,
                            color = if (isSelected) Slate950 else Color.White,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }
            }

            // Results summary bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (searchQuery.isBlank()) {
                        "All Bikes in Room Database"
                    } else {
                        "Results for \"$searchQuery\""
                    },
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Slate400
                )

                Surface(
                    color = Slate850,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.border(1.dp, Slate800, RoundedCornerShape(8.dp))
                ) {
                    Text(
                        text = "${searchResults.size} bikes",
                        color = CyanNeon,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            // Flow-based search results list
            if (searchResults.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.TwoWheeler,
                            contentDescription = "No results found",
                            tint = Slate700,
                            modifier = Modifier.size(64.dp)
                        )
                        Text(
                            text = "No motorcycles found",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "Try searching for a model name (e.g. \"Ninja\", \"R15\", \"Duke\", \"Classic\") or brand (e.g. \"Yamaha\", \"KTM\", \"Honda\").",
                            fontSize = 13.sp,
                            color = Slate400,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Button(
                            onClick = {
                                if (searchQuery.isNotBlank()) {
                                    viewModel.askMotoAdvisor("What bikes in your catalog match or relate to: $searchQuery?")
                                }
                                showAiAdvisor = true
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = CyanNeon, contentColor = Slate950),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(imageVector = Icons.Filled.AutoAwesome, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Ask Moto AI Advisor", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .testTag("search_results_list"),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 80.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    items(
                        items = searchResults,
                        key = { it.id }
                    ) { bike ->
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
}
