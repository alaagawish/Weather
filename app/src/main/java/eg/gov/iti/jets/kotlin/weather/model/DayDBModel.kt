package eg.gov.iti.jets.kotlin.weather.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "day")
data class DayDBModel(
    @PrimaryKey
    val id: Int,
    val temp: Double,
    val feels_like: Double,
    val all: Int,
    val speed: Double,
    val deg: Int,
    val gust: Double,
    val temp_min: Double,
    val temp_max: Double,
    val pressure: Double,
    val sea_level: Double,
    val grnd_level: Double,
    val humidity: Double,
    val temp_kf: Double,
    val main: String,
    val description: String,
    val icon: String,
    val lat: Double,
    var lon: Double,
    val name: String,
    val country: String,
    val timeZone: Int,
    val sunrise: Long,
    val sunset: Long
)
