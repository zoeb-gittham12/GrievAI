package com.rork.grievai.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material.icons.filled.Pending
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.rork.grievai.data.ComplaintPriority
import com.rork.grievai.data.ComplaintStatus
import com.rork.grievai.ui.theme.AssignedGradient
import com.rork.grievai.ui.theme.CriticalGradient
import com.rork.grievai.ui.theme.HighGradient
import com.rork.grievai.ui.theme.LowGradient
import com.rork.grievai.ui.theme.MediumGradient
import com.rork.grievai.ui.theme.PriorityCritical
import com.rork.grievai.ui.theme.PriorityHigh
import com.rork.grievai.ui.theme.PriorityLow
import com.rork.grievai.ui.theme.PriorityMedium
import com.rork.grievai.ui.theme.ProgressGradient
import com.rork.grievai.ui.theme.ResolvedGradient
import com.rork.grievai.ui.theme.ReviewGradient
import com.rork.grievai.ui.theme.StatusAssigned
import com.rork.grievai.ui.theme.StatusInProgress
import com.rork.grievai.ui.theme.StatusResolved
import com.rork.grievai.ui.theme.StatusReview
import com.rork.grievai.ui.theme.StatusSubmitted
import com.rork.grievai.ui.theme.SubmittedGradient

@Composable
fun StatusChip(status: ComplaintStatus, modifier: Modifier = Modifier) {
    val (color, bg) = when (status) {
        ComplaintStatus.SUBMITTED -> StatusSubmitted to StatusSubmitted.copy(alpha = 0.15f)
        ComplaintStatus.UNDER_REVIEW -> StatusReview to StatusReview.copy(alpha = 0.15f)
        ComplaintStatus.ASSIGNED -> StatusAssigned to StatusAssigned.copy(alpha = 0.15f)
        ComplaintStatus.IN_PROGRESS -> StatusInProgress to StatusInProgress.copy(alpha = 0.15f)
        ComplaintStatus.RESOLVED -> StatusResolved to StatusResolved.copy(alpha = 0.15f)
    }
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(50))
            .background(bg)
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(6.dp)
                .clip(CircleShape)
                .background(color)
        )
        Spacer(Modifier.width(6.dp))
        Text(
            text = status.label,
            style = MaterialTheme.typography.labelMedium,
            color = color,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
fun PriorityBadge(priority: ComplaintPriority, modifier: Modifier = Modifier) {
    val (color, icon) = when (priority) {
        ComplaintPriority.LOW -> PriorityLow to Icons.Filled.Check
        ComplaintPriority.MEDIUM -> PriorityMedium to Icons.Filled.Speed
        ComplaintPriority.HIGH -> PriorityHigh to Icons.Filled.Build
        ComplaintPriority.CRITICAL -> PriorityCritical to Icons.Filled.RocketLaunch
    }
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(50))
            .background(color.copy(alpha = 0.15f))
            .padding(horizontal = 10.dp, vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(13.dp)
        )
        Spacer(Modifier.width(4.dp))
        Text(
            text = priority.label,
            style = MaterialTheme.typography.labelSmall,
            color = color,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun PriorityPill(priority: ComplaintPriority, modifier: Modifier = Modifier) {
    val gradient: Brush = when (priority) {
        ComplaintPriority.LOW -> LowGradient
        ComplaintPriority.MEDIUM -> MediumGradient
        ComplaintPriority.HIGH -> HighGradient
        ComplaintPriority.CRITICAL -> CriticalGradient
    }
    val icon: ImageVector = when (priority) {
        ComplaintPriority.LOW -> Icons.Filled.Check
        ComplaintPriority.MEDIUM -> Icons.Filled.Speed
        ComplaintPriority.HIGH -> Icons.Filled.Build
        ComplaintPriority.CRITICAL -> Icons.Filled.RocketLaunch
    }
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(50))
            .background(gradient)
            .padding(horizontal = 12.dp, vertical = 7.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(14.dp)
        )
        Spacer(Modifier.width(5.dp))
        Text(
            text = priority.label,
            style = MaterialTheme.typography.labelMedium,
            color = Color.White,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun StatusGradientBadge(status: ComplaintStatus, modifier: Modifier = Modifier) {
    val gradient: Brush = when (status) {
        ComplaintStatus.SUBMITTED -> SubmittedGradient
        ComplaintStatus.UNDER_REVIEW -> ReviewGradient
        ComplaintStatus.ASSIGNED -> AssignedGradient
        ComplaintStatus.IN_PROGRESS -> ProgressGradient
        ComplaintStatus.RESOLVED -> ResolvedGradient
    }
    val icon: ImageVector = when (status) {
        ComplaintStatus.SUBMITTED -> Icons.Filled.Pending
        ComplaintStatus.UNDER_REVIEW -> Icons.Filled.Forum
        ComplaintStatus.ASSIGNED -> Icons.Filled.Assignment
        ComplaintStatus.IN_PROGRESS -> Icons.Filled.PlayArrow
        ComplaintStatus.RESOLVED -> Icons.Filled.Verified
    }
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(50))
            .background(gradient)
            .padding(horizontal = 12.dp, vertical = 7.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(14.dp)
        )
        Spacer(Modifier.width(5.dp))
        Text(
            text = status.label,
            style = MaterialTheme.typography.labelMedium,
            color = Color.White,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun ConfidenceBar(score: Float, modifier: Modifier = Modifier) {
    val animatedProgress by animateFloatAsState(
        targetValue = score,
        animationSpec = tween(900),
        label = "confidence"
    )
    val color by animateColorAsState(
        targetValue = when {
            score >= 0.9f -> Color(0xFF4CAF50)
            score >= 0.75f -> Color(0xFF2196F3)
            else -> Color(0xFFFFA726)
        },
        animationSpec = tween(600),
        label = "confidenceColor"
    )
    Column(modifier = modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(50))
                .background(color.copy(alpha = 0.15f))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(animatedProgress)
                    .height(6.dp)
                    .clip(RoundedCornerShape(50))
                    .background(color)
            )
        }
    }
}
