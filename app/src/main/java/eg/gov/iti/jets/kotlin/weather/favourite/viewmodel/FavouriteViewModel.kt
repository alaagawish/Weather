package eg.gov.iti.jets.kotlin.weather.favourite.viewmodel

import androidx.lifecycle.*
import eg.gov.iti.jets.kotlin.weather.model.FavouritePlace
import eg.gov.iti.jets.kotlin.weather.model.RepositoryInterface
import eg.gov.iti.jets.kotlin.weather.network.APIState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class FavouriteViewModel(private val repositoryInterface: RepositoryInterface) : ViewModel() {

    val favLocalPlacesStateFlow = MutableStateFlow<APIState>(APIState.Waiting)

    init {
        getAllFavPlaces()

    }

    fun deletePlaceFromFav(favouritePlace: FavouritePlace) {
        viewModelScope.launch(Dispatchers.IO) {
            repositoryInterface.deleteFavPlace(favouritePlace)
            getAllFavPlaces()
        }
    }

    fun addPlaceToFav(favouritePlace: FavouritePlace) {
        viewModelScope.launch(Dispatchers.IO) {
            repositoryInterface.addPlaceToFav(favouritePlace)
            getAllFavPlaces()
        }
    }

    fun getAllFavPlaces() {
        viewModelScope.launch {

            repositoryInterface.getAllFav.catch { e ->
                favLocalPlacesStateFlow.value = APIState.Failure(e)

            }.collect { d -> favLocalPlacesStateFlow.value = APIState.SuccessFavPlaces(d) }

        }
    }
}