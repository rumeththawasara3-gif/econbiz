package com.example.data.repository

import com.example.data.model.Assignment
import com.example.data.model.Course
import com.example.data.model.Enrollment
import com.example.data.model.EnrollmentStatus
import com.example.data.model.GradeDistribution
import com.example.data.model.PaymentStatus
import com.example.data.model.PerformanceStats
import com.example.data.model.ReminderNotification
import com.example.data.model.ReminderType
import com.example.data.model.RubricItem
import com.example.data.model.Student
import com.example.data.model.Submission
import com.example.data.model.SubmissionStatus
import com.example.data.model.TopicMastery
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

data class BatchGradingResult(
    val gradedCount: Int,
    val averageScore: Float,
    val topStudentName: String,
    val badgesAwarded: Int
)

class EconBizRepository {

    private val _courses = MutableStateFlow<List<Course>>(emptyList())
    val courses: StateFlow<List<Course>> = _courses.asStateFlow()

    private val _students = MutableStateFlow<List<Student>>(emptyList())
    val students: StateFlow<List<Student>> = _students.asStateFlow()

    private val _enrollments = MutableStateFlow<List<Enrollment>>(emptyList())
    val enrollments: StateFlow<List<Enrollment>> = _enrollments.asStateFlow()

    private val _assignments = MutableStateFlow<List<Assignment>>(emptyList())
    val assignments: StateFlow<List<Assignment>> = _assignments.asStateFlow()

    private val _submissions = MutableStateFlow<List<Submission>>(emptyList())
    val submissions: StateFlow<List<Submission>> = _submissions.asStateFlow()

    private val _reminders = MutableStateFlow<List<ReminderNotification>>(emptyList())
    val reminders: StateFlow<List<ReminderNotification>> = _reminders.asStateFlow()

    init {
        seedInitialData()
    }

