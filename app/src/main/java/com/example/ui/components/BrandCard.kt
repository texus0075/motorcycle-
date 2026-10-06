package com.example.ui.components

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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.TwoWheeler
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
import com.example.data.models.BrandEntity
import com.example.ui.theme.CyanNeon
import com.example.ui.theme.Slate400
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate800
import com.example.ui.theme.Slate850
import com.example.ui.theme.Slate950

@Composable
fun BrandCard(
    brand: BrandEntity,
    modelCount: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Slate850,
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, Slate800, RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .testTag("brand_card_${brand.id}")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Logo Image with Fallback Monogram
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(Slate950)
                            .border(1.5.dp, Color(brand.accentColorHex), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        AsyncImage(
                            model = brand.logoUrl,
                            contentDescription = brand.name,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.size(48.dp)
                        )
                        // Fallback text monogram if image doesn't load immediately
                        Text(
                            text = brand.name.take(2).uppercase(),
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Black,
                                color = Color(brand.accentColorHex)
                            )
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = brand.name,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = Color.White
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "${brand.country} • Est. ${brand.foundedYear}",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = Slate400,
                                    fontSize = 11.sp
                                )
                            )
                        }
                    }
                }

                // Model Count Badge
                Box(
                    modifier = Modifier
                        .background(Slate950, RoundedCornerShape(8.dp))
                        .border(0.5.dp, Slate700, RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.TwoWheeler,
                            contentDescription = null,
                            tint = CyanNeon,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "$modelCount models",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Short History / description
            Text(
                text = brand.history,
                style = MaterialTheme.typography.bodySmall.copy(color = Slate400),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Footer link
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "View Lineup",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = CyanNeon,
                        fontWeight = FontWeight.Bold
                    )
                )
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    tint = CyanNeon,
                    modifier = Modifier.size(12.dp)
                )
            }
        }
    }
}
