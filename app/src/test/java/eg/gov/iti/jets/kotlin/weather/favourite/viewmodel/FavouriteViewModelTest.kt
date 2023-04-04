package eg.gov.iti.jets.kotlin.weather.favourite.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import eg.gov.iti.jets.kotlin.weather.MainRule
import eg.gov.iti.jets.kotlin.weather.model.APIState
import eg.gov.iti.jets.kotlin.weather.model.FakeRepository
import eg.gov.iti.jets.kotlin.weather.model.FavouritePlace
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.`is`
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class FavouriteViewModelTest {
    @ExperimentalCoroutinesApi
    @get:Rule
    val rule = InstantTaskExecutorRule()

    @ExperimentalCoroutinesApi
    @get:Rule
    val mainRule = MainRule()


    lateinit var repository: FakeRepository
    lateinit var favouriteViewModel: FavouriteViewModel

    private val favouritePlace = FavouritePlace(0,11, 1.0, 2.0, "", "", "", 2.9)
    private val favouritePlace2 = FavouritePlace(1,22, 1.0, 2.0, "", "", "", 2.9)
    private val favouritePlace3 = FavouritePlace(2,33, 1.0, 2.0, "", "", "", 2.9)
    private val favouritePlace4 = FavouritePlace(3,44, 1.0, 2.0, "", "", "", 2.9)

    @Before
    fun setUp() {
        repository = FakeRepository()
        favouriteViewModel = FavouriteViewModel(repository)

    }

    @After
    fun tearDown() {
        repository.close()
    }

    @Test
    fun `deletePlaceFromFav takePlace DeleteDone`() = runBlockingTest {
        favouriteViewModel.addPlaceToFav(favouritePlace)
        favouriteViewModel.addPlaceToFav(favouritePlace2)
        launch {
            favouriteViewModel.getAllFavPlaces()
            favouriteViewModel.favLocalPlacesStateFlow.collectLatest { r ->
                when (r) {
                    is APIState.SuccessFavPlaces -> {
                        assertThat(r.list.size, `is`(2))
                        cancel()
                    }
                    else -> {

                    }
                }
            }
        }
        launch {
            favouriteViewModel.deletePlaceFromFav(favouritePlace)
            favouriteViewModel.favLocalPlacesStateFlow.collectLatest { r ->
                when (r) {
                    is APIState.SuccessFavPlaces -> {
                        assertThat(r.list.size, `is`(1))
                        cancel()
                    }
                    else -> {

                    }
                }
            }
        }


    }

    @Test
    fun `addPlaceToFav takePlace InsertionDone`() = runBlockingTest {
        favouriteViewModel.addPlaceToFav(favouritePlace)
        launch {
            favouriteViewModel.getAllFavPlaces()
            favouriteViewModel.favLocalPlacesStateFlow.collectLatest { r ->
                when (r) {
                    is APIState.SuccessFavPlaces -> {
                        assertThat(r.list[0], `is`(favouritePlace))
                        cancel()
                    }
                    else -> {

                    }
                }
            }
        }


    }

    @Test
    fun `getAllFavPlaces ReturnListOfFavPlaces`() = runBlockingTest {
        favouriteViewModel.addPlaceToFav(favouritePlace)
        favouriteViewModel.addPlaceToFav(favouritePlace2)
        favouriteViewModel.addPlaceToFav(favouritePlace3)
        launch {
            favouriteViewModel.getAllFavPlaces()
            favouriteViewModel.favLocalPlacesStateFlow.collectLatest { r ->
                when (r) {
                    is APIState.SuccessFavPlaces -> {
                        assertThat(r.list.size, `is`(3))
                        cancel()
                    }
                    else -> {

                    }
                }
            }
        }


    }


}