package eg.gov.iti.jets.kotlin.weather.model

import eg.gov.iti.jets.kotlin.weather.db.FakeLocalSource
import eg.gov.iti.jets.kotlin.weather.network.FakeDayClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.`is`
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class RepositoryTest {

    private val alert = AlertsDB(1, "type1", 11111111, 22222222, "desc", "tag", false)
    private val alert2 = AlertsDB(2, "type2", 33333333, 44444444, "desc", "tag", false)
    private val alert4 = AlertsDB(4, "type4", 77777777, 88888888, "desc", "tag", false)
    private val favouritePlace = FavouritePlace(90909090, 22.3, 23.4, "egypt", "main", "icon", 33.4)
    private val favouritePlace2 =
        FavouritePlace(333333333, 22.3, 23.4, "egypt", "main", "icon", 33.4)


    private val hour = HourlyDBModel(1122112, 22.0, "main", "desc", "icon")
    private val hour2 = HourlyDBModel(44444444, 22.0, "main", "desc", "icon")

    private val day = DayDBModel(
        111111, 11.3, 23.4, "c", 212, 121, 33.5, 12, 12, 2.0, 2, 11221122, 3.0, "", "", ""
    )


    private val localDay = mutableListOf<DayDBModel>(

    )
    private lateinit var fakeLocalSource: FakeLocalSource
    private lateinit var fakeDayClient: FakeDayClient
    private lateinit var repository: Repository

    @Before
    fun setUp() {
        fakeDayClient = FakeDayClient()
        fakeLocalSource = FakeLocalSource()
        repository = Repository.getInstance(fakeDayClient, fakeLocalSource, Dispatchers.Unconfined)
    }

    @After
    fun tearDown() {

    }

    @Test
    fun getOneCallByNetwork_LatAndLngAndUnitAndLang_ReturnOnCall() = runBlockingTest {

        val result = repository.getOneCallRemote(11.3, 122.4, "metric", "en")

        result.collectLatest { assertThat(it.lat, `is`(11.3)) }
    }

    @Test
    fun addAlert_Alert_AddDone() = runBlockingTest {
        repository.addAlert(alert)
        repository.addAlert(alert2)

        val result = repository.getAllAlerts

        result.collectLatest { assertThat(it.size, `is`(2)) }
        repository.deleteAlert(alert)
        repository.deleteAlert(alert2)
    }

    @Test
    fun deleteAlert_Alert_DeleteDone() = runBlockingTest {
        val alertDelete = AlertsDB(4, "type4", 77777777, 88888888, "desc", "tag", false)
        val job = launch {
            repository.deleteAlert(alertDelete)
        }
        job.join()
        launch {
            val result = repository.getAllAlerts
            result.collectLatest { assertThat(it.size, `is`(0)) }
        }


    }

    @Test
    fun getAlerts_returnLocalAlerts() = runBlockingTest {
        repository.addAlert(alert)
        repository.addAlert(alert4)
        val result = repository.getAllAlerts
        result.collectLatest { assertThat(it.size, `is`(2)) }
        result.collectLatest { assertThat(it[1].id, `is`(4)) }

    }

    @Test
    fun addDay_Day_addDone() = runBlockingTest {

        repository.addDay(day)
        val result = repository.getDay

        result.collectLatest { assertThat(it, `is`(localDay.toList())) }
        result.collectLatest { assertThat(it.dt, `is`(111111)) }
    }

    @Test
    fun deleteDay_deleteDone() = runBlockingTest {
        repository.addDay(day)
        repository.deleteAll()

        val result = repository.getDay


        result.collectLatest { assertThat(it, `is`(null)) }

    }

    @Test
    fun getDay_returnLocalDay() = runBlockingTest {
        repository.addDay(day)
        val result = repository.getDay
        result.collectLatest { assertThat(it.dt, `is`(day.dt)) }
    }

    @Test
    fun addHour_Hour_addDone() = runBlockingTest {
        repository.addDayHours(hour)
        val result = repository.getDayHours
        result.collectLatest { assertThat(it.size, `is`(1)) }
        repository.deleteAllHours()
    }

    @Test
    fun deleteAllHours_DeleteDone() = runBlockingTest {
        repository.deleteAllHours()
        val result = repository.getDayHours
        result.collectLatest { assertThat(it.size, `is`(0)) }
    }

    @Test
    fun getDayHours_hours() = runBlockingTest {

        val result = repository.getDayHours
        result.collectLatest { assertThat(it.size, `is`(0)) }
        repository.addDayHours(hour)
        repository.addDayHours(hour2)

        repository.getDayHours.collectLatest { assertThat(it.size, `is`(2)) }


    }


    @Test
    fun getAllFavPlaces_FavPlaces() = runBlockingTest {
        repository.addPlaceToFav(favouritePlace)
        repository.addPlaceToFav(favouritePlace2)
        val result = repository.getAllFav

        result.collectLatest { assertThat(it.size, `is`(2)) }
        repository.deleteFavPlace(favouritePlace)
        repository.deleteFavPlace(favouritePlace2)
    }

    @Test
    fun addPlaceToFav_Place_AddDone() = runBlockingTest {
        val favouritePlace = FavouritePlace(90909090, 22.3, 23.4, "egypt", "main", "icon", 33.4)

        val job = launch {
            repository.addPlaceToFav(favouritePlace)
        }
        job.join()
        launch {
            val result = repository.getAllFav
            result.collectLatest { assertThat(it.size, `is`(1)) }
        }
        repository.deleteFavPlace(favouritePlace)
    }

    @Test
    fun deletePlaceFromFav_Place_Deleted() = runBlockingTest {
        repository.deleteFavPlace(favouritePlace)
        val result = repository.getAllFav
        result.collectLatest { assertThat(it.size, `is`(0)) }
    }

    @Test
    fun addComingDay_Day_AddDone() = runBlockingTest {
        repository.addComingDay(DailyDBModel(2222222, 22.9, 33.0, "main", "description", "icon"))
        val result = repository.getNextDays
        result.collectLatest { assertThat(it[0].dt, `is`(2222222)) }
        result.collectLatest { assertThat(it.size, `is`(1)) }
        repository.deleteAllComingDays()
    }

    @Test
    fun deleteAllComingDays_DeleteDone() = runBlockingTest {
        repository.addComingDay(DailyDBModel(2222222, 22.9, 33.0, "main", "description", "icon"))

        repository.deleteAllComingDays()
        val result = repository.getNextDays
        result.collectLatest { assertThat(it.size, `is`(0)) }
    }

    @Test
    fun getNextDays_Days() = runBlockingTest {
        repository.addComingDay(DailyDBModel(2222222, 22.9, 33.0, "main", "description", "icon"))
        repository.addComingDay(DailyDBModel(3333333, 22.9, 33.0, "main", "description", "icon"))

        val result = repository.getNextDays

        result.collectLatest { assertThat(it.size, `is`(2)) }
        result.collectLatest { assertThat(it[1].dt, `is`(3333333)) }
    }
}