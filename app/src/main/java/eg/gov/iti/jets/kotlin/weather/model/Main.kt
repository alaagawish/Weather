package eg.gov.iti.jets.kotlin.weather.model


data class Listt(
    val dt: Int,
    val main: Main,
    val weather: Weather,
    val clouds: Clouds,
    val wind: Wind,
    val visibility: Long,
    val pop: Int,
    val sys: Sys,
    val dt_txt: String
)

data class Temp(
    val day: Double,
    val min: Double,
    val max: Double,
    val night: Double,
    val eve: Double,
    val morn: Double
)

data class Daily(
    val dt: Long,
    val sunrise: Long,
    val sunset: Long,
    val moonrise: Long,
    val moonset: Long,
    val moon_phase: Double,
    val temp: Temp,
    val feels_like: FeelsLike,
    val pressure: Int,
    val humidity: Int,
    val dew_point: Double,
    val wind_speed: Double,
    val wind_deg: Int,
    val wind_gust: Double,
    val weather: List<WeatherObject>,
    val clouds: Int,
    val pop: Int,
    val rain: Double,
    val uvi: Double

)

data class FeelsLike(val day: Double, val night: Double, val eve: Double, val morn: Double)

data class Main(
    val temp: Double,
    val feels_like: Double,
    val temp_min: Double,
    val temp_max: Double,
    val pressure: Double,
    val sea_level: Double,
    val grnd_level: Double,
    val humidity: Double,
    val temp_kf: Double
)

data class OneCall(
    val lat: Double,
    val lon: Double,
    val timezone: String,
    val timezone_offset: Double,
    val current: Current,
    val daily: List<Daily>
)

data class Current(
    val dt: Long,
    val sunrise: Long,
    val sunset: Long,
    val temp: Int,
    val feels_like: Double,
    val pressure: Int,
    val humidity: Int,
    val dew_point: Double,
    val uvi: Double,
    val clouds: Int,
    val visibility: Long,
    val wind_speed: Double,
    val wind_deg: Int,
    val weather: List<WeatherObject>
)

data class Weather(val list: List<WeatherObject>)
data class WeatherObject(val id: Int, val main: String, val description: String, val icon: String)
data class Clouds(val all: Int)
data class Wind(val speed: Double, val deg: Int, val gust: Double)
data class Sys(val pod: String)
data class Coordinate(val lat: Double, var lon: Double)
data class City(
    val id: Long,
    val name: String,
    val coord: Coordinate,
    val country: String,
    val population: Int,
    val timeZone: Int,
    val sunrise: Long,
    val sunset: Long
)



