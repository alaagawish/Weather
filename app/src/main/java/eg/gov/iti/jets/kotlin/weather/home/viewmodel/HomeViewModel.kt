package eg.gov.iti.jets.kotlin.weather.home.viewmodel

import android.util.Log
import android.view.View
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.google.android.material.snackbar.Snackbar
import eg.gov.iti.jets.kotlin.weather.*
import eg.gov.iti.jets.kotlin.weather.model.DailyDBModel
import eg.gov.iti.jets.kotlin.weather.model.DayDBModel
import eg.gov.iti.jets.kotlin.weather.model.HourlyDBModel
import eg.gov.iti.jets.kotlin.weather.model.RepositoryInterface
import eg.gov.iti.jets.kotlin.weather.network.APIState
import kotlinx.coroutines.Dispatchers

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

    }

    fun getForecastData(lat: Double, lon: Double, unit: String = "standard", lang: String = "en") {
        viewModelScope.launch {
            repositoryInterface.getOneCallRemote(lat, lon, unit, lang)
                .catch { e -> forecastStateFlow.value = APIState.Failure(e) }
                .collect { data -> forecastStateFlow.value = APIState.Success(data) }

        }
    }

    private fun resetLocalSource() {
        viewModelScope.launch(Dispatchers.IO) {
            repositoryInterface.deleteAllComingDays()
            repositoryInterface.deleteAll()
            repositoryInterface.deleteAllHours()
        }
    }

    fun getDayStored() = repositoryInterface.getDay.asLiveData()
    fun getNextDaysStored() = repositoryInterface.getNextDays.asLiveData()
    fun getHoursStored() = repositoryInterface.getDayHours.asLiveData()
    fun addDay(dayDBModel: DayDBModel, list: List<HourlyDBModel>, daysList: List<DailyDBModel>) {
        resetLocalSource()
        viewModelScope.launch(Dispatchers.IO) {
            repositoryInterface.addDay(dayDBModel)
            for (i in list)
                repositoryInterface.addDayHours(i)
            for (i in daysList)
                repositoryInterface.addComingDay(i)

        }
    }
}