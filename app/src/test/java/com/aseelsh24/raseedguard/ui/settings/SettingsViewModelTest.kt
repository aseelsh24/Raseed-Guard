package com.aseelsh24.raseedguard.ui.settings

import android.app.Application
import com.aseelsh24.raseedguard.data.repository.SettingsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
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
class TestApplication : Application()

@OptIn(ExperimentalCoroutinesApi::class)
class SettingsViewModelTest {

    @Before
    fun setup() {
        Dispatchers.setMain(StandardTestDispatcher())
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

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

        // Wait for initial value to propagate (though initialValue is immediate, connection might take a tick)
        advanceUntilIdle()

        // Assert Initial
        assertTrue("Initial value should be true", viewModel.alertsEnabled.value)

        // Act
        fakeRepository.setAlertsEnabled(false)

        // Wait for flow emission to be processed
        advanceUntilIdle()

        // Assert Update
        assertFalse("Value should be false after repository update", viewModel.alertsEnabled.value)

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

        advanceUntilIdle()

        // Assert Initial
        assertTrue("Initial value should be true", viewModel.weeklyReminderEnabled.value)

        // Act
        fakeRepository.setWeeklyReminderEnabled(false)

        advanceUntilIdle()

        // Assert Update
        assertFalse("Value should be false after repository update", viewModel.weeklyReminderEnabled.value)

        collectJob.cancel()
    }
}
