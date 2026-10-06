package com.example.ui.screens

import android.content.Intent
import androidx.compose.ui.platform.LocalContext
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.data.models.MotorcycleEntity
import com.example.data.models.UseCasePriority
import com.example.ui.components.ComparisonSpecRow
import com.example.ui.components.ScoreRow
import com.example.ui.navigation.MotoScreen
import com.example.ui.theme.AmberOrange
import com.example.ui.theme.CyanNeon
import com.example.ui.theme.GoldWinner
import com.example.ui.theme.Slate400
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate800
import com.example.ui.theme.Slate850
import com.example.ui.theme.Slate900
import com.example.ui.theme.Slate950
import com.example.ui.theme.TrackGreen
import com.example.ui.viewmodel.MotoViewModel

@Composable
fun ComparisonScreen(
    viewModel: MotoViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val report by viewModel.comparisonReport.collectAsStateWithLifecycle()
    val slotIds by viewModel.comparisonSlotIds.collectAsStateWithLifecycle()
    val priority by viewModel.comparisonPriority.collectAsStateWithLifecycle()
    val allMotorcycles by viewModel.allMotorcycles.collectAsStateWithLifecycle()
    val aiVerdict by viewModel.aiComparisonVerdict.collectAsStateWithLifecycle()
    val isAiLoading by viewModel.isAiComparisonLoading.collectAsStateWithLifecycle()

    var showAddBikeModal by remember { mutableStateOf(false) }
    var saveSuccessMessage by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Slate950)
            .testTag("comparison_screen")
    ) {
        // --- Header ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Slate900)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "Comparison Studio",
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                    color = Color.White
                )
                Text(
                    text = "Side-by-side technical shootout (Up to 3 bikes)",
                    style = MaterialTheme.typography.bodySmall.copy(color = Slate400)
                )
            }

            if (report != null) {
                val rep = report!!
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    // Share Button
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Slate850,
                        modifier = Modifier
                            .border(1.dp, Slate700, RoundedCornerShape(10.dp))
                            .clickable {
                                val bikeNames = if (rep.bikeC != null) {
                                    "${rep.bikeA.modelName} vs ${rep.bikeB.modelName} vs ${rep.bikeC.modelName}"
                                } else {
                                    "${rep.bikeA.modelName} vs ${rep.bikeB.modelName}"
                                }
                                val winner = when (rep.overallWinnerIndex) {
                                    0 -> rep.bikeA.modelName
                                    1 -> rep.bikeB.modelName
                                    else -> rep.bikeC?.modelName ?: rep.bikeA.modelName
                                }
                                val shareText = "MotoScope Shootout:\n$bikeNames\n\nOverall Winner: $winner\n\nCheck out the full specs comparison on MotoScope!"
                                val sendIntent = Intent().apply {
                                    action = Intent.ACTION_SEND
                                    putExtra(Intent.EXTRA_TEXT, shareText)
                                    type = "text/plain"
                                }
                                context.startActivity(Intent.createChooser(sendIntent, "Share Comparison"))
                            }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Filled.Share, contentDescription = "Share", tint = CyanNeon, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Share",
                                style = MaterialTheme.typography.labelSmall.copy(color = Color.White, fontWeight = FontWeight.Bold)
                            )
                        }
                    }

                    // Save Button
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Slate850,
                        modifier = Modifier
                            .border(1.dp, Slate700, RoundedCornerShape(10.dp))
                            .clickable {
                                viewModel.saveCurrentComparison()
                                saveSuccessMessage = true
                            }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                            .testTag("save_comparison_btn")
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Filled.Bookmark, contentDescription = null, tint = AmberOrange, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (saveSuccessMessage) "Saved!" else "Save",
                                style = MaterialTheme.typography.labelSmall.copy(color = Color.White, fontWeight = FontWeight.Bold)
                            )
                        }
                    }
                }
            }
        }

        // --- Empty Slot Fallback if < 2 bikes selected ---
        if (report == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "⚖️", fontSize = 56.sp)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Select 2 or 3 Bikes to Compare",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Choose motorcycles from the catalog or tap below to pick iconic rivals for a full spec and decision shootout.",
                        style = MaterialTheme.typography.bodyMedium.copy(color = Slate400),
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    Button(
                        onClick = {
                            // Preload popular trio
                            viewModel.setComparisonBikes(listOf("yamaha-r15-v4", "ktm-rc-200", "kawasaki-ninja-300"))
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = CyanNeon, contentColor = Slate950),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Load R15 vs RC 200 vs Ninja 300", fontWeight = FontWeight.Bold)
                    }
                }
            }
            return
        }

        val r = report!!
        val bikes = listOfNotNull(r.bikeA, r.bikeB, r.bikeC)

        LazyColumn(
            contentPadding = PaddingValues(bottom = 120.dp)
        ) {
            // --- 1. BIKE HEADER CARDS (Side-by-side Columns) ---
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Slate900)
                        .padding(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        bikes.forEachIndexed { index, bike ->
                            ComparisonHeaderCard(
                                bike = bike,
                                onRemove = {
                                    viewModel.removeFromCompare(bike.id)
                                },
                                onClick = {
                                    viewModel.navigateTo(MotoScreen.MotorcycleDetail(bike.id))
                                },
                                modifier = Modifier.weight(1f)
                            )
                        }

                        // Add 3rd bike slot button if only 2 selected
                        if (bikes.size < 3) {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(170.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Slate950)
                                    .border(1.dp, Slate800, RoundedCornerShape(12.dp))
                                    .clickable { showAddBikeModal = true },
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(imageVector = Icons.Filled.Add, contentDescription = "Add 3rd Bike", tint = CyanNeon, modifier = Modifier.size(28.dp))
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "Add 3rd Bike",
                                        style = MaterialTheme.typography.labelSmall.copy(color = Slate400, fontWeight = FontWeight.Bold)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // --- 2. PRIORITY / USE-CASE MODE SELECTOR ---
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Slate950)
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Filled.Tune, contentDescription = null, tint = CyanNeon, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "RIDING PURPOSE / PRIORITY",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = Slate400,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp
                                )
                            )
                        }

                        Text(
                            text = priority.displayName,
                            style = MaterialTheme.typography.labelSmall.copy(color = CyanNeon, fontWeight = FontWeight.Bold)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(UseCasePriority.entries) { mode ->
                            val isSelected = priority == mode
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = if (isSelected) CyanNeon else Slate850,
                                modifier = Modifier
                                    .border(1.dp, if (isSelected) CyanNeon else Slate800, RoundedCornerShape(10.dp))
                                    .clickable { viewModel.setComparisonPriority(mode) }
                                    .testTag("priority_${mode.name}")
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(text = mode.icon, fontSize = 12.sp)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = mode.displayName,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = if (isSelected) Slate950 else Color.White,
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // --- 3. OVERALL SCORE & FINAL VERDICT CARD ---
            item {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Slate900,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .border(1.5.dp, GoldWinner.copy(alpha = 0.7f), RoundedCornerShape(16.dp))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "INTELLIGENT VERDICT REPORT",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = GoldWinner,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp
                                )
                            )
                            Text(text = "Mode: ${priority.displayName}", style = MaterialTheme.typography.labelSmall.copy(color = Slate400))
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Overall Scores Podium Cards
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            ScorePodium(
                                name = r.bikeA.modelName,
                                score = r.overallScoreA,
                                isWinner = r.overallWinnerIndex == 0,
                                modifier = Modifier.weight(1f)
                            )
                            ScorePodium(
                                name = r.bikeB.modelName,
                                score = r.overallScoreB,
                                isWinner = r.overallWinnerIndex == 1,
                                modifier = Modifier.weight(1f)
                            )
                            if (r.bikeC != null && r.overallScoreC != null) {
                                ScorePodium(
                                    name = r.bikeC.modelName,
                                    score = r.overallScoreC,
                                    isWinner = r.overallWinnerIndex == 2,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Written Verdict
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = Slate950,
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, Slate800, RoundedCornerShape(10.dp))
                                .padding(12.dp)
                        ) {
                            Text(
                                text = r.writtenVerdict,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = Color.White,
                                    lineHeight = 20.sp
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Gemini AI Shootout Section
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Slate950,
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, CyanNeon.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                                .padding(12.dp)
                                .testTag("comparison_gemini_verdict_card")
                        ) {
                            Column {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Filled.AutoAwesome,
                                            contentDescription = null,
                                            tint = CyanNeon,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "GEMINI 2.5 SHOOTOUT VERDICT",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                color = CyanNeon,
                                                fontWeight = FontWeight.Bold,
                                                letterSpacing = 1.sp
                                            )
                                        )
                                    }

                                    if (!isAiLoading && aiVerdict == null) {
                                        Button(
                                            onClick = { viewModel.requestAiComparisonVerdict() },
                                            colors = ButtonDefaults.buttonColors(containerColor = CyanNeon, contentColor = Slate950),
                                            shape = RoundedCornerShape(8.dp),
                                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                            modifier = Modifier.height(28.dp)
                                        ) {
                                            Text("Generate", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        }
                                    } else if (!isAiLoading && aiVerdict != null) {
                                        IconButton(
                                            onClick = { viewModel.requestAiComparisonVerdict() },
                                            modifier = Modifier.size(24.dp)
                                        ) {
                                            Icon(imageVector = Icons.Filled.Refresh, contentDescription = "Regenerate", tint = CyanNeon, modifier = Modifier.size(16.dp))
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                if (isAiLoading) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(vertical = 8.dp)
                                    ) {
                                        CircularProgressIndicator(color = CyanNeon, modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "Gemini AI is analyzing dyno curves & ergonomics...",
                                            style = MaterialTheme.typography.bodySmall.copy(color = Slate400, fontSize = 12.sp)
                                        )
                                    }
                                } else if (aiVerdict != null) {
                                    Text(
                                        text = aiVerdict!!,
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            color = Color.White,
                                            lineHeight = 20.sp,
                                            fontSize = 12.5.sp
                                        )
                                    )
                                } else {
                                    Text(
                                        text = "Tap 'Generate' to have Google Gemini synthesize a tailored head-to-head decision for your riding purpose.",
                                        style = MaterialTheme.typography.bodySmall.copy(color = Slate400, fontSize = 12.sp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Winner Accolade Badges
                        Text(
                            text = "CATEGORY CHAMPIONS",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = Slate400,
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp,
                                letterSpacing = 1.sp
                            )
                        )
                        Spacer(modifier = Modifier.height(6.dp))

                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            ChampionBadgeRow("🏆 Overall Champion", bikes[r.overallWinnerIndex].modelName)
                            ChampionBadgeRow("🔥 Performance Champion", bikes[r.performanceWinnerIndex].modelName)
                            ChampionBadgeRow("⛽ Mileage / Running Cost", bikes[r.mileageWinnerIndex].modelName)
                            ChampionBadgeRow("💰 Best Value for Money", bikes[r.valueWinnerIndex].modelName)
                            ChampionBadgeRow("🛡 Safety & Stopping Hardware", bikes[r.safetyWinnerIndex].modelName)
                            ChampionBadgeRow("🏙 City Traffic & Commuting", bikes[r.cityWinnerIndex].modelName)
                            ChampionBadgeRow("🛣 Highway & Touring", bikes[r.touringWinnerIndex].modelName)
                            ChampionBadgeRow("👨‍🎓 Beginner Friendly Pick", bikes[r.beginnerWinnerIndex].modelName)
                        }
                    }
                }
            }

            // --- 4. DETAILED CATEGORY SCORES ---
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = "DIMENSIONAL SCORES BREAKDOWN",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = Slate400,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    r.categoryScores.forEach { catScore ->
                        ScoreRow(
                            categoryScore = catScore,
                            nameA = r.bikeA.modelName,
                            nameB = r.bikeB.modelName,
                            nameC = r.bikeC?.modelName
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }

            // --- 5. HEAD-TO-HEAD SPECIFICATION TABLE ---
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Text(
                        text = "HEAD-TO-HEAD SPECIFICATION MATRIX",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = Slate400,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    )
                    Text(
                        text = "🏆 marks the leading specification where meaningful",
                        style = MaterialTheme.typography.bodySmall.copy(color = Slate400)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                }
            }

            // Powertrain Specs
            item {
                val sA = r.bikeA.specs
                val sB = r.bikeB.specs
                val sC = r.bikeC?.specs

                val powerWinner = calcLeader(listOfNotNull(sA.maxPowerHp, sB.maxPowerHp, sC?.maxPowerHp), higherIsBetter = true)
                val torqueWinner = calcLeader(listOfNotNull(sA.maxTorqueNm, sB.maxTorqueNm, sC?.maxTorqueNm), higherIsBetter = true)
                val speedWinner = calcLeader(listOfNotNull(sA.topSpeedKmh.toDouble(), sB.topSpeedKmh.toDouble(), sC?.topSpeedKmh?.toDouble()), higherIsBetter = true)
                val accelWinner = calcLeader(listOfNotNull(sA.accel0To100Sec, sB.accel0To100Sec, sC?.accel0To100Sec), higherIsBetter = false)
                val mileageWinner = calcLeader(listOfNotNull(sA.mileageKmpl, sB.mileageKmpl, sC?.mileageKmpl), higherIsBetter = true)
                val weightWinner = calcLeader(listOfNotNull(sA.kerbWeightKg, sB.kerbWeightKg, sC?.kerbWeightKg), higherIsBetter = false)
                val tankWinner = calcLeader(listOfNotNull(sA.fuelTankCapacityL, sB.fuelTankCapacityL, sC?.fuelTankCapacityL), higherIsBetter = true)
                val priceWinner = calcLeader(listOfNotNull(r.bikeA.basePrice, r.bikeB.basePrice, r.bikeC?.basePrice), higherIsBetter = false)

                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    ComparisonSpecRow("Estimated Price", r.bikeA.priceDisplay, r.bikeB.priceDisplay, r.bikeC?.priceDisplay, priceWinner)
                    ComparisonSpecRow("Engine Displacement", "${sA.displacementCc} cc", "${sB.displacementCc} cc", sC?.let { "${it.displacementCc} cc" })
                    ComparisonSpecRow("Maximum Power", sA.maxPowerDisplay, sB.maxPowerDisplay, sC?.maxPowerDisplay, powerWinner)
                    ComparisonSpecRow("Maximum Torque", sA.maxTorqueDisplay, sB.maxTorqueDisplay, sC?.maxTorqueDisplay, torqueWinner)
                    ComparisonSpecRow("Top Speed", "${sA.topSpeedKmh} km/h", "${sB.topSpeedKmh} km/h", sC?.let { "${it.topSpeedKmh} km/h" }, speedWinner)
                    ComparisonSpecRow("0-100 km/h Sprint", "${sA.accel0To100Sec}s", "${sB.accel0To100Sec}s", sC?.let { "${it.accel0To100Sec}s" }, accelWinner)
                    ComparisonSpecRow("Certified Mileage", "${sA.mileageKmpl} km/l", "${sB.mileageKmpl} km/l", sC?.let { "${it.mileageKmpl} km/l" }, mileageWinner)
                    ComparisonSpecRow("Kerb Weight", "${sA.kerbWeightKg} kg", "${sB.kerbWeightKg} kg", sC?.let { "${it.kerbWeightKg} kg" }, weightWinner)
                    ComparisonSpecRow("Fuel Tank Capacity", "${sA.fuelTankCapacityL}L", "${sB.fuelTankCapacityL}L", sC?.let { "${it.fuelTankCapacityL}L" }, tankWinner)
                    ComparisonSpecRow("Seat Height", "${sA.seatHeightMm} mm", "${sB.seatHeightMm} mm", sC?.let { "${it.seatHeightMm} mm" })
                    ComparisonSpecRow("Front Brake", sA.frontBrake, sB.frontBrake, sC?.frontBrake)
                    ComparisonSpecRow("ABS Setup", sA.absSystem, sB.absSystem, sC?.absSystem)
                    ComparisonSpecRow("Quickshifter", sA.quickShifter, sB.quickShifter, sC?.quickShifter)
                    ComparisonSpecRow("TFT Cockpit", if (sA.hasTftDisplay) "Yes" else "No", if (sB.hasTftDisplay) "Yes" else "No", sC?.let { if (it.hasTftDisplay) "Yes" else "No" })
                    ComparisonSpecRow("Traction Control", if (sA.hasTractionControl) "Yes" else "No", if (sB.hasTractionControl) "Yes" else "No", sC?.let { if (it.hasTractionControl) "Yes" else "No" })
                    ComparisonSpecRow("Cornering ABS", if (sA.hasCorneringAbs) "Yes" else "No", if (sB.hasCorneringAbs) "Yes" else "No", sC?.let { if (it.hasCorneringAbs) "Yes" else "No" })
                }
            }
        }
    }

    // Modal to add 3rd bike
    if (showAddBikeModal) {
        AlertDialog(
            onDismissRequest = { showAddBikeModal = false },
            title = { Text("Select 3rd Motorcycle", color = Color.White, fontWeight = FontWeight.Bold) },
            text = {
                val availableToAdd = allMotorcycles.filter { !slotIds.contains(it.id) }
                LazyColumn(modifier = Modifier.height(300.dp)) {
                    items(availableToAdd) { bike ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.addToCompare(bike.id)
                                    showAddBikeModal = false
                                }
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            AsyncImage(
                                model = bike.heroImageUrl,
                                contentDescription = null,
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(RoundedCornerShape(6.dp)),
                                contentScale = ContentScale.Crop
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(bike.modelName, color = Color.White, fontWeight = FontWeight.Bold)
                                Text("${bike.specs.maxPowerHp} HP • ${bike.priceDisplay}", color = Slate400, fontSize = 11.sp)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showAddBikeModal = false }) {
                    Text("Close", color = CyanNeon)
                }
            },
            containerColor = Slate900
        )
    }
}

