package eg.gov.iti.jets.kotlin.weather.model


data class Temp(
    val day: Double,
    val min: Double,
    val max: Double,
)

data class Daily(
    val dt: Long,
    val temp: Temp,
    val weather: List<WeatherObject>,
    val uvi: Double
)

data class OneCall(
    val lat: Double,
    val lon: Double,
    val timezone: String,
    val current: Current,
    val hourly: List<Hourly>,
    val daily: List<Daily>
)

data class Hourly(
    val dt: Long,
    val temp: Double,
    val weather: List<WeatherObject>,
)

data class Current(
    val dt: Long,
    val sunrise: Long,
    val sunset: Long,
    val temp: Double,
    val feels_like: Double,
    val pressure: Int,
    val humidity: Int,
    val uvi: Double,
    val clouds: Int,
    val visibility: Long,
    val wind_speed: Double,
    val weather: List<WeatherObject>
)

data class WeatherObject(
    val main: String,
    val description: String,
    val icon: String
)
