package eg.gov.iti.jets.kotlin.weather.db

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import eg.gov.iti.jets.kotlin.weather.MainRule
import eg.gov.iti.jets.kotlin.weather.model.HourlyDBModel
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
class HourDaoTest {

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
    fun getAllDayHours_returnSavedHours() = runBlockingTest {
        val hour1 = HourlyDBModel(1679556049, 33.9, "Sunny", "desc", "0x1")
        val hour2 = HourlyDBModel(1677777777, 33.9, "Sunny", "desc", "0x1")
        val hour3 = HourlyDBModel(1679999999, 33.9, "Sunny", "desc", "0x1")

        delay(100)
        dayDatabase.getHourDao().addDayHours(hour1)
        dayDatabase.getHourDao().addDayHours(hour2)
        dayDatabase.getHourDao().addDayHours(hour3)
        val job = coroutineContext.job
        if (job.isCompleted) {
            dayDatabase.getHourDao().getDayHours.collectLatest { res ->
                MatcherAssert.assertThat(
                    res.size,
                    CoreMatchers.`is`(3)
                )
            }
        }

        val checkJob = coroutineContext.job
        if (checkJob.isCompleted) {
            dayDatabase.getHourDao().getDayHours.collectLatest { res ->
                MatcherAssert.assertThat(
                    res[0],
                    CoreMatchers.`is`(hour1)
                )
            }
        }

    }

    @Test
    fun addDayHours_Hours_AddDone() = runBlockingTest {

        val hour1 = HourlyDBModel(1679556049, 33.9, "cold", "desc", "0x1")
        val hour2 = HourlyDBModel(1677777777, 33.9, "Sunny", "desc", "0x1")
        val hour3 = HourlyDBModel(1679999999, 33.9, "Sunny", "desc", "0x1")

        delay(100)
        dayDatabase.getHourDao().addDayHours(hour1)
        dayDatabase.getHourDao().addDayHours(hour2)
        dayDatabase.getHourDao().addDayHours(hour3)
        val job = coroutineContext.job
        if (job.isCompleted) {
            dayDatabase.getHourDao().getDayHours.collectLatest { res ->
                MatcherAssert.assertThat(
                    res[0].main,
                    CoreMatchers.`is`(hour1.main)
                )
            }
        }

    }

    @Test
    fun deleteAllHours_DeleteDone() = runBlockingTest {

        val hour1 = HourlyDBModel(1679556049, 33.9, "Sunny", "desc", "0x1")
        val hour2 = HourlyDBModel(1677777777, 33.9, "Sunny", "desc", "0x1")
        val hour3 = HourlyDBModel(1679999999, 33.9, "Sunny", "desc", "0x1")

        delay(100)
        dayDatabase.getHourDao().addDayHours(hour1)
        dayDatabase.getHourDao().addDayHours(hour2)
        dayDatabase.getHourDao().addDayHours(hour3)
        val job = coroutineContext.job
        if (job.isCompleted) {
            dayDatabase.getHourDao().getDayHours.collectLatest { res ->
                MatcherAssert.assertThat(
                    res.size,
                    CoreMatchers.`is`(3)
                )
            }
        }

        dayDatabase.getDailyDao().deleteAllComingDays()
        val deleteJob = coroutineContext.job
        if (deleteJob.isCompleted) {
            dayDatabase.getHourDao().getDayHours.collectLatest { res ->
                MatcherAssert.assertThat(
                    res.size,
                    CoreMatchers.`is`(0)
                )
            }
        }

    }

}