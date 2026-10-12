package com.example.ui.screens

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
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
import com.example.data.model.ReminderNotification
import com.example.data.model.ReminderType
import com.example.data.notification.NotificationHelper
import com.example.ui.theme.Amber500
import com.example.ui.theme.Blue500
import com.example.ui.theme.Emerald600
import com.example.ui.theme.Rose500
import com.example.ui.viewmodel.EconBizViewModel

@Composable
fun RemindersScreen(
    viewModel: EconBizViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val reminders by viewModel.reminders.collectAsState()
    val showCustomDialog by viewModel.showSendReminderDialog.collectAsState()

    var hasPermission by remember {
        mutableStateOf(NotificationHelper.hasNotificationPermission(context))
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasPermission = granted
    }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
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
                        text = "Push Reminders & Alerts",
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.ExtraBold)
                    )
                    Text(
                        text = "Broadcast assignment reminders & motivational nudges",
                        style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                    )
                }

                Button(
                    onClick = { viewModel.showSendReminderDialog.value = true },
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.testTag("new_custom_reminder_button")
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Custom Alert")
                }
            }
        }

        // Notification Permission Banner
        item {
            if (!hasPermission && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF3C7)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Amber500.copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                "Enable Push Notifications",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = Color(0xFF92400E))
                            )
                            Text(
                                "Allow EconBiz to post system notification tray alerts on this device.",
                                style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFFB45309))
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Amber500),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Enable")
                        }
                    }
                }
            } else {
                Surface(
                    color = Color(0xFFD1FAE5),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Emerald600, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Push Notifications Active: Channel 'EconBiz Reminders' ready",
                            style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF065F46), fontWeight = FontWeight.SemiBold)
                        )
                    }
                }
            }
        }

        // 1-Tap Quick Action Broadcast Templates
        item {
            Text(
                text = "1-Tap Quick Broadcasts",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
        }

        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                QuickBroadcastCard(
                    title = "⏳ Assignment Deadline in 24 Hours",
                    message = "Reminder: Assignment 02 (Price Elasticity & Tax Calculations) is due tomorrow at 11:59 PM. Submit on time to keep your streak!",
                    audience = "A/L 2026 Batch",
                    type = ReminderType.DEADLINE,
                    onSend = {
                        viewModel.sendAssignmentReminder(
                            context = context,
                            title = "⏳ EconBiz: Assignment Deadline in 24 Hours!",
                            message = "Reminder: Assignment 02 is due tomorrow at 11:59 PM. Keep your study streak active!",
                            type = ReminderType.DEADLINE,
                            targetAudience = "A/L 2026 Batch"
                        )
                    }
                )

                QuickBroadcastCard(
                    title = "🔴 Live Masterclass in 15 Minutes",
                    message = "Live Zoom Economics Masterclass on Central Banking & Monetary Transmission starts in 15 minutes. Have your notes ready!",
                    audience = "All Enrolled Students",
                    type = ReminderType.LIVE_CLASS,
                    onSend = {
                        viewModel.sendAssignmentReminder(
                            context = context,
                            title = "🔴 EconBiz Live: Class Starting in 15 Mins!",
                            message = "Central Banking & Monetary Policy masterclass discussion starting now.",
                            type = ReminderType.LIVE_CLASS,
                            targetAudience = "All Enrolled Students"
                        )
                    }
                )

                QuickBroadcastCard(
                    title = "🌟 Motivational Streak & Retention Boost",
                    message = "Outstanding dedication! Keep up your daily study streak for the upcoming term exam. Every concept mastered gets you closer to an A*!",
                    audience = "All Active Students",
                    type = ReminderType.MOTIVATIONAL,
                    onSend = {
                        viewModel.sendAssignmentReminder(
                            context = context,
                            title = "🌟 EconBiz: Keep Up Your Amazing Streak!",
                            message = "Consistency is the key to Island Ranks in Economics. Top effort this week!",
                            type = ReminderType.MOTIVATIONAL,
                            targetAudience = "All Active Students"
                        )
                    }
                )
            }
        }

        // Delivered Notification History
        item {
            Text(
                text = "Broadcast History",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
        }

        items(reminders, key = { it.id }) { reminder ->
            ReminderHistoryCard(reminder = reminder)
        }
    }

    // Custom Reminder Dialog
    if (showCustomDialog) {
        var title by remember { mutableStateOf("") }
        var message by remember { mutableStateOf("") }
        var targetAudience by remember { mutableStateOf("All 2026 A/L Students") }
        var reminderType by remember { mutableStateOf(ReminderType.DEADLINE) }

        AlertDialog(
            onDismissRequest = { viewModel.showSendReminderDialog.value = false },
            title = {
                Text("Broadcast Assignment Reminder", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold))
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Notification Title") },
                        placeholder = { Text("e.g. ⏳ Unit 3 Model Papers Due Tonight") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("custom_reminder_title_input")
                    )
                    OutlinedTextField(
                        value = message,
                        onValueChange = { message = it },
                        label = { Text("Notification Message") },
                        placeholder = { Text("e.g. Submit by 11:59 PM to receive auto-graded diagnostic feedback.") },
                        minLines = 3,
                        modifier = Modifier.fillMaxWidth().testTag("custom_reminder_msg_input")
                    )
                    Text("Target Audience:", style = MaterialTheme.typography.labelMedium)
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        val audiences = listOf("All Students", "All 2026 A/L", "2025 Sprint Batch", "Pending Submissions", "At-Risk Students")
                        items(audiences) { aud ->
                            val isChosen = targetAudience == aud
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isChosen) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant)
                                    .clickable { targetAudience = aud }
                                    .padding(horizontal = 8.dp, vertical = 6.dp)
                            ) {
                                Text(aud, fontSize = 11.sp, color = if (isChosen) Color.White else MaterialTheme.colorScheme.onSurface)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.sendAssignmentReminder(
                            context = context,
                            title = title,
                            message = message,
                            type = reminderType,
                            targetAudience = targetAudience
                        )
                    },
                    modifier = Modifier.testTag("confirm_send_reminder_button")
                ) {
                    Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Dispatch Notification")
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.showSendReminderDialog.value = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun QuickBroadcastCard(
    title: String,
    message: String,
    audience: String,
    type: ReminderType,
    onSend: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                )
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = audience,
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 10.sp
                        )
                    )
                }
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = message,
                style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
            )
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Button(
                    onClick = onSend,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Broadcast Push Alert", fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
private fun ReminderHistoryCard(reminder: ReminderNotification) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(
                        when (reminder.type) {
                            ReminderType.DEADLINE -> Color(0xFFFEF3C7)
                            ReminderType.LIVE_CLASS -> Color(0xFFDBEAFE)
                            ReminderType.GRADE_RELEASE -> Color(0xFFD1FAE5)
                            ReminderType.MOTIVATIONAL -> Color(0xFFFFF7ED)
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = when (reminder.type) {
                        ReminderType.DEADLINE -> Icons.Default.Timer
                        ReminderType.LIVE_CLASS -> Icons.Default.Videocam
                        ReminderType.GRADE_RELEASE -> Icons.Default.CheckCircle
                        ReminderType.MOTIVATIONAL -> Icons.Default.Star
                    },
                    contentDescription = null,
                    tint = when (reminder.type) {
                        ReminderType.DEADLINE -> Amber500
                        ReminderType.LIVE_CLASS -> Blue500
                        ReminderType.GRADE_RELEASE -> Emerald600
                        ReminderType.MOTIVATIONAL -> Color(0xFFEA580C)
                    },
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = reminder.title,
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = reminder.timestamp,
                        style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 10.sp)
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = reminder.message,
                    style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Delivered to: ${reminder.targetAudience}",
                    style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.primary, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
                )
            }
        }
    }
}
