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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import com.example.data.models.BrandEntity
import com.example.data.models.MotorcycleEntity
import com.example.data.models.MotorcycleSpecs
import com.example.data.models.VariantEntity
import com.example.ui.theme.AmberOrange
import com.example.ui.theme.CyanNeon
import com.example.ui.theme.RacingRed
import com.example.ui.theme.Slate400
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate800
import com.example.ui.theme.Slate850
import com.example.ui.theme.Slate900
import com.example.ui.theme.Slate950
import com.example.ui.theme.TrackGreen
import com.example.ui.viewmodel.MotoViewModel

@Composable
fun AdminScreen(
    viewModel: MotoViewModel,
    modifier: Modifier = Modifier
) {
    val allBrands by viewModel.allBrands.collectAsStateWithLifecycle()
    val allMotorcycles by viewModel.allMotorcycles.collectAsStateWithLifecycle()

    var selectedTab by remember { mutableIntStateOf(0) }
    var showAddBikeDialog by remember { mutableStateOf(false) }
    var showAddBrandDialog by remember { mutableStateOf(false) }
    var showResetConfirm by remember { mutableStateOf(false) }
    var statusMessage by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Slate950)
            .testTag("admin_screen")
    ) {
        // --- Header ---
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Slate900)
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Admin Studio",
                        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )
                    Text(
                        text = "Manage global catalog, add models, validate specs",
                        style = MaterialTheme.typography.bodySmall.copy(color = Slate400)
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = {
                            viewModel.syncCatalog()
                            statusMessage = "Catalog synchronized! ${allBrands.size} Brands & ${allMotorcycles.size} Motorcycles loaded."
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = CyanNeon, contentColor = Slate950),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                        modifier = Modifier.height(34.dp)
                    ) {
                        Icon(imageVector = Icons.Filled.Refresh, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Sync Data", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }

                    OutlinedButton(
                        onClick = { showResetConfirm = true },
                        border = androidx.compose.foundation.BorderStroke(1.dp, AmberOrange),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = AmberOrange),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                        modifier = Modifier.height(34.dp)
                    ) {
                        Text("Reset", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Cloud Firestore Status Banner
            Surface(
                color = if (viewModel.isFirebaseConfigured) CyanNeon.copy(alpha = 0.12f) else Slate850,
                shape = RoundedCornerShape(10.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, if (viewModel.isFirebaseConfigured) CyanNeon.copy(alpha = 0.4f) else Slate800),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (viewModel.isFirebaseConfigured) "☁️ Firestore Live Cloud Connected" else "☁️ Room-to-Firestore Sync Service Ready",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = Color.White
                        )
                        Text(
                            text = if (viewModel.isFirebaseConfigured)
                                "Bikes sync dynamically in real-time without app re-downloads."
                            else
                                "Dynamic cloud synchronization ready. Add google-services.json to link your Firebase project.",
                            fontSize = 10.sp,
                            color = Slate400
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    OutlinedButton(
                        onClick = {
                            viewModel.uploadCatalogToFirestore()
                        },
                        border = androidx.compose.foundation.BorderStroke(1.dp, CyanNeon),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = CyanNeon),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                        modifier = Modifier.height(28.dp)
                    ) {
                        Text("Push to Cloud", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

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
                modifier = Modifier.border(1.dp, Slate800, RoundedCornerShape(10.dp))
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("Motorcycles (${allMotorcycles.size})", fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("Brands (${allBrands.size})", fontWeight = FontWeight.Bold) }
                )
            }
        }

        if (statusMessage != null) {
            Surface(
                color = TrackGreen.copy(alpha = 0.15f),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
                    .border(1.dp, TrackGreen, RoundedCornerShape(8.dp))
            ) {
                Row(
                    modifier = Modifier.padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(imageVector = Icons.Filled.CheckCircle, contentDescription = null, tint = TrackGreen, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(statusMessage!!, color = Color.White, style = MaterialTheme.typography.bodySmall)
                }
            }
        }

        // --- Tab 0: Motorcycles Management ---
        if (selectedTab == 0) {
            Column(modifier = Modifier.fillMaxSize()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "MANAGE SPEC DATABASE",
                        style = MaterialTheme.typography.labelSmall.copy(color = Slate400, fontWeight = FontWeight.Bold)
                    )
                    Button(
                        onClick = { showAddBikeDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = CyanNeon, contentColor = Slate950),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.testTag("admin_add_bike_btn")
                    ) {
                        Icon(imageVector = Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Add Motorcycle", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }

                LazyColumn(
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 100.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(allMotorcycles) { bike ->
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Slate900,
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, Slate800, RoundedCornerShape(12.dp))
                                .padding(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "${bike.brandId.uppercase()} • ${bike.modelName}",
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                        color = Color.White
                                    )
                                    Text(
                                        text = "${bike.category} | ${bike.specs.maxPowerHp} HP | ${bike.priceDisplay}",
                                        style = MaterialTheme.typography.bodySmall.copy(color = Slate400)
                                    )
                                    Text(
                                        text = "Source: ${bike.source}",
                                        style = MaterialTheme.typography.labelSmall.copy(color = CyanNeon, fontSize = 10.sp)
                                    )
                                }

                                IconButton(
                                    onClick = {
                                        viewModel.deleteMotorcycle(bike)
                                        statusMessage = "Deleted ${bike.modelName}"
                                    }
                                ) {
                                    Icon(imageVector = Icons.Filled.Delete, contentDescription = "Delete", tint = RacingRed)
                                }
                            }
                        }
                    }
                }
            }
        }

        // --- Tab 1: Brands Management ---
        if (selectedTab == 1) {
            Column(modifier = Modifier.fillMaxSize()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "MANAGE GLOBAL BRANDS",
                        style = MaterialTheme.typography.labelSmall.copy(color = Slate400, fontWeight = FontWeight.Bold)
                    )
                    Button(
                        onClick = { showAddBrandDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = CyanNeon, contentColor = Slate950),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.testTag("admin_add_brand_btn")
                    ) {
                        Icon(imageVector = Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Add Brand", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }

                LazyColumn(
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 100.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(allBrands) { brand ->
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Slate900,
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, Slate800, RoundedCornerShape(12.dp))
                                .padding(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = brand.name,
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                        color = Color.White
                                    )
                                    Text(
                                        text = "${brand.country} • Est. ${brand.foundedYear}",
                                        style = MaterialTheme.typography.bodySmall.copy(color = Slate400)
                                    )
                                    Text(
                                        text = brand.websiteUrl,
                                        style = MaterialTheme.typography.labelSmall.copy(color = CyanNeon, fontSize = 10.sp)
                                    )
                                }

                                IconButton(
                                    onClick = {
                                        viewModel.deleteBrand(brand)
                                        statusMessage = "Deleted ${brand.name}"
                                    }
                                ) {
                                    Icon(imageVector = Icons.Filled.Delete, contentDescription = "Delete", tint = RacingRed)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // --- ADD MOTORCYCLE MODAL WITH VALIDATION ---
    if (showAddBikeDialog) {
        var newBrandId by remember { mutableStateOf(allBrands.firstOrNull()?.id ?: "yamaha") }
        var newModelName by remember { mutableStateOf("") }
        var newCategory by remember { mutableStateOf("Sport") }
        var newYear by remember { mutableStateOf("2024") }
        var newPrice by remember { mutableStateOf("3500") }
        var newPowerHp by remember { mutableStateOf("40.0") }
        var newTorqueNm by remember { mutableStateOf("35.0") }
        var newDisplacementCc by remember { mutableStateOf("399.0") }
        var newWeightKg by remember { mutableStateOf("165.0") }
        var newMileageKmpl by remember { mutableStateOf("30.0") }
        var newSource by remember { mutableStateOf("Official Homologation Sheet") }
        var validationError by remember { mutableStateOf<String?>(null) }

        AlertDialog(
            onDismissRequest = { showAddBikeDialog = false },
            title = { Text("Publish New Motorcycle", color = Color.White, fontWeight = FontWeight.Bold) },
            text = {
                LazyColumn(modifier = Modifier.height(400.dp)) {
                    item {
                        if (validationError != null) {
                            Text(
                                text = "⚠️ $validationError",
                                color = RacingRed,
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                        }

                        AdminTextField("Brand ID (e.g. yamaha, ktm)", newBrandId) { newBrandId = it }
                        AdminTextField("Model Name", newModelName) { newModelName = it }
                        AdminTextField("Category (Sport, Naked, ADV, Cruiser)", newCategory) { newCategory = it }
                        AdminTextField("Model Year", newYear) { newYear = it }
                        AdminTextField("Base Price ($)", newPrice) { newPrice = it }
                        AdminTextField("Power (HP)", newPowerHp) { newPowerHp = it }
                        AdminTextField("Torque (Nm)", newTorqueNm) { newTorqueNm = it }
                        AdminTextField("Displacement (cc)", newDisplacementCc) { newDisplacementCc = it }
                        AdminTextField("Kerb Weight (kg)", newWeightKg) { newWeightKg = it }
                        AdminTextField("Mileage (km/l)", newMileageKmpl) { newMileageKmpl = it }
                        AdminTextField("Verified Source Sheet", newSource) { newSource = it }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        // Strict validation
                        if (newModelName.isBlank()) {
                            validationError = "Model name cannot be empty"
                            return@Button
                        }
                        val priceVal = newPrice.toDoubleOrNull()
                        val hpVal = newPowerHp.toDoubleOrNull()
                        val ccVal = newDisplacementCc.toDoubleOrNull()
                        if (priceVal == null || hpVal == null || ccVal == null) {
                            validationError = "Price, Power and Displacement must be valid numbers"
                            return@Button
                        }

                        val id = "${newBrandId.trim().lowercase()}-${newModelName.trim().lowercase().replace(" ", "-")}"
                        val newBike = MotorcycleEntity(
                            id = id,
                            brandId = newBrandId.trim().lowercase(),
                            modelName = newModelName.trim(),
                            category = newCategory.trim(),
                            modelYear = newYear.toIntOrNull() ?: 2024,
                            basePrice = priceVal,
                            currency = "$",
                            priceDisplay = "$$priceVal",
                            rating = 4.7f,
                            reviewCount = 10,
                            isTrending = true,
                            isNew = true,
                            isPopular = false,
                            heroImageUrl = "https://images.unsplash.com/photo-1558981403-c5f9899a28bc?auto=format&fit=crop&w=1000&q=80",
                            source = newSource.trim(),
                            sourceUrl = "",
                            lastUpdated = "2024",
                            specs = MotorcycleSpecs(
                                engineType = "DOHC 4-Stroke Liquid Cooled",
                                displacementCc = ccVal,
                                cylinders = 1,
                                maxPowerHp = hpVal,
                                maxPowerDisplay = "$hpVal HP",
                                maxTorqueNm = newTorqueNm.toDoubleOrNull() ?: 30.0,
                                maxTorqueDisplay = "${newTorqueNm.toDoubleOrNull() ?: 30.0} Nm",
                                topSpeedKmh = 160,
                                accel0To100Sec = 5.5,
                                mileageKmpl = newMileageKmpl.toDoubleOrNull() ?: 30.0,
                                kerbWeightKg = newWeightKg.toDoubleOrNull() ?: 165.0,
                                absSystem = "Dual-Channel ABS"
                            )
                        )
                        viewModel.addOrUpdateMotorcycle(newBike)
                        statusMessage = "Successfully published ${newBike.modelName}!"
                        showAddBikeDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CyanNeon, contentColor = Slate950)
                ) {
                    Text("Publish to Catalog", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddBikeDialog = false }) {
                    Text("Cancel", color = Slate400)
                }
            },
            containerColor = Slate900
        )
    }

    // --- ADD BRAND MODAL ---
    if (showAddBrandDialog) {
        var brandName by remember { mutableStateOf("") }
        var brandCountry by remember { mutableStateOf("") }
        var brandYear by remember { mutableStateOf("1960") }
        var brandHistory by remember { mutableStateOf("") }
        var brandUrl by remember { mutableStateOf("https://") }
        var brandError by remember { mutableStateOf<String?>(null) }

        AlertDialog(
            onDismissRequest = { showAddBrandDialog = false },
            title = { Text("Register Manufacturer Brand", color = Color.White, fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    if (brandError != null) {
                        Text("⚠️ $brandError", color = RacingRed, style = MaterialTheme.typography.bodySmall)
                        Spacer(modifier = Modifier.height(4.dp))
                    }
                    AdminTextField("Brand Name (e.g. MV Agusta)", brandName) { brandName = it }
                    AdminTextField("Country (e.g. Italy)", brandCountry) { brandCountry = it }
                    AdminTextField("Founded Year", brandYear) { brandYear = it }
                    AdminTextField("Brand History", brandHistory) { brandHistory = it }
                    AdminTextField("Official Website", brandUrl) { brandUrl = it }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (brandName.isBlank()) {
                            brandError = "Brand name cannot be blank"
                            return@Button
                        }
                        val id = brandName.trim().lowercase().replace(" ", "")
                        val brand = BrandEntity(
                            id = id,
                            name = brandName.trim(),
                            country = brandCountry.trim(),
                            foundedYear = brandYear.toIntOrNull() ?: 1950,
                            history = brandHistory.trim().ifEmpty { "Pioneering global motorcycle manufacturer." },
                            websiteUrl = brandUrl.trim(),
                            logoUrl = "https://images.unsplash.com/photo-1558981403-c5f9899a28bc?auto=format&fit=crop&w=400&q=80",
                            accentColorHex = 0xFF38BDF8
                        )
                        viewModel.addOrUpdateBrand(brand)
                        statusMessage = "Added Brand ${brand.name}!"
                        showAddBrandDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CyanNeon, contentColor = Slate950)
                ) {
                    Text("Register Brand", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddBrandDialog = false }) {
                    Text("Cancel", color = Slate400)
                }
            },
            containerColor = Slate900
        )
    }

    // Reset confirmation
    if (showResetConfirm) {
        AlertDialog(
            onDismissRequest = { showResetConfirm = false },
            title = { Text("Reset Motorcycle Database?", color = Color.White, fontWeight = FontWeight.Bold) },
            text = {
                Text(
                    "This will restore all default official global models (Yamaha, KTM, Kawasaki, Ducati, BMW, Triumph, Royal Enfield, etc.) and preloaded verified specifications.",
                    color = Slate400
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.resetCatalogToDefaults()
                        statusMessage = "Database refreshed to official global seed!"
                        showResetConfirm = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AmberOrange, contentColor = Slate950)
                ) {
                    Text("Confirm Reset", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetConfirm = false }) {
                    Text("Cancel", color = Slate400)
                }
            },
            containerColor = Slate900
        )
    }
}

@Composable
fun AdminTextField(label: String, value: String, onValueChange: (String) -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, color = Slate400, fontSize = 11.sp) },
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White,
            focusedBorderColor = CyanNeon,
            unfocusedBorderColor = Slate800,
            focusedContainerColor = Slate950,
            unfocusedContainerColor = Slate950
        ),
        shape = RoundedCornerShape(8.dp),
        singleLine = true,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    )
}
