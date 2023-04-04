package eg.gov.iti.jets.kotlin.weather.alert.view

import eg.gov.iti.jets.kotlin.weather.model.AlertsDB

interface AlertOnClickListener {
    fun deleteAlert(alertsDB: AlertsDB)
}