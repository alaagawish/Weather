package eg.gov.iti.jets.kotlin.weather.favourite.view

import eg.gov.iti.jets.kotlin.weather.model.Daily

interface PlaceOnClickListener {
    fun displayPlace(daily: Daily)
    fun deletePlace(daily: Daily)

}