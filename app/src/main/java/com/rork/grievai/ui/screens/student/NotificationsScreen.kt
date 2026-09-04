package com.rork.grievai.ui.screens.student

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Comment
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.rork.grievai.data.MockRepository
import com.rork.grievai.data.NotificationItem
import com.rork.grievai.data.NotificationType
import com.rork.grievai.ui.components.SkeletonCard

private fun iconForType(type: NotificationType): ImageVector = when (type) {
    NotificationType.COMPLAINT_SUBMITTED -> Icons.Filled.Assignment
    NotificationType.PRIORITY_UPDATED -> Icons.Filled.Speed
    NotificationType.STATUS_CHANGED -> Icons.Filled.Campaign
    NotificationType.COMPLAINT_RESOLVED -> Icons.Filled.CheckCircle
    NotificationType.ADMIN_REPLY -> Icons.Filled.AdminPanelSettings
    NotificationType.NEW_COMMENT -> Icons.Filled.Comment
    NotificationType.NEW_SUPPORTER -> Icons.Filled.FavoriteBorder
}

private fun colorForType(type: NotificationType): Color = when (type) {
    NotificationType.COMPLAINT_SUBMITTED -> Color(0xFF2196F3)
    NotificationType.PRIORITY_UPDATED -> Color(0xFFFF9800)
    NotificationType.STATUS_CHANGED -> Color(0xFF7E57C2)
    NotificationType.COMPLAINT_RESOLVED -> Color(0xFF4CAF50)
    NotificationType.ADMIN_REPLY -> Color(0xFF3F51B5)
    NotificationType.NEW_COMMENT -> Color(0xFF26C6DA)
    NotificationType.NEW_SUPPORTER -> Color(0xFFEC407A)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsScreen(
    onComplaintClick: (String) -> Unit
) {
    var notifications by remember { mutableStateOf<List<NotificationItem>?>(null) }
    var revealed by remember { mutableStateOf(0) }
    LaunchedEffect(Unit) {
        notifications = MockRepository.getNotifications()
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            androidx.compose.material3.TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.Notifications, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(22.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Notifications", fontWeight = FontWeight.Bold)
                    }
                },
                colors = androidx.compose.material3.TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        }
    ) { padding ->
        if (notifications == null) {
            LazyColumn(modifier = Modifier.padding(padding)) {
                items(4) { SkeletonCard() }
            }
            return@Scaffold
        }

        val unread = notifications!!.count { !it.read }
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            if (unread > 0) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text("$unread unread notifications", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.Medium)
                    }
                }
            }
            items(notifications!!, key = { it.id }) { notif ->
                NotificationRow(notif = notif, onClick = {
                    notif.complaintId?.let(onComplaintClick)
                })
            }
        }
    }
}

@Composable
private fun NotificationRow(notif: NotificationItem, onClick: () -> Unit) {
    val color = colorForType(notif.type)
    val scale by animateFloatAsState(
        targetValue = if (notif.read) 1f else 1f,
        animationSpec = tween(200),
        label = "notif"
    )
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(MaterialTheme.colorScheme.surface)
            .clickable(onClick = onClick)
            .padding(14.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(color.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(iconForType(notif.type), contentDescription = null, tint = color, modifier = Modifier.size(22.dp))
        }
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(notif.title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface, modifier = Modifier.weight(1f))
                if (!notif.read) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(color)
                    )
                }
            }
            Spacer(Modifier.height(3.dp))
            Text(notif.message, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(4.dp))
            Text(formatRelative(notif.timestamp), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f))
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
