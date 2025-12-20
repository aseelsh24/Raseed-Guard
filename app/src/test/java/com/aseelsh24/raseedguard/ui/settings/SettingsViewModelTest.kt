package com.aseelsh24.raseedguard.ui.settings

import android.app.Application
import com.aseelsh24.raseedguard.data.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.mockito.Mockito

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

class SettingsViewModelTest {

    @Test
    fun `alertsEnabled reflects repository state`() = runTest {
        // Arrange
        val fakeRepository = FakeSettingsRepository()
        val mockApplication = Mockito.mock(Application::class.java)
        val viewModel = SettingsViewModel(fakeRepository, mockApplication)

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
        val mockApplication = Mockito.mock(Application::class.java)
        val viewModel = SettingsViewModel(fakeRepository, mockApplication)

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
