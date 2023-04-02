package eg.gov.iti.jets.kotlin.weather.db

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import eg.gov.iti.jets.kotlin.weather.MainRule
import eg.gov.iti.jets.kotlin.weather.model.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.job
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
        ).allowMainThreadQueries()
            .build()

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

    @Test
    fun addAlert_Alert_AddDone() = runBlockingTest {

        val alert = AlertsDB(1, "new alert", 1679556049, 1679666049, "description", "tag", false)
        localSource.addAlert(alert)
        delay(100)
        val result = localSource.getAllAlerts

        val job = coroutineContext.job
        if (job.isCompleted) {
            result.collectLatest { res ->
                assertThat(
                    res[0].type,
                    `is`(alert.type)
                )
            }
        }

    }

    @Test
    fun getAllAlerts_Alerts() = runBlockingTest {

        val alert = AlertsDB(1, "new alert", 1679556049, 1679666049, "description", "tag", false)
        val alert2 = AlertsDB(2, "new alert2", 1679556049, 1679666049, "description", "tag", false)
        val alert3 = AlertsDB(3, "new alert2", 1679556049, 1679666049, "description", "tag", false)
        val alert4 = AlertsDB(4, "new alert2", 1679556049, 1679666049, "description", "tag", false)

        localSource.addAlert(alert)
        localSource.addAlert(alert2)
        localSource.addAlert(alert3)
        localSource.addAlert(alert4)
        delay(100)
        val result = localSource.getAllAlerts

        val job = coroutineContext.job
        if (job.isCompleted) {
            result.collectLatest { res ->
                assertThat(
                    res.size,
                    `is`(4)
                )
            }
        }

        val checkJob = coroutineContext.job
        if (checkJob.isCompleted) {
            result.collectLatest { res ->
                assertThat(
                    res[2],
                    `is`(alert3)
                )
            }
        }

    }

    @Test
    fun deleteAlert_Alert_DeleteDone() = runBlockingTest {

        val alert = AlertsDB(1, "new alert", 1679556049, 1679666049, "description", "tag", false)
        val alert2 = AlertsDB(2, "new alert2", 1679556049, 1679666049, "description", "tag", false)

        localSource.addAlert(alert)
        localSource.addAlert(alert2)
        delay(100)
        val result = localSource.getAllAlerts

        val job = coroutineContext.job
        if (job.isCompleted) {
            result.collectLatest { res ->
                assertThat(
                    res.size,
                    `is`(2)
                )
            }
        }
        localSource.deleteAlert(alert)
        val checkJob = coroutineContext.job
        if (checkJob.isCompleted) {
            result.collectLatest { res ->
                assertThat(
                    res.size,
                    `is`(1)
                )
            }
        }

    }


    @Test
    fun getAllNextDays_returnNextDays() = runBlockingTest {
        val saturday = DailyDBModel(1679556049, 22.4, -40.3, "Cold", "description", "0xd")
        val sunday = DailyDBModel(1679666049, 22.4, -40.3, "Cold", "description", "0xd")
        val monday = DailyDBModel(1679557749, 22.4, -40.3, "Cold", "description", "0xd")
        delay(100)
        localSource.addComingDay(saturday)
        localSource.addComingDay(sunday)
        localSource.addComingDay(monday)

        val result = localSource.getNextDays
        val job = coroutineContext.job
        if (job.isCompleted) {
            result.collectLatest { res ->
                assertThat(
                    res.size,
                    `is`(3)
                )
            }

        }

        val checkJob = coroutineContext.job
        if (checkJob.isCompleted) {
            dayDatabase.getDailyDao().getNextDays.collectLatest { res ->
                assertThat(
                    res[2],
                    `is`(monday)
                )
            }
        }

    }

    @Test
    fun addNextDays_Days_AddDone() = runBlockingTest {

        val saturday = DailyDBModel(1679556049, 22.4, -40.3, "Cold", "description", "0xd")
        val sunday = DailyDBModel(1679666049, 22.4, -40.3, "Cold", "description", "0xd")

        delay(100)
        localSource.addComingDay(saturday)
        localSource.addComingDay(sunday)

        val result = localSource.getNextDays

        val job = coroutineContext.job
        if (job.isCompleted) {
            result.collectLatest { res ->
                assertThat(
                    res[0].dt,
                    `is`(saturday.dt)
                )
            }
        }

    }

    @Test
    fun deleteAllDays_DeleteDone() = runBlockingTest {

        val saturday = DailyDBModel(1679556049, 22.4, -40.3, "Cold", "description", "0xd")
        val sunday = DailyDBModel(1679666049, 22.4, -40.3, "Cold", "description", "0xd")
        delay(100)
        localSource.addComingDay(saturday)
        localSource.addComingDay(sunday)

        val result = localSource.getNextDays
        val job = coroutineContext.job
        if (job.isCompleted) {
            result.collectLatest { res ->
                assertThat(
                    res.size,
                    `is`(2)
                )
            }
        }
        localSource.deleteAllComingDays()
        val deleteJob = coroutineContext.job
        if (deleteJob.isCompleted) {
            localSource.getNextDays.collectLatest { res ->
                assertThat(
                    res.size,
                    `is`(0)
                )
            }
        }

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
        localSource.addDay(day)
        val job = coroutineContext.job
        if (job.isCompleted) {
            localSource.getDay.collectLatest { res ->
                assertThat(
                    res.lat,
                    `is`(day.lat)
                )
            }
        }

        val checkJob = coroutineContext.job
        if (checkJob.isCompleted) {
            localSource.getDay.collectLatest { res ->
                assertThat(
                    res,
                    `is`(day)
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
        localSource.addDay(day)
        val job = coroutineContext.job
        if (job.isCompleted) {
            localSource.getDay.collectLatest { res ->
                assertThat(
                    res.dt,
                    `is`(day.dt)
                )
            }
        }

    }

    @Test
    fun deleteDay_DeleteDone() = runBlockingTest {

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
        localSource.addDay(day)
        val job = coroutineContext.job
        if (job.isCompleted) {
            localSource.getDay.collectLatest { res ->
                assertThat(
                    res,
                    `is`(day)
                )
            }
        }

        localSource.deleteAll()
        val deleteJob = coroutineContext.job
        if (deleteJob.isCompleted) {
            localSource.getDay.collectLatest { res ->
                assertThat(
                    res,
                    `is`(null)
                )
            }
        }

    }


    @Test
    fun getAllFavPlaces_returnFavPlaces() = runBlockingTest {
        val place1 = FavouritePlace(111111111, 11.4, 23.5, "cairo", "cold", "0xd", 22.4)
        val place2 = FavouritePlace(222222222, 11.4, 23.5, "cairo", "cold", "0xd", 22.4)
        val place3 = FavouritePlace(333333333, 11.4, 23.5, "cairo", "cold", "0xd", 22.4)

        delay(100)
        localSource.addPlaceToFav(place1)
        localSource.addPlaceToFav(place2)
        localSource.addPlaceToFav(place3)
        val job = coroutineContext.job
        if (job.isCompleted) {
            localSource.getAllFavPlaces.collectLatest { res ->
                assertThat(
                    res.size,
                    `is`(3)
                )
            }
        }

        val checkJob = coroutineContext.job
        if (checkJob.isCompleted) {
            localSource.getAllFavPlaces.collectLatest { res ->
                assertThat(
                    res[2],
                    `is`(place3)
                )
            }
        }

    }

    @Test
    fun addPlaceToFav_Place_AddDone() = runBlockingTest {

        val place1 = FavouritePlace(111111111, 11.4, 23.5, "cairo", "cold", "0xd", 22.4)
        val place2 = FavouritePlace(222222222, 11.4, 23.5, "cairo", "cold", "0xd", 22.4)
        val place3 = FavouritePlace(333333333, 11.4, 23.5, "cairo", "cold", "0xd", 22.4)

        delay(100)
        localSource.addPlaceToFav(place1)
        localSource.addPlaceToFav(place2)
        localSource.addPlaceToFav(place3)
        val job = coroutineContext.job
        if (job.isCompleted) {
            localSource.getAllFavPlaces.collectLatest { res ->
                assertThat(
                    res[0].dt,
                    `is`(place1.dt)
                )
            }
        }

    }

    @Test
    fun deletePlaceFromFav_Place_DeleteDone() = runBlockingTest {

        val place1 = FavouritePlace(111111111, 11.4, 23.5, "cairo", "cold", "0xd", 22.4)
        val place2 = FavouritePlace(222222222, 11.4, 23.5, "cairo", "cold", "0xd", 22.4)
        val place3 = FavouritePlace(333333333, 11.4, 23.5, "cairo", "cold", "0xd", 22.4)

        delay(100)
        localSource.addPlaceToFav(place1)
        localSource.addPlaceToFav(place2)
        localSource.addPlaceToFav(place3)
        val job = coroutineContext.job
        if (job.isCompleted) {
            localSource.getAllFavPlaces.collectLatest { res ->
                assertThat(
                    res.size,
                    `is`(3)
                )
            }
        }

        localSource.deletePlaceFromFav(place1)
        localSource.deletePlaceFromFav(place3)
        val deleteJob = coroutineContext.job
        if (deleteJob.isCompleted) {
            localSource.getAllFavPlaces.collectLatest { res ->
                assertThat(
                    res.size,
                    `is`(1)
                )
            }
        }

    }

    @Test
    fun getAllDayHours_returnSavedHours() = runBlockingTest {
        val hour1 = HourlyDBModel(1679556049, 33.9, "Sunny", "desc", "0x1")
        val hour2 = HourlyDBModel(1677777777, 33.9, "Sunny", "desc", "0x1")
        val hour3 = HourlyDBModel(1679999999, 33.9, "Sunny", "desc", "0x1")

        delay(100)
        localSource.addDayHours(hour1)
        localSource.addDayHours(hour2)
        localSource.addDayHours(hour3)
        val job = coroutineContext.job
        if (job.isCompleted) {
            localSource.getDayHours.collectLatest { res ->
                assertThat(
                    res.size,
                    `is`(3)
                )
            }
        }

        val checkJob = coroutineContext.job
        if (checkJob.isCompleted) {
            localSource.getDayHours.collectLatest { res ->
                assertThat(
                    res[0],
                    `is`(hour1)
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
        localSource.addDayHours(hour1)
        localSource.addDayHours(hour2)
        localSource.addDayHours(hour3)
        val job = coroutineContext.job
        if (job.isCompleted) {
            localSource.getDayHours.collectLatest { res ->
                assertThat(
                    res[0].main,
                    `is`(hour1.main)
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
        localSource.addDayHours(hour1)
        localSource.addDayHours(hour2)
        localSource.addDayHours(hour3)
        val job = coroutineContext.job
        if (job.isCompleted) {
            localSource.getDayHours.collectLatest { res ->
                assertThat(
                    res.size,
                    `is`(3)
                )
            }
        }

        localSource.deleteAllComingDays()
        val deleteJob = coroutineContext.job
        if (deleteJob.isCompleted) {
            localSource.getDayHours.collectLatest { res ->
                assertThat(
                    res.size,
                    `is`(0)
                )
            }
        }

    }

}