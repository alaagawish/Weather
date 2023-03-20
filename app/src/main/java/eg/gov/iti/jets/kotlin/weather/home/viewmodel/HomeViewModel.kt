package eg.gov.iti.jets.kotlin.weather.home.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import eg.gov.iti.jets.kotlin.weather.LANGUAGE
import eg.gov.iti.jets.kotlin.weather.UNIT
import eg.gov.iti.jets.kotlin.weather.model.RepositoryInterface
import eg.gov.iti.jets.kotlin.weather.network.APIState
import eg.gov.iti.jets.kotlin.weather.sharedPreferences

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class HomeViewModel(private val repositoryInterface: RepositoryInterface) : ViewModel() {

    val forecastStateFlow = MutableStateFlow<APIState>(APIState.Waiting)

    init {
        getForecastData(
            30.0, 30.0, sharedPreferences.getString(UNIT, "metric")!!, sharedPreferences.getString(
                LANGUAGE, "en"
            )!!
        )
    }

    fun getForecastData(lat: Double, lon: Double, unit: String = "standard", lang: String = "en") {
        viewModelScope.launch {
            repositoryInterface.getOneCallRemote(lat, lon, unit, lang)
                .catch { e -> forecastStateFlow.value = APIState.Failure(e) }
                .collect { data -> forecastStateFlow.value = APIState.Success(data) }
        }
    }

}