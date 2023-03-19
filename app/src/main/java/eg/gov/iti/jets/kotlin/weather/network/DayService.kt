package eg.gov.iti.jets.kotlin.weather.network


import eg.gov.iti.jets.kotlin.weather.model.Forecast
import eg.gov.iti.jets.kotlin.weather.model.OneCall
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query


interface DayService {
    //    @GET("forecast?lat={lat}&lon={lon}&appid=3968301fd964c3b3a958dc7f3257d9f0")
//    suspend fun getForecast(@Path("lat") lat: Double, @Path("lon") lon: Double): Forecast
    @GET("forecast?appid=3968301fd964c3b3a958dc7f3257d9f0")
    suspend fun getForecast(@Query("lat") lat: Double, @Query("lon") lon: Double): Forecast

    @GET("onecall?appid=3968301fd964c3b3a958dc7f3257d9f0&exclude=minutely")
    suspend fun getOneCall(@Query("lat") lat: Double, @Query("lon") lon: Double): OneCall

}