@Composable
fun ComparisonHeaderCard(
    bike: MotorcycleEntity,
    onRemove: () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = Slate850,
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .border(1.dp, Slate800, RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(95.dp)
                    .background(Slate950)
            ) {
                AsyncImage(
                    model = bike.heroImageUrl,
                    contentDescription = bike.modelName,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // Remove X button
                IconButton(
                    onClick = onRemove,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(4.dp)
                        .size(24.dp)
                        .background(Color.Black.copy(alpha = 0.7f), CircleShape)
                ) {
                    Icon(imageVector = Icons.Filled.Close, contentDescription = "Remove", tint = Color.White, modifier = Modifier.size(14.dp))
                }
            }

            Column(modifier = Modifier.padding(8.dp)) {
                Text(
                    text = bike.brandId.uppercase(),
                    style = MaterialTheme.typography.labelSmall.copy(color = CyanNeon, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                )
                Text(
                    text = bike.modelName,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = bike.priceDisplay,
                    style = MaterialTheme.typography.labelSmall.copy(color = TrackGreen, fontWeight = FontWeight.Bold, fontSize = 10.sp),
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
fun ScorePodium(
    name: String,
    score: Int,
    isWinner: Boolean,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(
                if (isWinner) GoldWinner.copy(alpha = 0.15f) else Slate950,
                RoundedCornerShape(12.dp)
            )
            .border(
                width = if (isWinner) 1.5.dp else 0.8.dp,
                color = if (isWinner) GoldWinner else Slate800,
                shape = RoundedCornerShape(12.dp)
            )
            .padding(10.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            if (isWinner) {
                Text(text = "👑 OVERALL WINNER", style = MaterialTheme.typography.labelSmall.copy(color = GoldWinner, fontWeight = FontWeight.Black, fontSize = 9.sp))
            }
            Text(
                text = "$score",
                style = MaterialTheme.typography.displayMedium.copy(
                    fontWeight = FontWeight.Black,
                    color = if (isWinner) GoldWinner else Color.White,
                    fontSize = 28.sp
                )
            )
            Text(text = "/100", style = MaterialTheme.typography.labelSmall.copy(color = Slate400, fontSize = 10.sp))
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = name,
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun ChampionBadgeRow(title: String, bikeName: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Slate950, RoundedCornerShape(6.dp))
            .border(0.5.dp, Slate800, RoundedCornerShape(6.dp))
            .padding(horizontal = 8.dp, vertical = 5.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = title, style = MaterialTheme.typography.labelSmall.copy(color = Slate400, fontSize = 11.sp))
        Text(text = bikeName, style = MaterialTheme.typography.labelSmall.copy(color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp))
    }
}

private fun calcLeader(values: List<Double>, higherIsBetter: Boolean): Int? {
    if (values.size < 2) return null
    val target = if (higherIsBetter) values.maxOrNull() else values.minOrNull() ?: return null
    // If all equal, tie
    if (values.all { it == target }) return null
    return values.indexOf(target)
}
