package com.rork.grievai.ui.screens.admin

import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.rork.grievai.data.Complaint
import com.rork.grievai.data.ComplaintPriority
import com.rork.grievai.data.ComplaintStatus
import com.rork.grievai.data.MockRepository
import com.rork.grievai.data.User
import com.rork.grievai.ui.components.ComplaintCard
import com.rork.grievai.ui.components.SkeletonCard
import com.rork.grievai.ui.components.SkeletonStatRow
import com.rork.grievai.ui.components.StatCard
import com.rork.grievai.ui.theme.AnalyticsGradient
import com.rork.grievai.ui.theme.Amber400
import com.rork.grievai.ui.theme.Amber600
import com.rork.grievai.ui.theme.CardAccentGradient
import com.rork.grievai.ui.theme.CriticalGradient
import com.rork.grievai.ui.theme.HeroGradient
import com.rork.grievai.ui.theme.Indigo700

@Composable
fun AdminDashboard(
    user: User,
    onComplaintClick: (String) -> Unit,
    onSeeAllComplaints: () -> Unit,
    onGoAnalytics: () -> Unit,
    onGoAI: () -> Unit,
    onProfileClick: () -> Unit
) {
    var complaints by remember { mutableStateOf<List<Complaint>?>(null) }
    LaunchedEffect(Unit) { complaints = MockRepository.getAllComplaints() }

    val total = complaints?.size ?: 0
    val pending = complaints?.count { it.status != ComplaintStatus.RESOLVED } ?: 0
    val resolved = complaints?.count { it.status == ComplaintStatus.RESOLVED } ?: 0
    val critical = complaints?.count { it.priority == ComplaintPriority.CRITICAL } ?: 0

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            // Hero
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(HeroGradient)
                    .padding(20.dp)
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.25f))
                                .clickable(onClick = onProfileClick),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Filled.AdminPanelSettings, contentDescription = "View profile", tint = Color.White, modifier = Modifier.size(26.dp))
                        }
                        Spacer(Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Admin Console", style = MaterialTheme.typography.bodyMedium, color = Color.White.copy(alpha = 0.85f))
                            Text(user.name, style = MaterialTheme.typography.headlineMedium, color = Color.White, fontWeight = FontWeight.Bold)
                        }
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Filled.Notifications, contentDescription = null, tint = Color.White, modifier = Modifier.size(22.dp))
                        }
                    }
                    Spacer(Modifier.height(16.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color.White.copy(alpha = 0.15f))
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        MiniStat("Total", "$total", Color.White)
                        Box(modifier = Modifier.size(1.dp).height(30.dp).background(Color.White.copy(alpha = 0.3f)))
                        MiniStat("Pending", "$pending", Amber400)
                        Box(modifier = Modifier.size(1.dp).height(30.dp).background(Color.White.copy(alpha = 0.3f)))
                        MiniStat("Resolved", "$resolved", Color(0xFF81C784))
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            if (complaints == null) {
                SkeletonStatRow()
                Spacer(Modifier.height(12.dp))
                SkeletonStatRow()
            } else {
                Row(modifier = Modifier.padding(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    StatCard("Total Complaints", "$total", Icons.Filled.Assignment, CardAccentGradient, modifier = Modifier.weight(1f))
                    StatCard("Pending", "$pending", Icons.Filled.Build, Brush.linearGradient(listOf(Amber600, Amber400)), modifier = Modifier.weight(1f))
                }
                Spacer(Modifier.height(12.dp))
                Row(modifier = Modifier.padding(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    StatCard("Resolved", "$resolved", Icons.Filled.CheckCircle, com.rork.grievai.ui.theme.ResolvedGradient, modifier = Modifier.weight(1f))
                    StatCard("Critical", "$critical", Icons.Filled.LocalFireDepartment, CriticalGradient, modifier = Modifier.weight(1f))
                }
            }

            Spacer(Modifier.height(20.dp))

            // Quick actions
            Text("Quick Actions", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface, modifier = Modifier.padding(horizontal = 16.dp))
            Spacer(Modifier.height(10.dp))
            Row(modifier = Modifier.padding(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                AdminQuickAction("Manage\nComplaints", Icons.Filled.Assignment, CardAccentGradient, onSeeAllComplaints, Modifier.weight(1f))
                AdminQuickAction("Analytics", Icons.Filled.TrendingUp, AnalyticsGradient, onGoAnalytics, Modifier.weight(1f))
                AdminQuickAction("AI\nInsights", Icons.Filled.AutoAwesome, com.rork.grievai.ui.theme.AIGradient, onGoAI, Modifier.weight(1f))
            }

            Spacer(Modifier.height(20.dp))

            // Recent activity
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Recent Activity", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface, modifier = Modifier.weight(1f))
                Text("See All", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.SemiBold, modifier = Modifier.clickable(onClick = onSeeAllComplaints).padding(4.dp))
            }
            Spacer(Modifier.height(8.dp))
            if (complaints == null) {
                SkeletonCard()
            } else {
                complaints!!.take(4).forEach { c ->
                    ComplaintCard(
                        complaint = c,
                        onClick = { onComplaintClick(c.id) },
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                    )
                }
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun MiniStat(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, style = MaterialTheme.typography.titleLarge, color = color, fontWeight = FontWeight.Bold)
        Text(label, style = MaterialTheme.typography.labelSmall, color = Color.White.copy(alpha = 0.8f))
    }
}

@Composable
private fun AdminQuickAction(label: String, icon: ImageVector, gradient: Brush, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(18.dp))
            .background(gradient)
            .clickable(onClick = onClick)
            .padding(14.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color.White.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(22.dp))
        }
        Spacer(Modifier.height(8.dp))
        Text(label, style = MaterialTheme.typography.labelMedium, color = Color.White, fontWeight = FontWeight.SemiBold, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
    }
}