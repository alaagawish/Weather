package eg.gov.iti.jets.kotlin.weather.network

import eg.gov.iti.jets.kotlin.weather.model.OneCall

interface RemoteSource {
    //    suspend fun getDayForecastByNetwork(lat: Double, lon: Double): Forecast
    suspend fun getOneCallByNetwork(lat: Double, lon: Double, unit: String, lang: String): OneCall
}