package eg.gov.iti.jets.kotlin.weather.network

import eg.gov.iti.jets.kotlin.weather.model.OneCall

interface RemoteSource {
    suspend fun getOneCallByNetwork(lat: Double, lon: Double, unit: String, lang: String): OneCall
}