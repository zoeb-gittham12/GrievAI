package com.rork.grievai.ui.screens.student

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.rork.grievai.data.Complaint
import com.rork.grievai.data.ComplaintStatus
import com.rork.grievai.data.MockRepository
import com.rork.grievai.data.User
import com.rork.grievai.ui.components.ComplaintCard
import com.rork.grievai.ui.components.EmptyState
import com.rork.grievai.ui.components.SkeletonCard

private val statusFilters = listOf("All", "Pending", "Resolved")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentComplaintsScreen(
    user: User,
    onComplaintClick: (String) -> Unit
) {
    var complaints by remember { mutableStateOf<List<Complaint>?>(null) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("All") }
    var refreshing by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        complaints = MockRepository.getStudentComplaints(user.id)
    }

    val filtered = complaints?.filter { c ->
        (selectedFilter == "All" ||
            (selectedFilter == "Pending" && c.status != ComplaintStatus.RESOLVED) ||
            (selectedFilter == "Resolved" && c.status == ComplaintStatus.RESOLVED)) &&
            (searchQuery.isBlank() || c.title.contains(searchQuery, true) || c.description.contains(searchQuery, true))
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            androidx.compose.material3.TopAppBar(
                title = { Text("My Complaints", fontWeight = FontWeight.Bold) },
                colors = androidx.compose.material3.TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding)
        ) {
            // Search bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search complaints...") },
                leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant) },
                trailingIcon = { Icon(Icons.Filled.Tune, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant) },
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp),
                shape = RoundedCornerShape(16.dp),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    focusedContainerColor = MaterialTheme.colorScheme.surface
                )
            )

            // Filter chips
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                statusFilters.forEach { filter ->
                    FilterChip(
                        selected = selectedFilter == filter,
                        onClick = { selectedFilter = filter },
                        label = { Text(filter) },
                        leadingIcon = if (selectedFilter == filter) { { Icon(Icons.Filled.Check, contentDescription = null, modifier = Modifier.size(16.dp)) } } else null
                    )
                }
            }

            // Stats summary
            if (complaints != null) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    MiniCount(label = "Total", value = complaints!!.size, color = MaterialTheme.colorScheme.primary)
                    MiniCount(label = "Pending", value = complaints!!.count { it.status != ComplaintStatus.RESOLVED }, color = MaterialTheme.colorScheme.secondary)
                    MiniCount(label = "Resolved", value = complaints!!.count { it.status == ComplaintStatus.RESOLVED }, color = com.rork.grievai.ui.theme.PriorityLow)
                }
            }

            // List
            if (complaints == null) {
                LazyColumn {
                    items(4) { SkeletonCard() }
                }
            } else if (filtered!!.isEmpty()) {
                EmptyState(
                    icon = Icons.Filled.Assignment,
                    title = "No complaints found",
                    message = if (searchQuery.isNotBlank()) "Try a different search term." else "Submit your first complaint to get started."
                )
            } else {
                LazyColumn(
                    state = rememberLazyListState(),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(bottom = 24.dp)
                ) {
                    items(filtered, key = { it.id }) { c ->
                        ComplaintCard(
                            complaint = c,
                            onClick = { onComplaintClick(c.id) },
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                            showAuthor = false
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MiniCount(label: String, value: Int, color: androidx.compose.ui.graphics.Color) {
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(color.copy(alpha = 0.1f))
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text("$value", style = MaterialTheme.typography.titleMedium, color = color, fontWeight = FontWeight.Bold)
        Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
