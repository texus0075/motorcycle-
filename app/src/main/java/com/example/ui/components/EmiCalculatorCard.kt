package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AmberOrange
import com.example.ui.theme.CyanNeon
import com.example.ui.theme.Slate400
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate800
import com.example.ui.theme.Slate850
import com.example.ui.theme.Slate900
import com.example.ui.theme.TrackGreen
import java.text.NumberFormat
import java.util.Locale
import kotlin.math.pow
import kotlin.math.roundToInt

@Composable
fun EmiCalculatorCard(
    basePrice: Double,
    currencySymbol: String = "$",
    modifier: Modifier = Modifier
) {
    var isExpanded by remember { mutableStateOf(false) }

    // Down payment percentage: 10% to 50% (default 20%)
    var downPaymentPercent by remember { mutableFloatStateOf(20f) }
    // Tenure in months: 12, 24, 36, 48, 60 (default 36)
    var selectedTenureMonths by remember { mutableIntStateOf(36) }
    // Annual interest rate: 7.0% to 15.0% (default 9.5%)
    var annualInterestRate by remember { mutableFloatStateOf(9.5f) }

    val activeBasePrice = if (basePrice > 0) basePrice else 2000.0
    val downPaymentAmount = activeBasePrice * (downPaymentPercent / 100f)
    val principalLoan = (activeBasePrice - downPaymentAmount).coerceAtLeast(0.0)

    // Monthly EMI calculation
    val monthlyRate = (annualInterestRate / 12f) / 100.0
    val tenureN = selectedTenureMonths.toDouble()
    val monthlyEmi = if (principalLoan > 0 && monthlyRate > 0) {
        val factor = (1.0 + monthlyRate).pow(tenureN)
        (principalLoan * monthlyRate * factor) / (factor - 1.0)
    } else {
        0.0
    }

    val totalPayment = (monthlyEmi * tenureN) + downPaymentAmount
    val totalInterest = (monthlyEmi * tenureN - principalLoan).coerceAtLeast(0.0)

    // Estimated On-road price: Base + RTO (10%) + Insurance (4%) + Handling (1%)
    val rtoCost = activeBasePrice * 0.10
    val insuranceCost = activeBasePrice * 0.04
    val estimatedOnRoad = activeBasePrice + rtoCost + insuranceCost

    fun formatAmount(amount: Double): String {
        val formatter = NumberFormat.getNumberInstance(Locale.US)
        formatter.maximumFractionDigits = 0
        return "$currencySymbol${formatter.format(amount.roundToInt())}"
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Slate900, RoundedCornerShape(12.dp))
            .border(1.dp, Slate800, RoundedCornerShape(12.dp))
            .padding(16.dp)
    ) {
        // Card Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { isExpanded = !isExpanded },
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = CyanNeon.copy(alpha = 0.12f),
                    modifier = Modifier.padding(end = 10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Calculate,
                        contentDescription = "EMI Calculator",
                        tint = CyanNeon,
                        modifier = Modifier.padding(6.dp)
                    )
                }
                Column {
                    Text(
                        text = "EMI & On-Road Estimator",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )
                    Text(
                        text = "Starts at ${formatAmount(monthlyEmi)}/mo • ${formatAmount(estimatedOnRoad)} On-Road",
                        style = MaterialTheme.typography.bodySmall.copy(color = CyanNeon, fontWeight = FontWeight.SemiBold)
                    )
                }
            }
            Icon(
                imageVector = if (isExpanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                contentDescription = null,
                tint = Slate400
            )
        }

        AnimatedVisibility(visible = isExpanded) {
            Column(modifier = Modifier.padding(top = 16.dp)) {
                // Monthly EMI Banner
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Slate850,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "ESTIMATED MONTHLY EMI",
                                style = MaterialTheme.typography.labelSmall.copy(color = Slate400, fontSize = 10.sp)
                            )
                            Text(
                                text = "${formatAmount(monthlyEmi)} / month",
                                style = MaterialTheme.typography.headlineSmall.copy(
                                    fontWeight = FontWeight.Black,
                                    color = TrackGreen
                                )
                            )
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "TOTAL INTEREST",
                                style = MaterialTheme.typography.labelSmall.copy(color = Slate400, fontSize = 10.sp)
                            )
                            Text(
                                text = formatAmount(totalInterest),
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = AmberOrange
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Down Payment Slider
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Down Payment: ${downPaymentPercent.roundToInt()}%",
                        style = MaterialTheme.typography.bodyMedium.copy(color = Color.White, fontWeight = FontWeight.SemiBold)
                    )
                    Text(
                        text = formatAmount(downPaymentAmount),
                        style = MaterialTheme.typography.bodyMedium.copy(color = CyanNeon, fontWeight = FontWeight.Bold)
                    )
                }
                Slider(
                    value = downPaymentPercent,
                    onValueChange = { downPaymentPercent = it },
                    valueRange = 10f..50f,
                    steps = 7,
                    colors = SliderDefaults.colors(
                        thumbColor = CyanNeon,
                        activeTrackColor = CyanNeon,
                        inactiveTrackColor = Slate800
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Tenure Selection Chips
                Text(
                    text = "Loan Duration (Months)",
                    style = MaterialTheme.typography.bodyMedium.copy(color = Color.White, fontWeight = FontWeight.SemiBold)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(12, 24, 36, 48, 60).forEach { tenure ->
                        val isSelected = selectedTenureMonths == tenure
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSelected) CyanNeon else Slate850,
                            border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) CyanNeon else Slate700),
                            modifier = Modifier
                                .weight(1f)
                                .clickable { selectedTenureMonths = tenure }
                        ) {
                            Text(
                                text = "${tenure}M",
                                modifier = Modifier.padding(vertical = 8.dp),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                style = MaterialTheme.typography.labelMedium.copy(
                                    color = if (isSelected) Slate900 else Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Interest Rate Slider
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Interest Rate (p.a.)",
                        style = MaterialTheme.typography.bodyMedium.copy(color = Color.White, fontWeight = FontWeight.SemiBold)
                    )
                    Text(
                        text = String.format(Locale.US, "%.1f%%", annualInterestRate),
                        style = MaterialTheme.typography.bodyMedium.copy(color = AmberOrange, fontWeight = FontWeight.Bold)
                    )
                }
                Slider(
                    value = annualInterestRate,
                    onValueChange = { annualInterestRate = it },
                    valueRange = 7.0f..15.0f,
                    steps = 15,
                    colors = SliderDefaults.colors(
                        thumbColor = AmberOrange,
                        activeTrackColor = AmberOrange,
                        inactiveTrackColor = Slate800
                    )
                )

                HorizontalDivider(
                    color = Slate800,
                    modifier = Modifier.padding(vertical = 12.dp)
                )

                // On-Road Price Breakdown
                Text(
                    text = "Estimated On-Road Price Breakdown",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = Color.White)
                )
                Spacer(modifier = Modifier.height(8.dp))
                BreakdownRow(label = "Ex-Showroom Price", amount = formatAmount(activeBasePrice))
                BreakdownRow(label = "RTO Registration (~10%)", amount = formatAmount(rtoCost))
                BreakdownRow(label = "Comprehensive Insurance (~4%)", amount = formatAmount(insuranceCost))
                BreakdownRow(
                    label = "Total Estimated On-Road",
                    amount = formatAmount(estimatedOnRoad),
                    isHighlight = true
                )
            }
        }
    }
}

@Composable
private fun BreakdownRow(label: String, amount: String, isHighlight: Boolean = false) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall.copy(
                color = if (isHighlight) Color.White else Slate400,
                fontWeight = if (isHighlight) FontWeight.Bold else FontWeight.Normal
            )
        )
        Text(
            text = amount,
            style = MaterialTheme.typography.bodySmall.copy(
                color = if (isHighlight) TrackGreen else Color.White,
                fontWeight = if (isHighlight) FontWeight.Black else FontWeight.SemiBold
            )
        )
    }
}
