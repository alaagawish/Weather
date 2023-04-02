package eg.gov.iti.jets.kotlin.weather.db

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import eg.gov.iti.jets.kotlin.weather.MainRule
import eg.gov.iti.jets.kotlin.weather.model.FavouritePlace
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
class FavDaoTest {

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
    fun getAllFavPlaces_returnFavPlaces() = runBlockingTest {
        val place1 = FavouritePlace(111111111, 11.4, 23.5, "cairo", "cold", "0xd", 22.4)
        val place2 = FavouritePlace(222222222, 11.4, 23.5, "cairo", "cold", "0xd", 22.4)
        val place3 = FavouritePlace(333333333, 11.4, 23.5, "cairo", "cold", "0xd", 22.4)

        delay(100)
        dayDatabase.getFavDao().addFavPlace(place1)
        dayDatabase.getFavDao().addFavPlace(place2)
        dayDatabase.getFavDao().addFavPlace(place3)
        val job = coroutineContext.job
        if (job.isCompleted) {
            dayDatabase.getFavDao().getAllFavPlaces.collectLatest { res ->
                MatcherAssert.assertThat(
                    res.size,
                    CoreMatchers.`is`(3)
                )
            }
        }

        val checkJob = coroutineContext.job
        if (checkJob.isCompleted) {
            dayDatabase.getFavDao().getAllFavPlaces.collectLatest { res ->
                MatcherAssert.assertThat(
                    res[2],
                    CoreMatchers.`is`(place3)
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
        dayDatabase.getFavDao().addFavPlace(place1)
        dayDatabase.getFavDao().addFavPlace(place2)
        dayDatabase.getFavDao().addFavPlace(place3)
        val job = coroutineContext.job
        if (job.isCompleted) {
            dayDatabase.getFavDao().getAllFavPlaces.collectLatest { res ->
                MatcherAssert.assertThat(
                    res[0].dt,
                    CoreMatchers.`is`(place1.dt)
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
        dayDatabase.getFavDao().addFavPlace(place1)
        dayDatabase.getFavDao().addFavPlace(place2)
        dayDatabase.getFavDao().addFavPlace(place3)
        val job = coroutineContext.job
        if (job.isCompleted) {
            dayDatabase.getFavDao().getAllFavPlaces.collectLatest { res ->
                MatcherAssert.assertThat(
                    res.size,
                    CoreMatchers.`is`(3)
                )
            }
        }

        dayDatabase.getFavDao().deletePlaceFromFav(place1)
        dayDatabase.getFavDao().deletePlaceFromFav(place3)
        val deleteJob = coroutineContext.job
        if (deleteJob.isCompleted) {
            dayDatabase.getFavDao().getAllFavPlaces.collectLatest { res ->
                MatcherAssert.assertThat(
                    res.size,
                    CoreMatchers.`is`(1)
                )
            }
        }

    }
}