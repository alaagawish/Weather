package eg.gov.iti.jets.kotlin.weather.model

import androidx.room.Entity

//data class Forecast(
//    val cod: String,
//    val message: Int,
//    val cnt: Int,
//    val list: List<Listt>,
//    val city: City
//)

//data class Listt(
//    val dt: Int,
////    val main: Main,
//    val weather: List<Weather>,
//    val clouds: Clouds,
//    val wind: Wind,
//    val visibility: Long,
//    val pop: Int,
//    val sys: Sys,
//    val dt_txt: String
//)
//@Entity

data class Temp(
    val day: Double,
    val min: Double,
    val max: Double,
//    val night: Double,
//    val eve: Double,
//    val morn: Double
)

//@Entity

data class Daily(
    val dt: Long,
//    val sunrise: Long,
//    val sunset: Long,
//    val moonrise: Long,
//    val moonset: Long,
//    val moon_phase: Double,
    val temp: Temp,
//    val feels_like: FeelsLike,
//    val pressure: Int,
//    val humidity: Int,
//    val dew_point: Double,
//    val wind_speed: Double,
//    val wind_deg: Int,
//    val wind_gust: Double,
    val weather: List<WeatherObject>,
//    val clouds: Int,
//    val pop: Int,
//    val rain: Double,
    val uvi: Double

)

//data class FeelsLike(val day: Double, val night: Double, val eve: Double, val morn: Double)

//data class Main(
//    val temp: Double,
//    val feels_like: Double,
//    val temp_min: Double,
//    val temp_max: Double,
//    val pressure: Double,
//    val sea_level: Double,
//    val grnd_level: Double,
//    val humidity: Double,
//    val temp_kf: Double
//)
//@Entity

data class OneCall(
    val lat: Double,
    val lon: Double,
    val timezone: String,
//    val timezone_offset: Double,
    val current: Current,
    val hourly: List<Hourly>,
    val daily: List<Daily>
)

//@Entity

data class Hourly(
    val dt: Long,
    val temp: Double,
//    val feels_like: Double,
//    val pressure: Int,
//    val humidity: Int,
//    val dew_point: Double,
//    val uvi: Double,
//    val clouds: Int,
//    val visibility: Long,
//    val wind_speed: Double,
//    val wind_deg: Int,
//    val wind_gust: Double,
    val weather: List<WeatherObject>,
//    val pop: Int
)

//@Entity

data class Current(
    val dt: Long,
    val sunrise: Long,
    val sunset: Long,
    val temp: Double,
    val feels_like: Double,
    val pressure: Int,
    val humidity: Int,
//    val dew_point: Double,
    val uvi: Double,
    val clouds: Int,
    val visibility: Long,
    val wind_speed: Double,
//    val wind_deg: Int,
    val weather: List<WeatherObject>
)

//data class Weather(val list: List<WeatherObject>)
//@Entity
data class WeatherObject(val id: Int, val main: String, val description: String, val icon: String)
//data class Clouds(val all: Int)
//data class Wind(val speed: Double, val deg: Int, val gust: Double)
//data class Sys(val pod: String)
//data class Coordinate(val lat: Double, var lon: Double)
//data class City(
//    val id: Long,
//    val name: String,
//    val coord: Coordinate,
//    val country: String,
//    val population: Int,
//    val timeZone: Int,
//    val sunrise: Long,
//    val sunset: Long
//)



