package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CompareArrows
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.models.MotorcycleEntity
import com.example.ui.theme.CyanNeon
import com.example.ui.theme.Slate400
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate850
import com.example.ui.theme.Slate900
import com.example.ui.theme.Slate950

@Composable
fun ComparisonSlotBar(
    slotBikes: List<MotorcycleEntity>,
    onRemoveBike: (String) -> Unit,
    onCompareClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = slotBikes.isNotEmpty(),
        enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
        exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
        modifier = modifier
    ) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = Slate900.copy(alpha = 0.95f),
            shadowElevation = 12.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp)
                .border(1.5.dp, CyanNeon.copy(alpha = 0.7f), RoundedCornerShape(20.dp))
                .testTag("comparison_slot_bar")
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Slots (1, 2, or 3 bikes)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    slotBikes.forEachIndexed { index, bike ->
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(Slate950)
                                .border(1.dp, CyanNeon, RoundedCornerShape(10.dp))
                        ) {
                            AsyncImage(
                                model = bike.heroImageUrl,
                                contentDescription = bike.modelName,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.size(44.dp)
                            )
                            // Remove Button 'x'
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .size(16.dp)
                                    .background(Color.Black.copy(alpha = 0.8f), CircleShape)
                                    .clickable { onRemoveBike(bike.id) }
                                    .testTag("remove_compare_${bike.id}"),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Close,
                                    contentDescription = "Remove",
                                    tint = Color.White,
                                    modifier = Modifier.size(10.dp)
                                )
                            }
                        }

                        if (index < slotBikes.size - 1) {
                            Text(
                                text = "VS",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Black,
                                    color = CyanNeon,
                                    fontSize = 10.sp
                                )
                            )
                        }
                    }

                    // Empty placeholder slot if < 3
                    if (slotBikes.size < 3) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(Slate850)
                                .border(1.dp, Slate700, RoundedCornerShape(10.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "+${3 - slotBikes.size}",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = Slate400,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Compare Action CTA
                Button(
                    onClick = onCompareClick,
                    enabled = slotBikes.size >= 2,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = CyanNeon,
                        contentColor = Slate950,
                        disabledContainerColor = Slate700,
                        disabledContentColor = Slate400
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.testTag("launch_compare_btn")
                ) {
                    Icon(
                        imageVector = Icons.Filled.CompareArrows,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (slotBikes.size >= 2) "Compare (${slotBikes.size})" else "Select 2+",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }
        }
    }
}