    private fun seedInitialData() {
        val course1 = Course(
            id = "c1",
            title = "A/L 2026 Economics Master Theory",
            gradeBatch = "2026 A/L",
            code = "ECON-26-TH",
            enrolledCount = 124,
            colorHex = 0xFF1E3A8A,
            description = "Complete syllabus coverage: Microeconomics, Macroeconomics, Money & Banking, and Public Finance.",
            syllabusTopics = listOf("Price Mechanism", "Elasticity", "National Income", "Monetary Policy", "Fiscal Deficit")
        )
        val course2 = Course(
            id = "c2",
            title = "A/L 2025 Past Paper & Model Question Sprint",
            gradeBatch = "2025 A/L",
            code = "ECON-25-REV",
            enrolledCount = 88,
            colorHex = 0xFF0D9488,
            description = "Intensive model paper discussions, essay structuring techniques, and timed marking schemes.",
            syllabusTopics = listOf("Essay Techniques", "Part I MCQs", "Structured Evaluation", "Current Economic Issues")
        )
        val course3 = Course(
            id = "c3",
            title = "O/L Business & Accounting Studies Foundation",
            gradeBatch = "2026 O/L",
            code = "BIZ-OL-26",
            enrolledCount = 65,
            colorHex = 0xFFD97706,
            description = "Foundations of business environment, double entry bookkeeping, and marketing fundamentals.",
            syllabusTopics = listOf("Business Environment", "Sole Proprietorship", "Double Entry", "Financial Statements")
        )
        val course4 = Course(
            id = "c4",
            title = "A/L 2027 Advanced Economics Starter Batch",
            gradeBatch = "2027 A/L",
            code = "ECON-27-BEG",
            enrolledCount = 42,
            colorHex = 0xFF7C3AED,
            description = "Early orientation to basic economics problems, scarcity, opportunity cost, and production possibility curves.",
            syllabusTopics = listOf("Scarcity & Choice", "Production Possibility Curve", "Economic Systems", "Demand Theory")
        )
        _courses.value = listOf(course1, course2, course3, course4)

        // Seed Students
        val studentList = listOf(
            Student(
                id = "s1",
                fullName = "Nethmi Bandara",
                indexNumber = "EB-2026-012",
                school = "Visakha Vidyalaya, Colombo",
                email = "nethmi.b@gmail.com",
                phone = "+94 77 234 5678",
                targetExamYear = "2026 A/L",
                enrolledCourseId = "c1",
                courseTitle = "A/L 2026 Economics Master Theory",
                overallAverage = 92.5f,
                letterGrade = "A*",
                studyStreakDays = 24,
                attendanceRate = 98,
                isAtRisk = false,
                badges = listOf("Macro Master", "100% Submission Streak", "Top Analyst", "PED Ace"),
                bio = "Targeting Island Rank 1 in Commerce Stream."
            ),
            Student(
                id = "s2",
                fullName = "Kavindu Jayasuriya",
                indexNumber = "EB-2026-034",
                school = "Royal College, Colombo",
                email = "kavindu.j@gmail.com",
                phone = "+94 71 890 1234",
                targetExamYear = "2026 A/L",
                enrolledCourseId = "c1",
                courseTitle = "A/L 2026 Economics Master Theory",
                overallAverage = 84.0f,
                letterGrade = "A",
                studyStreakDays = 15,
                attendanceRate = 92,
                isAtRisk = false,
                badges = listOf("PPC Pioneer", "Fiscal Pro", "Consistent Learner"),
                bio = "Interested in central banking and monetary policy."
            ),
            Student(
                id = "s3",
                fullName = "Daham Senaratne",
                indexNumber = "EB-2025-078",
                school = "Ananda College, Colombo",
                email = "daham.sena@gmail.com",
                phone = "+94 70 456 7890",
                targetExamYear = "2025 A/L",
                enrolledCourseId = "c2",
                courseTitle = "A/L 2025 Past Paper & Model Question Sprint",
                overallAverage = 78.5f,
                letterGrade = "B",
                studyStreakDays = 9,
                attendanceRate = 88,
                isAtRisk = false,
                badges = listOf("Model Paper Sprinter", "Speedy Grader"),
                bio = "Aiming for an A in upcoming A/L exam."
            ),
            Student(
                id = "s4",
                fullName = "Dilini Wijesekara",
                indexNumber = "EB-2026-091",
                school = "Devi Balika Vidyalaya",
                email = "dilini.w@gmail.com",
                phone = "+94 76 345 6789",
                targetExamYear = "2026 A/L",
                enrolledCourseId = "c1",
                courseTitle = "A/L 2026 Economics Master Theory",
                overallAverage = 68.0f,
                letterGrade = "C",
                studyStreakDays = 5,
                attendanceRate = 79,
                isAtRisk = false,
                badges = listOf("Elasticity Ace"),
                bio = "Needs more revision on exchange rate mechanics."
            ),
            Student(
                id = "s5",
                fullName = "Rashmika Fernando",
                indexNumber = "EB-2026-115",
                school = "St. Joseph's College, Colombo",
                email = "rashmika.f@gmail.com",
                phone = "+94 77 987 6543",
                targetExamYear = "2026 A/L",
                enrolledCourseId = "c1",
                courseTitle = "A/L 2026 Economics Master Theory",
                overallAverage = 42.0f,
                letterGrade = "F",
                studyStreakDays = 1,
                attendanceRate = 54,
                isAtRisk = true,
                badges = listOf("Starter"),
                bio = "Missed Unit 3 tests. Needs retention support & reminder."
            ),
            Student(
                id = "s6",
                fullName = "Tharushi Alwis",
                indexNumber = "EB-2026-140",
                school = "Musaeus College, Colombo",
                email = "tharushi.a@gmail.com",
                phone = "+94 72 112 2334",
                targetExamYear = "2026 A/L",
                enrolledCourseId = "c1",
                courseTitle = "A/L 2026 Economics Master Theory",
                overallAverage = 88.0f,
                letterGrade = "A",
                studyStreakDays = 19,
                attendanceRate = 95,
                isAtRisk = false,
                badges = listOf("Macro Master", "Fast Responder", "PPC Pioneer"),
                bio = "Excellent in mathematical economics calculations."
            )
        )
        _students.value = studentList

        // Seed Enrollments
        val enrollmentList = listOf(
            Enrollment(
                id = "e1",
                studentId = "s_new1",
                studentName = "Chamathka Wickramasinghe",
                studentIndex = "EB-2026-189",
                studentSchool = "Sirimavo Bandaranaike Vidyalaya",
                studentPhone = "+94 77 555 4321",
                studentEmail = "chamathka.w@gmail.com",
                courseId = "c1",
                courseTitle = "A/L 2026 Economics Master Theory",
                status = EnrollmentStatus.PENDING_APPROVAL,
                paymentStatus = PaymentStatus.SLIP_PENDING,
                enrolledDate = "Today, 10:15 AM",
                paymentReference = "Sampath Bank Slip #SLIP-98421",
                monthlyFee = "LKR 4,500"
            ),
            Enrollment(
                id = "e2",
                studentId = "s_new2",
                studentName = "Sanuka Senadheera",
                studentIndex = "EB-2026-190",
                studentSchool = "Mahanama College, Colombo",
                studentPhone = "+94 71 444 3210",
                studentEmail = "sanuka.sena@gmail.com",
                courseId = "c1",
                courseTitle = "A/L 2026 Economics Master Theory",
                status = EnrollmentStatus.PENDING_APPROVAL,
                paymentStatus = PaymentStatus.PAID_VERIFIED,
                enrolledDate = "Today, 08:30 AM",
                paymentReference = "Online Gateway #TXN-778129",
                monthlyFee = "LKR 4,500"
            ),
            Enrollment(
                id = "e3",
                studentId = "s1",
                studentName = "Nethmi Bandara",
                studentIndex = "EB-2026-012",
                studentSchool = "Visakha Vidyalaya, Colombo",
                studentPhone = "+94 77 234 5678",
                studentEmail = "nethmi.b@gmail.com",
                courseId = "c1",
                courseTitle = "A/L 2026 Economics Master Theory",
                status = EnrollmentStatus.ACTIVE,
                paymentStatus = PaymentStatus.PAID_VERIFIED,
                enrolledDate = "2026-09-01",
                paymentReference = "Monthly Recurring #AUTOPAY-01",
                monthlyFee = "LKR 4,500"
            ),
            Enrollment(
                id = "e4",
                studentId = "s2",
                studentName = "Kavindu Jayasuriya",
                studentIndex = "EB-2026-034",
                studentSchool = "Royal College, Colombo",
                studentPhone = "+94 71 890 1234",
                studentEmail = "kavindu.j@gmail.com",
                courseId = "c1",
                courseTitle = "A/L 2026 Economics Master Theory",
                status = EnrollmentStatus.ACTIVE,
                paymentStatus = PaymentStatus.PAID_VERIFIED,
                enrolledDate = "2026-09-02",
                paymentReference = "Commercial Bank #TX-4401",
                monthlyFee = "LKR 4,500"
            ),
            Enrollment(
                id = "e5",
                studentId = "s_new3",
                studentName = "Minoli Ranasinghe",
                studentIndex = "EB-2027-015",
                studentSchool = "Holy Family Convent, Bambalapitiya",
                studentPhone = "+94 77 888 9900",
                studentEmail = "minoli.r@gmail.com",
                courseId = "c4",
                courseTitle = "A/L 2027 Advanced Economics Starter Batch",
                status = EnrollmentStatus.PENDING_APPROVAL,
                paymentStatus = PaymentStatus.FREE_TRIAL,
                enrolledDate = "Yesterday",
                paymentReference = "7-Day Free Trial Pass",
                monthlyFee = "Free Trial"
            ),
            Enrollment(
                id = "e6",
                studentId = "s5",
                studentName = "Rashmika Fernando",
                studentIndex = "EB-2026-115",
                studentSchool = "St. Joseph's College, Colombo",
                studentPhone = "+94 77 987 6543",
                studentEmail = "rashmika.f@gmail.com",
                courseId = "c1",
                courseTitle = "A/L 2026 Economics Master Theory",
                status = EnrollmentStatus.ACTIVE,
                paymentStatus = PaymentStatus.SCHOLARSHIP,
                enrolledDate = "2026-08-20",
                paymentReference = "EconBiz Merit Support Grant",
                monthlyFee = "Scholarship"
            )
        )
        _enrollments.value = enrollmentList

        // Seed Assignments
        val assignmentList = listOf(
            Assignment(
                id = "a1",
                courseId = "c1",
                courseTitle = "A/L 2026 Economics Master Theory",
                title = "Assignment 02: Price Elasticity of Demand (PED) Calculations & Tax Incidence",
                topic = "Microeconomics",
                deadline = "Tomorrow, 11:59 PM",
                maxScore = 100,
                submissionCount = 4,
                pendingCount = 2,
                gradedCount = 2,
                averageScore = 86.0f,
                description = "Calculate point elasticity and arc elasticity, determine revenue impact of price changes, and analyze consumer vs producer tax burden.",
                questionSample = "Q1: If price increases from Rs. 40 to Rs. 50 and quantity demanded falls from 200 to 140, calculate PED. Explain if demand is elastic.",
                rubricKeys = listOf("Formula & Percentage Change Accuracy (40 pts)", "Interpretation of Elastic vs Inelastic (35 pts)", "Tax Incidence Diagram Reasoning (25 pts)")
            ),
            Assignment(
                id = "a2",
                courseId = "c1",
                courseTitle = "A/L 2026 Economics Master Theory",
                title = "Assignment 03: Central Bank Open Market Operations & Inflation Control",
                topic = "Monetary Economics",
                deadline = "Friday, 06:00 PM",
                maxScore = 100,
                submissionCount = 3,
                pendingCount = 3,
                gradedCount = 0,
                averageScore = 0f,
                description = "Evaluate how the Central Bank of Sri Lanka uses policy interest rates and statutory reserve ratios (SRR) to curb inflation.",
                questionSample = "Q1: Explain the monetary transmission mechanism when the policy repo rate is increased by 150 basis points.",
                rubricKeys = listOf("Policy Rate Definition & CBSL Tools (35 pts)", "Commercial Bank Lending Transmission (35 pts)", "Aggregate Demand & CPI Impact (30 pts)")
            ),
            Assignment(
                id = "a3",
                courseId = "c2",
                courseTitle = "A/L 2025 Past Paper & Model Question Sprint",
                title = "Unit 4 Evaluation: Fiscal Deficit & External Debt Restructuring",
                topic = "Public Finance & Macroeconomics",
                deadline = "Next Monday, 11:59 PM",
                maxScore = 100,
                submissionCount = 5,
                pendingCount = 0,
                gradedCount = 5,
                averageScore = 79.5f,
                description = "Essay structure analysis on primary budget deficit vs overall fiscal deficit and revenue mobilization through direct vs indirect taxation.",
                questionSample = "Distinguish between Primary Budget Deficit and Overall Budget Deficit with Sri Lankan budgetary examples.",
                rubricKeys = listOf("Deficit Definitions & Formulas (40 pts)", "Tax Mobilization Comparison (30 pts)", "Debt Sustainability Reasoning (30 pts)")
            )
        )
        _assignments.value = assignmentList

        // Seed Submissions
        val submissionList = listOf(
            Submission(
                id = "sub1",
                assignmentId = "a1",
                assignmentTitle = "Assignment 02: Price Elasticity of Demand (PED) Calculations",
                courseTitle = "A/L 2026 Economics Master Theory",
                studentId = "s1",
                studentName = "Nethmi Bandara",
                studentIndex = "EB-2026-012",
                submittedAt = "Yesterday, 04:30 PM",
                status = SubmissionStatus.AUTO_GRADED,
                score = 96,
                letterGrade = "A*",
                studentAnswerText = "PED = (% ΔQ) / (% ΔP) = ((140-200)/200) / ((50-40)/40) = (-30%) / (+25%) = -1.20. Since |PED| > 1, demand is price elastic. Total revenue will decrease when price rises. In elastic demand, consumer tax burden is lower than producer burden.",
                autoGradingFeedback = "Outstanding algorithmic solution. Formula, percentage calculations, absolute elasticity classification, and tax burden conclusion are all 100% correct.",
                rubricBreakdown = listOf(
                    RubricItem("Formula & Calculation Accuracy", 40, 40, "Flawless arithmetic and percentage steps."),
                    RubricItem("Elasticity Interpretation", 35, 34, "Precise explanation of revenue effect."),
                    RubricItem("Tax Incidence Reasoning", 25, 22, "Accurate deduction of consumer vs producer incidence.")
                ),
                accuracyAnalysis = "Concept match: 98% | Semantic economics terms: 10/10 | Calculation precision: 100%"
            ),
            Submission(
                id = "sub2",
                assignmentId = "a1",
                assignmentTitle = "Assignment 02: Price Elasticity of Demand (PED) Calculations",
                courseTitle = "A/L 2026 Economics Master Theory",
                studentId = "s2",
                studentName = "Kavindu Jayasuriya",
                studentIndex = "EB-2026-034",
                submittedAt = "Yesterday, 06:12 PM",
                status = SubmissionStatus.AUTO_GRADED,
                score = 82,
                letterGrade = "A",
                studentAnswerText = "PED formula is (ΔQ/Q) / (ΔP/P). Here ΔQ is -60, Q=200, so -30%. ΔP is 10, P=40, so 25%. PED = -1.2. The commodity is elastic. Raising price reduces total revenue. Tax incidence falls mostly on seller.",
                autoGradingFeedback = "Strong mathematical proof and correct elasticity coefficient. Minor omission: could explicitly elaborate on why producers bear more burden (steeper supply relative to elastic demand).",
                rubricBreakdown = listOf(
                    RubricItem("Formula & Calculation Accuracy", 40, 38, "Accurate formulas and final quotient."),
                    RubricItem("Elasticity Interpretation", 35, 28, "Correct conclusion on total revenue."),
                    RubricItem("Tax Incidence Reasoning", 25, 16, "Brief explanation provided; diagram citation omitted.")
                ),
                accuracyAnalysis = "Concept match: 86% | Semantic economics terms: 8/10 | Calculation precision: 95%"
            ),
            Submission(
                id = "sub3",
                assignmentId = "a1",
                assignmentTitle = "Assignment 02: Price Elasticity of Demand (PED) Calculations",
                courseTitle = "A/L 2026 Economics Master Theory",
                studentId = "s4",
                studentName = "Dilini Wijesekara",
                studentIndex = "EB-2026-091",
                submittedAt = "Today, 09:20 AM",
                status = SubmissionStatus.PENDING_GRADING,
                score = null,
                letterGrade = null,
                studentAnswerText = "PED = percentage change in quantity divided by percentage change in price. Percentage change in Q = -60 / 200 = -30%. Percentage change in P = 10 / 40 = 25%. Answer = -1.2. Since it is greater than 1 without sign, it is elastic demand.",
                autoGradingFeedback = "",
                rubricBreakdown = listOf(
                    RubricItem("Formula & Calculation Accuracy", 40, 0, "Awaiting automated evaluation"),
                    RubricItem("Elasticity Interpretation", 35, 0, "Awaiting automated evaluation"),
                    RubricItem("Tax Incidence Reasoning", 25, 0, "Awaiting automated evaluation")
                ),
                accuracyAnalysis = "Ready for Auto-Grading engine run"
            ),
            Submission(
                id = "sub4",
                assignmentId = "a2",
                assignmentTitle = "Assignment 03: Central Bank Open Market Operations & Inflation Control",
                courseTitle = "A/L 2026 Economics Master Theory",
                studentId = "s6",
                studentName = "Tharushi Alwis",
                studentIndex = "EB-2026-140",
                submittedAt = "Today, 11:05 AM",
                status = SubmissionStatus.PENDING_GRADING,
                score = null,
                letterGrade = null,
                studentAnswerText = "When CBSL hikes policy repo rates, commercial bank borrowing costs increase. Commercial banks raise lending rates, making investment loans and consumer credit more expensive. This shrinks money supply and slows aggregate demand (AD), putting downward pressure on general price level (CPI).",
                autoGradingFeedback = "",
                rubricBreakdown = listOf(
                    RubricItem("Policy Rate Definition & CBSL Tools", 35, 0, "Awaiting automated evaluation"),
                    RubricItem("Commercial Bank Lending Transmission", 35, 0, "Awaiting automated evaluation"),
                    RubricItem("Aggregate Demand & CPI Impact", 30, 0, "Awaiting automated evaluation")
                ),
                accuracyAnalysis = "Ready for Auto-Grading engine run"
            ),
            Submission(
                id = "sub5",
                assignmentId = "a2",
                assignmentTitle = "Assignment 03: Central Bank Open Market Operations & Inflation Control",
                courseTitle = "A/L 2026 Economics Master Theory",
                studentId = "s5",
                studentName = "Rashmika Fernando",
                studentIndex = "EB-2026-115",
                submittedAt = "Today, 10:45 AM",
                status = SubmissionStatus.PENDING_GRADING,
                score = null,
                letterGrade = null,
                studentAnswerText = "Central bank increases interest rates so people save more money and spend less in shops. When people spend less, shops decrease their prices so inflation goes down.",
                autoGradingFeedback = "",
                rubricBreakdown = listOf(
                    RubricItem("Policy Rate Definition & CBSL Tools", 35, 0, "Awaiting automated evaluation"),
                    RubricItem("Commercial Bank Lending Transmission", 35, 0, "Awaiting automated evaluation"),
                    RubricItem("Aggregate Demand & CPI Impact", 30, 0, "Awaiting automated evaluation")
                ),
                accuracyAnalysis = "Ready for Auto-Grading engine run"
            )
        )
        _submissions.value = submissionList

        // Seed Reminders
        val reminderList = listOf(
            ReminderNotification(
                id = "rem1",
                title = "⏳ EconBiz Reminder: PED Assignment Due Tomorrow!",
                message = "Friendly reminder: Assignment 02 (Price Elasticity & Tax Incidence) is due tomorrow at 11:59 PM. Submit on time to keep your streak active!",
                timestamp = "Today, 09:00 AM",
                targetAudience = "All 2026 A/L Students",
                type = ReminderType.DEADLINE
            ),
            ReminderNotification(
                id = "rem2",
                title = "🌟 Motivational Boost: Top Streak Achievers",
                message = "Congratulations to students with 14+ day study streaks! Keep up your consistency for the upcoming A/L Economics term test.",
                timestamp = "Yesterday",
                targetAudience = "All Students",
                type = ReminderType.MOTIVATIONAL
            ),
            ReminderNotification(
                id = "rem3",
                title = "📊 Auto-Graded Results: Unit 4 Discussion",
                message = "Model answers and diagnostic feedback have been released. Check your rubric score breakdown in the EconBiz portal.",
                timestamp = "2 days ago",
                targetAudience = "A/L 2025 Sprint Batch",
                type = ReminderType.GRADE_RELEASE
            )
        )
        _reminders.value = reminderList
    }

