package eg.gov.iti.jets.kotlin.weather.network


import eg.gov.iti.jets.kotlin.weather.model.Forecast
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path


interface DayService {
    @GET("forecast?lat={lat}&lon={lon}&appid=3968301fd964c3b3a958dc7f3257d9f0")
    suspend fun getForecast(@Path("lat") lat: Double, @Path("lon") lon: Double):Response<Forecast>

}