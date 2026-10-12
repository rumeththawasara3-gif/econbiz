package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Submission
import com.example.data.model.SubmissionStatus
import com.example.ui.components.SubmissionCard
import com.example.ui.theme.Amber500
import com.example.ui.theme.Blue500
import com.example.ui.theme.Emerald500
import com.example.ui.theme.Emerald600
import com.example.ui.viewmodel.EconBizViewModel

@Composable
fun AutoGradingScreen(
    viewModel: EconBizViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val assignments by viewModel.assignments.collectAsState()
    val submissions by viewModel.submissions.collectAsState()
    val isGrading by viewModel.isGradingInProgress.collectAsState()
    val gradingModalResult by viewModel.gradingSuccessModal.collectAsState()
    val selectedSubmission by viewModel.selectedSubmission.collectAsState()

    var selectedAssignmentId by remember { mutableStateOf<String?>(null) }

    val pendingCount = submissions.count { it.status == SubmissionStatus.PENDING_GRADING }
    val gradedCount = submissions.count { it.status != SubmissionStatus.PENDING_GRADING }

    val displayedSubmissions = remember(submissions, selectedAssignmentId) {
        if (selectedAssignmentId == null) submissions
        else submissions.filter { it.assignmentId == selectedAssignmentId }
    }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero Auto-Grading Engine Banner
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth().testTag("auto_grader_hero")
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.linearGradient(
                                listOf(Color(0xFF0F284E), Color(0xFF1E3A8A))
                            )
                        )
                        .padding(20.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Default.Psychology,
                                    contentDescription = null,
                                    tint = Amber500,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "EconBiz Auto-Grading Engine",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (pendingCount > 0) Amber500 else Emerald500)
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = if (pendingCount > 0) "$pendingCount Queued" else "Up to date",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "Instant automated rubric evaluation for A/L & O/L economics papers. Parses formulas, economic reasoning, and generates student feedback.",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Color.White.copy(alpha = 0.9f)
                            )
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = { viewModel.runAutomatedBatchGrading(context) },
                            enabled = !isGrading,
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (pendingCount > 0) Color(0xFFD97706) else Emerald600
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("run_batch_auto_grader_button")
                        ) {
                            if (isGrading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    color = Color.White,
                                    strokeWidth = 2.dp
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Executing Rubric Scan & Auto-Grading...", fontWeight = FontWeight.Bold)
                            } else {
                                Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (pendingCount > 0) "Run Batch Auto-Grade ($pendingCount Papers)" else "Re-Run All Rubric Auto-Grading",
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }

        // Assignment Selection Chips
        item {
            Column {
                Text(
                    text = "Economics Assignments",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                Spacer(modifier = Modifier.height(8.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    item {
                        val isAllSelected = selectedAssignmentId == null
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isAllSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant)
                                .clickable { selectedAssignmentId = null }
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = "All Submissions (${submissions.size})",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    color = if (isAllSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                                    fontWeight = FontWeight.SemiBold
                                )
                            )
                        }
                    }
                    items(assignments) { assignment ->
                        val isSelected = selectedAssignmentId == assignment.id
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant)
                                .clickable { selectedAssignmentId = assignment.id }
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = "${assignment.topic} (${assignment.pendingCount} pending)",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                                    fontWeight = FontWeight.SemiBold
                                )
                            )
                        }
                    }
                }
            }
        }

        // Submissions List
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Student Submissions",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    text = "$gradedCount Graded • $pendingCount Pending",
                    style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                )
            }
        }

        items(displayedSubmissions, key = { it.id }) { submission ->
            SubmissionCard(
                submission = submission,
                onInspect = { viewModel.selectedSubmission.value = submission }
            )
        }
    }

    // Celebratory Batch Complete Dialog
    gradingModalResult?.let { result ->
        AlertDialog(
            onDismissRequest = { viewModel.dismissGradingModal() },
            icon = {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(Emerald600),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                }
            },
            title = {
                Text(
                    "Batch Auto-Grading Complete!",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Successfully evaluated and graded ${result.gradedCount} student papers using EconBiz curriculum rubrics.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Papers Graded:", style = MaterialTheme.typography.bodySmall)
                                Text("${result.gradedCount}", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold))
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Class Average Score:", style = MaterialTheme.typography.bodySmall)
                                Text("${result.averageScore}%", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold, color = Emerald600))
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Top Performer:", style = MaterialTheme.typography.bodySmall)
                                Text(result.topStudentName, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary))
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Achievement Badges:", style = MaterialTheme.typography.bodySmall)
                                Text("+${result.badgesAwarded} Awarded", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold, color = Amber500))
                            }
                        }
                    }
                    Text(
                        text = "Students were notified and their study streaks and GPAs updated.",
                        style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { viewModel.dismissGradingModal() },
                    colors = ButtonDefaults.buttonColors(containerColor = Emerald600)
                ) {
                    Text("Awesome!")
                }
            }
        )
    }

    // Inspect & Rubric Review Dialog
    selectedSubmission?.let { sub ->
        var currentScore by remember(sub) { mutableFloatStateOf((sub.score ?: 75).toFloat()) }
        var currentFeedback by remember(sub) { mutableStateOf(sub.autoGradingFeedback.ifBlank { "Good conceptual understanding." }) }

        AlertDialog(
            onDismissRequest = { viewModel.selectedSubmission.value = null },
            title = {
                Column {
                    Text(
                        sub.studentName,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        "${sub.studentIndex} • ${sub.assignmentTitle}",
                        style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                    )
                }
            },
            text = {
                Column(
                    modifier = Modifier.verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text("Student's Answer:", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = sub.studentAnswerText,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(10.dp)
                        )
                    }

                    Text("Auto-Graded Rubric Breakdown:", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
                    sub.rubricBreakdown.forEach { item ->
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.surface,
                            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(8.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(item.criterion, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold), modifier = Modifier.weight(1f))
                                    Text("${item.awardedPoints} / ${item.maxPoints} pts", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary))
                                }
                                if (item.evaluationNotes.isNotBlank()) {
                                    Text(item.evaluationNotes, style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant))
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Adjust Score: ${currentScore.toInt()} / 100", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
                    Slider(
                        value = currentScore,
                        onValueChange = { currentScore = it },
                        valueRange = 0f..100f,
                        steps = 99
                    )

                    OutlinedTextField(
                        value = currentFeedback,
                        onValueChange = { currentFeedback = it },
                        label = { Text("Teacher Feedback / Coaching Notes") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.updateSubmission(sub.id, currentScore.toInt(), currentFeedback)
                    }
                ) {
                    Text("Save & Finalize Grade")
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.selectedSubmission.value = null }) {
                    Text("Close")
                }
            }
        )
    }
}
