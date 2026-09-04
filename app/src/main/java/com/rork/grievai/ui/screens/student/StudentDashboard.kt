package com.rork.grievai.ui.screens.student

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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Public
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
import com.rork.grievai.data.ComplaintStatus
import com.rork.grievai.data.MockRepository
import com.rork.grievai.data.User
import com.rork.grievai.ui.components.ComplaintCard
import com.rork.grievai.ui.components.SkeletonCard
import com.rork.grievai.ui.components.SkeletonStatRow
import com.rork.grievai.ui.components.StatCard
import com.rork.grievai.ui.theme.CardAccentGradient
import com.rork.grievai.ui.theme.CriticalGradient
import com.rork.grievai.ui.theme.HeroGradient
import com.rork.grievai.ui.theme.Indigo700
import com.rork.grievai.ui.theme.ResolvedGradient

@Composable
fun StudentDashboard(
    user: User,
    onComplaintClick: (String) -> Unit,
    onSubmitComplaint: () -> Unit,
    onPublicFeed: () -> Unit,
    onSeeAllComplaints: () -> Unit
) {
    var complaints by remember { mutableStateOf<List<Complaint>?>(null) }
    LaunchedEffect(Unit) { complaints = MockRepository.getStudentComplaints(user.id) }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        floatingActionButton = {
            androidx.compose.material3.FloatingActionButton(
                onClick = onSubmitComplaint,
                containerColor = MaterialTheme.colorScheme.primary,
                shape = RoundedCornerShape(18.dp)
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Submit Complaint", tint = Color.White)
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            // Hero welcome card
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
                                .background(Color.White.copy(alpha = 0.25f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                user.name.take(1).uppercase(),
                                color = Color.White,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                "Welcome back,",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.White.copy(alpha = 0.85f)
                            )
                            Text(
                                user.name,
                                style = MaterialTheme.typography.headlineMedium,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
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
                    Spacer(Modifier.height(20.dp))
                    // Inline stat summary
                    val pending = complaints?.count { it.status != ComplaintStatus.RESOLVED } ?: 0
                    val resolved = complaints?.count { it.status == ComplaintStatus.RESOLVED } ?: 0
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color.White.copy(alpha = 0.15f))
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        MiniStat("Total", "${complaints?.size ?: 0}", Color.White)
                        Box(modifier = Modifier.size(1.dp).height(30.dp).background(Color.White.copy(alpha = 0.3f)))
                        MiniStat("Pending", "$pending", Color(0xFFFFD54F))
                        Box(modifier = Modifier.size(1.dp).height(30.dp).background(Color.White.copy(alpha = 0.3f)))
                        MiniStat("Resolved", "$resolved", Color(0xFF81C784))
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // Stats grid
            if (complaints == null) {
                SkeletonStatRow()
                Spacer(Modifier.height(12.dp))
                SkeletonStatRow()
            } else {
                val pending = complaints!!.count { it.status != ComplaintStatus.RESOLVED }
                val resolved = complaints!!.count { it.status == ComplaintStatus.RESOLVED }
                val critical = complaints!!.count { it.priority == com.rork.grievai.data.ComplaintPriority.CRITICAL }
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatCard(
                        title = "Pending", value = "$pending",
                        icon = Icons.Filled.HourglassEmpty,
                        gradient = Brush.linearGradient(listOf(com.rork.grievai.ui.theme.Amber600, com.rork.grievai.ui.theme.Amber400)),
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        title = "Resolved", value = "$resolved",
                        icon = Icons.Filled.CheckCircle,
                        gradient = ResolvedGradient,
                        modifier = Modifier.weight(1f)
                    )
                }
                Spacer(Modifier.height(12.dp))
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatCard(
                        title = "Critical", value = "$critical",
                        icon = Icons.Filled.Assignment,
                        gradient = CriticalGradient,
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        title = "AI Analyses", value = "${complaints!!.size}",
                        icon = Icons.Filled.AutoAwesome,
                        gradient = CardAccentGradient,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(Modifier.height(20.dp))

            // Quick actions
            Text(
                "Quick Actions",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Spacer(Modifier.height(10.dp))
            Row(
                modifier = Modifier.padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                QuickActionCard(
                    icon = Icons.Filled.Add, label = "New\nComplaint",
                    gradient = CardAccentGradient,
                    onClick = onSubmitComplaint,
                    modifier = Modifier.weight(1f)
                )
                QuickActionCard(
                    icon = Icons.Filled.Public, label = "Public\nFeed",
                    gradient = Brush.linearGradient(listOf(com.rork.grievai.ui.theme.Blue500, com.rork.grievai.ui.theme.Blue400)),
                    onClick = onPublicFeed,
                    modifier = Modifier.weight(1f)
                )
                QuickActionCard(
                    icon = Icons.Filled.Search, label = "Track\nStatus",
                    gradient = Brush.linearGradient(listOf(com.rork.grievai.ui.theme.StatusReview, com.rork.grievai.ui.theme.Indigo400)),
                    onClick = onSeeAllComplaints,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(Modifier.height(20.dp))

            // Recent complaints
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Recently Submitted",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    "See All",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(4.dp)
                )
            }
            Spacer(Modifier.height(8.dp))
            if (complaints == null) {
                SkeletonCard()
            } else {
                complaints!!.take(3).forEach { c ->
                    ComplaintCard(
                        complaint = c,
                        onClick = { onComplaintClick(c.id) },
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                    )
                }
                if (complaints!!.isEmpty()) {
                    com.rork.grievai.ui.components.EmptyState(
                        icon = Icons.Filled.Assignment,
                        title = "No complaints yet",
                        message = "Tap the + button to submit your first grievance."
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
private fun QuickActionCard(
    icon: ImageVector,
    label: String,
    gradient: Brush,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
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
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = Color.White,
            fontWeight = FontWeight.SemiBold,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
    }
}


