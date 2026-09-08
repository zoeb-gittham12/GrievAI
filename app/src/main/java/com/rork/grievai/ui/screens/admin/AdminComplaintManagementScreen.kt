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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Reply
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
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
import com.rork.grievai.data.ComplaintPriority
import com.rork.grievai.data.ComplaintStatus
import com.rork.grievai.data.Departments
import com.rork.grievai.data.MockRepository
import com.rork.grievai.data.User
import com.rork.grievai.ui.components.ComplaintCard
import com.rork.grievai.ui.components.EmptyState
import com.rork.grievai.ui.components.PriorityPill
import com.rork.grievai.ui.components.SkeletonCard
import com.rork.grievai.ui.components.StatusGradientBadge

private val statusOptions = listOf("All", "Submitted", "Under Review", "Assigned", "In Progress", "Resolved")
private val priorityOptions = listOf("All", "Low", "Medium", "High", "Critical")
private val sortOptions = listOf("Newest", "Priority", "Most Supported")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminComplaintManagementScreen(
    user: User,
    onComplaintClick: (String) -> Unit
) {
    var complaints by remember { mutableStateOf<List<Complaint>?>(null) }
    var searchQuery by remember { mutableStateOf("") }
    var statusFilter by remember { mutableStateOf("All") }
    var priorityFilter by remember { mutableStateOf("All") }
    var sortBy by remember { mutableStateOf("Newest") }
    var sortMenuOpen by remember { mutableStateOf(false) }
    var refreshKey by remember { mutableStateOf(0) }
    var manageComplaintId by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit, refreshKey) { complaints = MockRepository.getAllComplaints() }

    // Re-resolve the sheet's complaint from the latest list on every refresh so
    // it reflects status/priority changes immediately instead of showing stale data.
    val manageComplaint = manageComplaintId?.let { id -> complaints?.find { it.id == id } }

    val filtered = complaints?.filter { c ->
        (statusFilter == "All" || c.status.label == statusFilter) &&
                (priorityFilter == "All" || c.priority.label == priorityFilter) &&
                (searchQuery.isBlank() || c.title.contains(searchQuery, true) || c.authorName.contains(searchQuery, true))
    }?.let { list ->
        when (sortBy) {
            "Newest" -> list.sortedByDescending { it.createdAt }
            "Priority" -> list.sortedByDescending { it.priority.ordinal }
            "Most Supported" -> list.sortedByDescending { it.supporters }
            else -> list
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text("All Complaints", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            // Search
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search by title or student...") },
                leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant) },
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp),
                shape = RoundedCornerShape(16.dp),
                singleLine = true,
                colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    focusedContainerColor = MaterialTheme.colorScheme.surface
                )
            )

            // Status filter row (horizontal scroll)
            androidx.compose.foundation.lazy.LazyRow(
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(statusOptions) { opt ->
                    FilterChip(
                        selected = statusFilter == opt,
                        onClick = { statusFilter = opt },
                        label = { Text(opt) }
                    )
                }
            }

            // Priority filter row + sort
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 2.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                androidx.compose.foundation.lazy.LazyRow(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(priorityOptions) { opt ->
                        FilterChip(
                            selected = priorityFilter == opt,
                            onClick = { priorityFilter = opt },
                            label = { Text(opt) }
                        )
                    }
                }
                Box {
                    TextButton(onClick = { sortMenuOpen = true }) {
                        Icon(Icons.Filled.FilterList, contentDescription = null, modifier = Modifier.size(16.dp))
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

            // Count
            Text(
                "${filtered?.size ?: 0} complaints",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
            )

            if (complaints == null) {
                LazyColumn { items(4) { SkeletonCard() } }
            } else if (filtered!!.isEmpty()) {
                EmptyState(
                    icon = Icons.Filled.Assignment,
                    title = "No complaints match",
                    message = "Adjust filters to see complaints."
                )
            } else {
                LazyColumn(
                    state = rememberLazyListState(),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(bottom = 24.dp)
                ) {
                    items(filtered, key = { it.id }) { c ->
                        ComplaintCard(
                            complaint = c,
                            onClick = { manageComplaintId = c.id },
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }
    }

    // Management sheet
    manageComplaint?.let { complaint ->
        AdminManageSheet(
            complaint = complaint,
            actorName = user.name,
            onDismiss = { manageComplaintId = null },
            onChanged = { refreshKey++ },
            onDeleted = { manageComplaintId = null; refreshKey++ },
            onViewDetail = {
                val id = complaint.id
                manageComplaintId = null
                onComplaintClick(id)
            }
        )
    }
}

/**
 * Status can only move forward one step at a time: SUBMITTED -> UNDER_REVIEW ->
 * ASSIGNED -> IN_PROGRESS -> RESOLVED. UNDER_REVIEW applies immediately with an
 * automatic AI-flagged note. ASSIGNED and IN_PROGRESS require a dialog for the
 * admin to enter the relevant details. RESOLVED is only reachable once every
 * prior stage is complete, and asks for a final confirmation since it's terminal.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AdminManageSheet(
    complaint: Complaint,
    actorName: String,
    onDismiss: () -> Unit,
    onChanged: () -> Unit,
    onDeleted: () -> Unit,
    onViewDetail: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var priorityMenuOpen by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showAssignDialog by remember { mutableStateOf(false) }
    var showInProgressDialog by remember { mutableStateOf(false) }
    var showResolveConfirm by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var successMessage by remember { mutableStateOf<String?>(null) }

    val nextStatus = ComplaintStatus.values().getOrNull(complaint.status.ordinal + 1)

    fun applyAdvance(department: String? = null, coordinator: String? = null, notes: String? = null) {
        when (val result = MockRepository.advanceComplaintStatus(
            complaintId = complaint.id,
            actorName = actorName,
            department = department,
            coordinator = coordinator,
            progressNotes = notes
        )) {
            is MockRepository.UpdateResult.Success -> {
                errorMessage = null
                successMessage = when (result.complaint.status) {
                    ComplaintStatus.UNDER_REVIEW -> "AI flagged this complaint for review."
                    ComplaintStatus.RESOLVED -> "Complaint marked as resolved."
                    else -> "Status updated to ${result.complaint.status.label}."
                }
                onChanged()
            }
            is MockRepository.UpdateResult.Error -> {
                successMessage = null
                errorMessage = result.message
            }
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(20.dp)) {
            Text("Manage Complaint", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
            Spacer(Modifier.height(4.dp))
            Text(complaint.title, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(16.dp))

            // Current state
            Row(verticalAlignment = Alignment.CenterVertically) {
                StatusGradientBadge(complaint.status)
                Spacer(Modifier.width(8.dp))
                PriorityPill(complaint.priority)
            }

            // Inline feedback banner (clear success/error handling, no dialog needed)
            errorMessage?.let { msg ->
                Spacer(Modifier.height(12.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.errorContainer)
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Filled.ErrorOutline, contentDescription = null, tint = MaterialTheme.colorScheme.onErrorContainer, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text(msg, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onErrorContainer)
                }
            }
            successMessage?.let { msg ->
                Spacer(Modifier.height(12.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Filled.AutoAwesome, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimaryContainer, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text(msg, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onPrimaryContainer, fontWeight = FontWeight.Medium)
                }
            }

            Spacer(Modifier.height(20.dp))

            // Sequential status advancement
            Text("Update Status", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
            Spacer(Modifier.height(4.dp))
            Text(
                "Complaints move through each stage in order — steps can't be skipped.",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(8.dp))

            if (nextStatus == null) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Filled.Verified, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("This complaint is fully resolved.", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
                }
            } else {
                Button(
                    onClick = {
                        errorMessage = null
                        successMessage = null
                        when (nextStatus) {
                            ComplaintStatus.UNDER_REVIEW -> applyAdvance() // no dialog — automatic AI-flagged note
                            ComplaintStatus.ASSIGNED -> showAssignDialog = true
                            ComplaintStatus.IN_PROGRESS -> showInProgressDialog = true
                            ComplaintStatus.RESOLVED -> showResolveConfirm = true
                            ComplaintStatus.SUBMITTED -> Unit // unreachable
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = if (nextStatus == ComplaintStatus.RESOLVED)
                        ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary)
                    else ButtonDefaults.buttonColors()
                ) {
                    Icon(
                        when (nextStatus) {
                            ComplaintStatus.UNDER_REVIEW -> Icons.Filled.Forum
                            ComplaintStatus.ASSIGNED -> Icons.Filled.Assignment
                            ComplaintStatus.IN_PROGRESS -> Icons.Filled.PlayArrow
                            ComplaintStatus.RESOLVED -> Icons.Filled.Verified
                            ComplaintStatus.SUBMITTED -> Icons.Filled.Check
                        },
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text("Mark as ${nextStatus.label}", fontWeight = FontWeight.SemiBold)
                }
            }

            Spacer(Modifier.height(14.dp))

            // Priority selector (independent of the status sequence)
            Text("Assign Priority", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
            Spacer(Modifier.height(8.dp))
            Box {
                Button(
                    onClick = { priorityMenuOpen = true },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors()
                ) {
                    Text(complaint.priority.label)
                    Spacer(Modifier.weight(1f))
                    Icon(Icons.Filled.Build, contentDescription = null, modifier = Modifier.size(16.dp))
                }
                DropdownMenu(expanded = priorityMenuOpen, onDismissRequest = { priorityMenuOpen = false }) {
                    ComplaintPriority.values().forEach { pr ->
                        DropdownMenuItem(
                            text = { Text(pr.label) },
                            onClick = {
                                priorityMenuOpen = false
                                when (val result = MockRepository.updateComplaintPriority(complaint.id, pr)) {
                                    is MockRepository.UpdateResult.Success -> {
                                        errorMessage = null
                                        successMessage = "Priority set to ${pr.label}."
                                        onChanged()
                                    }
                                    is MockRepository.UpdateResult.Error -> {
                                        successMessage = null
                                        errorMessage = result.message
                                    }
                                }
                            }
                        )
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            // Action buttons
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Button(
                    onClick = onViewDetail,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Filled.Forum, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("Reply")
                }
            }
            Spacer(Modifier.height(10.dp))
            OutlinedButton(
                onClick = { showDeleteDialog = true },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
            ) {
                Icon(Icons.Filled.Delete, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(6.dp))
                Text("Delete (Spam)")
            }
            Spacer(Modifier.height(20.dp))
        }
    }

    // ── Assign dialog: department + coordinator ─────────────────────────────
    if (showAssignDialog) {
        var department by remember { mutableStateOf(complaint.department) }
        var coordinator by remember { mutableStateOf("") }
        var deptMenuOpen by remember { mutableStateOf(false) }
        AlertDialog(
            onDismissRequest = { showAssignDialog = false },
            title = { Text("Assign Complaint", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text(
                        "Choose the department and coordinator responsible for handling this complaint.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.height(14.dp))
                    ExposedDropdownMenuBox(expanded = deptMenuOpen, onExpandedChange = { deptMenuOpen = it }) {
                        OutlinedTextField(
                            value = department,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Department") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = deptMenuOpen) },
                            modifier = Modifier.fillMaxWidth().menuAnchor(),
                            singleLine = true
                        )
                        DropdownMenu(expanded = deptMenuOpen, onDismissRequest = { deptMenuOpen = false }) {
                            Departments.all.forEach { dept ->
                                DropdownMenuItem(text = { Text(dept) }, onClick = { department = dept; deptMenuOpen = false })
                            }
                        }
                    }
                    Spacer(Modifier.height(10.dp))
                    OutlinedTextField(
                        value = coordinator,
                        onValueChange = { coordinator = it },
                        label = { Text("Coordinator Name") },
                        placeholder = { Text("e.g. Dr. Rajesh Kumar") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                TextButton(
                    enabled = department.isNotBlank() && coordinator.isNotBlank(),
                    onClick = {
                        showAssignDialog = false
                        applyAdvance(department = department, coordinator = coordinator)
                    }
                ) { Text("Assign", fontWeight = FontWeight.Bold) }
            },
            dismissButton = { TextButton(onClick = { showAssignDialog = false }) { Text("Cancel") } }
        )
    }

    // ── In-progress dialog: work details ─────────────────────────────────────
    if (showInProgressDialog) {
        var notes by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { showInProgressDialog = false },
            title = { Text("Mark In Progress", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text(
                        "Add a short note on what work is being done, so the student can see progress.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.height(14.dp))
                    OutlinedTextField(
                        value = notes,
                        onValueChange = { notes = it },
                        label = { Text("Progress Notes") },
                        placeholder = { Text("e.g. Maintenance team dispatched, ETA 2 days") },
                        minLines = 3,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                TextButton(
                    enabled = notes.isNotBlank(),
                    onClick = {
                        showInProgressDialog = false
                        applyAdvance(notes = notes)
                    }
                ) { Text("Update", fontWeight = FontWeight.Bold) }
            },
            dismissButton = { TextButton(onClick = { showInProgressDialog = false }) { Text("Cancel") } }
        )
    }

    // ── Resolve confirmation (terminal action, so confirm before applying) ──
    if (showResolveConfirm) {
        AlertDialog(
            onDismissRequest = { showResolveConfirm = false },
            title = { Text("Resolve Complaint?", fontWeight = FontWeight.Bold) },
            text = { Text("This marks the complaint as fully resolved and notifies the student. This can't be undone.") },
            confirmButton = {
                TextButton(onClick = { showResolveConfirm = false; applyAdvance() }) {
                    Text("Resolve", color = MaterialTheme.colorScheme.tertiary, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = { TextButton(onClick = { showResolveConfirm = false }) { Text("Cancel") } }
        )
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Complaint?", fontWeight = FontWeight.Bold) },
            text = { Text("This complaint will be removed as spam. This action cannot be undone.") },
            confirmButton = {
                TextButton(onClick = {
                    showDeleteDialog = false
                    MockRepository.deleteComplaint(complaint.id)
                    onDeleted()
                }) {
                    Text("Delete", color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = { TextButton(onClick = { showDeleteDialog = false }) { Text("Cancel") } }
        )
    }
}