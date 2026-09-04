package com.rork.grievai.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInHorizontally
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material.icons.filled.Pending
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.rork.grievai.data.ComplaintStatus
import com.rork.grievai.data.TimelineEvent
import com.rork.grievai.ui.theme.PriorityCritical
import com.rork.grievai.ui.theme.PriorityHigh
import com.rork.grievai.ui.theme.PriorityLow
import com.rork.grievai.ui.theme.PriorityMedium
import com.rork.grievai.ui.theme.StatusAssigned
import com.rork.grievai.ui.theme.StatusInProgress
import com.rork.grievai.ui.theme.StatusResolved
import com.rork.grievai.ui.theme.StatusReview
import com.rork.grievai.ui.theme.StatusSubmitted

private fun statusIcon(status: ComplaintStatus): ImageVector = when (status) {
    ComplaintStatus.SUBMITTED -> Icons.Filled.Pending
    ComplaintStatus.UNDER_REVIEW -> Icons.Filled.Forum
    ComplaintStatus.ASSIGNED -> Icons.Filled.Assignment
    ComplaintStatus.IN_PROGRESS -> Icons.Filled.PlayArrow
    ComplaintStatus.RESOLVED -> Icons.Filled.Verified
}

private fun statusColor(status: ComplaintStatus): Color = when (status) {
    ComplaintStatus.SUBMITTED -> StatusSubmitted
    ComplaintStatus.UNDER_REVIEW -> StatusReview
    ComplaintStatus.ASSIGNED -> StatusAssigned
    ComplaintStatus.IN_PROGRESS -> StatusInProgress
    ComplaintStatus.RESOLVED -> StatusResolved
}

/**
 * Animated vertical timeline showing the status journey of a complaint.
 */
@Composable
fun ComplaintTimeline(
    events: List<TimelineEvent>,
    modifier: Modifier = Modifier
) {
    var revealed by remember { mutableStateOf(0) }
    LaunchedEffect(events.size) {
        events.forEachIndexed { index, _ ->
            kotlinx.coroutines.delay(180)
            revealed = index + 1
        }
    }

    Column(modifier = modifier.fillMaxWidth().padding(horizontal = 4.dp)) {
        events.forEachIndexed { index, event ->
            val isRevealed = index < revealed
            val isLast = index == events.lastIndex
            AnimatedVisibility(
                visible = isRevealed,
                enter = fadeIn(tween(400)) + slideInHorizontally(tween(400)) { -it / 4 }
            ) {
                TimelineRow(
                    event = event,
                    isLast = isLast,
                    index = index
                )
            }
        }
    }
}

@Composable
private fun TimelineRow(event: TimelineEvent, isLast: Boolean, index: Int) {
    val color = statusColor(event.status)
    Row(modifier = Modifier.fillMaxWidth()) {
        // Node + connector column
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(end = 14.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(
                        if (event.completed) color else color.copy(alpha = 0.25f)
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (event.completed) {
                    Icon(
                        statusIcon(event.status),
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(color)
                    )
                }
            }
            if (!isLast) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .height(46.dp)
                        .background(
                            if (event.completed) color.copy(alpha = 0.5f)
                            else color.copy(alpha = 0.18f)
                        )
                )
            }
        }

        // Content
        Column(modifier = Modifier.weight(1f).padding(top = 4.dp, bottom = if (isLast) 0.dp else 12.dp)) {
            Text(
                text = event.title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = if (event.completed) MaterialTheme.colorScheme.onSurface
                else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = event.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            if (event.timestamp.isNotBlank()) {
                Spacer(Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = event.by,
                        style = MaterialTheme.typography.labelSmall,
                        color = color,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = "· ${formatRelative(event.timestamp)}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "Pending",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

private fun formatRelative(iso: String): String {
    return try {
        val fmt = java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", java.util.Locale.US)
        val date = fmt.parse(iso) ?: return ""
        val diff = System.currentTimeMillis() - date.time
        val hours = diff / 3_600_000
        when {
            hours < 1 -> "just now"
            hours < 24 -> "${hours}h ago"
            hours < 720 -> "${hours / 24}d ago"
            else -> "${hours / 720}mo ago"
        }
    } catch (_: Exception) { "" }
}
