package eg.gov.iti.jets.kotlin.weather.db

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import eg.gov.iti.jets.kotlin.weather.MainRule
import eg.gov.iti.jets.kotlin.weather.model.*
import junit.framework.TestCase.assertNull
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
@MediumTest
class LocalSourceTest {

    @ExperimentalCoroutinesApi
    @get:Rule
    val mainRule = MainRule()

    @ExperimentalCoroutinesApi
    @get:Rule
    val rule = InstantTaskExecutorRule()

    lateinit var dayDatabase: DayDatabase
    lateinit var localSource: LocalSource

    @Before
    fun setUp() {
        dayDatabase = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(), DayDatabase::class.java
        ).allowMainThreadQueries().build()

        localSource = LocalSource(
            dayDatabase.getFavDao(),
            dayDatabase.getDayDao(),
            dayDatabase.getAlertsDao(),
            dayDatabase.getHourDao(),
            dayDatabase.getDailyDao()
        )
    }

    @After
    fun tearDown() {
        dayDatabase.close()
    }

    private val day = DayDBModel(
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

    private val alert =
        AlertsDB(1, "new alert", 1679556049, 1679666049, "description", "tag", false)
    private val alert2 =
        AlertsDB(2, "new alert2", 1679556049, 1679666049, "description", "tag", false)
    private val alert3 =
        AlertsDB(3, "new alert2", 1679556049, 1679666049, "description", "tag", false)
    private val alert4 =
        AlertsDB(4, "new alert2", 1679556049, 1679666049, "description", "tag", false)

    private val saturday = DailyDBModel(11, 22.4, -40.3, "Cold", "description", "0xd")
    private val sunday = DailyDBModel(22, 22.4, -40.3, "Cold", "description", "0xd")
    private val monday = DailyDBModel(33, 22.4, -40.3, "Cold", "description", "0xd")

    private val hour1 = HourlyDBModel(1, 33.9, "Sunny", "desc", "0x1")
    private val hour2 = HourlyDBModel(2, 33.9, "Sunny", "desc", "0x1")
    private val hour3 = HourlyDBModel(3, 33.9, "Sunny", "desc", "0x1")
    private val place1 = FavouritePlace(1,111111111, 11.4, 23.5, "cairo", "cold", "0xd", 22.4)
    private val place2 = FavouritePlace(2,222222222, 11.4, 23.5, "cairo", "cold", "0xd", 22.4)
    private val place3 = FavouritePlace(3,333333333, 11.4, 23.5, "cairo", "cold", "0xd", 22.4)

    @Test
    fun addAlert_Alert_AddDone() = runBlockingTest {

        localSource.addAlert(alert)
        val result = localSource.getAllAlerts

        launch {
            result.collectLatest { res ->
                assertThat(
                    res[0].type, `is`(alert.type)
                )
                cancel()
            }
        }

    }

    @Test
    fun getAllAlerts_Alerts() = runBlockingTest {

        localSource.addAlert(alert)
        localSource.addAlert(alert2)
        localSource.addAlert(alert3)
        localSource.addAlert(alert4)
        val result = localSource.getAllAlerts

        launch {
            result.collectLatest { res ->
                assertThat(
                    res.size, `is`(4)
                )
                assertThat(
                    res[2], `is`(alert3)
                )
                cancel()
            }
        }

    }

    @Test
    fun deleteAlert_Alert_DeleteDone() = runBlockingTest {


        localSource.addAlert(alert)
        localSource.addAlert(alert2)
        val result = localSource.getAllAlerts

        launch {
            result.collectLatest { res ->
                assertThat(
                    res.size, `is`(2)
                )
                cancel()
            }
        }
        localSource.deleteAlert(alert)
        launch {
            result.collectLatest { res ->
                assertThat(
                    res.size, `is`(1)
                )
                cancel()
            }
        }

    }


    @Test
    fun getAllNextDays_returnNextDays() = runBlockingTest {

        localSource.addComingDay(saturday)
        localSource.addComingDay(sunday)
        localSource.addComingDay(monday)

        val result = localSource.getNextDays
        launch {
            result.collectLatest { res ->
                assertThat(
                    res.size, `is`(3)
                )
                cancel()
            }

        }

        launch {
            dayDatabase.getDailyDao().getNextDays.collectLatest { res ->
                assertThat(
                    res[2], `is`(monday)
                )
                cancel()

            }
        }

    }

    @Test
    fun addNextDays_Days_AddDone() = runBlockingTest {


        localSource.addComingDay(saturday)
        localSource.addComingDay(sunday)

        val result = localSource.getNextDays

        launch {
            result.collectLatest { res ->
                assertThat(
                    res[0].dt, `is`(saturday.dt)
                )
                cancel()

            }
        }

    }

    @Test
    fun deleteAllDays_DeleteDone() = runBlockingTest {

        localSource.addComingDay(saturday)
        localSource.addComingDay(sunday)

        val result = localSource.getNextDays
        launch {
            result.collectLatest { res ->
                assertThat(
                    res.size, `is`(2)
                )
                cancel()

            }
        }
        localSource.deleteAllComingDays()
        launch {
            localSource.getNextDays.collectLatest { res ->
                assertThat(
                    res.size, `is`(0)
                )
                cancel()

            }
        }

    }


    @Test
    fun getDay_returnDayDetails() = runBlockingTest {

        localSource.addDay(day)
        launch {
            localSource.getDay.collectLatest { res ->
                assertThat(
                    res.lat, `is`(day.lat)
                )
                assertThat(
                    res, `is`(day)
                )
                cancel()
            }
        }


    }

    @Test
    fun addDay_Day_AddDone() = runBlockingTest {

        localSource.addDay(day)
        launch {
            localSource.getDay.collectLatest { res ->
                assertThat(
                    res.dt, `is`(day.dt)
                )
                cancel()
            }
        }

    }

    @Test
    fun deleteDay_DeleteDone() = runBlockingTest {

        localSource.addDay(day)
        launch {
            localSource.getDay.collectLatest { res ->
                assertThat(
                    res, `is`(day)
                )
                cancel()
            }
        }

        localSource.deleteAll()
        launch {
            localSource.getDay.collectLatest { res ->
                assertNull(res)
                cancel()
            }
        }

    }


    @Test
    fun getAllFavPlaces_returnFavPlaces() = runBlockingTest {


        localSource.addPlaceToFav(place1)
        localSource.addPlaceToFav(place2)
        localSource.addPlaceToFav(place3)
        launch {
            localSource.getAllFavPlaces.collectLatest { res ->
                assertThat(
                    res.size, `is`(3)
                )
                assertThat(
                    res[2], `is`(place3)
                )
                cancel()
            }
        }

    }

    @Test
    fun addPlaceToFav_Place_AddDone() = runBlockingTest {

        localSource.addPlaceToFav(place1)
        localSource.addPlaceToFav(place2)
        localSource.addPlaceToFav(place3)
        launch {
            localSource.getAllFavPlaces.collectLatest { res ->
                assertThat(
                    res[0].dt, `is`(place1.dt)
                )
                cancel()
            }
        }

    }

    @Test
    fun deletePlaceFromFav_Place_DeleteDone() = runBlockingTest {


        localSource.addPlaceToFav(place1)
        localSource.addPlaceToFav(place2)
        localSource.addPlaceToFav(place3)
        launch {
            localSource.getAllFavPlaces.collectLatest { res ->
                assertThat(
                    res.size, `is`(3)
                )
                cancel()
            }
        }

        localSource.deletePlaceFromFav(place1)
        localSource.deletePlaceFromFav(place3)
        launch {
            localSource.getAllFavPlaces.collectLatest { res ->
                assertThat(
                    res.size, `is`(1)
                )
                cancel()
            }
        }

    }

    @Test
    fun getAllDayHours_returnSavedHours() = runBlockingTest {

        localSource.addDayHours(hour1)
        localSource.addDayHours(hour2)
        localSource.addDayHours(hour3)
        launch {
            localSource.getDayHours.collectLatest { res ->
                assertThat(
                    res.size, `is`(3)
                )
                cancel()
            }
        }

        launch {
            localSource.getDayHours.collectLatest { res ->
                assertThat(
                    res[0], `is`(hour1)
                )
                cancel()
            }
        }

    }

    @Test
    fun addDayHours_Hours_AddDone() = runBlockingTest {


        localSource.addDayHours(hour1)
        localSource.addDayHours(hour2)
        localSource.addDayHours(hour3)
        launch {
            localSource.getDayHours.collectLatest { res ->
                assertThat(
                    res[0].main, `is`(hour1.main)
                )
                cancel()
            }
        }

    }

    @Test
    fun deleteAllHours_DeleteDone() = runBlockingTest {


        localSource.addDayHours(hour1)
        localSource.addDayHours(hour2)
        localSource.addDayHours(hour3)
        launch {
            localSource.getDayHours.collectLatest { res ->
                assertThat(
                    res.size, `is`(3)
                )
                cancel()
            }
        }

        localSource.deleteAllHours()
        launch {
            localSource.getDayHours.collectLatest { res ->
                assertThat(
                    res.size, `is`(0)
                )
                cancel()
            }
        }

    }

}