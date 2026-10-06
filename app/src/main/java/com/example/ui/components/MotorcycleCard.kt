package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CompareArrows
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.models.MotorcycleEntity
import com.example.ui.theme.AmberOrange
import com.example.ui.theme.CyanNeon
import com.example.ui.theme.Slate400
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate800
import com.example.ui.theme.Slate850
import com.example.ui.theme.Slate950
import com.example.ui.theme.TrackGreen

@Composable
fun MotorcycleCard(
    motorcycle: MotorcycleEntity,
    isInCompare: Boolean,
    isFavorite: Boolean,
    onCardClick: () -> Unit,
    onCompareToggle: () -> Unit,
    onFavoriteToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    val compareBorderColor by animateColorAsState(
        if (isInCompare) CyanNeon else Slate700,
        label = "compareBorder"
    )

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Slate850,
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, compareBorderColor, RoundedCornerShape(16.dp))
            .clickable(onClick = onCardClick)
            .testTag("motorcycle_card_${motorcycle.id}")
    ) {
        Column {
            // Image Header with overlay badges
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .background(Slate950)
            ) {
                AsyncImage(
                    model = motorcycle.heroImageUrl,
                    contentDescription = motorcycle.modelName,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // Gradient scrim at bottom
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.8f)),
                                startY = 100f
                            )
                        )
                )

                // Top Badges: Category & Favorite Button
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .background(Slate950.copy(alpha = 0.85f), RoundedCornerShape(8.dp))
                            .border(0.8.dp, Slate700, RoundedCornerShape(8.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = motorcycle.category.uppercase(),
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = CyanNeon,
                                fontSize = 10.sp,
                                letterSpacing = 0.5.sp
                            )
                        )
                    }

                    // Favorite Button
                    IconButton(
                        onClick = onFavoriteToggle,
                        modifier = Modifier
                            .size(34.dp)
                            .background(Slate950.copy(alpha = 0.75f), CircleShape)
                            .testTag("fav_btn_${motorcycle.id}")
                    ) {
                        Icon(
                            imageVector = if (isFavorite) Icons.Filled.Bookmark else Icons.Filled.BookmarkBorder,
                            contentDescription = "Favorite",
                            tint = if (isFavorite) AmberOrange else Slate400,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                // Brand badge pill bottom left
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(10.dp)
                        .background(Color.Black.copy(alpha = 0.85f), RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = motorcycle.brandId.uppercase(),
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    )
                }
            }

            // Body info
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = motorcycle.modelName,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = Color.White,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.Star,
                            contentDescription = "Rating",
                            tint = AmberOrange,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = "${motorcycle.rating}",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Key Specs Grid Row (HP, CC, Weight)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    SpecChip(label = "POWER", value = "${motorcycle.specs.maxPowerHp} HP")
                    SpecChip(label = "ENGINE", value = "${motorcycle.specs.displacementCc.toInt()} cc")
                    SpecChip(label = "WEIGHT", value = "${motorcycle.specs.kerbWeightKg.toInt()} kg")
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Price and Compare Button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "EST. PRICE",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, color = Slate400)
                        )
                        Text(
                            text = motorcycle.priceDisplay,
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = TrackGreen,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    // Add to Compare Action Button
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = if (isInCompare) CyanNeon else Slate800,
                        modifier = Modifier
                            .clickable(onClick = onCompareToggle)
                            .testTag("compare_toggle_${motorcycle.id}")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = if (isInCompare) Icons.Filled.Check else Icons.Filled.CompareArrows,
                                contentDescription = "Compare",
                                tint = if (isInCompare) Slate950 else CyanNeon,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (isInCompare) "Comparing" else "Compare",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = if (isInCompare) Slate950 else CyanNeon
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SpecChip(label: String, value: String) {
    Box(
        modifier = Modifier
            .background(Slate950, RoundedCornerShape(6.dp))
            .border(0.5.dp, Slate800, RoundedCornerShape(6.dp))
            .padding(horizontal = 8.dp, vertical = 5.dp)
    ) {
        Column(horizontalAlignment = Alignment.Start) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 9.sp,
                    color = Slate400,
                    fontWeight = FontWeight.SemiBold
                )
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontSize = 12.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            )
        }
    }
}
