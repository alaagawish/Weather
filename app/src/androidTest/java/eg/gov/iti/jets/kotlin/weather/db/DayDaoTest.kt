package eg.gov.iti.jets.kotlin.weather.db

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import eg.gov.iti.jets.kotlin.weather.MainRule
import eg.gov.iti.jets.kotlin.weather.model.DayDBModel
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
class DayDaoTest {

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
    fun getDay_returnDayDetails() = runBlockingTest {
        val day = DayDBModel(
            1679556049,
            22.4,
            -40.3,
            "egypt",
            10000000,
            20000000,
            55.9,
            12,
            10,
            9.0,
            2,
            22222222,
            3.0,
            "cloudy",
            "desc",
            "0xD"
        )

        delay(100)
        dayDatabase.getDayDao().addDay(day)
        val job = coroutineContext.job
        if (job.isCompleted) {
            dayDatabase.getDayDao().getDay.collectLatest { res ->
                MatcherAssert.assertThat(
                    res.lat,
                    CoreMatchers.`is`(day.lat)
                )
            }
        }

        val checkJob = coroutineContext.job
        if (checkJob.isCompleted) {
            dayDatabase.getDayDao().getDay.collectLatest { res ->
                MatcherAssert.assertThat(
                    res,
                    CoreMatchers.`is`(day)
                )
            }
        }

    }

    @Test
    fun addDay_Day_AddDone() = runBlockingTest {

        val day = DayDBModel(
            1679556049,
            22.4,
            -40.3,
            "egypt",
            10000000,
            20000000,
            55.9,
            12,
            10,
            9.0,
            2,
            22222222,
            3.0,
            "cloudy",
            "desc",
            "0xD"
        )
        delay(100)
        dayDatabase.getDayDao().addDay(day)
        val job = coroutineContext.job
        if (job.isCompleted) {
            dayDatabase.getDayDao().getDay.collectLatest { res ->
                MatcherAssert.assertThat(
                    res.dt,
                    CoreMatchers.`is`(day.dt)
                )
            }
        }

    }

    @Test
    fun deleteAllDays_DeleteDone() = runBlockingTest {

        val day = DayDBModel(
            1679556049,
            22.4,
            -40.3,
            "cairo",
            10000000,
            20000000,
            55.9,
            12,
            10,
            9.0,
            2,
            22222222,
            3.0,
            "cloudy",
            "desc",
            "0xD"
        )
        delay(100)
        dayDatabase.getDayDao().addDay(day)
        val job = coroutineContext.job
        if (job.isCompleted) {
            dayDatabase.getDayDao().getDay.collectLatest { res ->
                MatcherAssert.assertThat(
                    res,
                    CoreMatchers.`is`(day)
                )
            }
        }

        dayDatabase.getDayDao().deleteAll()
        val deleteJob = coroutineContext.job
        if (deleteJob.isCompleted) {
            dayDatabase.getDayDao().getDay.collectLatest { res ->
                MatcherAssert.assertThat(
                    res,
                    CoreMatchers.`is`(null)
                )
            }
        }

    }
}