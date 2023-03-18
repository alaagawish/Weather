package eg.gov.iti.jets.kotlin.weather.alert.view

import eg.gov.iti.jets.kotlin.weather.model.Daily

interface AlertOnClickListener {
    fun deleteAlert(daily: Daily)
}