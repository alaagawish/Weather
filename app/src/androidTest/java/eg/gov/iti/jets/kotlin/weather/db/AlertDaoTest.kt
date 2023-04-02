package eg.gov.iti.jets.kotlin.weather.db

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import eg.gov.iti.jets.kotlin.weather.MainRule
import eg.gov.iti.jets.kotlin.weather.model.AlertsDB
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@SmallTest
class AlertDaoTest {

    @ExperimentalCoroutinesApi
    @get:Rule
    val mainRule = MainRule()

    @ExperimentalCoroutinesApi
    @get:Rule
    val rule = InstantTaskExecutorRule()


    private lateinit var dayDatabase: DayDatabase

    @Before
    fun setUp() {
        dayDatabase = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(), DayDatabase::class.java
        ).build()
    }

    @After
    fun tearDown() {
        dayDatabase.close()

    }

    @Test
    fun getAllAlerts_Alerts() = runBlockingTest {

        val alert = AlertsDB(1, "new alert", 1679556049, 1679666049, "description", "tag", false)
        val alert2 = AlertsDB(2, "new alert2", 1679556049, 1679666049, "description", "tag", false)
        val alert3 = AlertsDB(3, "new alert2", 1679556049, 1679666049, "description", "tag", false)
        val alert4 = AlertsDB(4, "new alert2", 1679556049, 1679666049, "description", "tag", false)
        delay(100)
        dayDatabase.getAlertsDao().addAlert(alert)
        dayDatabase.getAlertsDao().addAlert(alert2)
        dayDatabase.getAlertsDao().addAlert(alert3)
        dayDatabase.getAlertsDao().addAlert(alert4)
        val job = coroutineContext.job
        if (job.isCompleted) {
            dayDatabase.getAlertsDao().getAllAlerts.collectLatest { res ->
                assertThat(
                    res.size,
                    `is`(4)
                )
            }
        }

        val checkJob = coroutineContext.job
        if (checkJob.isCompleted) {
            dayDatabase.getAlertsDao().getAllAlerts.collectLatest { res ->
                assertThat(
                    res[2],
                    `is`(alert3)
                )
            }
        }

    }

    @Test
    fun addAlert_Alert_AddDone() = runBlockingTest {

        val alert = AlertsDB(1, "new alert", 1679556049, 1679666049, "description", "tag", false)
        delay(100)
        dayDatabase.getAlertsDao().addAlert(alert)
        val job = coroutineContext.job
        if (job.isCompleted) {
            dayDatabase.getAlertsDao().getAllAlerts.collectLatest { res ->
                assertThat(
                    res[0].id,
                    `is`(alert.id)
                )
            }
        }

    }

    @Test
    fun deleteAlert_Alert_DeleteDone() = runBlockingTest {

        val alert = AlertsDB(1, "new alert", 1679556049, 1679666049, "description", "tag", false)
        val alert2 = AlertsDB(2, "new alert2", 1679556049, 1679666049, "description", "tag", false)
        delay(100)
        dayDatabase.getAlertsDao().addAlert(alert)
        dayDatabase.getAlertsDao().addAlert(alert2)
        val job = coroutineContext.job
        if (job.isCompleted) {
            dayDatabase.getAlertsDao().getAllAlerts.collectLatest { res ->
                assertThat(
                    res.size,
                    `is`(2)
                )
            }
        }

        dayDatabase.getAlertsDao().deleteAlert(alert)
        val deleteJob = coroutineContext.job
        if (deleteJob.isCompleted) {
            dayDatabase.getAlertsDao().getAllAlerts.collectLatest { res ->
                assertThat(
                    res.size,
                    `is`(1)
                )
            }
        }

    }

}