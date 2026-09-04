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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.rork.grievai.data.Complaint
import com.rork.grievai.data.Departments
import com.rork.grievai.data.MockRepository
import com.rork.grievai.ui.components.ComplaintCard
import com.rork.grievai.ui.components.EmptyState
import com.rork.grievai.ui.components.SkeletonCard

private val sortOptions = listOf("Popularity", "Newest", "Most Supported")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PublicFeedScreen(
    onComplaintClick: (String) -> Unit
) {
    var complaints by remember { mutableStateOf<List<Complaint>?>(null) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedDept by remember { mutableStateOf("All") }
    var sortBy by remember { mutableStateOf("Popularity") }
    var sortMenuOpen by remember { mutableStateOf(false) }
    var refreshKey by remember { mutableStateOf(0) }

    LaunchedEffect(Unit, refreshKey) {
        complaints = MockRepository.getPublicComplaints()
    }

    val filtered = complaints?.filter { c ->
        (selectedDept == "All" || c.department == selectedDept) &&
        (searchQuery.isBlank() || c.title.contains(searchQuery, true) || c.description.contains(searchQuery, true))
    }?.let { list ->
        when (sortBy) {
            "Popularity" -> list.sortedByDescending { it.upvotes }
            "Newest" -> list.sortedByDescending { it.createdAt }
            "Most Supported" -> list.sortedByDescending { it.supporters }
            else -> list
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            androidx.compose.material3.TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.Public, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(22.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Public Feed", fontWeight = FontWeight.Bold)
                    }
                },
                colors = androidx.compose.material3.TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            // Search
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search public complaints...") },
                leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant) },
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp),
                shape = RoundedCornerShape(16.dp),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    focusedContainerColor = MaterialTheme.colorScheme.surface
                )
            )

            // Department filter chips (horizontal scroll)
            androidx.compose.foundation.lazy.LazyRow(
                modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    FilterChip(
                        selected = selectedDept == "All",
                        onClick = { selectedDept = "All" },
                        label = { Text("All") }
                    )
                }
                items(Departments.all) { dept ->
                    FilterChip(
                        selected = selectedDept == dept,
                        onClick = { selectedDept = dept },
                        label = { Text(dept) }
                    )
                }
            }

            // Sort row
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("${filtered?.size ?: 0} complaints", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.weight(1f))
                Box {
                    TextButton(onClick = { sortMenuOpen = true }) {
                        Icon(Icons.Filled.Sort, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(4.dp))
                        Text(sortBy, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold)
                    }
                    DropdownMenu(expanded = sortMenuOpen, onDismissRequest = { sortMenuOpen = false }) {
                        sortOptions.forEach { opt ->
                            DropdownMenuItem(
                                text = { Text(opt) },
                                leadingIcon = if (sortBy == opt) { { Icon(Icons.Filled.Check, contentDescription = null, modifier = Modifier.size(16.dp)) } } else null,
                                onClick = { sortBy = opt; sortMenuOpen = false }
                            )
                        }
                    }
                }
            }

            // List
            if (complaints == null) {
                LazyColumn { items(4) { SkeletonCard() } }
            } else if (filtered!!.isEmpty()) {
                EmptyState(
                    icon = Icons.Filled.Public,
                    title = "No public complaints",
                    message = "Be the first to post a public complaint on this topic."
                )
            } else {
                LazyColumn(
                    state = rememberLazyListState(),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(bottom = 24.dp)
                ) {
                    // Top supporter highlight
                    item {
                        val top = filtered.firstOrNull()
                        if (top != null && top.upvotes > 50) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 6.dp)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f))
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Filled.Whatshot, contentDescription = null, tint = MaterialTheme.colorScheme.secondary, modifier = Modifier.size(20.dp))
                                Spacer(Modifier.width(8.dp))
                                Text("Trending: ${top.title}", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSecondaryContainer, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
                                Text("${top.upvotes}", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.secondary, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                    items(filtered, key = { it.id }) { c ->
                        ComplaintCard(
                            complaint = c,
                            onClick = { onComplaintClick(c.id) },
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }
    }
}
