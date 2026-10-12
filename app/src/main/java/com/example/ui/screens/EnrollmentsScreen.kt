package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.EnrollmentStatus
import com.example.data.model.PaymentStatus
import com.example.ui.components.EnrollmentCard
import com.example.ui.theme.Amber500
import com.example.ui.theme.Emerald600
import com.example.ui.viewmodel.EconBizViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EnrollmentsScreen(
    viewModel: EconBizViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val enrollments by viewModel.filteredEnrollments.collectAsState()
    val allEnrollments by viewModel.enrollments.collectAsState()
    val courses by viewModel.courses.collectAsState()
    val currentFilter by viewModel.enrollmentFilter.collectAsState()
    val searchQuery by viewModel.enrollmentSearchQuery.collectAsState()
    val selectedCourseId by viewModel.selectedCourseFilterId.collectAsState()
    val showAddDialog by viewModel.showAddEnrollmentDialog.collectAsState()

    val pendingCount = allEnrollments.count { it.status == EnrollmentStatus.PENDING_APPROVAL }
    val activeCount = allEnrollments.count { it.status == EnrollmentStatus.ACTIVE }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.showAddEnrollmentDialog.value = true },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White,
                shape = CircleShape,
                modifier = Modifier.testTag("fab_add_enrollment")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Direct Enrollment")
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Course Enrollments",
                            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.ExtraBold)
                        )
                        Text(
                            text = "$pendingCount pending approval • $activeCount active students",
                            style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                        )
                    }
                }
            }

            // Search Bar
            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { viewModel.enrollmentSearchQuery.value = it },
                    placeholder = { Text("Search by student name, index, school...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { viewModel.enrollmentSearchQuery.value = "" }) {
                                Icon(Icons.Default.Clear, contentDescription = "Clear")
                            }
                        }
                    },
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("search_enrollment_input")
                )
            }

            // Filter Chips by Status
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    val tabs = listOf(
                        null to "All (${allEnrollments.size})",
                        EnrollmentStatus.PENDING_APPROVAL to "Pending ($pendingCount)",
                        EnrollmentStatus.ACTIVE to "Active ($activeCount)",
                        EnrollmentStatus.REJECTED to "Declined"
                    )

                    items(tabs) { (status, label) ->
                        val isSelected = currentFilter == status
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(
                                    if (isSelected) MaterialTheme.colorScheme.primary
                                    else MaterialTheme.colorScheme.surfaceVariant
                                )
                                .clickable { viewModel.enrollmentFilter.value = status }
                                .padding(horizontal = 14.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = label,
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )
                        }
                    }
                }
            }

            // Course Selector Filter
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    item {
                        val isAllSelected = selectedCourseId == null
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isAllSelected) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.surface)
                                .clickable { viewModel.selectedCourseFilterId.value = null }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "All Batches",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = if (isAllSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                                    fontWeight = FontWeight.SemiBold
                                )
                            )
                        }
                    }
                    items(courses) { course ->
                        val isSelected = selectedCourseId == course.id
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.surface)
                                .clickable { viewModel.selectedCourseFilterId.value = course.id }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = course.gradeBatch,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                                    fontWeight = FontWeight.SemiBold
                                )
                            )
                        }
                    }
                }
            }

            // Enrollment List
            if (enrollments.isEmpty()) {
                item {
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                Icons.Default.FilterList,
                                contentDescription = null,
                                modifier = Modifier.size(36.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "No enrollments found for current filter",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium)
                            )
                        }
                    }
                }
            } else {
                items(enrollments, key = { it.id }) { enrollment ->
                    EnrollmentCard(
                        enrollment = enrollment,
                        onApprove = { viewModel.approveEnrollment(enrollment, context) },
                        onReject = { viewModel.rejectEnrollment(enrollment) }
                    )
                }
            }
        }
    }

    // Add Direct Enrollment Dialog
    if (showAddDialog) {
        var studentName by remember { mutableStateOf("") }
        var indexNumber by remember { mutableStateOf("") }
        var school by remember { mutableStateOf("") }
        var phone by remember { mutableStateOf("+94 ") }
        var email by remember { mutableStateOf("") }
        var selectedCourse by remember { mutableStateOf(courses.firstOrNull()?.id ?: "") }
        var selectedPaymentStatus by remember { mutableStateOf(PaymentStatus.PAID_VERIFIED) }

        AlertDialog(
            onDismissRequest = { viewModel.showAddEnrollmentDialog.value = false },
            title = {
                Text("Enroll Student Directly", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold))
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = studentName,
                        onValueChange = { studentName = it },
                        label = { Text("Student Full Name") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("add_student_name_input")
                    )
                    OutlinedTextField(
                        value = indexNumber,
                        onValueChange = { indexNumber = it },
                        label = { Text("EconBiz Index (e.g. EB-2026-099)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = school,
                        onValueChange = { school = it },
                        label = { Text("School / College") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        label = { Text("WhatsApp / Phone") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email Address") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Text("Select Course Batch:", style = MaterialTheme.typography.labelMedium)
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(courses) { course ->
                            val isChosen = selectedCourse == course.id
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isChosen) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant)
                                    .clickable { selectedCourse = course.id }
                                    .padding(horizontal = 8.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = course.gradeBatch,
                                    fontSize = 11.sp,
                                    color = if (isChosen) Color.White else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.addEnrollment(
                            name = studentName,
                            index = indexNumber,
                            school = school,
                            phone = phone,
                            email = email,
                            courseId = selectedCourse,
                            paymentStatus = selectedPaymentStatus,
                            context = context
                        )
                    },
                    modifier = Modifier.testTag("confirm_enrollment_button")
                ) {
                    Text("Confirm Enrollment")
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.showAddEnrollmentDialog.value = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
