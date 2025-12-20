package com.aseelsh24.raseedguard.ui.settings

import android.app.Application
import com.aseelsh24.raseedguard.data.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class FakeSettingsRepository : SettingsRepository {
    private val _alertsEnabled = MutableStateFlow(true)
    private val _weeklyReminderEnabled = MutableStateFlow(true)
    private val _activePlanId = MutableStateFlow<String?>(null)

    override val alertsEnabled: Flow<Boolean> = _alertsEnabled.asStateFlow()
    override val weeklyReminderEnabled: Flow<Boolean> = _weeklyReminderEnabled.asStateFlow()
    override val activePlanId: Flow<String?> = _activePlanId.asStateFlow()

    override suspend fun setActivePlanId(id: String?) {
        _activePlanId.value = id
    }

    override suspend fun setAlertsEnabled(enabled: Boolean) {
        _alertsEnabled.value = enabled
    }

    override suspend fun setWeeklyReminderEnabled(enabled: Boolean) {
        _weeklyReminderEnabled.value = enabled
    }
}

// A simple subclass to instantiate Application for testing purposes.
// This relies on the environment allowing instantiation of Android classes.
// If this fails with "Method not mocked", we might need another approach,
// but without Mockito/Robolectric, options are limited.
class TestApplication : Application()

class SettingsViewModelTest {

    @Test
    fun `alertsEnabled reflects repository state`() = runTest {
        // Arrange
        val fakeRepository = FakeSettingsRepository()
        val testApplication = TestApplication()
        val viewModel = SettingsViewModel(fakeRepository, testApplication)

        // Create a background collector to keep the WhileSubscribed flow active
        val collectJob = backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.alertsEnabled.collect {}
        }

        // Assert Initial
        assertTrue(viewModel.alertsEnabled.value)

        // Act
        fakeRepository.setAlertsEnabled(false)

        // Assert Update
        assertFalse(viewModel.alertsEnabled.value)

        collectJob.cancel()
    }

    @Test
    fun `weeklyReminderEnabled reflects repository state`() = runTest {
        // Arrange
        val fakeRepository = FakeSettingsRepository()
        val testApplication = TestApplication()
        val viewModel = SettingsViewModel(fakeRepository, testApplication)

        // Create a background collector to keep the WhileSubscribed flow active
        val collectJob = backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.weeklyReminderEnabled.collect {}
        }

        // Assert Initial
        assertTrue(viewModel.weeklyReminderEnabled.value)

        // Act
        fakeRepository.setWeeklyReminderEnabled(false)

        // Assert Update
        assertFalse(viewModel.weeklyReminderEnabled.value)

        collectJob.cancel()
    }
}
