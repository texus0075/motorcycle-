package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.GoldWinner
import com.example.ui.theme.Slate400
import com.example.ui.theme.Slate800
import com.example.ui.theme.Slate850
import com.example.ui.theme.Slate950

@Composable
fun ComparisonSpecRow(
    specTitle: String,
    valueA: String,
    valueB: String,
    valueC: String? = null,
    winnerIndex: Int? = null, // 0, 1, or 2 (or null if tie/informational)
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Slate950)
            .border(0.5.dp, Slate800)
            .padding(10.dp)
    ) {
        // Spec Name
        Text(
            text = specTitle.uppercase(),
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                color = Slate400,
                fontSize = 11.sp,
                letterSpacing = 0.5.sp
            ),
            modifier = Modifier.padding(bottom = 6.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Bike A Cell
            SpecValueCell(
                value = valueA,
                isWinner = winnerIndex == 0,
                modifier = Modifier.weight(1f)
            )

            // Bike B Cell
            SpecValueCell(
                value = valueB,
                isWinner = winnerIndex == 1,
                modifier = Modifier.weight(1f)
            )

            // Bike C Cell (if 3-bike comparison)
            if (valueC != null) {
                SpecValueCell(
                    value = valueC,
                    isWinner = winnerIndex == 2,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun SpecValueCell(
    value: String,
    isWinner: Boolean,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(
                if (isWinner) GoldWinner.copy(alpha = 0.12f) else Slate850,
                RoundedCornerShape(8.dp)
            )
            .border(
                width = if (isWinner) 1.dp else 0.5.dp,
                color = if (isWinner) GoldWinner else Slate800,
                shape = RoundedCornerShape(8.dp)
            )
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontWeight = if (isWinner) FontWeight.Bold else FontWeight.Normal,
                    color = if (isWinner) GoldWinner else Color.White,
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center
                ),
                maxLines = 2
            )
            if (isWinner) {
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = "🏆", fontSize = 12.sp)
            }
        }
    }
}

@Composable
fun DetailSpecRow(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium.copy(color = Slate400),
            modifier = Modifier.weight(1.2f)
        )
        Text(
            text = if (value.isBlank()) "Not available" else value,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.SemiBold,
                color = if (value.isBlank()) Slate400 else Color.White,
                textAlign = TextAlign.End
            ),
            modifier = Modifier.weight(1.8f)
        )
    }
}
