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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.CompareArrows
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.TwoWheeler
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import com.example.ui.components.AuthDialog
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
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
fun FavoritesScreen(
    viewModel: MotoViewModel,
    modifier: Modifier = Modifier
) {
    val favorites by viewModel.favorites.collectAsStateWithLifecycle()
    val savedComparisons by viewModel.savedComparisons.collectAsStateWithLifecycle()
    val recentlyViewed by viewModel.recentlyViewed.collectAsStateWithLifecycle()
    val allMotorcycles by viewModel.allMotorcycles.collectAsStateWithLifecycle()
    val comparisonSlots by viewModel.comparisonSlotIds.collectAsStateWithLifecycle()
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()

    var selectedTab by remember { mutableIntStateOf(0) }
    var showAuthDialog by remember { mutableStateOf(false) }

    val bikeMap = remember(allMotorcycles) { allMotorcycles.associateBy { it.id } }
    val favIds = favorites.map { it.motorcycleId }.toSet()
    val compareIds = comparisonSlots.toSet()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Slate950)
            .testTag("favorites_screen")
    ) {
        // --- Header ---
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Slate900)
                .padding(horizontal = 16.dp, vertical = 14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Garage & Saved",
                        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )
                    Text(
                        text = "Bookmarked motorcycles, saved match reports & history",
                        style = MaterialTheme.typography.bodySmall.copy(color = Slate400)
                    )
                }

                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = if (currentUser != null) Slate850 else CyanNeon.copy(alpha = 0.15f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, if (currentUser != null) Slate700 else CyanNeon),
                    modifier = Modifier.clickable { showAuthDialog = true }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (currentUser != null) androidx.compose.material.icons.filled.AccountCircle else androidx.compose.material.icons.filled.Lock,
                            contentDescription = null,
                            tint = CyanNeon,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (currentUser != null) {
                                (currentUser?.email?.substringBefore("@") ?: "Rider")
                            } else "Sign In",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Tab Selector
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = Slate950,
                contentColor = CyanNeon,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = CyanNeon
                    )
                },
                modifier = Modifier.border(1.dp, Slate800, RoundedCornerShape(12.dp))
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = {
                        Text(
                            text = "Favorites (${favorites.size})",
                            fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = {
                        Text(
                            text = "Comparisons (${savedComparisons.size})",
                            fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                )
                Tab(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    text = {
                        Text(
                            text = "History (${recentlyViewed.size})",
                            fontWeight = if (selectedTab == 2) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                )
            }
        }

        // --- Tab 0: Favorites ---
        if (selectedTab == 0) {
            val favBikes = favorites.mapNotNull { bikeMap[it.motorcycleId] }
            if (favBikes.isEmpty()) {
                EmptyStateView(
                    icon = "⭐",
                    title = "No Bookmarked Motorcycles",
                    description = "Tap the bookmark icon on any motorcycle to store it in your garage for quick reference."
                )
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 100.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    items(favBikes, key = { it.id }) { bike ->
                        MotorcycleCard(
                            motorcycle = bike,
                            isInCompare = compareIds.contains(bike.id),
                            isFavorite = true,
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

        // --- Tab 1: Saved Comparisons ---
        if (selectedTab == 1) {
            if (savedComparisons.isEmpty()) {
                EmptyStateView(
                    icon = "⚖️",
                    title = "No Saved Comparisons",
                    description = "Run a comparison between 2 or 3 bikes and tap 'Save' to keep the match verdict here."
                )
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 100.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(savedComparisons, key = { it.id }) { comp ->
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = Slate850,
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, Slate800, RoundedCornerShape(14.dp))
                                .clickable {
                                    val ids = listOfNotNull(comp.bike1Id, comp.bike2Id, comp.bike3Id)
                                    viewModel.setComparisonBikes(ids)
                                    viewModel.navigateTo(MotoScreen.Comparison)
                                }
                                .padding(16.dp)
                        ) {
                            Column {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = comp.title,
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                        color = Color.White
                                    )
                                }

                                Spacer(modifier = Modifier.height(6.dp))

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "Verdict Winner: ",
                                        style = MaterialTheme.typography.bodySmall.copy(color = Slate400)
                                    )
                                    Text(
                                        text = "${comp.primaryWinnerName} 🏆",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = AmberOrange,
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                Text(
                                    text = "Tap to review full technical breakdown →",
                                    style = MaterialTheme.typography.labelSmall.copy(color = CyanNeon)
                                )
                            }
                        }
                    }
                }
            }
        }

        // --- Tab 2: Recently Viewed ---
        if (selectedTab == 2) {
            val recentBikes = recentlyViewed.mapNotNull { bikeMap[it.motorcycleId] }
            if (recentBikes.isEmpty()) {
                EmptyStateView(
                    icon = "🕒",
                    title = "No Browsing History Yet",
                    description = "Motorcycles you explore will appear here automatically for easy back-tracking."
                )
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 100.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    items(recentBikes, key = { it.id }) { bike ->
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

        if (showAuthDialog) {
            AuthDialog(
                currentUser = currentUser,
                onDismiss = { showAuthDialog = false },
                onEmailSignIn = { email, pass, cb -> viewModel.signInWithEmail(email, pass, cb) },
                onEmailSignUp = { email, pass, cb -> viewModel.signUpWithEmail(email, pass, cb) },
                onAnonymousSignIn = { cb -> viewModel.signInAnonymously(cb) },
                onSendPhoneOtp = { phone, act, cb -> viewModel.sendPhoneOtp(phone, act, cb) },
                onVerifyPhoneCredential = { cred, cb -> viewModel.verifyPhoneCredential(cred, cb) },
                onSignOut = { viewModel.signOut() }
            )
        }
    }
}

@Composable
fun EmptyStateView(icon: String, title: String, description: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = icon, fontSize = 48.sp)
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = Color.White
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium.copy(color = Slate400),
                textAlign = TextAlign.Center
            )
        }
    }
}
