package com.rork.grievai.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.rork.grievai.data.MonthlyComplaints
import com.rork.grievai.data.PriorityDistribution
import com.rork.grievai.ui.theme.PriorityCritical
import com.rork.grievai.ui.theme.PriorityHigh
import com.rork.grievai.ui.theme.PriorityLow
import com.rork.grievai.ui.theme.PriorityMedium
import kotlin.math.cos
import kotlin.math.sin

/**
 * Animated vertical bar chart for monthly complaints.
 */
@Composable
fun BarChart(
    data: List<MonthlyComplaints>,
    modifier: Modifier = Modifier,
    barColor: Color = MaterialTheme.colorScheme.primary,
    resolvedColor: Color = MaterialTheme.colorScheme.tertiary
) {
    val maxCount = data.maxOfOrNull { it.count } ?: 1
    Row(
        modifier = modifier.fillMaxWidth().height(180.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.Bottom
    ) {
        data.forEach { entry ->
            val animatedHeight by animateFloatAsState(
                targetValue = entry.count.toFloat() / maxCount,
                animationSpec = tween(800),
                label = "bar_${entry.month}"
            )
            val animatedResolved by animateFloatAsState(
                targetValue = entry.resolved.toFloat() / maxCount,
                animationSpec = tween(800, delayMillis = 200),
                label = "resolved_${entry.month}"
            )
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Bottom
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height((160 * animatedHeight).dp)
                        .clip(RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp))
                        .background(barColor.copy(alpha = 0.25f))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height((160 * animatedResolved).dp)
                            .clip(RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp))
                            .background(resolvedColor)
                            .align(Alignment.BottomCenter)
                    )
                }
                Spacer(Modifier.height(6.dp))
                Text(
                    text = entry.month.take(3),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

/**
 * Donut chart for priority distribution.
 */
@Composable
fun DonutChart(
    distribution: PriorityDistribution,
    modifier: Modifier = Modifier,
    size: Int = 160
) {
    val total = distribution.low + distribution.medium + distribution.high + distribution.critical
    val slices = listOf(
        ChartSlice(PriorityCritical, distribution.critical.toFloat(), distribution.critical),
        ChartSlice(PriorityHigh, distribution.high.toFloat(), distribution.high),
        ChartSlice(PriorityMedium, distribution.medium.toFloat(), distribution.medium),
        ChartSlice(PriorityLow, distribution.low.toFloat(), distribution.low)
    )
    val animatedSweep by animateFloatAsState(
        targetValue = 360f,
        animationSpec = tween(1000),
        label = "donutSweep"
    )
    Box(
        modifier = modifier.size(size.dp),
        contentAlignment = Alignment.Center
    ) {
        CanvasPieChart(slices = slices, total = total.toFloat(), sweep = animatedSweep, size = size)
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "$total",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "Total",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

private data class ChartSlice(val color: Color, val value: Float, val count: Int)

@Composable
private fun CanvasPieChart(
    slices: List<ChartSlice>,
    total: Float,
    sweep: Float,
    size: Int
) {
    val center = size / 2f
    val radius = size / 2f
    val innerRadius = radius * 0.62f
    androidx.compose.foundation.Canvas(
        modifier = Modifier.size(size.dp)
    ) {
        var startAngle = -90f
        val drawnSweep = sweep
        slices.forEach { slice ->
            val sliceSweep = (slice.value / total) * 360f
            val actualSweep = minOf(sliceSweep, drawnSweep - (startAngle + 90f).coerceAtLeast(0f))
            if (actualSweep > 0f) {
                drawArc(
                    color = slice.color,
                    startAngle = startAngle,
                    sweepAngle = actualSweep,
                    useCenter = false,
                    topLeft = androidx.compose.ui.geometry.Offset(0f, 0f),
                    size = androidx.compose.ui.geometry.Size(size.toFloat(), size.toFloat()),
                    style = androidx.compose.ui.graphics.drawscope.Stroke(width = (radius - innerRadius), cap = androidx.compose.ui.graphics.StrokeCap.Butt)
                )
            }
            startAngle += sliceSweep
        }
    }
}

/**
 * Horizontal progress list for department-wise breakdown.
 */
@Composable
fun HorizontalBarList(
    items: List<Pair<String, Int>>,
    modifier: Modifier = Modifier,
    barColor: Color = MaterialTheme.colorScheme.primary
) {
    val max = items.maxOfOrNull { it.second } ?: 1
    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        items.forEach { (label, value) ->
            val animated by animateFloatAsState(
                targetValue = value.toFloat() / max,
                animationSpec = tween(700),
                label = "hbar_$label"
            )
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "$value",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Spacer(Modifier.height(4.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(50))
                        .background(barColor.copy(alpha = 0.12f))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(animated)
                            .height(8.dp)
                            .clip(RoundedCornerShape(50))
                            .background(barColor)
                    )
                }
            }
        }
    }
}

/**
 * Legend for the donut chart.
 */
@Composable
fun ChartLegend(
    items: List<Pair<Color, String>>,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(6.dp)) {
        items.forEach { (color, label) ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(color)
                )
                Spacer(Modifier.width(6.dp))
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
