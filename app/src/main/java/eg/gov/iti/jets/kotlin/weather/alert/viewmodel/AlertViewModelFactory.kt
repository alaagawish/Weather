package eg.gov.iti.jets.kotlin.weather.alert.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import eg.gov.iti.jets.kotlin.weather.model.RepositoryInterface

class AlertViewModelFactory(private val repositoryInterface: RepositoryInterface) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(AlertViewModelFactory::class.java)) {
            AlertViewModelFactory(repositoryInterface) as T
        } else {
            throw IllegalArgumentException("AlertViewModelFactory, NO FOUND")
        }
    }

}