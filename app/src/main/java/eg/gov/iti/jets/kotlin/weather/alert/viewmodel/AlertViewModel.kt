package eg.gov.iti.jets.kotlin.weather.alert.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import eg.gov.iti.jets.kotlin.weather.model.AlertsDB
import eg.gov.iti.jets.kotlin.weather.model.RepositoryInterface
import eg.gov.iti.jets.kotlin.weather.model.APIState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class AlertViewModel(private val repositoryInterface: RepositoryInterface) : ViewModel() {

    val alertsLocalPlacesStateFlow = MutableStateFlow<APIState>(APIState.Waiting)

    init {
        getAllAlerts()
    }

    fun deleteAlert(alertsDB: AlertsDB) {
        viewModelScope.launch(Dispatchers.IO) {
            repositoryInterface.deleteAlert(alertsDB)
            getAllAlerts()
        }
    }

    fun addAlert(alertsDB: AlertsDB) {
        viewModelScope.launch(Dispatchers.IO) {
            repositoryInterface.addAlert(alertsDB)
            getAllAlerts()
        }
    }

    fun getAllAlerts() {
        viewModelScope.launch {

            repositoryInterface.getAllAlerts.catch { e ->
                alertsLocalPlacesStateFlow.value = APIState.Failure(e)

            }.collect { d -> alertsLocalPlacesStateFlow.value = APIState.SuccessRoomAlerts(d) }

        }
    }

}