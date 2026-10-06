package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.CompareArrows
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.TwoWheeler
import androidx.compose.material.icons.outlined.Business
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.TwoWheeler
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.local.MotoScopeDatabase
import com.example.data.repository.MotorcycleRepository
import com.example.ui.components.ComparisonSlotBar
import com.example.ui.navigation.MotoScreen
import com.example.ui.screens.AdminScreen
import com.example.ui.screens.BrandDetailScreen
import com.example.ui.screens.BrandsScreen
import com.example.ui.screens.CatalogScreen
import com.example.ui.screens.ComparisonScreen
import com.example.ui.screens.FavoritesScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.MotorcycleDetailScreen
import com.example.ui.theme.AmberOrange
import com.example.ui.theme.CyanNeon
import com.example.ui.theme.MotoScopeTheme
import com.example.ui.theme.Slate400
import com.example.ui.theme.Slate800
import com.example.ui.theme.Slate850
import com.example.ui.theme.Slate900
import com.example.ui.theme.Slate950
import com.example.ui.viewmodel.MotoViewModel
import com.example.ui.viewmodel.MotoViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val database = MotoScopeDatabase.getInstance(applicationContext)
        val firestoreSyncService = com.example.data.sync.FirestoreSyncService(applicationContext, database.motoDao())
        val repository = MotorcycleRepository(database.motoDao(), firestoreSyncService)

        setContent {
            MotoScopeTheme(darkTheme = true) {
                val viewModel: MotoViewModel = viewModel(
                    factory = MotoViewModelFactory(repository)
                )
                MotoAppRoot(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun MotoAppRoot(viewModel: MotoViewModel) {
    val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()
    val comparisonSlots by viewModel.comparisonSlotIds.collectAsStateWithLifecycle()
    val allMotorcycles by viewModel.allMotorcycles.collectAsStateWithLifecycle()

    val bikeMap = remember(allMotorcycles) { allMotorcycles.associateBy { it.id } }
    val slotBikes = comparisonSlots.mapNotNull { bikeMap[it] }

    // Intercept hardware/gesture back press
    BackHandler(enabled = currentScreen != MotoScreen.Home) {
        viewModel.navigateBack()
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Slate950,
        bottomBar = {
            Column(
                modifier = Modifier
                    .background(Slate900)
                    .windowInsetsPadding(WindowInsets.navigationBars)
            ) {
                // Show floating quick-compare bar only on main browsing tabs when slots are populated
                if (currentScreen is MotoScreen.Home || currentScreen is MotoScreen.Catalog || currentScreen is MotoScreen.Brands) {
                    ComparisonSlotBar(
                        slotBikes = slotBikes,
                        onRemoveBike = { viewModel.removeFromCompare(it) },
                        onCompareClick = { viewModel.navigateTo(MotoScreen.Comparison) }
                    )
                }

                NavigationBar(
                    containerColor = Slate900,
                    tonalElevation = 8.dp,
                    modifier = Modifier.testTag("main_navigation_bar")
                ) {
                    // 1. Home
                    NavigationBarItem(
                        selected = currentScreen is MotoScreen.Home,
                        onClick = { viewModel.navigateTo(MotoScreen.Home) },
                        icon = {
                            Icon(
                                imageVector = if (currentScreen is MotoScreen.Home) Icons.Filled.Home else Icons.Outlined.Home,
                                contentDescription = "Home"
                            )
                        },
                        label = { Text("Home", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                        colors = navItemColors(),
                        modifier = Modifier.testTag("nav_home")
                    )

                    // 2. Catalog
                    NavigationBarItem(
                        selected = currentScreen is MotoScreen.Catalog,
                        onClick = { viewModel.navigateTo(MotoScreen.Catalog) },
                        icon = {
                            Icon(
                                imageVector = if (currentScreen is MotoScreen.Catalog) Icons.Filled.TwoWheeler else Icons.Outlined.TwoWheeler,
                                contentDescription = "Catalog"
                            )
                        },
                        label = { Text("Explore", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                        colors = navItemColors(),
                        modifier = Modifier.testTag("nav_catalog")
                    )

                    // 3. Brands
                    NavigationBarItem(
                        selected = currentScreen is MotoScreen.Brands || currentScreen is MotoScreen.BrandDetail,
                        onClick = { viewModel.navigateTo(MotoScreen.Brands) },
                        icon = {
                            Icon(
                                imageVector = if (currentScreen is MotoScreen.Brands) Icons.Filled.Business else Icons.Outlined.Business,
                                contentDescription = "Brands"
                            )
                        },
                        label = { Text("Brands", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                        colors = navItemColors(),
                        modifier = Modifier.testTag("nav_brands")
                    )

                    // 4. Compare (with Badge)
                    NavigationBarItem(
                        selected = currentScreen is MotoScreen.Comparison,
                        onClick = { viewModel.navigateTo(MotoScreen.Comparison) },
                        icon = {
                            BadgedBox(
                                badge = {
                                    if (comparisonSlots.isNotEmpty()) {
                                        Badge(containerColor = CyanNeon, contentColor = Slate950) {
                                            Text("${comparisonSlots.size}", fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.CompareArrows,
                                    contentDescription = "Compare"
                                )
                            }
                        },
                        label = { Text("Compare", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                        colors = navItemColors(),
                        modifier = Modifier.testTag("nav_compare")
                    )

                    // 5. Garage / Saved
                    NavigationBarItem(
                        selected = currentScreen is MotoScreen.Favorites,
                        onClick = { viewModel.navigateTo(MotoScreen.Favorites) },
                        icon = {
                            Icon(
                                imageVector = if (currentScreen is MotoScreen.Favorites) Icons.Filled.Bookmark else Icons.Filled.BookmarkBorder,
                                contentDescription = "Garage"
                            )
                        },
                        label = { Text("Saved", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                        colors = navItemColors(),
                        modifier = Modifier.testTag("nav_favorites")
                    )

                    // 6. Admin Studio
                    NavigationBarItem(
                        selected = currentScreen is MotoScreen.Admin,
                        onClick = { viewModel.navigateTo(MotoScreen.Admin) },
                        icon = {
                            Icon(
                                imageVector = Icons.Filled.AdminPanelSettings,
                                contentDescription = "Admin"
                            )
                        },
                        label = { Text("Studio", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                        colors = navItemColors(),
                        modifier = Modifier.testTag("nav_admin")
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (val screen = currentScreen) {
                is MotoScreen.Home -> HomeScreen(viewModel = viewModel)
                is MotoScreen.Catalog -> CatalogScreen(viewModel = viewModel)
                is MotoScreen.Brands -> BrandsScreen(viewModel = viewModel)
                is MotoScreen.BrandDetail -> BrandDetailScreen(brandId = screen.brandId, viewModel = viewModel)
                is MotoScreen.MotorcycleDetail -> MotorcycleDetailScreen(bikeId = screen.bikeId, viewModel = viewModel)
                is MotoScreen.Comparison -> ComparisonScreen(viewModel = viewModel)
                is MotoScreen.Favorites -> FavoritesScreen(viewModel = viewModel)
                is MotoScreen.Admin -> AdminScreen(viewModel = viewModel)
            }
        }
    }
}

@Composable
private fun navItemColors() = NavigationBarItemDefaults.colors(
    selectedIconColor = CyanNeon,
    selectedTextColor = CyanNeon,
    unselectedIconColor = Slate400,
    unselectedTextColor = Slate400,
    indicatorColor = Slate800
)
