package com.example

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.Dashboard
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.PersonAdd
import androidx.compose.material.icons.outlined.School
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.model.EnrollmentStatus
import com.example.data.model.SubmissionStatus
import com.example.data.notification.NotificationHelper
import com.example.ui.components.ConfettiEffect
import com.example.ui.screens.AutoGradingScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.EnrollmentsScreen
import com.example.ui.screens.RemindersScreen
import com.example.ui.screens.StudentsScreen
import com.example.ui.theme.Amber500
import com.example.ui.theme.Emerald500
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.EconBizTab
import com.example.ui.viewmodel.EconBizViewModel
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Initialize Android notification channel for assignment reminders
        NotificationHelper.initNotificationChannel(this)

        setContent {
            MyApplicationTheme {
                EconBizApp()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EconBizApp(
    viewModel: EconBizViewModel = viewModel()
) {
    val currentTab by viewModel.currentTab.collectAsState()
    val confettiTrigger by viewModel.confettiTrigger.collectAsState()
    val uiEvent by viewModel.uiEvent.collectAsState()
    val enrollments by viewModel.enrollments.collectAsState()
    val submissions by viewModel.submissions.collectAsState()

    val pendingEnrollmentsCount = enrollments.count { it.status == EnrollmentStatus.PENDING_APPROVAL }
    val pendingGradingCount = submissions.count { it.status == SubmissionStatus.PENDING_GRADING }

    // Request notification permission if needed on Android 13+
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { /* Handled */ }

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    // Auto-dismiss floating toast event after 3 seconds
    LaunchedEffect(uiEvent) {
        if (uiEvent != null) {
            delay(3500)
            viewModel.clearUiEvent()
        }
    }

    // Handle back button: return to Dashboard if in other tabs
    BackHandler(enabled = currentTab != EconBizTab.DASHBOARD) {
        viewModel.setTab(EconBizTab.DASHBOARD)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(MaterialTheme.colorScheme.primary)
                                    .padding(6.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.TrendingUp,
                                    contentDescription = "EconBiz",
                                    tint = Amber500,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "EconBiz",
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        fontWeight = FontWeight.ExtraBold,
                                        letterSpacing = (-0.5).sp
                                    )
                                )
                                Text(
                                    text = "Economics & Business Portal",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        fontSize = 10.sp
                                    )
                                )
                            }
                        }
                    },
                    actions = {
                        // Quick Push Reminders Button with badge
                        IconButton(
                            onClick = { viewModel.setTab(EconBizTab.REMINDERS) },
                            modifier = Modifier.testTag("top_bar_reminders_button")
                        ) {
                            Icon(
                                Icons.Default.Notifications,
                                contentDescription = "Reminders",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                )
            },
            bottomBar = {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 6.dp,
                    modifier = Modifier
                        .windowInsetsPadding(WindowInsets.navigationBars)
                        .testTag("bottom_nav_bar")
                ) {
                    // Insights / Dashboard
                    NavigationBarItem(
                        selected = currentTab == EconBizTab.DASHBOARD,
                        onClick = { viewModel.setTab(EconBizTab.DASHBOARD) },
                        icon = {
                            Icon(
                                if (currentTab == EconBizTab.DASHBOARD) Icons.Filled.Dashboard else Icons.Outlined.Dashboard,
                                contentDescription = "Insights"
                            )
                        },
                        label = { Text("Insights", fontSize = 11.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            indicatorColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    )

                    // Enrollments with pending badge
                    NavigationBarItem(
                        selected = currentTab == EconBizTab.ENROLLMENTS,
                        onClick = { viewModel.setTab(EconBizTab.ENROLLMENTS) },
                        icon = {
                            BadgedBox(
                                badge = {
                                    if (pendingEnrollmentsCount > 0) {
                                        Badge(containerColor = Amber500) {
                                            Text("$pendingEnrollmentsCount")
                                        }
                                    }
                                }
                            ) {
                                Icon(
                                    if (currentTab == EconBizTab.ENROLLMENTS) Icons.Filled.PersonAdd else Icons.Outlined.PersonAdd,
                                    contentDescription = "Enrollments"
                                )
                            }
                        },
                        label = { Text("Enrollments", fontSize = 11.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            indicatorColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    )

                    // Auto-Grading with pending submissions badge
                    NavigationBarItem(
                        selected = currentTab == EconBizTab.AUTO_GRADING,
                        onClick = { viewModel.setTab(EconBizTab.AUTO_GRADING) },
                        icon = {
                            BadgedBox(
                                badge = {
                                    if (pendingGradingCount > 0) {
                                        Badge(containerColor = Color(0xFFEF4444)) {
                                            Text("$pendingGradingCount")
                                        }
                                    }
                                }
                            ) {
                                Icon(
                                    if (currentTab == EconBizTab.AUTO_GRADING) Icons.Filled.AutoAwesome else Icons.Outlined.AutoAwesome,
                                    contentDescription = "Auto-Grade"
                                )
                            }
                        },
                        label = { Text("Auto-Grade", fontSize = 11.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            indicatorColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    )

                    // Students
                    NavigationBarItem(
                        selected = currentTab == EconBizTab.STUDENTS,
                        onClick = { viewModel.setTab(EconBizTab.STUDENTS) },
                        icon = {
                            Icon(
                                if (currentTab == EconBizTab.STUDENTS) Icons.Filled.School else Icons.Outlined.School,
                                contentDescription = "Students"
                            )
                        },
                        label = { Text("Students", fontSize = 11.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            indicatorColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    )

                    // Reminders
                    NavigationBarItem(
                        selected = currentTab == EconBizTab.REMINDERS,
                        onClick = { viewModel.setTab(EconBizTab.REMINDERS) },
                        icon = {
                            Icon(
                                if (currentTab == EconBizTab.REMINDERS) Icons.Filled.Notifications else Icons.Outlined.Notifications,
                                contentDescription = "Reminders"
                            )
                        },
                        label = { Text("Reminders", fontSize = 11.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            indicatorColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    )
                }
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                when (currentTab) {
                    EconBizTab.DASHBOARD -> DashboardScreen(viewModel = viewModel)
                    EconBizTab.ENROLLMENTS -> EnrollmentsScreen(viewModel = viewModel)
                    EconBizTab.AUTO_GRADING -> AutoGradingScreen(viewModel = viewModel)
                    EconBizTab.STUDENTS -> StudentsScreen(viewModel = viewModel)
                    EconBizTab.REMINDERS -> RemindersScreen(viewModel = viewModel)
                }
            }
        }

        // Celebratory Confetti Particle Canvas overlay
        ConfettiEffect(
            trigger = confettiTrigger,
            modifier = Modifier.fillMaxSize()
        )

        // Floating In-App Toast Pill
        AnimatedVisibility(
            visible = uiEvent != null,
            enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut(),
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 80.dp, start = 16.dp, end = 16.dp)
        ) {
            uiEvent?.let { event ->
                Surface(
                    shape = RoundedCornerShape(24.dp),
                    color = if (event.isSuccess) Color(0xFF0F172A) else Color(0xFF7F1D1D),
                    tonalElevation = 6.dp,
                    shadowElevation = 8.dp
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(if (event.isSuccess) Emerald500 else Color(0xFFF87171))
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = event.message,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Color.White,
                                fontWeight = FontWeight.Medium
                            )
                        )
                    }
                }
            }
        }
    }
}
