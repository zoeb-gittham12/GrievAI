package com.rork.grievai.ui.screens.auth

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AlternateEmail
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.CorporateFare
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.VpnKey
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
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.rork.grievai.data.Departments
import com.rork.grievai.data.SessionViewModel
import com.rork.grievai.data.UserRole
import com.rork.grievai.ui.components.AuthTextField
import com.rork.grievai.ui.components.PrimaryButton
import com.rork.grievai.ui.theme.HeroGradient

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpScreen(
    role: UserRole,
    onBack: () -> Unit,
    onSignUpSuccess: () -> Unit,
    onLogin: () -> Unit,
    sessionViewModel: SessionViewModel = viewModel()
) {
    val isLoading by sessionViewModel.isLoading.collectAsStateWithLifecycle()
    val error by sessionViewModel.error.collectAsStateWithLifecycle()

    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var idCardNumber by remember { mutableStateOf("") }
    var enrollment by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var department by remember { mutableStateOf("") }
    var deptExpanded by remember { mutableStateOf(false) }
    var passwordMismatch by remember { mutableStateOf(false) }

    // Admins choose: join an existing university, or found a new one.
    // Students always join an existing one via join code.
    var isCreatingNewUniversity by remember { mutableStateOf(false) }
    var joinCode by remember { mutableStateOf("") }
    var newUniversityName by remember { mutableStateOf("") }

    Scaffold(containerColor = MaterialTheme.colorScheme.background) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            Box(modifier = Modifier.fillMaxWidth().height(220.dp).background(HeroGradient))
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Spacer(Modifier.height(16.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                    Spacer(Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.White.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Filled.AutoAwesome, contentDescription = null, tint = Color.White, modifier = Modifier.size(22.dp))
                    }
                    Spacer(Modifier.width(10.dp))
                    Text("GrievAI", style = MaterialTheme.typography.titleLarge, color = Color.White, fontWeight = FontWeight.Bold)
                }
                Spacer(Modifier.height(20.dp))
                Text(
                    if (role == UserRole.ADMIN) "Admin Registration" else "Create Student Account",
                    style = MaterialTheme.typography.displaySmall,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    "Join the intelligent grievance portal.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.85f)
                )
                Spacer(Modifier.height(32.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(24.dp))
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(20.dp)
                ) {
                    Column {
                        AuthTextField(
                            value = name, onValueChange = { name = it; sessionViewModel.clearError() },
                            label = "Full Name", leadingIcon = Icons.Filled.Person, imeAction = ImeAction.Next
                        )
                        Spacer(Modifier.height(14.dp))
                        AuthTextField(
                            value = email, onValueChange = { email = it; sessionViewModel.clearError() },
                            label = "University Email", leadingIcon = Icons.Filled.AlternateEmail,
                            keyboardType = KeyboardType.Email, imeAction = ImeAction.Next
                        )
                        Spacer(Modifier.height(14.dp))
                        AuthTextField(
                            value = idCardNumber, onValueChange = { idCardNumber = it; sessionViewModel.clearError() },
                            label = "ID Card Number", leadingIcon = Icons.Filled.Badge, imeAction = ImeAction.Next
                        )
                        Spacer(Modifier.height(14.dp))
                        // Department dropdown
                        ExposedDropdownMenuBox(expanded = deptExpanded, onExpandedChange = { deptExpanded = it }) {
                            OutlinedTextField(
                                value = department,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Department") },
                                leadingIcon = { Icon(Icons.Filled.School, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = deptExpanded) },
                                modifier = Modifier.fillMaxWidth().menuAnchor(),
                                shape = RoundedCornerShape(14.dp),
                                singleLine = true
                            )
                            androidx.compose.material3.DropdownMenu(expanded = deptExpanded, onDismissRequest = { deptExpanded = false }) {
                                Departments.all.forEach { dept ->
                                    DropdownMenuItem(
                                        text = { Text(dept) },
                                        onClick = { department = dept; deptExpanded = false }
                                    )
                                }
                            }
                        }
                        if (role == UserRole.STUDENT) {
                            Spacer(Modifier.height(14.dp))
                            AuthTextField(
                                value = enrollment, onValueChange = { enrollment = it; sessionViewModel.clearError() },
                                label = "Enrollment Number", leadingIcon = Icons.Filled.Badge, imeAction = ImeAction.Next
                            )
                        }

                        // ── University linkage ──────────────────────────
                        Spacer(Modifier.height(20.dp))
                        if (role == UserRole.ADMIN) {
                            Text(
                                "University",
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(Modifier.height(8.dp))
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(MaterialTheme.colorScheme.surfaceVariant)
                                    .padding(4.dp)
                            ) {
                                listOf(false to "Join Existing", true to "Create New").forEach { (value, label) ->
                                    val selected = isCreatingNewUniversity == value
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(if (selected) MaterialTheme.colorScheme.primary else Color.Transparent)
                                            .clickable {
                                                isCreatingNewUniversity = value
                                                sessionViewModel.clearError()
                                            }
                                            .padding(vertical = 10.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            label,
                                            color = if (selected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                            fontWeight = FontWeight.Bold,
                                            style = MaterialTheme.typography.bodySmall
                                        )
                                    }
                                }
                            }
                            Spacer(Modifier.height(14.dp))
                            if (isCreatingNewUniversity) {
                                AuthTextField(
                                    value = newUniversityName,
                                    onValueChange = { newUniversityName = it; sessionViewModel.clearError() },
                                    label = "University Name", leadingIcon = Icons.Filled.CorporateFare,
                                    imeAction = ImeAction.Done
                                )
                                Spacer(Modifier.height(6.dp))
                                Text(
                                    "You'll get a join code afterward to share with your students and other admins.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            } else {
                                AuthTextField(
                                    value = joinCode,
                                    onValueChange = { joinCode = it.uppercase(); sessionViewModel.clearError() },
                                    label = "University Join Code", leadingIcon = Icons.Filled.VpnKey,
                                    imeAction = ImeAction.Done
                                )
                            }
                        } else {
                            AuthTextField(
                                value = joinCode,
                                onValueChange = { joinCode = it.uppercase(); sessionViewModel.clearError() },
                                label = "University Join Code", leadingIcon = Icons.Filled.VpnKey,
                                imeAction = ImeAction.Next
                            )
                            Spacer(Modifier.height(6.dp))
                            Text(
                                "Get this code from your university admin.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Spacer(Modifier.height(14.dp))
                        AuthTextField(
                            value = password,
                            onValueChange = { password = it; passwordMismatch = false; sessionViewModel.clearError() },
                            label = "Password", leadingIcon = Icons.Filled.Lock,
                            isPassword = true, imeAction = ImeAction.Next
                        )
                        Spacer(Modifier.height(14.dp))
                        AuthTextField(
                            value = confirmPassword,
                            onValueChange = { confirmPassword = it; passwordMismatch = false; sessionViewModel.clearError() },
                            label = "Confirm Password", leadingIcon = Icons.Filled.Lock,
                            isPassword = true, imeAction = ImeAction.Done,
                            isError = passwordMismatch || error != null,
                            errorMessage = if (passwordMismatch) "Passwords do not match" else error
                        )

                        Spacer(Modifier.height(20.dp))
                        PrimaryButton(
                            text = "Create Account",
                            onClick = {
                                passwordMismatch = password != confirmPassword
                                if (!passwordMismatch) {
                                    sessionViewModel.signup(
                                        name = name,
                                        email = email,
                                        idCardNumber = idCardNumber,
                                        password = password,
                                        role = role,
                                        department = department,
                                        enrollmentNumber = enrollment,
                                        joinCode = if (role == UserRole.STUDENT || !isCreatingNewUniversity) joinCode else "",
                                        newUniversityName = if (role == UserRole.ADMIN && isCreatingNewUniversity) newUniversityName else "",
                                        onSuccess = onSignUpSuccess
                                    )
                                }
                            },
                            loading = isLoading
                        )
                    }
                }

                Spacer(Modifier.height(20.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Already have an account?", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    TextButton(onClick = onLogin) {
                        Text("Sign In", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                    }
                }
                Spacer(Modifier.height(24.dp))
            }
        }
    }
}