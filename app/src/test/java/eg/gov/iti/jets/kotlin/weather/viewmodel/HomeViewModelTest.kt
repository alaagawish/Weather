package eg.gov.iti.jets.kotlin.weather.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import eg.gov.iti.jets.kotlin.weather.MainRule
import eg.gov.iti.jets.kotlin.weather.model.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.test.*
import org.hamcrest.CoreMatchers.`is`
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class HomeViewModelTest {

    @ExperimentalCoroutinesApi
    @get:Rule
    val rule = InstantTaskExecutorRule()

    @ExperimentalCoroutinesApi
    @get:Rule
    val mainRule = MainRule()


    private val testDispatcher = TestCoroutineDispatcher()

    private lateinit var homeViewModel: HomeViewModel
    lateinit var repository: FakeRepository
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
    private val place1 = FavouritePlace(111111111, 11.4, 23.5, "cairo", "cold", "0xd", 22.4)
    private val place2 = FavouritePlace(222222222, 11.4, 23.5, "cairo", "cold", "0xd", 22.4)
    private val place3 = FavouritePlace(333333333, 11.4, 23.5, "cairo", "cold", "0xd", 22.4)

    private val day = DayDBModel(
        11111,
        11.1,
        22.2,
        "",
        44444,
        33333333,
        33.3,
        1,
        2,
        22.2,
        2,
        3333,
        44.4,
        "",
        "",
        ""
    )

    @Before
    fun setUp() {
        repository = FakeRepository()
        homeViewModel = HomeViewModel(repository)

    }

    @After
    fun tearDown() {
        repository.close()
        testDispatcher.cleanupTestCoroutines()
    }

    @Test
    fun addDay_dayDetailsAndHoursListAndComingDaysList_DoneAdd() = runTest {

        homeViewModel.addDay(day, mutableListOf(), mutableListOf())

        launch {
            homeViewModel.getDayStored()
            homeViewModel.dayLocalStateFlow.collect { r ->
                when (r) {
                    is APIState.SuccessRoomDay -> {
                        assertThat(r.day.dt, `is`(day.dt))
                        cancel()

                    }
                    is APIState.Waiting -> {
                    }
                    else -> {

                    }
                }
            }
        }

    }

    @Test
    fun getHoursStored_returnStoredHours() = runBlockingTest {
        homeViewModel.addDay(
            day,
            mutableListOf(hour1, hour2),
            mutableListOf()
        )
        launch {
            homeViewModel.getHoursStored()
            homeViewModel.hoursLocalStateFlow.collect { r ->
                when (r) {
                    is APIState.SuccessRoomHours -> {
                        assertThat(
                            r.list.size,
                            `is`(2)
                        )
                        assertThat(
                            r.list[0],
                            `is`(hour1)
                        )
                        cancel()

                    }
                    is APIState.Waiting -> {
                    }
                    else -> {

                    }
                }
            }
        }


    }

    @Test
    fun getNextDaysStored_returnStoredDays() = runBlockingTest {

        homeViewModel.addDay(day, mutableListOf(), mutableListOf(saturday, sunday, monday))

        launch {
            homeViewModel.getNextDaysStored()
            homeViewModel.comingDaysLocalStateFlow.collectLatest { r ->
                when (r) {
                    is APIState.SuccessRoomDaily -> {
                        assertThat(r.list.size, `is`(3))
                        assertThat(r.list[1], `is`(sunday))
                        cancel()
                    }
                    else -> {

                    }
                }
            }
        }

    }

    @Test
    fun resetLocalSource_freeStoredDaysAndStoredHoursAndStoredDay() = runBlockingTest {
        homeViewModel.addDay(
            day, mutableListOf(hour2, hour3), mutableListOf(saturday, monday)
        )
        homeViewModel.resetLocalSource()

        launch {
            homeViewModel.comingDaysLocalStateFlow.collectLatest { r ->
                //no data so it will be in waiting state
                assertThat(r, `is`(APIState.Waiting))
                cancel()
            }
        }
        launch {

            homeViewModel.dayLocalStateFlow.collectLatest {
                assertThat(it, `is`(APIState.Waiting))
                cancel()
            }
        }
        launch {

            homeViewModel.hoursLocalStateFlow.collectLatest {
                assertThat(it, `is`(APIState.Waiting))
                cancel()

            }
        }

    }

    @Test
    fun getDayStored_returnDay() = runBlockingTest {
        homeViewModel.addDay(day, mutableListOf(), mutableListOf())
        homeViewModel.getDayStored()
        launch {
            homeViewModel.dayLocalStateFlow.collectLatest { r ->
                when (r) {
                    is APIState.SuccessRoomDay -> {
                        assertThat(r.day, `is`(day))
                        assertThat(r.day.dt, `is`(day.dt))
                        cancel()
                    }
                    else -> {

                    }
                }
            }
        }

    }

    @Test
    fun getForecastData_LatAndLngAndUnitAndLang_returnOneCall() = runBlockingTest {
        homeViewModel.getForecastData(11.2, 122.4, "metric", "en")
        launch {
            homeViewModel.forecastStateFlow.collectLatest { r ->
                when (r) {
                    is APIState.Success -> {
                        assertThat(r.oneCall.lat, `is`(11.2))
                        assertThat(r.oneCall.lon, `is`(122.4))
                        cancel()
                    }
                    else -> {}
                }
            }
        }

    }

}
