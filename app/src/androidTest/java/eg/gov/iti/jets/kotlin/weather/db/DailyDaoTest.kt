package eg.gov.iti.jets.kotlin.weather.db

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import eg.gov.iti.jets.kotlin.weather.MainRule
import eg.gov.iti.jets.kotlin.weather.model.DailyDBModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.job
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@SmallTest
class DailyDaoTest {

    @ExperimentalCoroutinesApi
    @get:Rule
    val mainRule = MainRule()

    @ExperimentalCoroutinesApi
    @get:Rule
    val rule = InstantTaskExecutorRule()

    lateinit var dayDatabase: DayDatabase

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
    fun getAllNextDays_returnNextDays() = runBlockingTest {
        val saturday = DailyDBModel(1679556049, 22.4, -40.3, "Cold", "description", "0xd")
        val sunday = DailyDBModel(1679666049, 22.4, -40.3, "Cold", "description", "0xd")
        val monday = DailyDBModel(1679557749, 22.4, -40.3, "Cold", "description", "0xd")
        delay(100)
        dayDatabase.getDailyDao().addComingDay(saturday)
        dayDatabase.getDailyDao().addComingDay(sunday)
        dayDatabase.getDailyDao().addComingDay(monday)
        val job = coroutineContext.job
        if (job.isCompleted) {
            dayDatabase.getDailyDao().getNextDays.collectLatest { res ->
                MatcherAssert.assertThat(
                    res.size,
                    CoreMatchers.`is`(3)
                )
            }
        }

        val checkJob = coroutineContext.job
        if (checkJob.isCompleted) {
            dayDatabase.getDailyDao().getNextDays.collectLatest { res ->
                MatcherAssert.assertThat(
                    res[2],
                    CoreMatchers.`is`(monday)
                )
            }
        }

    }

    @Test
    fun addNextDays_Days_AddDone() = runBlockingTest {

        val saturday = DailyDBModel(1679556049, 22.4, -40.3, "Cold", "description", "0xd")
        val sunday = DailyDBModel(1679666049, 22.4, -40.3, "Cold", "description", "0xd")

        delay(100)
        dayDatabase.getDailyDao().addComingDay(saturday)
        dayDatabase.getDailyDao().addComingDay(sunday)
        val job = coroutineContext.job
        if (job.isCompleted) {
            dayDatabase.getDailyDao().getNextDays.collectLatest { res ->
                MatcherAssert.assertThat(
                    res[0].dt,
                    CoreMatchers.`is`(saturday.dt)
                )
            }
        }

    }

    @Test
    fun deleteAllDays_DeleteDone() = runBlockingTest {

        val saturday = DailyDBModel(1679556049, 22.4, -40.3, "Cold", "description", "0xd")
        val sunday = DailyDBModel(1679666049, 22.4, -40.3, "Cold", "description", "0xd")
        delay(100)
        dayDatabase.getDailyDao().addComingDay(saturday)
        dayDatabase.getDailyDao().addComingDay(sunday)
        val job = coroutineContext.job
        if (job.isCompleted) {
            dayDatabase.getDailyDao().getNextDays.collectLatest { res ->
                MatcherAssert.assertThat(
                    res.size,
                    CoreMatchers.`is`(2)
                )
            }
        }

        dayDatabase.getDailyDao().deleteAllComingDays()
        val deleteJob = coroutineContext.job
        if (deleteJob.isCompleted) {
            dayDatabase.getAlertsDao().getAllAlerts.collectLatest { res ->
                MatcherAssert.assertThat(
                    res.size,
                    CoreMatchers.`is`(0)
                )
            }
        }

    }

}