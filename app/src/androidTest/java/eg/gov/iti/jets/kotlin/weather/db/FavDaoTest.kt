package eg.gov.iti.jets.kotlin.weather.db

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import eg.gov.iti.jets.kotlin.weather.MainRule
import eg.gov.iti.jets.kotlin.weather.model.FavouritePlace
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import kotlinx.coroutines.*
import kotlinx.coroutines.test.*
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.*

import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@SmallTest
class FavDaoTest {

    @ExperimentalCoroutinesApi
    @get:Rule
    val mainRule = MainRule()

    @ExperimentalCoroutinesApi
    @get:Rule
    val rule = InstantTaskExecutorRule()


    private val place1 = FavouritePlace(111111111, 11.4, 23.5, "cairo", "cold", "0xd", 22.4)
    private val place2 = FavouritePlace(222222222, 11.4, 23.5, "cairo", "cold", "0xd", 22.4)
    private val place3 = FavouritePlace(333333333, 11.4, 23.5, "cairo", "cold", "0xd", 22.4)


    lateinit var dayDatabase: DayDatabase
    lateinit var favDao: FavDao

    @Before
    fun setUp() {
        dayDatabase = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(), DayDatabase::class.java
        ).build()
        favDao = dayDatabase.getFavDao()
    }

    @After
    fun tearDown() {
        dayDatabase.close()
//        testCoroutineScope.cleanupTestCoroutines()
    }

    @Test
    fun getAllFavPlaces_returnFavPlaces() = runBlockingTest {
        favDao.addFavPlace(place1)
        favDao.addFavPlace(place2)
        favDao.addFavPlace(place3)
        launch {
            favDao.getAllFavPlaces.collectLatest { res ->
                assertThat(
                    res.size,
                    `is`(3)
                )
                assertThat(
                    res[2],
                    `is`(place3)
                )
                cancel()
            }
        }


    }

    @ExperimentalCoroutinesApi
    @Test
    fun addPlaceToFav_Place_AddDone() = runTest {

        dayDatabase.getFavDao().addFavPlace(place1)
        dayDatabase.getFavDao().addFavPlace(place2)
        dayDatabase.getFavDao().addFavPlace(place3)
        launch {
            dayDatabase.getFavDao().getAllFavPlaces
                .collectLatest { res ->
                    assertThat(
                        res[0].dt,
                        `is`(place1.dt)
                    )
                    cancel()

                }

        }

    }

    @Test
    fun deletePlaceFromFav_Place_DeleteDone() = runBlockingTest {

        dayDatabase.getFavDao().addFavPlace(place1)
        dayDatabase.getFavDao().addFavPlace(place2)
        dayDatabase.getFavDao().addFavPlace(place3)
        launch {
            dayDatabase.getFavDao().getAllFavPlaces.collectLatest { res ->
                assertThat(
                    res.size,
                    `is`(3)
                )
                cancel()
            }
        }


        dayDatabase.getFavDao().deletePlaceFromFav(place1)
        dayDatabase.getFavDao().deletePlaceFromFav(place3)
        launch {
            dayDatabase.getFavDao().getAllFavPlaces.collectLatest { res ->
                assertThat(
                    res.size,
                    `is`(1)
                )
                cancel()
            }

        }

    }
}