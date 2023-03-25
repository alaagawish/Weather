package eg.gov.iti.jets.kotlin.weather.settings.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import eg.gov.iti.jets.kotlin.weather.model.RepositoryInterface

class SettingsViewModel(private val repositoryInterface: RepositoryInterface) : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is settings Fragment"
    }
    val text: LiveData<String> = _text
}