package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.EnrollmentStatus
import com.example.data.model.ReminderType
import com.example.data.notification.NotificationHelper
import com.example.data.repository.EconBizRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("EconBiz", appName)
  }

  @Test
  fun `verify repository initial data and automated batch grading`() {
    val repo = EconBizRepository()
    val initialSubmissions = repo.submissions.value
    assertTrue("Should have initial submissions", initialSubmissions.isNotEmpty())

    // Run batch grading
    val result = repo.runAutomatedBatchGrading()
    assertTrue("Should have graded pending papers", result.gradedCount > 0)
    assertTrue("Average score should be positive", result.averageScore > 0f)

    // Verify stats
    val stats = repo.getPerformanceStats()
    assertEquals("All pending should be graded", 0, stats.pendingAutoGradingCount)
  }

  @Test
  fun `verify enrollment approval workflow`() {
    val repo = EconBizRepository()
    val pending = repo.enrollments.value.first { it.status == EnrollmentStatus.PENDING_APPROVAL }
    repo.approveEnrollment(pending.id)

    val updated = repo.enrollments.value.first { it.id == pending.id }
    assertEquals(EnrollmentStatus.ACTIVE, updated.status)
  }
}
