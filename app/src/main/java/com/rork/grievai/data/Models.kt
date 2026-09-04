package com.rork.grievai.data

import kotlinx.serialization.Serializable

enum class UserRole { STUDENT, ADMIN }

enum class ComplaintStatus {
    SUBMITTED, UNDER_REVIEW, ASSIGNED, IN_PROGRESS, RESOLVED;

    val label: String get() = when (this) {
        SUBMITTED -> "Submitted"
        UNDER_REVIEW -> "Under Review"
        ASSIGNED -> "Assigned"
        IN_PROGRESS -> "In Progress"
        RESOLVED -> "Resolved"
    }

    val order: Int get() = ordinal
}

enum class ComplaintPriority {
    LOW, MEDIUM, HIGH, CRITICAL;

    val label: String get() = name.replaceFirstChar { it.titlecase() }
}

enum class ComplaintVisibility { PUBLIC, PRIVATE }

@Serializable
data class User(
    val id: String,
    val name: String,
    val email: String,
    val idCardNumber: String,
    val institutionId: String = "",
    val institutionName: String = "",
    val institutionJoinCode: String = "",
    val role: UserRole,
    val department: String = "",
    val enrollmentNumber: String = "",
    val avatarUrl: String? = null,
    val joinDate: String = "2024-08-15"
)

@Serializable
data class AIAnalysis(
    val category: String,
    val priority: ComplaintPriority,
    val confidenceScore: Float, // 0..1
    val suggestedDepartment: String,
    val insights: String,
    val topComplaintType: String = ""
)

@Serializable
data class TimelineEvent(
    val id: String,
    val status: ComplaintStatus,
    val title: String,
    val description: String,
    val timestamp: String,
    val by: String,
    val completed: Boolean
)

@Serializable
data class Comment(
    val id: String,
    val authorName: String,
    val authorAvatarUrl: String? = null,
    val text: String,
    val timestamp: String,
    val likes: Int,
    val likedByMe: Boolean = false,
    val replies: List<Comment> = emptyList()
)

@Serializable
data class Complaint(
    val id: String,
    val title: String,
    val description: String,
    val department: String,
    val category: String,
    val status: ComplaintStatus,
    val priority: ComplaintPriority,
    val visibility: ComplaintVisibility,
    val authorName: String,
    val authorId: String,
    val authorAvatarUrl: String? = null,
    val createdAt: String,
    val updatedAt: String,
    val upvotes: Int,
    val upvotedByMe: Boolean = false,
    val supporters: Int,
    val attachmentCount: Int = 0,
    val aiAnalysis: AIAnalysis,
    val timeline: List<TimelineEvent>,
    val comments: List<Comment> = emptyList(),
    val resolutionTimeHours: Int? = null
)

@Serializable
data class NotificationItem(
    val id: String,
    val type: NotificationType,
    val title: String,
    val message: String,
    val timestamp: String,
    val read: Boolean = false,
    val complaintId: String? = null
)

enum class NotificationType {
    COMPLAINT_SUBMITTED, PRIORITY_UPDATED, STATUS_CHANGED,
    COMPLAINT_RESOLVED, ADMIN_REPLY, NEW_COMMENT, NEW_SUPPORTER
}

data class DepartmentStat(
    val name: String,
    val total: Int,
    val resolved: Int,
    val pending: Int
)

data class PriorityDistribution(
    val low: Int,
    val medium: Int,
    val high: Int,
    val critical: Int
)

data class MonthlyComplaints(
    val month: String,
    val count: Int,
    val resolved: Int
)

data class AIInsightCard(
    val title: String,
    val value: String,
    val description: String,
    val trend: String,
    val trendUp: Boolean
)

object Departments {
    val all = listOf(
        "Computer Science",
        "Electrical Engineering",
        "Mechanical Engineering",
        "Civil Engineering",
        "Library",
        "Hostel & Accommodation",
        "Examination Cell",
        "Finance & Fees",
        "Sports & Recreation",
        "Administration",
        "Transport",
        "Cafeteria"
    )
}

object ComplaintCategories {
    val all = listOf(
        "Infrastructure",
        "Faculty Behavior",
        "Harassment",
        "Academic Issue",
        "Facility Maintenance",
        "Fee Payment",
        "Examination",
        "Hostel Issue",
        "Library Resource",
        "Transport Issue",
        "Ragging",
        "Discrimination",
        "Technical Issue",
        "Administrative Delay"
    )
}