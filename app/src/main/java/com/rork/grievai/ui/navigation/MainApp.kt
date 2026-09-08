package com.rork.grievai.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Public
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import com.rork.grievai.data.User
import com.rork.grievai.data.UserRole
import com.rork.grievai.ui.screens.admin.AdminAIInsightsScreen
import com.rork.grievai.ui.screens.admin.AdminAnalyticsScreen
import com.rork.grievai.ui.screens.admin.AdminComplaintManagementScreen
import com.rork.grievai.ui.screens.admin.AdminDashboard
import com.rork.grievai.ui.screens.admin.AdminProfileScreen
import com.rork.grievai.ui.screens.student.NotificationsScreen
import com.rork.grievai.ui.screens.student.ProfileScreen
import com.rork.grievai.ui.screens.student.PublicFeedScreen
import com.rork.grievai.ui.screens.student.StudentComplaintsScreen
import com.rork.grievai.ui.screens.student.StudentDashboard

private data class NavTab(val route: String, val label: String, val icon: ImageVector)

private val studentTabs = listOf(
    NavTab("student_home", "Home", Icons.Filled.Home),
    NavTab("student_complaints", "Complaints", Icons.Filled.Assignment),
    NavTab("student_public", "Public Feed", Icons.Filled.Public),
    NavTab("student_notifications", "Alerts", Icons.Filled.Notifications),
    NavTab("student_profile", "Profile", Icons.Filled.Person)
)

private val adminTabs = listOf(
    NavTab("admin_home", "Dashboard", Icons.Filled.Dashboard),
    NavTab("admin_complaints", "Complaints", Icons.Filled.Assignment),
    NavTab("admin_analytics", "Analytics", Icons.Filled.Analytics),
    NavTab("admin_ai", "AI Insights", Icons.Filled.AutoAwesome),
    NavTab("admin_profile", "Profile", Icons.Filled.Person)
)

@Composable
fun MainApp(
    user: User,
    onComplaintClick: (String) -> Unit,
    onSubmitComplaint: () -> Unit,
    onLogout: () -> Unit,
    onEditProfile: (String, String, String) -> Unit
) {
    val tabs = if (user.role == UserRole.ADMIN) adminTabs else studentTabs
    var selectedTab by remember { mutableStateOf(0) }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp
            ) {
                tabs.forEachIndexed { index, tab ->
                    NavigationBarItem(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        icon = { Icon(tab.icon, contentDescription = tab.label, modifier = Modifier.size(22.dp)) },
                        label = { Text(tab.label, style = MaterialTheme.typography.labelSmall, fontWeight = if (selectedTab == index) FontWeight.SemiBold else FontWeight.Normal) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            indicatorColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }
            }
        }
    ) { padding ->
        androidx.compose.foundation.layout.Box(
            modifier = Modifier.fillMaxSize().padding(padding)
        ) {
            when (user.role) {
                UserRole.STUDENT -> {
                    when (selectedTab) {
                        0 -> StudentDashboard(
                            user = user,
                            onComplaintClick = onComplaintClick,
                            onSubmitComplaint = onSubmitComplaint,
                            onPublicFeed = { selectedTab = 2 },
                            onSeeAllComplaints = { selectedTab = 1 },
                            onProfileClick = { selectedTab = 4 }
                        )
                        1 -> StudentComplaintsScreen(user = user, onComplaintClick = onComplaintClick)
                        2 -> PublicFeedScreen(onComplaintClick = onComplaintClick)
                        3 -> NotificationsScreen(onComplaintClick = onComplaintClick)
                        4 -> ProfileScreen(user = user, onLogout = onLogout, onEditProfile = onEditProfile)
                    }
                }
                UserRole.ADMIN -> {
                    when (selectedTab) {
                        0 -> AdminDashboard(
                            user = user,
                            onComplaintClick = onComplaintClick,
                            onSeeAllComplaints = { selectedTab = 1 },
                            onGoAnalytics = { selectedTab = 2 },
                            onGoAI = { selectedTab = 3 },
                            onProfileClick = { selectedTab = 4 }
                        )
                        1 -> AdminComplaintManagementScreen(user = user, onComplaintClick = onComplaintClick)
                        2 -> AdminAnalyticsScreen()
                        3 -> AdminAIInsightsScreen()
                        4 -> AdminProfileScreen(
                            user = user,
                            onLogout = onLogout,
                            onEditProfile = { name, dept -> onEditProfile(name, dept, "") }
                        )
                    }
                }
            }
        }
    }
}