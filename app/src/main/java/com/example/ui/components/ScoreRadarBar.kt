package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.CategoryScore
import com.example.ui.theme.CyanNeon
import com.example.ui.theme.GoldWinner
import com.example.ui.theme.Slate400
import com.example.ui.theme.Slate800
import com.example.ui.theme.Slate850
import com.example.ui.theme.Slate950

@Composable
fun ScoreRow(
    categoryScore: CategoryScore,
    nameA: String,
    nameB: String,
    nameC: String? = null,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Slate950, RoundedCornerShape(12.dp))
            .border(1.dp, Slate800, RoundedCornerShape(12.dp))
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = categoryScore.categoryName,
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = Color.White
            )

            val leaderName = when (categoryScore.leaderIndex) {
                0 -> nameA
                1 -> nameB
                else -> nameC ?: nameA
            }
            Text(
                text = "Leader: $leaderName 🏆",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = GoldWinner,
                    fontSize = 11.sp
                )
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Progress Bar A
        SingleScoreBar(
            name = nameA,
            score = categoryScore.scoreA,
            isLeader = categoryScore.leaderIndex == 0,
            barColor = CyanNeon
        )

        Spacer(modifier = Modifier.height(6.dp))

        // Progress Bar B
        SingleScoreBar(
            name = nameB,
            score = categoryScore.scoreB,
            isLeader = categoryScore.leaderIndex == 1,
            barColor = Color(0xFFFF6600) // High-contrast racing orange
        )

        // Progress Bar C (if 3 bikes)
        if (nameC != null && categoryScore.scoreC != null) {
            Spacer(modifier = Modifier.height(6.dp))
            SingleScoreBar(
                name = nameC,
                score = categoryScore.scoreC,
                isLeader = categoryScore.leaderIndex == 2,
                barColor = Color(0xFF49C300) // Lime racing green
            )
        }
    }
}

@Composable
fun SingleScoreBar(
    name: String,
    score: Int,
    isLeader: Boolean,
    barColor: Color
) {
    val progress by animateFloatAsState(
        targetValue = (score / 100f).coerceIn(0f, 1f),
        animationSpec = tween(durationMillis = 600),
        label = "scoreProgress"
    )

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = name,
            style = MaterialTheme.typography.bodySmall.copy(
                fontWeight = if (isLeader) FontWeight.Bold else FontWeight.Normal,
                color = if (isLeader) Color.White else Slate400,
                fontSize = 11.sp
            ),
            modifier = Modifier.width(90.dp),
            maxLines = 1
        )

        Box(
            modifier = Modifier
                .weight(1f)
                .height(10.dp)
                .clip(RoundedCornerShape(5.dp))
                .background(Slate850)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(fraction = progress)
                    .clip(RoundedCornerShape(5.dp))
                    .background(if (isLeader) GoldWinner else barColor)
            )
        }

        Spacer(modifier = Modifier.width(10.dp))

        Text(
            text = "$score",
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                color = if (isLeader) GoldWinner else Color.White
            ),
            modifier = Modifier.width(26.dp)
        )
    }
}
