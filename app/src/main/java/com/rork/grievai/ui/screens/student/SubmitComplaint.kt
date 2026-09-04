package com.rork.grievai.ui.screens.student

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.rork.grievai.data.AIAnalysis
import com.rork.grievai.data.ComplaintCategories
import com.rork.grievai.data.ComplaintPriority
import com.rork.grievai.data.ComplaintVisibility
import com.rork.grievai.data.Departments
import com.rork.grievai.data.MockRepository
import com.rork.grievai.data.User
import com.rork.grievai.ui.components.AIAnalysisCard
import com.rork.grievai.ui.components.PrimaryButton
import kotlinx.coroutines.delay
import com.rork.grievai.data.GeminiClassifier
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubmitComplaintScreen(
    user: User,
    onBack: () -> Unit,
    onSubmitted: (String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var department by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var deptExpanded by remember { mutableStateOf(false) }
    var catExpanded by remember { mutableStateOf(false) }
    var visibility by remember { mutableStateOf(ComplaintVisibility.PRIVATE) }
    var attachmentCount by remember { mutableStateOf(0) }
    var isAnalyzing by remember { mutableStateOf(false) }
    var aiResult by remember { mutableStateOf<AIAnalysis?>(null) }
    var isSubmitting by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            androidx.compose.material3.TopAppBar(
                title = { Text("Submit Complaint", fontWeight = FontWeight.Bold) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Filled.ArrowBack, contentDescription = "Back") } },
                colors = androidx.compose.material3.TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
                .imePadding()
        ) {
            // Title
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Complaint Title") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(14.dp)
            )
            Spacer(Modifier.height(12.dp))

            // Description
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Describe your complaint in detail") },
                modifier = Modifier.fillMaxWidth().height(140.dp),
                shape = RoundedCornerShape(14.dp)
            )
            Spacer(Modifier.height(12.dp))

            // Department dropdown
            ExposedDropdownMenuBox(expanded = deptExpanded, onExpandedChange = { deptExpanded = it }) {
                OutlinedTextField(
                    value = department,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Department") },
                    leadingIcon = { Icon(Icons.Filled.Category, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = deptExpanded) },
                    modifier = Modifier.fillMaxWidth().menuAnchor(),
                    shape = RoundedCornerShape(14.dp),
                    singleLine = true
                )
                DropdownMenu(expanded = deptExpanded, onDismissRequest = { deptExpanded = false }) {
                    Departments.all.forEach {
                        DropdownMenuItem(text = { Text(it) }, onClick = { department = it; deptExpanded = false })
                    }
                }
            }
            Spacer(Modifier.height(12.dp))

            // Category dropdown
            ExposedDropdownMenuBox(expanded = catExpanded, onExpandedChange = { catExpanded = it }) {
                OutlinedTextField(
                    value = category,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Category") },
                    leadingIcon = { Icon(Icons.Filled.Category, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = catExpanded) },
                    modifier = Modifier.fillMaxWidth().menuAnchor(),
                    shape = RoundedCornerShape(14.dp),
                    singleLine = true
                )
                DropdownMenu(expanded = catExpanded, onDismissRequest = { catExpanded = false }) {
                    ComplaintCategories.all.forEach {
                        DropdownMenuItem(text = { Text(it) }, onClick = { category = it; catExpanded = false })
                    }
                }
            }
            Spacer(Modifier.height(16.dp))

            // Attachments
            Text("Attachments", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
            Spacer(Modifier.height(8.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    .clickable { attachmentCount++ }
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Filled.AttachFile, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Spacer(Modifier.width(10.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text("Attach images or documents", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Medium)
                    Text("$attachmentCount file(s) attached", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Icon(Icons.Filled.Add, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            }

            Spacer(Modifier.height(16.dp))

            // Visibility
            Text("Visibility", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                VisibilityOption(
                    icon = Icons.Filled.Public, label = "Public",
                    subtitle = "Visible in feed",
                    selected = visibility == ComplaintVisibility.PUBLIC,
                    onClick = { visibility = ComplaintVisibility.PUBLIC },
                    modifier = Modifier.weight(1f)
                )
                VisibilityOption(
                    icon = Icons.Filled.Lock, label = "Private",
                    subtitle = "Only you & admin",
                    selected = visibility == ComplaintVisibility.PRIVATE,
                    onClick = { visibility = ComplaintVisibility.PRIVATE },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(Modifier.height(20.dp))

            // AI analyze button
            PrimaryButton(
                text = if (isAnalyzing) "Analyzing..." else "Run AI Analysis",
                onClick = {
                    if (title.isNotBlank() && description.isNotBlank() && category.isNotBlank()) {
                        isAnalyzing = true
                        aiResult = null
                    }
                },
                enabled = !isAnalyzing && title.isNotBlank() && description.isNotBlank() && category.isNotBlank(),
                loading = isAnalyzing
            )

            // Simulate AI analysis
            // Real Gemini classification (falls back if API fails / no key)
            LaunchedEffect(isAnalyzing) {
                if (isAnalyzing) {
                    val result = withContext(Dispatchers.IO) {
                        GeminiClassifier.classify(
                            title = title,
                            description = description,
                            selectedCategory = category,
                            selectedDepartment = department
                        )
                    }
                    aiResult = result
                    isAnalyzing = false
                }
            }

            // AI result card
            AnimatedVisibility(
                visible = aiResult != null,
                enter = fadeIn(tween(500)) + slideInVertically(tween(500)) { it / 3 }
            ) {
                Column {
                    Spacer(Modifier.height(16.dp))
                    aiResult?.let { AIAnalysisCard(analysis = it, animateOnShow = true) }
                    Spacer(Modifier.height(20.dp))
                    PrimaryButton(
                        text = "Submit Complaint",
                        onClick = {
                            isSubmitting = true
                        },
                        loading = isSubmitting,
                        enabled = !isSubmitting
                    )
                }
            }

            // Handle submission
            LaunchedEffect(isSubmitting) {
                if (isSubmitting) {
                    delay(700)
                    val complaint = MockRepository.submitComplaint(
                        title = title,
                        description = description,
                        department = department,
                        category = category,
                        visibility = visibility,
                        authorName = user.name,
                        authorId = user.id,
                        attachmentCount = attachmentCount
                    )
                    isSubmitting = false
                    onSubmitted(complaint.id)
                }
            }

            Spacer(Modifier.height(32.dp))
        }
    }
}

@Composable
private fun VisibilityOption(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    subtitle: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val borderColor = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
    val bg = if (selected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f) else MaterialTheme.colorScheme.surface
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(bg)
            .clickable(onClick = onClick)
            .padding(14.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = if (selected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(18.dp))
            }
            Spacer(Modifier.width(8.dp))
            Text(label, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface)
        }
        Spacer(Modifier.height(4.dp))
        Text(subtitle, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
