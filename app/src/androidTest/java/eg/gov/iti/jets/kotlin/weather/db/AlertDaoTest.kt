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

    private val alert =
        AlertsDB(1, "new alert", 1679556049, 1679666049, "description", "tag", false)
    private val alert2 =
        AlertsDB(2, "new alert2", 1679556049, 1679666049, "description", "tag", false)
    private val alert3 =
        AlertsDB(3, "new alert2", 1679556049, 1679666049, "description", "tag", false)
    private val alert4 =
        AlertsDB(4, "new alert2", 1679556049, 1679666049, "description", "tag", false)

    @Test
    fun getAllAlerts_Alerts() = runBlockingTest {

        dayDatabase.getAlertsDao().addAlert(alert)
        dayDatabase.getAlertsDao().addAlert(alert2)
        dayDatabase.getAlertsDao().addAlert(alert3)
        dayDatabase.getAlertsDao().addAlert(alert4)
        launch {
            dayDatabase.getAlertsDao().getAllAlerts.collectLatest { res ->
                assertThat(
                    res.size,
                    `is`(4)
                )
                cancel()
            }
        }

        launch {
            dayDatabase.getAlertsDao().getAllAlerts.collectLatest { res ->

                assertThat(
                    res[2],
                    `is`(alert3)
                )
                cancel()
            }
        }

    }

    @Test
    fun addAlert_Alert_AddDone() = runBlockingTest {
        dayDatabase.getAlertsDao().addAlert(alert)
        launch {
            dayDatabase.getAlertsDao().getAllAlerts.collectLatest { res ->
                assertThat(
                    res[0].id,
                    `is`(alert.id)
                )
                cancel()
            }
        }

    }

    @Test
    fun deleteAlert_Alert_DeleteDone() = runBlockingTest {

        dayDatabase.getAlertsDao().addAlert(alert)
        dayDatabase.getAlertsDao().addAlert(alert2)
        launch {
            dayDatabase.getAlertsDao().getAllAlerts.collectLatest { res ->
                assertThat(
                    res.size,
                    `is`(2)
                )
                cancel()
            }
        }

        dayDatabase.getAlertsDao().deleteAlert(alert)
        launch {
            dayDatabase.getAlertsDao().getAllAlerts.collectLatest { res ->
                assertThat(
                    res.size,
                    `is`(1)
                )
                cancel()
            }
        }

    }

}