    // Enrollment Actions
    fun approveEnrollment(enrollmentId: String) {
        _enrollments.update { list ->
            list.map { item ->
                if (item.id == enrollmentId) {
                    item.copy(status = EnrollmentStatus.ACTIVE, paymentStatus = PaymentStatus.PAID_VERIFIED)
                } else item
            }
        }
        // If it's a new student, ensure they are in the active student list
        val approved = _enrollments.value.find { it.id == enrollmentId }
        if (approved != null && _students.value.none { it.id == approved.studentId }) {
            val newStudent = Student(
                id = approved.studentId,
                fullName = approved.studentName,
                indexNumber = approved.studentIndex,
                school = approved.studentSchool,
                email = approved.studentEmail,
                phone = approved.studentPhone,
                targetExamYear = "2026 A/L",
                enrolledCourseId = approved.courseId,
                courseTitle = approved.courseTitle,
                overallAverage = 75.0f,
                letterGrade = "B",
                studyStreakDays = 1,
                attendanceRate = 100,
                isAtRisk = false,
                badges = listOf("Newly Enrolled Scholar"),
                bio = "Newly verified enrollment via EconBiz Portal."
            )
            _students.update { it + newStudent }
        }
    }

    fun rejectEnrollment(enrollmentId: String) {
        _enrollments.update { list ->
            list.map { item ->
                if (item.id == enrollmentId) {
                    item.copy(status = EnrollmentStatus.REJECTED)
                } else item
            }
        }
    }

