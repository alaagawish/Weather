package eg.gov.iti.jets.kotlin.weather.model


data class Temp(
    val min: Double,
    val max: Double,
)

data class Alert(
    val sender_name: String,
    val event: String,
    val start: Long,
    val end: Long,
    val description: String,
    val tags: List<String>
)

data class Daily(
    val dt: Long,
    val temp: Temp,
    val weather: List<WeatherObject>,
)

data class OneCall(
    val lat: Double,
    val lon: Double,
    val timezone: String,
    val current: Current,
    val hourly: List<Hourly>,
    val daily: List<Daily>,
    val alerts: List<Alert>
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
