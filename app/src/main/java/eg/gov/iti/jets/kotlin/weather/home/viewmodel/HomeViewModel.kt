package eg.gov.iti.jets.kotlin.weather.home.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import eg.gov.iti.jets.kotlin.weather.model.RepositoryInterface
import eg.gov.iti.jets.kotlin.weather.network.APIState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class HomeViewModel(private val repositoryInterface: RepositoryInterface) : ViewModel() {

    val forecastStateFlow = MutableStateFlow<APIState>(APIState.Waiting)

    init {
        getForecastData(30.0, 30.0, lang = "ar", unit = "metric")
    }

    fun getForecastData(lat: Double, lon: Double,unit:String="standard",lang:String="en") {
        viewModelScope.launch {
            repositoryInterface.getOneCallRemote(lat, lon,unit,lang)
                .catch { e -> forecastStateFlow.value = APIState.Failure(e) }
                .collect { data -> forecastStateFlow.value = APIState.Success(data) }
        }
    }

}