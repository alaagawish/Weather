package eg.gov.iti.jets.kotlin.weather.home.viewmodel

import android.util.Log
import android.view.View
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.material.snackbar.Snackbar
import eg.gov.iti.jets.kotlin.weather.*
import eg.gov.iti.jets.kotlin.weather.model.RepositoryInterface
import eg.gov.iti.jets.kotlin.weather.network.APIState

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class HomeViewModel(private val repositoryInterface: RepositoryInterface) : ViewModel() {

    val forecastStateFlow = MutableStateFlow<APIState>(APIState.Waiting)

    init {

        Log.d("TAG", ":  ${sharedPreferences.getString(LATITUDE, "1.0")?.toDouble()} ")
        Log.d("TAG", ": ${sharedPreferences.getString(LONGITUDE, "1.0")?.toDouble()} ")
//        if (sharedPreferences.getString(LATITUDE, "1.0")?.toDouble() != 1.0)

            getForecastData(
                sharedPreferences.getString(LATITUDE, "1.0")?.toDouble()!!,
                sharedPreferences.getString(LONGITUDE, "1.0")?.toDouble()!!,
                sharedPreferences.getString(UNIT, "metric")!!,
                sharedPreferences.getString(
                    LANGUAGE, "en"
                )!!
            )

//   .setAction("Display", null).show()
    }

    fun getForecastData(lat: Double, lon: Double, unit: String = "standard", lang: String = "en") {
        viewModelScope.launch {
            repositoryInterface.getOneCallRemote(lat, lon, unit, lang)
                .catch { e -> forecastStateFlow.value = APIState.Failure(e) }
                .collect { data -> forecastStateFlow.value = APIState.Success(data) }
        }
    }

}