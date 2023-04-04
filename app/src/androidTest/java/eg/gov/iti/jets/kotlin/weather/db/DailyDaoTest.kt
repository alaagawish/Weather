package eg.gov.iti.jets.kotlin.weather.db

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import eg.gov.iti.jets.kotlin.weather.MainRule
import eg.gov.iti.jets.kotlin.weather.model.DailyDBModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collectLatest
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

    private val saturday = DailyDBModel(1, 22.4, -40.3, "Cold", "description", "0xd")
    private val sunday = DailyDBModel(2, 22.4, -40.3, "Cold", "description", "0xd")
    private val monday = DailyDBModel(3, 22.4, -40.3, "Cold", "description", "0xd")


    @Test
    fun getAllNextDays_returnNextDays() = runBlockingTest {

        dayDatabase.getDailyDao().addComingDay(saturday)
        dayDatabase.getDailyDao().addComingDay(sunday)
        dayDatabase.getDailyDao().addComingDay(monday)
        launch {
            dayDatabase.getDailyDao().getNextDays.collectLatest { res ->
                if (res.isNotEmpty()) {
                    MatcherAssert.assertThat(
                        res.size,
                        CoreMatchers.`is`(3)
                    )
                    MatcherAssert.assertThat(
                        res[2],
                        CoreMatchers.`is`(monday)
                    )
                    cancel()
                }
            }
        }

    }

    @Test
    fun addNextDays_Days_AddDone() = runBlockingTest {

        dayDatabase.getDailyDao().addComingDay(saturday)
        dayDatabase.getDailyDao().addComingDay(sunday)
        launch {
            dayDatabase.getDailyDao().getNextDays.collectLatest { res ->
                if (res.isNotEmpty()) {

                    MatcherAssert.assertThat(
                        res[0].dt,
                        CoreMatchers.`is`(saturday.dt)

                    )
                    cancel()
                }
            }
        }

    }

    @Test
    fun deleteAllDays_DeleteDone() = runBlockingTest {
        dayDatabase.getDailyDao().addComingDay(saturday)
        dayDatabase.getDailyDao().addComingDay(sunday)
        launch {
            dayDatabase.getDailyDao().getNextDays.collectLatest { res ->
                if (res.isNotEmpty()) {
                    MatcherAssert.assertThat(
                        res.size,
                        CoreMatchers.`is`(2)
                    )
                    cancel()
                }
            }
        }

        dayDatabase.getDailyDao().deleteAllComingDays()
        launch {
            dayDatabase.getAlertsDao().getAllAlerts.collectLatest { res ->

                MatcherAssert.assertThat(
                    res.size,
                    CoreMatchers.`is`(0)
                )
                cancel()

            }
        }

    }

}