    fun addDirectEnrollment(
        name: String,
        indexNumber: String,
        school: String,
        phone: String,
        email: String,
        courseId: String,
        paymentStatus: PaymentStatus
    ) {
        val course = _courses.value.find { it.id == courseId } ?: _courses.value.first()
        val newStudentId = "s_${System.currentTimeMillis()}"
        val newEnrollment = Enrollment(
            id = "e_${UUID.randomUUID().toString().take(6)}",
            studentId = newStudentId,
            studentName = name,
            studentIndex = indexNumber.ifBlank { "EB-2026-${(100..999).random()}" },
            studentSchool = school.ifBlank { "EconBiz Online Academy" },
            studentPhone = phone,
            studentEmail = email,
            courseId = course.id,
            courseTitle = course.title,
            status = EnrollmentStatus.ACTIVE,
            paymentStatus = paymentStatus,
            enrolledDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()),
            paymentReference = "Manual Teacher Enrollment",
            monthlyFee = "LKR 4,500"
        )
        _enrollments.update { listOf(newEnrollment) + it }

        val newStudent = Student(
            id = newStudentId,
            fullName = name,
            indexNumber = newEnrollment.studentIndex,
            school = newEnrollment.studentSchool,
            email = email,
            phone = phone,
            targetExamYear = course.gradeBatch,
            enrolledCourseId = course.id,
            courseTitle = course.title,
            overallAverage = 78.0f,
            letterGrade = "B",
            studyStreakDays = 1,
            attendanceRate = 100,
            isAtRisk = false,
            badges = listOf("Enrolled Scholar"),
            bio = "Enrolled by instructor in ${course.title}."
        )
        _students.update { listOf(newStudent) + it }
    }

    // Automated Grading Engine
    fun runAutomatedBatchGrading(): BatchGradingResult {
        var gradedCount = 0
        var totalScoreSum = 0
        var topScore = 0
        var topStudent = ""
        var badgesGiven = 0

        _submissions.update { list ->
            list.map { sub ->
                if (sub.status == SubmissionStatus.PENDING_GRADING) {
                    gradedCount++
                    val answerLower = sub.studentAnswerText.lowercase()

                    // Algorithmic evaluation based on economics concepts & criteria
                    val isAssignment1 = sub.assignmentTitle.contains("Elasticity", ignoreCase = true)
                    val score: Int
                    val feedback: String
                    val rubric: List<RubricItem>

                    if (isAssignment1) {
                        val hasFormula = answerLower.contains("change in") || answerLower.contains("%")
                        val hasCorrectNumber = answerLower.contains("-1.2") || answerLower.contains("1.2")
                        val hasElasticTerm = answerLower.contains("elastic")
                        val hasTax = answerLower.contains("tax") || answerLower.contains("burden") || answerLower.contains("incidence")

                        val p1 = if (hasFormula && hasCorrectNumber) 38 else 28
                        val p2 = if (hasElasticTerm) 33 else 20
                        val p3 = if (hasTax) 22 else 15
                        score = (p1 + p2 + p3).coerceIn(40, 98)

                        feedback = "Auto-Graded: Formula derivation verified (${p1}/40 pts). Elasticity threshold classification correct (${p2}/35 pts). ${if (hasTax) "Tax incidence reasoned accurately (${p3}/25 pts)." else "Suggestion: elaborate further on consumer vs seller tax burden."}"
                        rubric = listOf(
                            RubricItem("Formula & Calculation Accuracy", 40, p1, if (p1 >= 35) "Accurate formula and percentage computation." else "Needs clearer fraction working."),
                            RubricItem("Elasticity Interpretation", 35, p2, if (p2 >= 30) "Clear explanation of |PED| > 1." else "Clarify revenue relationship."),
                            RubricItem("Tax Incidence Reasoning", 25, p3, if (p3 >= 20) "Correct analysis of relative elasticities." else "Review consumer vs producer burden.")
                        )
                    } else {
                        // Assignment 2: Monetary Policy & CBSL
                        val hasCbslTools = answerLower.contains("policy") || answerLower.contains("repo") || answerLower.contains("rates")
                        val hasTransmission = answerLower.contains("commercial") || answerLower.contains("borrowing") || answerLower.contains("lending")
                        val hasAdInflation = answerLower.contains("aggregate demand") || answerLower.contains("inflation") || answerLower.contains("cpi") || answerLower.contains("prices")

                        val p1 = if (hasCbslTools) 32 else 22
                        val p2 = if (hasTransmission) 31 else 18
                        val p3 = if (hasAdInflation) 28 else 18
                        score = (p1 + p2 + p3).coerceIn(40, 95)

                        feedback = "Auto-Graded: CBSL policy rate transmission logic verified (${score}/100 pts). ${if (hasAdInflation) "Strong linkage to Aggregate Demand and CPI index." else "Tip: Explicitly link monetary contraction to Aggregate Demand curve shift."}"
                        rubric = listOf(
                            RubricItem("Policy Rate Definition & CBSL Tools", 35, p1, if (p1 >= 30) "Comprehensive understanding of CBSL tools." else "Mention overnight policy repo rate."),
                            RubricItem("Commercial Bank Lending Transmission", 35, p2, if (p2 >= 28) "Solid transmission mechanism flow." else "Explain commercial bank liquidity."),
                            RubricItem("Aggregate Demand & CPI Impact", 30, p3, if (p3 >= 25) "Good synthesis of inflation deceleration." else "Clarify AD shifting leftwards.")
                        )
                    }

                    val gradeLetter = when {
                        score >= 85 -> "A*"
                        score >= 75 -> "A"
                        score >= 65 -> "B"
                        score >= 55 -> "C"
                        score >= 45 -> "S"
                        else -> "F"
                    }

                    totalScoreSum += score
                    if (score > topScore) {
                        topScore = score
                        topStudent = sub.studentName
                    }
                    if (score >= 80) badgesGiven++

                    sub.copy(
                        status = SubmissionStatus.AUTO_GRADED,
                        score = score,
                        letterGrade = gradeLetter,
                        autoGradingFeedback = feedback,
                        rubricBreakdown = rubric,
                        accuracyAnalysis = "Auto-graded successfully • Rubric confidence 94%"
                    )
                } else {
                    sub
                }
            }
        }

        // Update student averages and streaks
        _students.update { list ->
            list.map { student ->
                val gradedForStudent = _submissions.value.filter { it.studentId == student.id && it.score != null }
                if (gradedForStudent.isNotEmpty()) {
                    val newAvg = gradedForStudent.mapNotNull { it.score }.average().toFloat()
                    val newGrade = when {
                        newAvg >= 85 -> "A*"
                        newAvg >= 75 -> "A"
                        newAvg >= 65 -> "B"
                        newAvg >= 55 -> "C"
                        newAvg >= 45 -> "S"
                        else -> "F"
                    }
                    val newBadges = student.badges.toMutableList()
                    if (newAvg >= 80 && !newBadges.contains("Auto-Grade Champion")) {
                        newBadges.add("Auto-Grade Champion")
                    }
                    student.copy(
                        overallAverage = (newAvg * 10).toInt() / 10f,
                        letterGrade = newGrade,
                        studyStreakDays = student.studyStreakDays + 1,
                        isAtRisk = newAvg < 50,
                        badges = newBadges
                    )
                } else student
            }
        }

        // Update assignments graded counts
        _assignments.update { list ->
            list.map { a ->
                val related = _submissions.value.filter { it.assignmentId == a.id }
                val graded = related.count { it.status != SubmissionStatus.PENDING_GRADING }
                val pending = related.count { it.status == SubmissionStatus.PENDING_GRADING }
                val avg = if (graded > 0) related.mapNotNull { it.score }.average().toFloat() else a.averageScore
                a.copy(gradedCount = graded, pendingCount = pending, averageScore = (avg * 10).toInt() / 10f)
            }
        }

        val avgScore = if (gradedCount > 0) totalScoreSum.toFloat() / gradedCount else 0f
        return BatchGradingResult(
            gradedCount = gradedCount,
            averageScore = (avgScore * 10).toInt() / 10f,
            topStudentName = topStudent.ifBlank { "Dilini Wijesekara" },
            badgesAwarded = badgesGiven
        )
    }

    fun updateSubmissionScore(submissionId: String, newScore: Int, feedback: String) {
        val gradeLetter = when {
            newScore >= 85 -> "A*"
            newScore >= 75 -> "A"
            newScore >= 65 -> "B"
            newScore >= 55 -> "C"
            newScore >= 45 -> "S"
            else -> "F"
        }
        _submissions.update { list ->
            list.map { sub ->
                if (sub.id == submissionId) {
                    sub.copy(
                        status = SubmissionStatus.REVIEWED,
                        score = newScore,
                        letterGrade = gradeLetter,
                        autoGradingFeedback = feedback
                    )
                } else sub
            }
        }
    }

    fun sendReminder(title: String, message: String, type: ReminderType, target: String) {
        val newReminder = ReminderNotification(
            id = "rem_${System.currentTimeMillis()}",
            title = title,
            message = message,
            timestamp = "Just now",
            targetAudience = target,
            type = type,
            isDelivered = true
        )
        _reminders.update { listOf(newReminder) + it }
    }

    fun getPerformanceStats(): PerformanceStats {
        val studentList = _students.value
        val enrollmentList = _enrollments.value
        val submissionList = _submissions.value

        val totalEnrolled = enrollmentList.count { it.status == EnrollmentStatus.ACTIVE }
        val pendingEnrollments = enrollmentList.count { it.status == EnrollmentStatus.PENDING_APPROVAL }
        val avgScore = if (studentList.isNotEmpty()) studentList.map { it.overallAverage }.average().toFloat() else 0f
        val passCount = studentList.count { it.overallAverage >= 55 }
        val passRate = if (studentList.isNotEmpty()) (passCount * 100) / studentList.size else 0
        val pendingGrading = submissionList.count { it.status == SubmissionStatus.PENDING_GRADING }
        val atRisk = studentList.count { it.isAtRisk }

        return PerformanceStats(
            totalEnrolled = totalEnrolled,
            pendingEnrollments = pendingEnrollments,
            classAverageScore = (avgScore * 10).toInt() / 10f,
            passRatePercentage = passRate,
            pendingAutoGradingCount = pendingGrading,
            atRiskStudentsCount = atRisk
        )
    }

    fun getGradeDistribution(): List<GradeDistribution> {
        val studentList = _students.value
        val grades = listOf("A*", "A", "B", "C", "S", "F")
        val total = studentList.size.coerceAtLeast(1)

        return grades.map { g ->
            val count = studentList.count { it.letterGrade == g }
            GradeDistribution(
                grade = g,
                count = count,
                percentage = (count.toFloat() / total) * 100f
            )
        }
    }

    fun getTopicMasteries(): List<TopicMastery> {
        return listOf(
            TopicMastery("Price Elasticity of Demand (PED)", 89, 45, "Mastered"),
            TopicMastery("Market Equilibrium & Price Controls", 84, 52, "Proficient"),
            TopicMastery("National Income Accounting (GDP)", 77, 38, "Proficient"),
            TopicMastery("Central Banking & Monetary Policy", 68, 30, "Needs Attention"),
            TopicMastery("Production Possibility Frontier (PPF)", 92, 40, "Mastered"),
            TopicMastery("Fiscal Deficit & Public Debt", 61, 25, "Needs Attention")
        )
    }
}
