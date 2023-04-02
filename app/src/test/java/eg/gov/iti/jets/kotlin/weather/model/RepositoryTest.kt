package eg.gov.iti.jets.kotlin.weather.model

import eg.gov.iti.jets.kotlin.weather.db.FakeLocalSource
import eg.gov.iti.jets.kotlin.weather.network.FakeDayClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.`is`
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class RepositoryTest {

    private val alert = AlertsDB(1, "type1", 11111111, 22222222, "desc", "tag", false)
    private val alert2 = AlertsDB(2, "type2", 33333333, 44444444, "desc", "tag", false)
    private val alert3 = AlertsDB(3, "type3", 55555555, 66666666, "desc", "tag", false)
    private val alert4 = AlertsDB(4, "type4", 77777777, 88888888, "desc", "tag", false)
    private val favouritePlace = FavouritePlace(90909090, 22.3, 23.4, "egypt", "main", "icon", 33.4)
    private val favouritePlace2 =
        FavouritePlace(333333333, 22.3, 23.4, "egypt", "main", "icon", 33.4)
    private val favouritePlace3 =
        FavouritePlace(888888888, 22.3, 23.4, "egypt", "main", "icon", 33.4)


    private val localFavs = mutableListOf(favouritePlace, favouritePlace2, favouritePlace3)
    private val localAlerts = mutableListOf(alert4, alert3)
    private val localHours = mutableListOf(
        HourlyDBModel(1122112, 22.0, "main", "desc", "icon"),
        HourlyDBModel(44444444, 22.0, "main", "desc", "icon")
    )
    private val localDay = mutableListOf(
        DayDBModel(
            111111,
            11.3,
            23.4,
            "cairo",
            21212121,
            12121212,
            33.5,
            12,
            12,
            2.0,
            2,
            11221122,
            3.0,
            "main",
            "desc",
            "icon"
        )
    )
    private lateinit var fakeLocalSource: FakeLocalSource
    private lateinit var fakeDayClient: FakeDayClient
    private lateinit var repository: Repository

    @Before
    fun setUp() {
        fakeDayClient = FakeDayClient()
        fakeLocalSource = FakeLocalSource(
            alerts = localAlerts,
            day = localDay,
            hours = localHours,
            favs = localFavs
        )
        repository = Repository.getInstance(fakeDayClient, fakeLocalSource, Dispatchers.Unconfined)
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

        result.collectLatest { assertThat(it, `is`(localAlerts.toList())) }
        result.collectLatest { assertThat(it.size, `is`(4)) }
    }

    @Test
    fun deleteAlert_Alert_DeleteDone() = runBlockingTest {
        val alertDelete = AlertsDB(4, "type4", 77777777, 88888888, "desc", "tag", false)

        repository.deleteAlert(alertDelete)

        val result = repository.getAllAlerts
        result.collectLatest { assertThat(it, `is`(localAlerts.toList())) }
        result.collectLatest { assertThat(it.size, `is`(1)) }
        result.collectLatest { assertThat(it[0], `is`(alert3)) }

    }

    @Test
    fun getAlerts_returnLocalAlerts() = runBlockingTest {
        val result = repository.getAllAlerts
        result.collectLatest { assertThat(it, `is`(localAlerts.toList())) }
        result.collectLatest { assertThat(it[0], `is`(localAlerts[0])) }
        result.collectLatest { assertThat(it.size, `is`(2)) }

    }

    @Test
    fun addDay_Day_addDone() = runBlockingTest {
        val day = DayDBModel(
            3333333333,
            11.3,
            23.4,
            "cairo",
            21212121,
            12121212,
            33.5,
            12,
            12,
            2.0,
            2,
            11221122,
            3.0,
            "main",
            "desc",
            "icon"
        )
        repository.addDay(day)
        val result = repository.getDay

        result.collectLatest { assertThat(it, `is`(localDay.toList())) }
        result.collectLatest { assertThat(it, `is`(day)) }
    }

    @Test
    fun deleteDay_deleteDone() = runBlockingTest {
        val day = DayDBModel(
            3333333333,
            11.3,
            23.4,
            "cairo",
            21212121,
            12121212,
            33.5,
            12,
            12,
            2.0,
            2,
            11221122,
            3.0,
            "main",
            "desc",
            "icon"
        )
        repository.deleteAll()
        repository.addDay(day)
        val result = repository.getDay

        result.collectLatest { assertThat(it, `is`(localDay.toList())) }
        result.collectLatest { assertThat(it, `is`(day)) }

    }

    @Test
    fun getDay_returnLocalDay() = runBlockingTest {

        val result = repository.getDay
        result.collectLatest { assertThat(it, `is`(localDay.toList())) }
        result.collectLatest { assertThat(it.dt, `is`(localDay[0].dt)) }
    }

    @Test
    fun addHour_Hour_addDone() = runBlockingTest {
        val result = repository.getDayHours
        result.collectLatest { assertThat(it, `is`(localHours.toList())) }
        result.collectLatest { assertThat(it.size, `is`(2)) }
    }

    @Test
    fun deleteAllHours_DeleteDone() = runBlockingTest {
        repository.deleteAllHours()
        val result = repository.getDayHours
        result.collectLatest { assertThat(it, `is`(localHours.toList())) }
        result.collectLatest { assertThat(it.size, `is`(0)) }
    }

    @Test
    fun getDayHours_hours() = runBlockingTest {
        val result = repository.getDayHours
        result.collectLatest { assertThat(it, `is`(localHours.toList())) }
        result.collectLatest { assertThat(it[0].dt, `is`(localHours[0].dt)) }
    }


    @Test
    fun addComingDay_Day_AddDone() = runBlockingTest {
        repository.addComingDay(DailyDBModel(2222222, 22.9, 33.0, "main", "description", "icon"))
        val result = repository.getNextDays
        result.collectLatest { assertThat(it[0].dt, `is`(2222222)) }
        result.collectLatest { assertThat(it.size, `is`(1)) }
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

    @Test
    fun getAllFavPlaces_FavPlaces() = runBlockingTest {
        val result = repository.getAllFav
        result.collectLatest { assertThat(it, `is`(localFavs.toList())) }
        result.collectLatest { assertThat(it.size, `is`(3)) }
    }

    @Test
    fun addPlaceToFav_Place_AddDone() = runBlockingTest {
        val favouritePlace = FavouritePlace(90909090, 22.3, 23.4, "egypt", "main", "icon", 33.4)
        repository.addPlaceToFav(favouritePlace)
        val result = repository.getAllFav
        result.collectLatest { assertThat(it, `is`(localFavs.toList())) }
        result.collectLatest { assertThat(it.size, `is`(4)) }
    }

    @Test
    fun deletePlaceFromFav_Place_Deleted() = runBlockingTest {
        repository.deleteFavPlace(favouritePlace)
        val result = repository.getAllFav
        result.collectLatest { assertThat(it, `is`(localFavs.toList())) }
        result.collectLatest { assertThat(it.size, `is`(2)) }
    }

}