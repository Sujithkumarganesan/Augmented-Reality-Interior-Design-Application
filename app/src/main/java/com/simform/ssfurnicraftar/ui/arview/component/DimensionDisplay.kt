package com.simform.ssfurnicraftar.ui.arview.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Height
import androidx.compose.material.icons.filled.Straighten
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.simform.ssfurnicraftar.R

/**
 * Data class to hold furniture dimensions
 */
data class FurnitureDimensions(
    val width: Float,
    val height: Float,
    val depth: Float
)

/**
 * Display furniture dimensions in AR view
 */
@Composable
fun DimensionDisplay(
    modifier: Modifier = Modifier,
    dimensions: FurnitureDimensions?,
    visible: Boolean = true
) {
    if (visible && dimensions != null) {
        Column(
            modifier = modifier
                .background(
                    color = Color.Black.copy(alpha = 0.8f),
                    shape = RoundedCornerShape(12.dp)
                )
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = stringResource(R.string.furniture_dimensions),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            // Compact dimension display
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                DimensionItem(
                    label = stringResource(R.string.dimension_width),
                    value = dimensions.width
                )
                
                Text(
                    text = "×",
                    color = Color.White.copy(alpha = 0.6f),
                    style = MaterialTheme.typography.bodyMedium
                )
                
                DimensionItem(
                    label = stringResource(R.string.dimension_height),
                    value = dimensions.height
                )
                
                Text(
                    text = "×",
                    color = Color.White.copy(alpha = 0.6f),
                    style = MaterialTheme.typography.bodyMedium
                )
                
                DimensionItem(
                    label = stringResource(R.string.dimension_depth),
                    value = dimensions.depth
                )
            }
        }
    }
}

@Composable
private fun DimensionItem(
    label: String,
    value: Float,
    modifier: Modifier = Modifier
) {
    val formattedValue = remember(value) { formatDimensionCompact(value) }
    
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = Color.White.copy(alpha = 0.7f)
        )
        
        Text(
            text = formattedValue,
            style = MaterialTheme.typography.bodyMedium,
            color = Color.White,
            fontWeight = FontWeight.Bold
        )
    }
}

/**
 * Format dimension value in compact format for product specifications
 * Shows measurements in meters with centimeters for clarity
 * Example: 1.85m (185cm) - helps customers visualize actual size
 */
private fun formatDimensionCompact(meters: Float): String {
    return when {
        meters == 0f -> "N/A"
        meters < 0.01f -> {
            // Very small - show in millimeters
            val mm = (meters * 1000).toInt()
            "${mm}mm"
        }
        meters < 1f -> {
            // Less than 1 meter - show in centimeters
            val cm = (meters * 100).toInt()
            "${cm}cm"
        }
        else -> {
            // 1 meter or more - show both meters and centimeters
            val totalCm = (meters * 100).toInt()
            val m = totalCm / 100
            val cm = totalCm % 100
            if (cm == 0) {
                "${m}m"
            } else {
                String.format("%d.%02dm", m, cm)
            }
        }
    }
}

/**
 * Format dimension value to display in meters with cm precision
 * Shows actual real-world measurements that match physical tape measure
 */
private fun formatDimension(meters: Float): String {
    return when {
        meters == 0f -> "N/A"
        meters < 0.01f -> "${(meters * 1000).toInt()} mm"
        meters < 1f -> {
            val cm = (meters * 100).toInt()
            "$cm cm"
        }
        else -> {
            val m = (meters * 100).toInt() / 100f
            val cm = ((meters * 100) % 100).toInt()
            if (cm == 0) {
                String.format("%.0f m", m)
            } else {
                String.format("%.2f m (%d cm)", meters, (meters * 100).toInt())
            }
        }
    }
}
