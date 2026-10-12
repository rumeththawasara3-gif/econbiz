package com.example.data.model

enum class EnrollmentStatus(val label: String) {
    PENDING_APPROVAL("Pending Approval"),
    ACTIVE("Active Enrolled"),
    REJECTED("Declined"),
    COMPLETED("Completed")
}

enum class PaymentStatus(val label: String) {
    PAID_VERIFIED("Verified Payment"),
    SLIP_PENDING("Bank Slip Pending"),
    FREE_TRIAL("Free 7-Day Trial"),
    SCHOLARSHIP("Academy Scholarship")
}

enum class SubmissionStatus(val label: String) {
    PENDING_GRADING("Needs Grading"),
    AUTO_GRADED("Auto-Graded"),
    REVIEWED("Teacher Verified")
}

enum class ReminderType(val label: String, val iconName: String) {
    DEADLINE("Assignment Deadline", "Timer"),
    LIVE_CLASS("Live Masterclass", "Videocam"),
    GRADE_RELEASE("Auto-Grades Released", "CheckCircle"),
    MOTIVATIONAL("Retention & Streak Boost", "Star")
}

data class Course(
    val id: String,
    val title: String,
    val gradeBatch: String,
    val code: String,
    val enrolledCount: Int,
    val colorHex: Long,
    val description: String,
    val syllabusTopics: List<String>
)

data class Student(
    val id: String,
    val fullName: String,
    val indexNumber: String,
    val school: String,
    val email: String,
    val phone: String,
    val targetExamYear: String,
    val enrolledCourseId: String,
    val courseTitle: String,
    val overallAverage: Float,
    val letterGrade: String,
    val studyStreakDays: Int,
    val attendanceRate: Int,
    val isAtRisk: Boolean,
    val badges: List<String>,
    val bio: String = ""
)

data class Enrollment(
    val id: String,
    val studentId: String,
    val studentName: String,
    val studentIndex: String,
    val studentSchool: String,
    val studentPhone: String,
    val studentEmail: String,
    val courseId: String,
    val courseTitle: String,
    val status: EnrollmentStatus,
    val paymentStatus: PaymentStatus,
    val enrolledDate: String,
    val paymentReference: String,
    val monthlyFee: String
)

data class RubricItem(
    val criterion: String,
    val maxPoints: Int,
    val awardedPoints: Int = 0,
    val evaluationNotes: String = ""
)

data class Assignment(
    val id: String,
    val courseId: String,
    val courseTitle: String,
    val title: String,
    val topic: String,
    val deadline: String,
    val maxScore: Int,
    val submissionCount: Int,
    val pendingCount: Int,
    val gradedCount: Int,
    val averageScore: Float,
    val description: String,
    val questionSample: String,
    val rubricKeys: List<String>
)

data class Submission(
    val id: String,
    val assignmentId: String,
    val assignmentTitle: String,
    val courseTitle: String,
    val studentId: String,
    val studentName: String,
    val studentIndex: String,
    val submittedAt: String,
    val status: SubmissionStatus,
    val score: Int?,
    val letterGrade: String?,
    val studentAnswerText: String,
    val autoGradingFeedback: String,
    val rubricBreakdown: List<RubricItem>,
    val accuracyAnalysis: String
)

data class ReminderNotification(
    val id: String,
    val title: String,
    val message: String,
    val timestamp: String,
    val targetAudience: String,
    val type: ReminderType,
    val isDelivered: Boolean = true
)

data class TopicMastery(
    val topicName: String,
    val masteryPercentage: Int,
    val questionsAnalyzed: Int,
    val status: String // "Mastered", "Proficient", "Needs Attention"
)

data class GradeDistribution(
    val grade: String,
    val count: Int,
    val percentage: Float
)

data class PerformanceStats(
    val totalEnrolled: Int,
    val pendingEnrollments: Int,
    val classAverageScore: Float,
    val passRatePercentage: Int,
    val pendingAutoGradingCount: Int,
    val atRiskStudentsCount: Int
)
