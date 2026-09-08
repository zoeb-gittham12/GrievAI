package com.rork.grievai.data

import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

/**
 * In-memory mock repository simulating an API-ready backend.
 * Replace functions with real Ktor calls when wiring a live service.
 */
object MockRepository {

    private val fmt = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US)

    private fun now(): String = fmt.format(Date())
    private fun hoursAgo(h: Int): String =
        fmt.format(Date(System.currentTimeMillis() - TimeUnit.HOURS.toMillis(h.toLong())))
    private fun daysAgo(d: Int): String = hoursAgo(d * 24)

    val demoStudent = User(
        id = "u_stu_001",
        name = "Aarav Sharma",
        email = "aarav.sharma@university.edu",
        idCardNumber = "UNI-2021-CS-0142",
        institutionId = "demo-institution",
        institutionName = "Demo University",
        role = UserRole.STUDENT,
        department = "Computer Science",
        enrollmentNumber = "EN2021CS0142",
        avatarUrl = null
    )

    val demoAdmin = User(
        id = "u_adm_001",
        name = "Dr. Priya Nair",
        email = "priya.nair@university.edu",
        idCardNumber = "UNI-ADMIN-0007",
        institutionId = "demo-institution",
        institutionName = "Demo University",
        role = UserRole.ADMIN,
        department = "Administration"
    )

    private val complaints: MutableList<Complaint> = mutableListOf(
        Complaint(
            id = "c_001",
            title = "Wi-Fi connectivity issues in CS Block",
            description = "The Wi-Fi in the Computer Science block (floors 2 and 3) has been unstable for over a week. Students are unable to attend online labs and submit assignments on time. Multiple routers appear to be non-functional.",
            department = "Computer Science",
            category = "Technical Issue",
            status = ComplaintStatus.IN_PROGRESS,
            priority = ComplaintPriority.HIGH,
            visibility = ComplaintVisibility.PUBLIC,
            authorName = "Aarav Sharma",
            authorId = "u_stu_001",
            createdAt = daysAgo(4),
            updatedAt = hoursAgo(6),
            upvotes = 87,
            supporters = 87,
            attachmentCount = 2,
            aiAnalysis = AIAnalysis(
                category = "Technical Issue",
                priority = ComplaintPriority.HIGH,
                confidenceScore = 0.94f,
                suggestedDepartment = "Computer Science",
                insights = "Recurring network infrastructure complaint with high student impact. Likely affects 300+ students. Recommend immediate network audit.",
                topComplaintType = "Network Infrastructure"
            ),
            timeline = listOf(
                TimelineEvent("t1", ComplaintStatus.SUBMITTED, "Complaint Submitted", "Received and logged into the system", daysAgo(4), "Aarav Sharma", true),
                TimelineEvent("t2", ComplaintStatus.UNDER_REVIEW, "Under Review", "AI categorized as Technical Issue, priority HIGH", daysAgo(4), "GrievAI System", true),
                TimelineEvent("t3", ComplaintStatus.ASSIGNED, "Assigned to Department", "Routed to CS Department — Network Cell", daysAgo(3), "Dr. Priya Nair", true),
                TimelineEvent("t4", ComplaintStatus.IN_PROGRESS, "In Progress", "Network technician scheduled for site visit", hoursAgo(30), "CS Dept Office", true),
                TimelineEvent("t5", ComplaintStatus.RESOLVED, "Resolved", "Pending technician visit", "", "", false)
            ),
            comments = listOf(
                Comment("cm1", "Ishaan Verma", null, "Same issue on floor 3. Completely unable to work.", hoursAgo(60), 12, false, listOf(
                    Comment("cm1_r1", "Aarav Sharma", null, "Yes! Floor 3 is worse.", hoursAgo(58), 3, false)
                )),
                Comment("cm2", "Meera Iyer", null, "Please fix soon, finals are approaching.", hoursAgo(40), 8, false)
            )
        ),
        Complaint(
            id = "c_002",
            title = "Unhygienic food in cafeteria",
            description = "The main cafeteria has been serving food with poor hygiene standards. Found insects in the dal served yesterday. This is a serious health concern for all students.",
            department = "Cafeteria",
            category = "Facility Maintenance",
            status = ComplaintStatus.UNDER_REVIEW,
            priority = ComplaintPriority.CRITICAL,
            visibility = ComplaintVisibility.PUBLIC,
            authorName = "Rohan Kapoor",
            authorId = "u_stu_002",
            createdAt = daysAgo(2),
            updatedAt = hoursAgo(12),
            upvotes = 142,
            supporters = 142,
            attachmentCount = 3,
            aiAnalysis = AIAnalysis(
                category = "Facility Maintenance",
                priority = ComplaintPriority.CRITICAL,
                confidenceScore = 0.97f,
                suggestedDepartment = "Cafeteria",
                insights = "Health-safety critical. Recommend immediate inspection and vendor review. Pattern matches 3 prior complaints this month.",
                topComplaintType = "Food Safety"
            ),
            timeline = listOf(
                TimelineEvent("t1", ComplaintStatus.SUBMITTED, "Complaint Submitted", "Received and logged", daysAgo(2), "Rohan Kapoor", true),
                TimelineEvent("t2", ComplaintStatus.UNDER_REVIEW, "Under Review", "AI flagged CRITICAL priority — health safety", daysAgo(2), "GrievAI System", true),
                TimelineEvent("t3", ComplaintStatus.ASSIGNED, "Assigned to Department", "Routed to Cafeteria + Health Cell", daysAgo(1), "Dr. Priya Nair", true),
                TimelineEvent("t4", ComplaintStatus.IN_PROGRESS, "In Progress", "Pending", "", "", false),
                TimelineEvent("t5", ComplaintStatus.RESOLVED, "Resolved", "Pending", "", "", false)
            ),
            comments = listOf(
                Comment("cm1", "Ananya Rao", null, "I got food poisoning last week. This needs immediate action.", daysAgo(1), 34, false),
                Comment("cm2", "Karthik Menon", null, "Attaching photos of the food.", hoursAgo(20), 15, false)
            )
        ),
        Complaint(
            id = "c_003",
            title = "AC not working in Library Reading Hall",
            description = "The air conditioning in the main reading hall has been down for 5 days. With temperatures above 35°C, it is impossible to study during afternoon hours.",
            department = "Library",
            category = "Facility Maintenance",
            status = ComplaintStatus.RESOLVED,
            priority = ComplaintPriority.MEDIUM,
            visibility = ComplaintVisibility.PUBLIC,
            authorName = "Sneha Patel",
            authorId = "u_stu_003",
            createdAt = daysAgo(10),
            updatedAt = daysAgo(6),
            upvotes = 56,
            supporters = 56,
            attachmentCount = 1,
            aiAnalysis = AIAnalysis(
                category = "Facility Maintenance",
                priority = ComplaintPriority.MEDIUM,
                confidenceScore = 0.88f,
                suggestedDepartment = "Library",
                insights = "Standard facility issue. Moderate student impact. Linked to scheduled maintenance.",
                topComplaintType = "HVAC Maintenance"
            ),
            timeline = listOf(
                TimelineEvent("t1", ComplaintStatus.SUBMITTED, "Complaint Submitted", "Received", daysAgo(10), "Sneha Patel", true),
                TimelineEvent("t2", ComplaintStatus.UNDER_REVIEW, "Under Review", "AI categorized — MEDIUM priority", daysAgo(10), "GrievAI System", true),
                TimelineEvent("t3", ComplaintStatus.ASSIGNED, "Assigned", "Routed to Library Maintenance", daysAgo(9), "Dr. Priya Nair", true),
                TimelineEvent("t4", ComplaintStatus.IN_PROGRESS, "In Progress", "Technician dispatched", daysAgo(8), "Library Office", true),
                TimelineEvent("t5", ComplaintStatus.RESOLVED, "Resolved", "AC repaired and tested", daysAgo(6), "Maintenance Team", true)
            ),
            resolutionTimeHours = 96,
            comments = listOf(
                Comment("cm1", "Devika Suresh", null, "Thank you for the quick fix!", daysAgo(5), 9, false)
            )
        ),
        Complaint(
            id = "c_004",
            title = "Delayed scholarship disbursement",
            description = "My merit scholarship for this semester has not been disbursed even after 3 months. I have submitted all required documents twice. This is causing financial hardship.",
            department = "Finance & Fees",
            category = "Administrative Delay",
            status = ComplaintStatus.ASSIGNED,
            priority = ComplaintPriority.HIGH,
            visibility = ComplaintVisibility.PRIVATE,
            authorName = "Aarav Sharma",
            authorId = "u_stu_001",
            createdAt = daysAgo(6),
            updatedAt = daysAgo(4),
            upvotes = 0,
            supporters = 0,
            attachmentCount = 4,
            aiAnalysis = AIAnalysis(
                category = "Administrative Delay",
                priority = ComplaintPriority.HIGH,
                confidenceScore = 0.91f,
                suggestedDepartment = "Finance & Fees",
                insights = "Financial urgency detected. Document trail indicates administrative bottleneck. Recommend escalation.",
                topComplaintType = "Scholarship Processing"
            ),
            timeline = listOf(
                TimelineEvent("t1", ComplaintStatus.SUBMITTED, "Submitted", "Received", daysAgo(6), "Aarav Sharma", true),
                TimelineEvent("t2", ComplaintStatus.UNDER_REVIEW, "Under Review", "AI flagged HIGH — financial impact", daysAgo(6), "GrievAI System", true),
                TimelineEvent("t3", ComplaintStatus.ASSIGNED, "Assigned", "Routed to Finance Office", daysAgo(4), "Dr. Priya Nair", true),
                TimelineEvent("t4", ComplaintStatus.IN_PROGRESS, "In Progress", "Pending", "", "", false),
                TimelineEvent("t5", ComplaintStatus.RESOLVED, "Resolved", "Pending", "", "", false)
            )
        ),
        Complaint(
            id = "c_005",
            title = "Hostel room allocation discrepancy",
            description = "I was allotted a triple-sharing room despite paying for double-sharing. The warden says the records show triple. Need clarification and refund of the difference.",
            department = "Hostel & Accommodation",
            category = "Hostel Issue",
            status = ComplaintStatus.SUBMITTED,
            priority = ComplaintPriority.MEDIUM,
            visibility = ComplaintVisibility.PRIVATE,
            authorName = "Aarav Sharma",
            authorId = "u_stu_001",
            createdAt = hoursAgo(8),
            updatedAt = hoursAgo(8),
            upvotes = 0,
            supporters = 0,
            aiAnalysis = AIAnalysis(
                category = "Hostel Issue",
                priority = ComplaintPriority.MEDIUM,
                confidenceScore = 0.86f,
                suggestedDepartment = "Hostel & Accommodation",
                insights = "Billing-allocation mismatch. Requires document verification. Pattern: 2 similar complaints this week.",
                topComplaintType = "Allocation Discrepancy"
            ),
            timeline = listOf(
                TimelineEvent("t1", ComplaintStatus.SUBMITTED, "Submitted", "Received and logged", hoursAgo(8), "Aarav Sharma", true),
                TimelineEvent("t2", ComplaintStatus.UNDER_REVIEW, "Under Review", "Pending", "", "", false),
                TimelineEvent("t3", ComplaintStatus.ASSIGNED, "Assigned", "Pending", "", "", false),
                TimelineEvent("t4", ComplaintStatus.IN_PROGRESS, "In Progress", "Pending", "", "", false),
                TimelineEvent("t5", ComplaintStatus.RESOLVED, "Resolved", "Pending", "", "", false)
            )
        ),
        Complaint(
            id = "c_006",
            title = "Broken laboratory equipment in EE lab",
            description = "5 oscilloscopes in the Electrical Engineering lab are non-functional. Practical sessions are being skipped, affecting coursework completion.",
            department = "Electrical Engineering",
            category = "Infrastructure",
            status = ComplaintStatus.RESOLVED,
            priority = ComplaintPriority.HIGH,
            visibility = ComplaintVisibility.PUBLIC,
            authorName = "Vikram Reddy",
            authorId = "u_stu_004",
            createdAt = daysAgo(14),
            updatedAt = daysAgo(9),
            upvotes = 73,
            supporters = 73,
            attachmentCount = 2,
            aiAnalysis = AIAnalysis(
                category = "Infrastructure",
                priority = ComplaintPriority.HIGH,
                confidenceScore = 0.92f,
                suggestedDepartment = "Electrical Engineering",
                insights = "Academic impact high. Equipment replacement required. Budget approval recommended.",
                topComplaintType = "Lab Equipment"
            ),
            timeline = listOf(
                TimelineEvent("t1", ComplaintStatus.SUBMITTED, "Submitted", "Received", daysAgo(14), "Vikram Reddy", true),
                TimelineEvent("t2", ComplaintStatus.UNDER_REVIEW, "Under Review", "AI flagged HIGH — academic impact", daysAgo(14), "GrievAI System", true),
                TimelineEvent("t3", ComplaintStatus.ASSIGNED, "Assigned", "Routed to EE Dept", daysAgo(13), "Dr. Priya Nair", true),
                TimelineEvent("t4", ComplaintStatus.IN_PROGRESS, "In Progress", "Procurement initiated", daysAgo(11), "EE Dept", true),
                TimelineEvent("t5", ComplaintStatus.RESOLVED, "Resolved", "5 new oscilloscopes installed", daysAgo(9), "Procurement Cell", true)
            ),
            resolutionTimeHours = 120,
            comments = listOf(
                Comment("cm1", "Pooja Bhat", null, "Finally! Thank you.", daysAgo(8), 11, false)
            )
        ),
        Complaint(
            id = "c_007",
            title = "Bus route 4 consistently late",
            description = "University bus on route 4 has been arriving 20-30 minutes late for two weeks, causing students to miss morning lectures.",
            department = "Transport",
            category = "Transport Issue",
            status = ComplaintStatus.IN_PROGRESS,
            priority = ComplaintPriority.MEDIUM,
            visibility = ComplaintVisibility.PUBLIC,
            authorName = "Arjun Nair",
            authorId = "u_stu_005",
            createdAt = daysAgo(3),
            updatedAt = hoursAgo(18),
            upvotes = 64,
            supporters = 64,
            aiAnalysis = AIAnalysis(
                category = "Transport Issue",
                priority = ComplaintPriority.MEDIUM,
                confidenceScore = 0.89f,
                suggestedDepartment = "Transport",
                insights = "Schedule reliability issue. Driver-route reassignment recommended.",
                topComplaintType = "Schedule Delay"
            ),
            timeline = listOf(
                TimelineEvent("t1", ComplaintStatus.SUBMITTED, "Submitted", "Received", daysAgo(3), "Arjun Nair", true),
                TimelineEvent("t2", ComplaintStatus.UNDER_REVIEW, "Under Review", "AI categorized — MEDIUM", daysAgo(3), "GrievAI System", true),
                TimelineEvent("t3", ComplaintStatus.ASSIGNED, "Assigned", "Routed to Transport Cell", daysAgo(2), "Dr. Priya Nair", true),
                TimelineEvent("t4", ComplaintStatus.IN_PROGRESS, "In Progress", "Route audit underway", hoursAgo(18), "Transport Office", true),
                TimelineEvent("t5", ComplaintStatus.RESOLVED, "Resolved", "Pending", "", "", false)
            ),
            comments = listOf(
                Comment("cm1", "Sahil Khan", null, "Same. Missed 3 lectures this week.", daysAgo(1), 19, false)
            )
        ),
        Complaint(
            id = "c_008",
            title = "Professor not following syllabus",
            description = "The Data Structures professor is consistently deviating from the prescribed syllabus, skipping core topics like graphs and trees that are important for placements.",
            department = "Computer Science",
            category = "Academic Issue",
            status = ComplaintStatus.UNDER_REVIEW,
            priority = ComplaintPriority.MEDIUM,
            visibility = ComplaintVisibility.PUBLIC,
            authorName = "Diya Gupta",
            authorId = "u_stu_006",
            createdAt = daysAgo(1),
            updatedAt = hoursAgo(4),
            upvotes = 38,
            supporters = 38,
            aiAnalysis = AIAnalysis(
                category = "Academic Issue",
                priority = ComplaintPriority.MEDIUM,
                confidenceScore = 0.83f,
                suggestedDepartment = "Computer Science",
                insights = "Academic concern. Recommend HoD review of lecture logs.",
                topComplaintType = "Curriculum Compliance"
            ),
            timeline = listOf(
                TimelineEvent("t1", ComplaintStatus.SUBMITTED, "Submitted", "Received", daysAgo(1), "Diya Gupta", true),
                TimelineEvent("t2", ComplaintStatus.UNDER_REVIEW, "Under Review", "AI flagged — academic impact", hoursAgo(20), "GrievAI System", true),
                TimelineEvent("t3", ComplaintStatus.ASSIGNED, "Assigned", "Pending", "", "", false),
                TimelineEvent("t4", ComplaintStatus.IN_PROGRESS, "In Progress", "Pending", "", "", false),
                TimelineEvent("t5", ComplaintStatus.RESOLVED, "Resolved", "Pending", "", "", false)
            )
        )
    )

    private val notifications: MutableList<NotificationItem> = mutableListOf(
        NotificationItem("n1", NotificationType.ADMIN_REPLY, "Admin Reply on your Complaint", "Dr. Priya Nair: Network technician visit scheduled for tomorrow.", hoursAgo(3), false, "c_001"),
        NotificationItem("n2", NotificationType.STATUS_CHANGED, "Status Updated", "Your complaint 'Hostel room allocation' is now Under Review.", hoursAgo(6), false, "c_005"),
        NotificationItem("n3", NotificationType.PRIORITY_UPDATED, "Priority Updated", "Cafeteria complaint flagged as CRITICAL by GrievAI.", hoursAgo(12), true, "c_002"),
        NotificationItem("n4", NotificationType.COMPLAINT_RESOLVED, "Complaint Resolved", "Library AC complaint has been resolved.", daysAgo(6), true, "c_003"),
        NotificationItem("n5", NotificationType.COMPLAINT_SUBMITTED, "Complaint Submitted", "Your complaint 'Bus route 4' was submitted successfully.", daysAgo(3), true, "c_007"),
        NotificationItem("n6", NotificationType.NEW_SUPPORTER, "New Supporters", "12 new supporters joined your Wi-Fi complaint.", hoursAgo(20), true, "c_001"),
        NotificationItem("n7", NotificationType.NEW_COMMENT, "New Comment", "Ishaan Verma commented on your complaint.", hoursAgo(60), true, "c_001")
    )

    suspend fun getStudentComplaints(userId: String = "u_stu_001"): List<Complaint> {
        delay(450)
        return complaints.filter { it.authorId == userId }
    }

    suspend fun getAllComplaints(): List<Complaint> {
        delay(550)
        return complaints.toList()
    }

    suspend fun getPublicComplaints(): List<Complaint> {
        delay(400)
        return complaints.filter { it.visibility == ComplaintVisibility.PUBLIC }
    }

    suspend fun getComplaint(id: String): Complaint? {
        delay(300)
        return complaints.find { it.id == id }
    }

    suspend fun getNotifications(): List<NotificationItem> {
        delay(400)
        return notifications.toList()
    }

    // ── Admin status workflow ────────────────────────────────────────────────
    // Complaints move strictly forward: SUBMITTED -> UNDER_REVIEW -> ASSIGNED ->
    // IN_PROGRESS -> RESOLVED. There is no way to skip a step from here; each
    // call only ever advances to (current status + 1).

    sealed class UpdateResult {
        data class Success(val complaint: Complaint) : UpdateResult()
        data class Error(val message: String) : UpdateResult()
    }

    fun advanceComplaintStatus(
        complaintId: String,
        actorName: String,
        department: String? = null,
        coordinator: String? = null,
        progressNotes: String? = null
    ): UpdateResult {
        val c = complaints.find { it.id == complaintId }
            ?: return UpdateResult.Error("Complaint not found.")
        val idx = complaints.indexOf(c)

        if (c.status == ComplaintStatus.RESOLVED) {
            return UpdateResult.Error("This complaint is already resolved.")
        }

        val nextStatus = ComplaintStatus.values().getOrNull(c.status.ordinal + 1)
            ?: return UpdateResult.Error("There is no further status to advance to.")

        val description = when (nextStatus) {
            ComplaintStatus.UNDER_REVIEW ->
                "AI flagged this complaint for review — matched against similar historical cases."
            ComplaintStatus.ASSIGNED -> {
                if (department.isNullOrBlank() || coordinator.isNullOrBlank()) {
                    return UpdateResult.Error("Department and coordinator are required to assign this complaint.")
                }
                "Assigned to $department — Coordinator: $coordinator"
            }
            ComplaintStatus.IN_PROGRESS -> {
                if (progressNotes.isNullOrBlank()) {
                    return UpdateResult.Error("Please add progress notes before marking this complaint in progress.")
                }
                progressNotes
            }
            ComplaintStatus.RESOLVED -> "Marked resolved by $actorName."
            ComplaintStatus.SUBMITTED -> "Submitted." // unreachable as a "next" status
        }

        val updatedTimeline = c.timeline.map { event ->
            if (event.status == nextStatus) {
                event.copy(description = description, timestamp = now(), by = actorName, completed = true)
            } else event
        }

        val resolutionHours = if (nextStatus == ComplaintStatus.RESOLVED) {
            try {
                val created = fmt.parse(c.createdAt)?.time ?: System.currentTimeMillis()
                ((System.currentTimeMillis() - created) / 3_600_000L).toInt().coerceAtLeast(1)
            } catch (e: Exception) { null }
        } else c.resolutionTimeHours

        val updated = c.copy(
            status = nextStatus,
            updatedAt = now(),
            timeline = updatedTimeline,
            resolutionTimeHours = resolutionHours
        )
        complaints[idx] = updated

        notifications.add(0, NotificationItem(
            id = "n_${System.currentTimeMillis()}",
            type = if (nextStatus == ComplaintStatus.RESOLVED) NotificationType.COMPLAINT_RESOLVED else NotificationType.STATUS_CHANGED,
            title = if (nextStatus == ComplaintStatus.RESOLVED) "Complaint Resolved" else "Status Updated",
            message = "Your complaint '${c.title}' is now ${nextStatus.label}.",
            timestamp = now(),
            read = false,
            complaintId = c.id
        ))

        return UpdateResult.Success(updated)
    }

    fun updateComplaintPriority(complaintId: String, priority: ComplaintPriority): UpdateResult {
        val c = complaints.find { it.id == complaintId }
            ?: return UpdateResult.Error("Complaint not found.")
        val idx = complaints.indexOf(c)
        val updated = c.copy(priority = priority, updatedAt = now())
        complaints[idx] = updated
        return UpdateResult.Success(updated)
    }

    fun deleteComplaint(complaintId: String): Boolean {
        return complaints.removeIf { it.id == complaintId }
    }

    fun toggleUpvote(complaintId: String) {
        val c = complaints.find { it.id == complaintId } ?: return
        val idx = complaints.indexOf(c)
        val updated = c.copy(
            upvotedByMe = !c.upvotedByMe,
            upvotes = if (c.upvotedByMe) c.upvotes - 1 else c.upvotes + 1,
            supporters = if (c.upvotedByMe) c.supporters - 1 else c.supporters + 1
        )
        complaints[idx] = updated
    }

    fun toggleCommentLike(complaintId: String, commentId: String) {
        val c = complaints.find { it.id == complaintId } ?: return
        val idx = complaints.indexOf(c)
        val updatedComments = c.comments.map { cm ->
            if (cm.id == commentId) {
                cm.copy(
                    likedByMe = !cm.likedByMe,
                    likes = if (cm.likedByMe) cm.likes - 1 else cm.likes + 1
                )
            } else {
                val r = cm.replies.map { reply ->
                    if (reply.id == commentId) reply.copy(
                        likedByMe = !reply.likedByMe,
                        likes = if (reply.likedByMe) reply.likes - 1 else reply.likes + 1
                    ) else reply
                }
                cm.copy(replies = r)
            }
        }
        complaints[idx] = c.copy(comments = updatedComments)
    }

    fun addComment(complaintId: String, text: String, author: String = "You") {
        val c = complaints.find { it.id == complaintId } ?: return
        val idx = complaints.indexOf(c)
        val newComment = Comment(
            id = "cm_${System.currentTimeMillis()}",
            authorName = author,
            text = text,
            timestamp = now(),
            likes = 0
        )
        complaints[idx] = c.copy(comments = c.comments + newComment)

        // Private complaints are a 1:1 thread between the student and an admin.
        // Whenever the *other* party posts, let the author know.
        if (c.visibility == ComplaintVisibility.PRIVATE && author != c.authorName) {
            notifications.add(0, NotificationItem(
                id = "n_${System.currentTimeMillis()}",
                type = NotificationType.ADMIN_REPLY,
                title = "New Reply",
                message = "$author replied on '${c.title}'.",
                timestamp = now(),
                read = false,
                complaintId = c.id
            ))
        }
    }

    fun submitComplaint(
        title: String,
        description: String,
        department: String,
        category: String,
        visibility: ComplaintVisibility,
        authorName: String,
        authorId: String,
        attachmentCount: Int = 0
    ): Complaint {
        // Simulate AI analysis
        val aiAnalysis = AIAnalysis(
            category = category,
            priority = predictPriority(description, category),
            confidenceScore = 0.82f + (Math.random() * 0.15).toFloat(),
            suggestedDepartment = department,
            insights = "AI analyzed complaint content and matched against ${3 + (Math.random() * 10).toInt()} similar historical cases.",
            topComplaintType = category
        )
        val complaint = Complaint(
            id = "c_${System.currentTimeMillis()}",
            title = title,
            description = description,
            department = department,
            category = category,
            status = ComplaintStatus.SUBMITTED,
            priority = aiAnalysis.priority,
            visibility = visibility,
            authorName = authorName,
            authorId = authorId,
            createdAt = now(),
            updatedAt = now(),
            upvotes = 0,
            supporters = 0,
            attachmentCount = attachmentCount,
            aiAnalysis = aiAnalysis,
            timeline = listOf(
                TimelineEvent("t1", ComplaintStatus.SUBMITTED, "Complaint Submitted", "Received and logged into the system", now(), authorName, true),
                TimelineEvent("t2", ComplaintStatus.UNDER_REVIEW, "Under Review", "Pending", "", "", false),
                TimelineEvent("t3", ComplaintStatus.ASSIGNED, "Assigned", "Pending", "", "", false),
                TimelineEvent("t4", ComplaintStatus.IN_PROGRESS, "In Progress", "Pending", "", "", false),
                TimelineEvent("t5", ComplaintStatus.RESOLVED, "Resolved", "Pending", "", "", false)
            )
        )
        complaints.add(0, complaint)
        notifications.add(0, NotificationItem(
            "n_${System.currentTimeMillis()}",
            NotificationType.COMPLAINT_SUBMITTED,
            "Complaint Submitted",
            "Your complaint '$title' was submitted and AI-analyzed.",
            now(), false, complaint.id
        ))
        return complaint
    }

    private fun predictPriority(description: String, category: String): ComplaintPriority {
        val text = (description + " " + category).lowercase()
        return when {
            listOf("harassment", "ragging", "safety", "health", "hygienic", "discriminat", "critical", "urgent", "emergency").any { it in text } -> ComplaintPriority.CRITICAL
            listOf("exam", "fee", "scholarship", "wifi", "network", "academic", "not working", "broken", "delay").any { it in text } -> ComplaintPriority.HIGH
            listOf("facility", "transport", "hostel", "library", "ac ").any { it in text } -> ComplaintPriority.MEDIUM
            else -> ComplaintPriority.LOW
        }
    }

    // ── Analytics data ────────────────────────────────────────────────────────

    fun getDepartmentStats(): List<DepartmentStat> = listOf(
        DepartmentStat("Computer Science", 48, 32, 16),
        DepartmentStat("Electrical Eng.", 35, 24, 11),
        DepartmentStat("Mechanical Eng.", 22, 18, 4),
        DepartmentStat("Library", 18, 16, 2),
        DepartmentStat("Hostel", 41, 27, 14),
        DepartmentStat("Cafeteria", 29, 15, 14),
        DepartmentStat("Transport", 19, 12, 7),
        DepartmentStat("Finance", 14, 10, 4)
    )

    fun getPriorityDistribution(): PriorityDistribution {
        val all = complaints
        return PriorityDistribution(
            low = all.count { it.priority == ComplaintPriority.LOW } + 24,
            medium = all.count { it.priority == ComplaintPriority.MEDIUM } + 58,
            high = all.count { it.priority == ComplaintPriority.HIGH } + 41,
            critical = all.count { it.priority == ComplaintPriority.CRITICAL } + 9
        )
    }

    fun getMonthlyComplaints(): List<MonthlyComplaints> = listOf(
        MonthlyComplaints("Jan", 42, 38),
        MonthlyComplaints("Feb", 51, 44),
        MonthlyComplaints("Mar", 67, 55),
        MonthlyComplaints("Apr", 48, 41),
        MonthlyComplaints("May", 35, 32),
        MonthlyComplaints("Jun", 28, 25),
        MonthlyComplaints("Jul", 73, 51)
    )

    fun getAIInsights(): List<AIInsightCard> = listOf(
        AIInsightCard("Total Complaints Analyzed", "1,247", "AI processed and categorized", "+12% this month", true),
        AIInsightCard("Avg. Categorization Confidence", "91.4%", "Across all complaint types", "+2.3%", true),
        AIInsightCard("Critical Predictions", "47", "Correctly flagged by AI", "94% accuracy", true),
        AIInsightCard("Avg. Resolution Time", "3.2 days", "AI-routed complaints", "-0.8 days", true),
        AIInsightCard("Top Complaint Type", "Facility Maintenance", "32% of all complaints", "Trending up", true),
        AIInsightCard("Duplicate Reduction", "38%", "Public feed reduced duplicates", "+9%", true)
    )

    fun getAIAnalysisSummary(): List<Triple<String, Int, Float>> = listOf(
        Triple("Facility Maintenance", 312, 0.93f),
        Triple("Academic Issue", 198, 0.89f),
        Triple("Technical Issue", 167, 0.95f),
        Triple("Hostel Issue", 142, 0.87f),
        Triple("Administrative Delay", 98, 0.91f),
        Triple("Transport Issue", 76, 0.88f)
    )

    fun getTopComplaintTypes(): List<Pair<String, Int>> = listOf(
        "Facility Maintenance" to 312,
        "Academic Issue" to 198,
        "Technical Issue" to 167,
        "Hostel Issue" to 142,
        "Administrative Delay" to 98,
        "Transport Issue" to 76,
        "Examination" to 54,
        "Harassment" to 29
    )

    fun getPriorityAnalysis(): List<Triple<String, Int, Float>> = listOf(
        Triple("Critical", 47, 0.94f),
        Triple("High", 124, 0.91f),
        Triple("Medium", 248, 0.87f),
        Triple("Low", 89, 0.82f)
    )
}