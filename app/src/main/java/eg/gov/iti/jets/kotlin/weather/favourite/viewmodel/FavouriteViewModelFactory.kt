package eg.gov.iti.jets.kotlin.weather.favourite.viewmodel


import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import eg.gov.iti.jets.kotlin.weather.model.RepositoryInterface

class FavouriteViewModelFactory(private val repositoryInterface: RepositoryInterface) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(FavouriteViewModel::class.java)) {
            FavouriteViewModel(repositoryInterface) as T
        } else {
            throw IllegalArgumentException(" FavouriteViewModelFactory , NO FOUND")
        }
    }

}