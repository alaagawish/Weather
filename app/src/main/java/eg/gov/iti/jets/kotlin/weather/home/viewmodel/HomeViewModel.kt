package eg.gov.iti.jets.kotlin.weather.home.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import eg.gov.iti.jets.kotlin.weather.*
import eg.gov.iti.jets.kotlin.weather.utils.Constants.LANGUAGE
import eg.gov.iti.jets.kotlin.weather.utils.Constants.UNIT
import eg.gov.iti.jets.kotlin.weather.model.DailyDBModel
import eg.gov.iti.jets.kotlin.weather.model.DayDBModel
import eg.gov.iti.jets.kotlin.weather.model.HourlyDBModel
import eg.gov.iti.jets.kotlin.weather.model.RepositoryInterface
import eg.gov.iti.jets.kotlin.weather.network.APIState
import eg.gov.iti.jets.kotlin.weather.utils.Constants.LATITUDE
import eg.gov.iti.jets.kotlin.weather.utils.Constants.LONGITUDE
import kotlinx.coroutines.Dispatchers

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class HomeViewModel(private val repositoryInterface: RepositoryInterface) : ViewModel() {

    val forecastStateFlow = MutableStateFlow<APIState>(APIState.Waiting)
    val dayLocalStateFlow = MutableStateFlow<APIState>(APIState.Waiting)
    val hoursLocalStateFlow = MutableStateFlow<APIState>(APIState.Waiting)
    val comingDaysLocalStateFlow = MutableStateFlow<APIState>(APIState.Waiting)

    init {
        getForecastData(
            sharedPreferences.getString(LATITUDE, "0.0")!!.toDouble(),
            sharedPreferences.getString(LONGITUDE, "0.0")!!.toDouble(),
            sharedPreferences.getString(UNIT, "standard")!!,
            sharedPreferences.getString(LANGUAGE, "en")!!
        )
        if (sharedPreferences.getBoolean("isSavedLocal", false)) {
            getNextDaysStored()
            getDayStored()
            getHoursStored()
        }

    }

    fun getForecastData(
        lat: Double = sharedPreferences.getString(LATITUDE, "0.0")!!.toDouble(),
        lon: Double = sharedPreferences.getString(LONGITUDE, "0.0")!!.toDouble(),
        unit: String = sharedPreferences.getString(UNIT, "standard")!!,
        lang: String = sharedPreferences.getString(LANGUAGE, "en")!!
    ) {
        println("gggggggggggggggggggggggg $lat $lon")
        viewModelScope.launch {
            repositoryInterface.getOneCallRemote(lat, lon, unit, lang)
                .catch { e -> forecastStateFlow.value = APIState.Failure(e) }
                .collect { data -> forecastStateFlow.value = APIState.Success(data) }

        }

        with(sharedPreferences.edit()) {
            putBoolean("isSavedLocal", true)
            apply()
        }

    }

    private fun resetLocalSource() {
        viewModelScope.launch(Dispatchers.IO) {
            repositoryInterface.deleteAllComingDays()
            repositoryInterface.deleteAll()
            repositoryInterface.deleteAllHours()
        }
    }

    private fun getDayStored() {
        println("getDayStored")

        viewModelScope.launch {
            repositoryInterface.getDay
                .catch { e -> dayLocalStateFlow.value = APIState.Failure(e) }
                .collect { data ->
                    if (data != null) dayLocalStateFlow.value = APIState.SuccessRoomDay(data)
                }
        }
    }

    private fun getNextDaysStored() {
        println("getNextDaysStored")

        viewModelScope.launch {
            repositoryInterface.getNextDays.catch { e ->
                comingDaysLocalStateFlow.value = APIState.Failure(e)
            }
                .collect { data ->
                    comingDaysLocalStateFlow.value = APIState.SuccessRoomDaily(data)
                }
        }
    }

    private fun getHoursStored() {
        println("getHoursStored")
        viewModelScope.launch {
            repositoryInterface.getDayHours.catch { e ->
                hoursLocalStateFlow.value = APIState.Failure(e)
            }
                .collect { data ->
                    hoursLocalStateFlow.value = APIState.SuccessRoomHours(data)
                }
        }
    }

    fun addDay(dayDBModel: DayDBModel, list: List<HourlyDBModel>, daysList: List<DailyDBModel>) {
        resetLocalSource()
        viewModelScope.launch(Dispatchers.IO) {
            repositoryInterface.deleteAllComingDays()
            repositoryInterface.deleteAll()
            repositoryInterface.deleteAllHours()
            for (i in list)
                repositoryInterface.addDayHours(i)
            for (i in daysList)
                repositoryInterface.addComingDay(i)
            repositoryInterface.addDay(dayDBModel)

        }
    }
}