package com.rork.grievai.ui.screens.admin

import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.rork.grievai.data.MockRepository
import com.rork.grievai.ui.components.ChartLegend
import com.rork.grievai.ui.components.CircularProgressStat
import com.rork.grievai.ui.components.DonutChart
import com.rork.grievai.ui.components.BarChart
import com.rork.grievai.ui.components.HorizontalBarList
import com.rork.grievai.ui.components.StatCard
import com.rork.grievai.ui.theme.AnalyticsGradient
import com.rork.grievai.ui.theme.CardAccentGradient
import com.rork.grievai.ui.theme.HeroGradient
import com.rork.grievai.ui.theme.PriorityCritical
import com.rork.grievai.ui.theme.PriorityHigh
import com.rork.grievai.ui.theme.PriorityLow
import com.rork.grievai.ui.theme.PriorityMedium

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminAnalyticsScreen() {
    val monthly = remember { MockRepository.getMonthlyComplaints() }
    val deptStats = remember { MockRepository.getDepartmentStats() }
    val priorityDist = remember { MockRepository.getPriorityDistribution() }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            androidx.compose.material3.TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.Analytics, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(22.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Analytics", fontWeight = FontWeight.Bold)
                    }
                },
                colors = androidx.compose.material3.TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
        ) {
            // Summary cards
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                StatCard("Total", "${monthly.sumOf { it.count }}", Icons.Filled.Assessment, CardAccentGradient, modifier = Modifier.weight(1f))
                StatCard("Resolved", "${monthly.sumOf { it.resolved }}", Icons.Filled.TrendingUp, com.rork.grievai.ui.theme.ResolvedGradient, modifier = Modifier.weight(1f))
            }
            Spacer(Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                StatCard("Resolution Rate", "82%", Icons.Filled.Schedule, AnalyticsGradient, modifier = Modifier.weight(1f), subtitle="this semester")
                StatCard("Avg Time", "3.2d", Icons.Filled.Build, Brush.linearGradient(listOf(com.rork.grievai.ui.theme.Amber600, com.rork.grievai.ui.theme.Amber400)), modifier = Modifier.weight(1f), subtitle="per complaint")
            }

            Spacer(Modifier.height(20.dp))

            // Monthly bar chart
            AnalyticsCard(title = "Monthly Complaints", subtitle = "Total vs resolved over time") {
                BarChart(data = monthly)
                Spacer(Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    LegendDot(MaterialTheme.colorScheme.tertiary, "Resolved")
                    Spacer(Modifier.width(12.dp))
                    LegendDot(MaterialTheme.colorScheme.primary.copy(alpha = 0.3f), "Total")
                }
            }

            Spacer(Modifier.height(16.dp))

            // Priority distribution donut
            AnalyticsCard(title = "Priority Distribution", subtitle = "AI-predicted priority levels") {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    DonutChart(distribution = priorityDist)
                    Spacer(Modifier.width(20.dp))
                    ChartLegend(
                        items = listOf(
                            PriorityCritical to "Critical",
                            PriorityHigh to "High",
                            PriorityMedium to "Medium",
                            PriorityLow to "Low"
                        )
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            // Department-wise
            AnalyticsCard(title = "Department-wise Complaints", subtitle = "Total complaints per department") {
                HorizontalBarList(
                    items = deptStats.map { it.name to it.total }
                )
            }

            Spacer(Modifier.height(16.dp))

            // Resolution time by department
            AnalyticsCard(title = "Resolution Performance", subtitle = "Resolved vs pending per department") {
                deptStats.forEach { stat ->
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(stat.name, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface, modifier = Modifier.weight(1f))
                        Text("${stat.resolved}/${stat.total}", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.SemiBold)
                        Spacer(Modifier.width(8.dp))
                        val pct = if (stat.total > 0) stat.resolved.toFloat() / stat.total else 0f
                        Box(
                            modifier = Modifier
                                .width(60.dp)
                                .height(6.dp)
                                .clip(RoundedCornerShape(50))
                                .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                        ) {
                            Box(
                                modifier = Modifier
                                    .width((60 * pct).dp)
                                    .height(6.dp)
                                    .clip(RoundedCornerShape(50))
                                    .background(com.rork.grievai.ui.theme.PriorityLow)
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun AnalyticsCard(
    title: String,
    subtitle: String,
    content: @Composable () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(18.dp)
    ) {
        Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
        Spacer(Modifier.height(2.dp))
        Text(subtitle, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(16.dp))
        content()
    }
}

@Composable
private fun LegendDot(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(color)
        )
        Spacer(Modifier.width(5.dp))
        Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
