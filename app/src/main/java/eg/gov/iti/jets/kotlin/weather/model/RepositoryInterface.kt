package eg.gov.iti.jets.kotlin.weather.model

import kotlinx.coroutines.flow.Flow

interface RepositoryInterface {
    suspend fun getForecastRemote(lat: Double, lon: Double): Flow<Forecast>
    suspend fun getOneCallRemote(lat: Double, lon: Double): Flow<OneCall>

}