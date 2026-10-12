package com.example.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.Assignment
import com.example.data.model.Course
import com.example.data.model.Enrollment
import com.example.data.model.EnrollmentStatus
import com.example.data.model.GradeDistribution
import com.example.data.model.PaymentStatus
import com.example.data.model.PerformanceStats
import com.example.data.model.ReminderNotification
import com.example.data.model.ReminderType
import com.example.data.model.Student
import com.example.data.model.Submission
import com.example.data.model.TopicMastery
import com.example.data.notification.NotificationHelper
import com.example.data.repository.BatchGradingResult
import com.example.data.repository.EconBizRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class EconBizTab(val label: String) {
    DASHBOARD("Insights"),
    ENROLLMENTS("Enrollments"),
    AUTO_GRADING("Auto-Grading"),
    STUDENTS("Students"),
    REMINDERS("Reminders")
}

data class UiNotificationEvent(
    val message: String,
    val isSuccess: Boolean = true,
    val timestamp: Long = System.currentTimeMillis()
)

class EconBizViewModel(
    private val repository: EconBizRepository = EconBizRepository()
) : ViewModel() {

    private val _currentTab = MutableStateFlow(EconBizTab.DASHBOARD)
    val currentTab: StateFlow<EconBizTab> = _currentTab.asStateFlow()

    val courses: StateFlow<List<Course>> = repository.courses
    val students: StateFlow<List<Student>> = repository.students
    val enrollments: StateFlow<List<Enrollment>> = repository.enrollments
    val assignments: StateFlow<List<Assignment>> = repository.assignments
    val submissions: StateFlow<List<Submission>> = repository.submissions
    val reminders: StateFlow<List<ReminderNotification>> = repository.reminders

    // UI filters
    val enrollmentFilter = MutableStateFlow<EnrollmentStatus?>(null)
    val enrollmentSearchQuery = MutableStateFlow("")
    val selectedCourseFilterId = MutableStateFlow<String?>(null)

    val studentSearchQuery = MutableStateFlow("")
    val studentSortBy = MutableStateFlow("highest_score") // "highest_score", "lowest_score", "name", "streak"

    // Dialog & Inspection States
    val selectedStudent = MutableStateFlow<Student?>(null)
    val selectedSubmission = MutableStateFlow<Submission?>(null)
    val showAddEnrollmentDialog = MutableStateFlow(false)
    val showSendReminderDialog = MutableStateFlow(false)

    // Animation & Celebration triggers
    private val _confettiTrigger = MutableStateFlow(0L)
    val confettiTrigger: StateFlow<Long> = _confettiTrigger.asStateFlow()

    private val _isGradingInProgress = MutableStateFlow(false)
    val isGradingInProgress: StateFlow<Boolean> = _isGradingInProgress.asStateFlow()

    private val _gradingSuccessModal = MutableStateFlow<BatchGradingResult?>(null)
    val gradingSuccessModal: StateFlow<BatchGradingResult?> = _gradingSuccessModal.asStateFlow()

    private val _uiEvent = MutableStateFlow<UiNotificationEvent?>(null)
    val uiEvent: StateFlow<UiNotificationEvent?> = _uiEvent.asStateFlow()

    fun setTab(tab: EconBizTab) {
        _currentTab.value = tab
    }

    fun triggerConfetti() {
        _confettiTrigger.update { it + 1 }
    }

    fun clearUiEvent() {
        _uiEvent.value = null
    }

    fun dismissGradingModal() {
        _gradingSuccessModal.value = null
    }

    // Filtered Enrollments
    val filteredEnrollments = combine(
        enrollments,
        enrollmentFilter,
        enrollmentSearchQuery,
        selectedCourseFilterId
    ) { list, status, query, courseId ->
        list.filter { item ->
            val matchesStatus = status == null || item.status == status
            val matchesCourse = courseId == null || item.courseId == courseId
            val matchesQuery = query.isBlank() ||
                item.studentName.contains(query, ignoreCase = true) ||
                item.studentIndex.contains(query, ignoreCase = true) ||
                item.studentSchool.contains(query, ignoreCase = true)
            matchesStatus && matchesCourse && matchesQuery
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Filtered Students
    val filteredStudents = combine(
        students,
        studentSearchQuery,
        studentSortBy
    ) { list, query, sort ->
        val filtered = list.filter {
            query.isBlank() ||
                it.fullName.contains(query, ignoreCase = true) ||
                it.indexNumber.contains(query, ignoreCase = true) ||
                it.school.contains(query, ignoreCase = true)
        }
        when (sort) {
            "highest_score" -> filtered.sortedByDescending { it.overallAverage }
            "lowest_score" -> filtered.sortedBy { it.overallAverage }
            "streak" -> filtered.sortedByDescending { it.studyStreakDays }
            "name" -> filtered.sortedBy { it.fullName }
            else -> filtered
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun approveEnrollment(enrollment: Enrollment, context: Context?) {
        repository.approveEnrollment(enrollment.id)
        triggerConfetti()
        _uiEvent.value = UiNotificationEvent(
            message = "Approved ${enrollment.studentName}'s enrollment! Account verified.",
            isSuccess = true
        )
        // Optionally fire assignment reminder / welcome notification
        if (context != null) {
            NotificationHelper.sendAssignmentReminderNotification(
                context = context,
                title = "🎓 EconBiz Enrollment Approved!",
                message = "Welcome ${enrollment.studentName}! Your enrollment in ${enrollment.courseTitle} is active.",
                reminderType = ReminderType.GRADE_RELEASE
            )
        }
    }

    fun rejectEnrollment(enrollment: Enrollment) {
        repository.rejectEnrollment(enrollment.id)
        _uiEvent.value = UiNotificationEvent(
            message = "Declined enrollment for ${enrollment.studentName}.",
            isSuccess = false
        )
    }

    fun addEnrollment(
        name: String,
        index: String,
        school: String,
        phone: String,
        email: String,
        courseId: String,
        paymentStatus: PaymentStatus,
        context: Context?
    ) {
        if (name.isBlank()) {
            _uiEvent.value = UiNotificationEvent("Please enter student name", false)
            return
        }
        repository.addDirectEnrollment(
            name = name,
            indexNumber = index,
            school = school,
            phone = phone,
            email = email,
            courseId = courseId,
            paymentStatus = paymentStatus
        )
        showAddEnrollmentDialog.value = false
        triggerConfetti()
        _uiEvent.value = UiNotificationEvent("Successfully enrolled $name!", true)
        if (context != null) {
            NotificationHelper.sendAssignmentReminderNotification(
                context = context,
                title = "🎉 New Student Enrolled: $name",
                message = "Student added with index ${index.ifBlank { "EB-New" }}.",
                reminderType = ReminderType.GRADE_RELEASE
            )
        }
    }

    fun runAutomatedBatchGrading(context: Context) {
        viewModelScope.launch {
            _isGradingInProgress.value = true
            // Realistic slight delay for grading animation & algorithmic parsing
            delay(1200)
            val result = repository.runAutomatedBatchGrading()
            _isGradingInProgress.value = false

            if (result.gradedCount > 0) {
                _gradingSuccessModal.value = result
                triggerConfetti()

                // Trigger real Android push notification for teacher & students
                val notifSent = NotificationHelper.sendAssignmentReminderNotification(
                    context = context,
                    title = "⚡ Auto-Graded ${result.gradedCount} Submissions!",
                    message = "Batch complete! Class Avg: ${result.averageScore}%. Top score: ${result.topStudentName}. Badges awarded: ${result.badgesAwarded}.",
                    reminderType = ReminderType.GRADE_RELEASE
                )

                _uiEvent.value = UiNotificationEvent(
                    message = "Auto-Grading Complete: ${result.gradedCount} papers graded! ${if (notifSent) "Push notification dispatched." else ""}",
                    isSuccess = true
                )
            } else {
                _uiEvent.value = UiNotificationEvent(
                    message = "All current submissions are already graded!",
                    isSuccess = true
                )
            }
        }
    }

    fun updateSubmission(submissionId: String, score: Int, feedback: String) {
        repository.updateSubmissionScore(submissionId, score, feedback)
        selectedSubmission.value = null
        _uiEvent.value = UiNotificationEvent("Submission reviewed & score updated to $score%", true)
    }

    fun sendAssignmentReminder(
        context: Context,
        title: String,
        message: String,
        type: ReminderType,
        targetAudience: String
    ) {
        if (title.isBlank() || message.isBlank()) {
            _uiEvent.value = UiNotificationEvent("Please fill in both title and message", false)
            return
        }

        repository.sendReminder(title, message, type, targetAudience)
        showSendReminderDialog.value = false

        // Dispatch real Android System Notification
        val posted = NotificationHelper.sendAssignmentReminderNotification(
            context = context,
            title = title,
            message = message,
            reminderType = type
        )

        _uiEvent.value = UiNotificationEvent(
            message = if (posted) "Push notification dispatched to students!" else "Reminder scheduled and broadcast logged.",
            isSuccess = true
        )
    }

    fun getPerformanceStats(): PerformanceStats = repository.getPerformanceStats()
    fun getGradeDistribution(): List<GradeDistribution> = repository.getGradeDistribution()
    fun getTopicMasteries(): List<TopicMastery> = repository.getTopicMasteries()
}
