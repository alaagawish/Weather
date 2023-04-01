package eg.gov.iti.jets.kotlin.weather.network


import eg.gov.iti.jets.kotlin.weather.model.OneCall
import retrofit2.http.GET
import retrofit2.http.Query


interface DayService {
    @GET("onecall?appid=3968301fd964c3b3a958dc7f3257d9f0&exclude=minutely")
    suspend fun getOneCall(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("units") units: String,
        @Query("lang") lang: String
    ): OneCall

}