package eg.gov.iti.jets.kotlin.weather.settings.viewmodel


import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import eg.gov.iti.jets.kotlin.weather.model.RepositoryInterface


class SettingsViewModelFactory(private val repositoryInterface: RepositoryInterface) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(SettingsViewModelFactory::class.java)) {
            SettingsViewModelFactory(repositoryInterface) as T
        } else {
            throw IllegalArgumentException("SettingsViewModelFactory, NO FOUND")
        }
    }

}